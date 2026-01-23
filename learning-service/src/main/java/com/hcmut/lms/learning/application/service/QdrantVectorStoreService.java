package com.hcmut.lms.learning.application.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing vectors in a Qdrant database
 */
public interface QdrantVectorStoreService {

    /**
     * Initialize a collection if not exists
     *
     * @param collectionName Collection name
     * @param vectorDimension Vector dimension
     */
    void initializeCollection(String collectionName, int vectorDimension);

    /**
     * Upsert a single vector point
     *
     * @param collectionName Collection name
     * @param pointId Unique point ID
     * @param vector Embedding vector
     * @param payload Metadata payload
     */
    void upsertPoint(String collectionName, UUID pointId, List<Float> vector, Map<String, Object> payload);

    /**
     * Upsert multiple vector points in batch
     *
     * @param collectionName Collection name
     * @param points List of points to upsert
     */
    void upsertPoints(String collectionName, List<VectorPoint> points);

    /**
     * Search for similar vectors
     *
     * @param collectionName Collection name
     * @param queryVector Query vector
     * @param limit Maximum results
     * @return List of search results
     */
    List<SearchResult> search(String collectionName, List<Float> queryVector, int limit);

    /**
     * Search with filter
     *
     * @param collectionName Collection name
     * @param queryVector Query vector
     * @param filter Filter conditions
     * @param limit Maximum results
     * @return List of search results
     */
    List<SearchResult> searchWithFilter(String collectionName, List<Float> queryVector,
                                        Map<String, Object> filter, int limit);

    /**
     * Delete points by IDs
     *
     * @param collectionName Collection name
     * @param pointIds Point IDs to delete
     */
    void deletePoints(String collectionName, List<UUID> pointIds);

    /**
     * Delete points by filter (e.g., by lectureKnowledgeId)
     *
     * @param collectionName Collection name
     * @param filter Filter conditions
     */
    void deleteByFilter(String collectionName, Map<String, Object> filter);

    /**
     * Check if a collection exists
     *
     * @param collectionName Collection name
     * @return true if exists
     */
    boolean collectionExists(String collectionName);

    /**
     * Vector point record for batch operations
     */
    record VectorPoint(
            UUID id,
            List<Float> vector,
            Map<String, Object> payload
    ) {}

    /**
     * Search result record
     */
    record SearchResult(
            UUID id,
            float score,
            Map<String, Object> payload
    ) {}
}
