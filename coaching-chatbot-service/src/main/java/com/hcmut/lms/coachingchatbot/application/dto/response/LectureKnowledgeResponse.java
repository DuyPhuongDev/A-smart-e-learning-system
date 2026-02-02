package com.hcmut.lms.coachingchatbot.application.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for lecture knowledge
 * Maps to LectureKnowledge entity in learning service
 */
public record LectureKnowledgeResponse(
                UUID lectureKnowledgeId,
                String syncStatus,
                Instant lastSyncedAt,
                String errorMessage,
                String embeddingModel,
                String contentType,
                Integer totalChunks,
                Instant createdAt,
                Instant updatedAt) {
}
