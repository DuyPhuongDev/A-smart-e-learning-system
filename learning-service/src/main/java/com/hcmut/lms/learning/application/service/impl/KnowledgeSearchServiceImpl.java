package com.hcmut.lms.learning.application.service.impl;

import com.hcmut.lms.learning.application.service.EmbeddingService;
import com.hcmut.lms.learning.application.service.KnowledgeSearchService;
import com.hcmut.lms.learning.application.service.QdrantVectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of KnowledgeSearchService
 * Provides semantic search across lecture knowledge using Qdrant
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeSearchServiceImpl implements KnowledgeSearchService {

    private final EmbeddingService embeddingService;
    private final QdrantVectorStoreService vectorStoreService;

    @Value("${qdrant.collection-name:lecture_knowledge}")
    private String collectionName;

    @Override
    public List<SearchResult> search(String query, int limit) {
        log.info("Searching for: '{}' with limit {}", query, limit);

        try {
            // Generate embedding for query
            List<Float> queryVector = embeddingService.generateEmbedding(query);

            // Search in Qdrant
            List<QdrantVectorStoreService.SearchResult> qdrantResults =
                    vectorStoreService.search(collectionName, queryVector, limit);

            // Convert to SearchResult
            return qdrantResults.stream()
                    .map(this::toSearchResult)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Search failed: {}", e.getMessage(), e);
            throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SearchResult> searchInLecture(UUID lectureId, String query, int limit) {
        log.info("Searching in lecture {} for: '{}' with limit {}", lectureId, query, limit);

        try {
            List<Float> queryVector = embeddingService.generateEmbedding(query);

            Map<String, Object> filter = new HashMap<>();
            // lectureKnowledgeId = lectureId (1:1 relationship)
            filter.put("lectureKnowledgeId", lectureId.toString());

            List<QdrantVectorStoreService.SearchResult> qdrantResults =
                    vectorStoreService.searchWithFilter(collectionName, queryVector, filter, limit);

            return qdrantResults.stream()
                    .map(this::toSearchResult)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Search in lecture failed: {}", e.getMessage(), e);
            throw new RuntimeException("Search in lecture failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SearchResult> searchInChapter(UUID chapterId, String query, int limit) {
        log.info("Searching in chapter {} for: '{}' with limit {}", chapterId, query, limit);

        try {
            List<Float> queryVector = embeddingService.generateEmbedding(query);

            Map<String, Object> filter = new HashMap<>();
            filter.put("chapterId", chapterId.toString());

            List<QdrantVectorStoreService.SearchResult> qdrantResults =
                    vectorStoreService.searchWithFilter(collectionName, queryVector, filter, limit);

            return qdrantResults.stream()
                    .map(this::toSearchResult)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Search in chapter failed: {}", e.getMessage(), e);
            throw new RuntimeException("Search in chapter failed: " + e.getMessage(), e);
        }
    }

    /**
     * Convert Qdrant search result to KnowledgeSearchService.SearchResult
     */
    private SearchResult toSearchResult(QdrantVectorStoreService.SearchResult qdrantResult) {
        Map<String, Object> payload = qdrantResult.payload();

        return new SearchResult(
                parseUUID(payload.get("chunkId")),
                parseUUID(payload.get("lectureKnowledgeId")),
                getContent(payload),
                qdrantResult.score(),
                getChunkIndex(payload),
                getIntOrNull(payload, "startTimeSeconds"),
                getIntOrNull(payload, "endTimeSeconds"),
                getIntOrNull(payload, "pageNumber")
        );
    }

    private UUID parseUUID(Object value) {
        if (value == null) return null;
        try {
            return UUID.fromString(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private String getContent(Map<String, Object> map) {
        Object value = map.get("content");
        return value != null ? value.toString() : null;
    }

    private int getChunkIndex(Map<String, Object> map) {
        Object value = map.get("chunkIndex");
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Integer getIntOrNull(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
