package com.hcmut.lms.coachingchatbot.application.mapper;

import com.hcmut.lms.coachingchatbot.application.dto.response.LectureKnowledgeChunkResponse;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledgeChunk.LectureKnowledgeChunk;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LectureKnowledgeChunkMapper {

    public LectureKnowledgeChunkResponse toResponse(LectureKnowledgeChunk entity) {
        if (entity == null) {
            return null;
        }

        return LectureKnowledgeChunkResponse.builder()
                .id(entity.getId())
                .lectureKnowledgeId(entity.getLectureKnowledge().getLectureKnowledgeId())
                .chunkIndex(entity.getChunkIndex())
                .chunkContent(entity.getChunkContent())
                .qdrantPointId(entity.getQdrantPointId())
                .startTimeSeconds(entity.getStartTimeSeconds())
                .endTimeSeconds(entity.getEndTimeSeconds())
                .pageNumber(entity.getPageNumber())
                .tokenCount(entity.getTokenCount())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Create a new chunk entity for video content (with time tracking)
     */
    public LectureKnowledgeChunk toVideoChunkEntity(
            LectureKnowledge lectureKnowledge,
            Integer chunkIndex,
            String chunkContent,
            UUID qdrantPointId,
            Integer startTimeSeconds,
            Integer endTimeSeconds,
            Integer tokenCount) {
        return LectureKnowledgeChunk.builder()
                .id(UUID.randomUUID())
                .lectureKnowledge(lectureKnowledge)
                .chunkIndex(chunkIndex)
                .chunkContent(chunkContent)
                .qdrantPointId(qdrantPointId)
                .startTimeSeconds(startTimeSeconds)
                .endTimeSeconds(endTimeSeconds)
                .tokenCount(tokenCount)
                .build();
    }

    /**
     * Create a new chunk entity for document content (with page tracking)
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
                .chunkContent(chunkContent)
                .qdrantPointId(qdrantPointId)
                .pageNumber(pageNumber)
                .tokenCount(tokenCount)
                .build();
    }

    /**
     * Create a new chunk entity for text content (no location tracking)
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
                .chunkContent(chunkContent)
                .qdrantPointId(qdrantPointId)
                .tokenCount(tokenCount)
                .build();
    }
}
