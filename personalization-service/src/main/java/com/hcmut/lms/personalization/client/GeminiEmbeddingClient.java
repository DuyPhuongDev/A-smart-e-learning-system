package com.hcmut.lms.personalization.client;

import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import com.hcmut.lms.personalization.config.GeminiConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiEmbeddingClient {

  public static final int EMBEDDING_DIMENSION = 768;
  private static final int BATCH_SIZE = 100;
  private static final int MAX_PARALLEL_BATCHES = 3;

  private final GeminiConfig geminiConfig;
  private final ExecutorService batchExecutor = Executors.newFixedThreadPool(MAX_PARALLEL_BATCHES);
  private final Semaphore parallelismSemaphore = new Semaphore(MAX_PARALLEL_BATCHES);

  private Client client;

  @PostConstruct
  public void init() {
    String apiKey = geminiConfig.getApiKey();
    if (apiKey == null || apiKey.isBlank()) {
      log.error("Gemini API key is not configured! Set gemini.api-key in application.yml");
      throw new IllegalStateException("Gemini API key is required");
    }
    this.client = Client.builder().apiKey(apiKey).build();
    log.info("Initialized Gemini Embedding Client with model: {}", geminiConfig.getEmbeddingModelName());
  }

  public List<Float> generateEmbedding(String text) {
    if (text == null || text.isBlank()) {
      throw new IllegalArgumentException("Text cannot be null or empty for embedding");
    }
    EmbedContentConfig config = EmbedContentConfig.builder().outputDimensionality(EMBEDDING_DIMENSION).build();
    EmbedContentResponse response = client.models.embedContent(geminiConfig.getEmbeddingModelName(), text, config);
    return getFloats(response);
  }

  private static @NonNull List<Float> getFloats(EmbedContentResponse response) {
    List<ContentEmbedding> contentEmbeddings = response.embeddings()
        .orElseThrow(() -> new RuntimeException("No embeddings in response from Gemini API"));
    if (contentEmbeddings.isEmpty()) {
      throw new RuntimeException("Empty embeddings list from Gemini API");
    }
    ContentEmbedding firstEmbedding = contentEmbeddings.getFirst();
    return firstEmbedding.values().orElseThrow(() -> new RuntimeException("Empty embedding values from Gemini API"));
  }

  // Use synchronous embedContent with multiple inputs for each sub-batch.
  public List<List<Float>> generateEmbeddings(List<String> texts, String taskType) {
    if (texts == null || texts.isEmpty()) {
      return List.of();
    }

    List<List<String>> subBatches = partitionList(texts);
    log.info(
        "Embedding {} texts in {} sub-batch(es), maxParallel={}", texts.size(), subBatches.size(),
        MAX_PARALLEL_BATCHES);

    List<CompletableFuture<List<List<Float>>>> futures = new ArrayList<>(subBatches.size());
    for (int i = 0; i < subBatches.size(); i++) {
      final int batchIndex = i;
      final List<String> batch = subBatches.get(i);
      futures.add(CompletableFuture.supplyAsync(
          () -> {
            try {
              parallelismSemaphore.acquire();
              try {
                return processBatch(batch, taskType, batchIndex);
              } finally {
                parallelismSemaphore.release();
              }
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              throw new RuntimeException("Batch embedding interrupted", e);
            }
          }, batchExecutor));
    }

    List<List<Float>> results = new ArrayList<>(texts.size());
    for (CompletableFuture<List<List<Float>>> future : futures) {
      results.addAll(future.join());
    }
    return results;
  }

  private List<List<Float>> processBatch(List<String> texts, String taskType, int batchIndex) {
    try {
      EmbedContentConfig config = EmbedContentConfig.builder()
          .taskType(mapTaskType(taskType))
          .outputDimensionality(EMBEDDING_DIMENSION)
          .build();

      EmbedContentResponse response = client.models.embedContent(geminiConfig.getEmbeddingModelName(), texts, config);

      List<List<Float>> embeddings = response.embeddings()
          .map(items -> items.stream().map(embedding -> embedding.values().orElse(Collections.emptyList())).toList())
          .orElse(List.of());

      if (embeddings.isEmpty()) {
        log.warn("Batch {} returned empty embeddings list, fallback to serial embedding", batchIndex);
        return fallbackSerial(texts);
      }

      return padEmptyEmbeddings(embeddings, texts.size());
    } catch (Exception ex) {
      log.warn(
          "Batch {} failed using synchronous embedContent: {}. fallback to serial embedding", batchIndex,
          ex.getMessage());
      return fallbackSerial(texts);
    }
  }

  private List<List<Float>> padEmptyEmbeddings(List<List<Float>> values, int expectedSize) {
    List<List<Float>> out = new ArrayList<>(values);
    while (out.size() < expectedSize) {
      out.add(List.of());
    }
    if (out.size() > expectedSize) {
      return new ArrayList<>(out.subList(0, expectedSize));
    }
    return out;
  }


  private List<List<Float>> fallbackSerial(List<String> texts) {
    List<List<Float>> fallback = new ArrayList<>(texts.size());
    for (String text : texts) {
      if (text != null && !text.isBlank()) {
        try {
          fallback.add(generateEmbedding(text));
        } catch (Exception ex) {
          log.warn("Serial fallback failed for one text: {}", ex.getMessage());
          fallback.add(List.of());
        }
      } else {
        fallback.add(List.of());
      }
    }
    return fallback;
  }

  private String mapTaskType(String taskType) {
    if (taskType == null || taskType.isBlank()) {
      return "TASK_TYPE_UNSPECIFIED";
    }
    String normalized = taskType.toLowerCase().replace('-', '_').replace(' ', '_');
    return switch (normalized) {
      case "retrieval_query" -> "RETRIEVAL_QUERY";
      case "retrieval_document" -> "RETRIEVAL_DOCUMENT";
      case "semantic_similarity" -> "SEMANTIC_SIMILARITY";
      case "classification" -> "CLASSIFICATION";
      case "clustering" -> "CLUSTERING";
      default -> "TASK_TYPE_UNSPECIFIED";
    };
  }

  private static <T> List<List<T>> partitionList(List<T> items) {
    List<List<T>> out = new ArrayList<>();
    for (int i = 0; i < items.size(); i += BATCH_SIZE) {
      out.add(new ArrayList<>(items.subList(i, Math.min(i + BATCH_SIZE, items.size()))));
    }
    return out;
  }

  public int getEmbeddingDimension() {
    return EMBEDDING_DIMENSION;
  }
}
