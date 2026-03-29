package com.hcmut.lms.coachingchatbot.application.mapper;

import com.hcmut.lms.coachingchatbot.application.dto.request.DocumentEnrichmentCallbackRequest;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.SyncStatus;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledgeChunk.LectureKnowledgeChunk;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for converting document/text enrichment callback payloads to entities.
 * Follows the same pattern as LectureKnowledgeChunkMapper.
 */
@Component
public class DocumentEnrichmentCallbackMapper {

    private static final String DEFAULT_EMBEDDING_MODEL = "gemini-text-embedding-004";

    /**
     * Sanitize text content by removing null bytes and other problematic characters
     * that PostgreSQL UTF-8 encoding doesn't support
     */
    private String sanitizeContent(String content) {
        if (content == null) {
            return null;
        }
        // Remove null bytes (0x00) which cause PostgreSQL UTF-8 errors
        // Also remove other control characters except newlines, tabs, and carriage returns
        return content.replaceAll("\u0000", "")
                     .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");
    }

    /**
     * Create a new LectureKnowledge entity from callback request
     */
    public LectureKnowledge toLectureKnowledge(UUID lectureId, String contentType) {
        return LectureKnowledge.builder()
                .lectureKnowledgeId(lectureId)
                .syncStatus(SyncStatus.PROCESSING)
                .embeddingModel(DEFAULT_EMBEDDING_MODEL)
                .contentType(contentType)
                .totalChunks(0)
                .build();
    }

    /**
     * Map a single enriched chunk to LectureKnowledgeChunk entity
     */
    public LectureKnowledgeChunk toChunkEntity(
            LectureKnowledge lectureKnowledge,
            DocumentEnrichmentCallbackRequest.EnrichedChunk enrichedChunk) {

        // Determine page number: use pageNumber if available, otherwise use startPage
        Integer pageNumber = enrichedChunk.getPageNumber() != null
                ? enrichedChunk.getPageNumber()
                : enrichedChunk.getStartPage();

        return LectureKnowledgeChunk.builder()
                .id(UUID.randomUUID())
                .lectureKnowledge(lectureKnowledge)
                .chunkIndex(enrichedChunk.getChunkIndex())
                .chunkContent(sanitizeContent(enrichedChunk.getChunkContent()))
                .qdrantPointId(UUID.randomUUID()) // Will be updated when syncing to Qdrant
                .pageNumber(pageNumber)
                .tokenCount(enrichedChunk.getTokenCount())
                .build();
    }

    /**
     * Map all enriched chunks to LectureKnowledgeChunk entities
     */
    public List<LectureKnowledgeChunk> toChunkEntities(
            LectureKnowledge lectureKnowledge,
            List<DocumentEnrichmentCallbackRequest.EnrichedChunk> enrichedChunks) {

        return enrichedChunks.stream()
                .map(chunk -> toChunkEntity(lectureKnowledge, chunk))
                .collect(Collectors.toList());
    }

    /**
     * Create chunk entity for document content (with page tracking)
     */
    public LectureKnowledgeChunk toDocumentChunkEntity(
            LectureKnowledge lectureKnowledge,
            Integer chunkIndex,
            String chunkContent,
            UUID qdrantPointId,
            Integer pageNumber,
            Integer tokenCount) {
        return LectureKnowledgeChunk.builder()
                .id(UUID.randomUUID())
                .lectureKnowledge(lectureKnowledge)
                .chunkIndex(chunkIndex)
                .chunkContent(sanitizeContent(chunkContent))
                .qdrantPointId(qdrantPointId)
                .pageNumber(pageNumber)
                .tokenCount(tokenCount)
                .build();
    }

    /**
     * Create chunk entity for text content (no page tracking)
     */
    public LectureKnowledgeChunk toTextChunkEntity(
            LectureKnowledge lectureKnowledge,
            Integer chunkIndex,
            String chunkContent,
            UUID qdrantPointId,
            Integer tokenCount) {
        return LectureKnowledgeChunk.builder()
                .id(UUID.randomUUID())
                .lectureKnowledge(lectureKnowledge)
                .chunkIndex(chunkIndex)
                .chunkContent(sanitizeContent(chunkContent))
                .qdrantPointId(qdrantPointId)
                .tokenCount(tokenCount)
                .build();
    }
}
