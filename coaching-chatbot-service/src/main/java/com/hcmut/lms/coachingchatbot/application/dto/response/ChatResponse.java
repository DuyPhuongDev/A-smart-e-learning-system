package com.hcmut.lms.coachingchatbot.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for chat answer from coaching chatbot
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private UUID sessionId;
    private String answer;
    private List<KnowledgeSource> sources;
    private Long processingTimeMs;
    private String languageDetected;

    /**
     * Represents a knowledge source used to answer the question.
     * Only stores chunkId and position info (no content for lightweight storage).
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgeSource {
        private UUID chunkId;
        private Integer chunkIndex;
        private Integer pageNumber;
        private Integer startTimeSeconds;
        private Integer endTimeSeconds;
    }
}
