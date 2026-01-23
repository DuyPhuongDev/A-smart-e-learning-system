package com.hcmut.lms.learning.application.service;

import java.util.List;
import java.util.UUID;

/**
 * Service for semantic search across lecture knowledge
 */
public interface KnowledgeSearchService {

    /**
     * Search for similar content by text query
     *
     * @param query Search query
     * @param limit Maximum results
     * @return List of search results
     */
    List<SearchResult> search(String query, int limit);

    /**
     * Search within a specific lecture
     *
     * @param lectureId Lecture ID
     * @param query Search query
     * @param limit Maximum results
     * @return List of search results
     */
    List<SearchResult> searchInLecture(UUID lectureId, String query, int limit);

    /**
     * Search within a specific chapter
     *
     * @param chapterId Chapter ID
     * @param query Search query
     * @param limit Maximum results
     * @return List of search results
     */
    List<SearchResult> searchInChapter(UUID chapterId, String query, int limit);

    /**
     * Search result record
     * Note: lectureKnowledgeId = lectureId (1:1 relationship)
     */
    record SearchResult(
            UUID chunkId,
            UUID lectureKnowledgeId,
            String content,
            float score,
            int chunkIndex,
            // Location tracking - for video
            Integer startTimeSeconds,
            Integer endTimeSeconds,
            // Location tracking - for document
            Integer pageNumber
    ) {}
}
