package com.hcmut.lms.coachingchatbot.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request DTO for creating lecture knowledge
 * Note: lectureKnowledgeId should be the same as lectureId from
 * course-management
 */
public record CreateLectureKnowledgeRequest(
                @NotNull(message = "Lecture ID is required") UUID lectureKnowledgeId,

                String embeddingModel) {
}
