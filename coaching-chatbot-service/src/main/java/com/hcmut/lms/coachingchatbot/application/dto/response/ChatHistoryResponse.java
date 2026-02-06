package com.hcmut.lms.coachingchatbot.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for chat history with pagination support.
 * Message structure is similar to ChatResponse for consistency.
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

    // Pagination info
    private Integer page;
    private Integer size;
    private Long totalMessages;
    private Integer totalPages;
    private Boolean hasNext;
    private Boolean hasPrevious;

    /**
     * Represents a single message in the conversation.
     * For assistant messages, includes knowledge sources used to generate the answer.
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

        /**
         * Knowledge sources used for this response (only for assistant messages).
         * Allows user to trace back to the original lecture content.
         */
        private List<ChatResponse.KnowledgeSource> sources;
    }
}
