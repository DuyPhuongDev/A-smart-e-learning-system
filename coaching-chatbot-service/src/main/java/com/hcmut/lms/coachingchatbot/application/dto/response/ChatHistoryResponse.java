package com.hcmut.lms.coachingchatbot.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for chat history
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryResponse {

    private UUID sessionId;
    private UUID studentId;
    private UUID lectureId;
    private List<MessageDto> messages;
    private Integer totalMessages;

    /**
     * Represents a single message in the conversation
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDto {
        private UUID messageId;
        private String role; // "user" or "assistant"
        private String content;
        private String languageCode;
        private Boolean isError;
        private Instant createdAt;
    }
}
