package com.hcmut.lms.learning.application.service;

import java.util.List;

/**
 * Service for generating embeddings and storing in Qdrant vector database
 */
public interface EmbeddingService {

    /**
     * Generate embedding vector for text
     *
     * @param text Text to embed
     * @return Embedding vector
     */
    List<Float> generateEmbedding(String text);

    /**
     * Generate embeddings for multiple texts (batch)
     *
     * @param texts List of texts
     * @return List of embedding vectors
     */
    List<List<Float>> generateEmbeddings(List<String> texts);

    /**
     * Get the dimension of embedding vectors
     *
     * @return Embedding dimension
     */
    int getEmbeddingDimension();
}
