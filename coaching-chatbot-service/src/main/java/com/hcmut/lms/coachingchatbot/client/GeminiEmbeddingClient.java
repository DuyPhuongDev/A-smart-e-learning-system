package com.hcmut.lms.coachingchatbot.client;

import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import com.hcmut.lms.coachingchatbot.config.GeminiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Client for Google Gemini Embedding API
 * Uses google-genai library with gemini-embedding-001 model
 * Handles text embedding operations for vector store
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GeminiEmbeddingClient {

    // Gemini embedding-001 produces 768-dimensional vectors
    public static final int EMBEDDING_DIMENSION = 768;

    private final GeminiConfig geminiConfig;

    private Client client;

    @PostConstruct
    public void init() {
        String apiKey = geminiConfig.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.error("Gemini API key is not configured! Set gemini.api-key in application.yml");
            throw new IllegalStateException("Gemini API key is required");
        }

        log.info("Initializing Gemini Embedding Client with model: {}", geminiConfig.getEmbeddingModelName());
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
        log.info("Gemini Embedding Client initialized successfully");
    }

    /**
     * Get the embedding model name from config
     */
    public String getEmbeddingModelName() {
        return geminiConfig.getEmbeddingModelName();
    }

    /**
     * Generate embedding for a single text
     *
     * @param text The input text to embed
     * @return List of float values representing the embedding vector
     */
    public List<Float> generateEmbedding(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be null or empty for embedding");
        }

        log.debug("Generating embedding for text of {} characters", text.length());

        try {
            EmbedContentConfig config = EmbedContentConfig.builder()
                    .outputDimensionality(EMBEDDING_DIMENSION)
                    .build();

            EmbedContentResponse response = client.models.embedContent(
                    geminiConfig.getEmbeddingModelName(), text, config);

            if (response == null) {
                throw new RuntimeException("Empty embedding response from Gemini API");
            }

            // Get embedding values from response - API returns Optional
            List<ContentEmbedding> contentEmbeddings = response.embeddings()
                    .orElseThrow(() -> new RuntimeException("No embeddings in response from Gemini API"));

            if (contentEmbeddings.isEmpty()) {
                throw new RuntimeException("Empty embeddings list from Gemini API");
            }

            // Get the first embedding's values
            ContentEmbedding firstEmbedding = contentEmbeddings.getFirst();
            if (firstEmbedding == null) {
                throw new RuntimeException("Null embedding from Gemini API");
            }

            List<Float> embedding = firstEmbedding.values()
                    .orElseThrow(() -> new RuntimeException("Empty embedding values from Gemini API"));

            log.debug("Generated embedding of dimension {}", embedding.size());
            return embedding;

        } catch (Exception e) {
            log.error("Failed to generate embedding: {}", e.getMessage(), e);
            throw new RuntimeException("Embedding generation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Generate embeddings for multiple texts (batch)
     *
     * @param texts List of texts to embed
     * @return List of embedding vectors
     */
    public List<List<Float>> generateEmbeddings(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }

        log.info("Generating embeddings for {} texts", texts.size());

        List<List<Float>> results = new ArrayList<>(texts.size());

        try {
            // Process each text individually
            for (String text : texts) {
                if (text != null && !text.isBlank()) {
                    List<Float> embedding = generateEmbedding(text);
                    results.add(embedding);
                } else {
                    // Add empty embedding for blank texts
                    results.add(new ArrayList<>());
                }
            }

            log.info("Generated {} embeddings successfully", results.size());
            return results;

        } catch (Exception e) {
            log.error("Failed to generate batch embeddings: {}", e.getMessage(), e);
            throw new RuntimeException("Batch embedding generation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get the embedding dimension for the current model
     *
     * @return Embedding dimension (768 for gemini-embedding-001)
     */
    public int getEmbeddingDimension() {
        return EMBEDDING_DIMENSION;
    }

    /**
     * Check if the client is properly initialized
     *
     * @return true if client is ready
     */
    public boolean isReady() {
        String apiKey = geminiConfig.getApiKey();
        return client != null && apiKey != null && !apiKey.isBlank();
    }
}
