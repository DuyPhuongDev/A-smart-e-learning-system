package com.hcmut.lms.coachingchatbot.application.service.impl;

import com.hcmut.lms.coachingchatbot.application.service.EmbeddingService;
import com.hcmut.lms.coachingchatbot.client.GeminiEmbeddingClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of EmbeddingService using Gemini Embedding Client
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingServiceImpl implements EmbeddingService {

    private final GeminiEmbeddingClient geminiEmbeddingClient;

    @Override
    public List<Float> generateEmbedding(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be null or empty for embedding");
        }

        log.debug("Generating embedding for text of {} characters", text.length());

        try {
            List<Float> result = geminiEmbeddingClient.generateEmbedding(text);
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
            List<List<Float>> results = geminiEmbeddingClient.generateEmbeddings(texts);
            log.info("Generated {} embeddings successfully", results.size());
            return results;

        } catch (Exception e) {
            log.error("Failed to generate batch embeddings: {}", e.getMessage(), e);
            throw new RuntimeException("Batch embedding generation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public int getEmbeddingDimension() {
        return geminiEmbeddingClient.getEmbeddingDimension();
    }
}
