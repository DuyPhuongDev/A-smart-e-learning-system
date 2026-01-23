package com.hcmut.lms.learning.application.service.impl;

import com.hcmut.lms.learning.application.service.EmbeddingService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of EmbeddingService using Gemini Embedding Model
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingServiceImpl implements EmbeddingService {

    private final EmbeddingModel geminiEmbeddingModel;

    // Gemini text-embedding-004 produces 768-dimensional vectors
    private static final int EMBEDDING_DIMENSION = 768;

    @Override
    public List<Float> generateEmbedding(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be null or empty for embedding");
        }

        log.debug("Generating embedding for text of {} characters", text.length());

        try {
            Response<Embedding> response = geminiEmbeddingModel.embed(text);
            Embedding embedding = response.content();

            // Convert float[] to List<Float>
            float[] vector = embedding.vector();
            List<Float> result = new ArrayList<>(vector.length);
            for (float v : vector) {
                result.add(v);
            }

            log.debug("Generated embedding of dimension {}", result.size());
            return result;

        } catch (Exception e) {
            log.error("Failed to generate embedding: {}", e.getMessage(), e);
            throw new RuntimeException("Embedding generation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<List<Float>> generateEmbeddings(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }

        log.info("Generating embeddings for {} texts", texts.size());

        try {
            // Convert to TextSegments
            List<TextSegment> segments = texts.stream()
                    .map(TextSegment::from)
                    .collect(Collectors.toList());

            Response<List<Embedding>> response = geminiEmbeddingModel.embedAll(segments);
            List<Embedding> embeddings = response.content();

            // Convert to List<List<Float>>
            List<List<Float>> results = new ArrayList<>(embeddings.size());
            for (Embedding embedding : embeddings) {
                float[] vector = embedding.vector();
                List<Float> result = new ArrayList<>(vector.length);
                for (float v : vector) {
                    result.add(v);
                }
                results.add(result);
            }

            log.info("Generated {} embeddings successfully", results.size());
            return results;

        } catch (Exception e) {
            log.error("Failed to generate batch embeddings: {}", e.getMessage(), e);
            throw new RuntimeException("Batch embedding generation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public int getEmbeddingDimension() {
        return EMBEDDING_DIMENSION;
    }
}
