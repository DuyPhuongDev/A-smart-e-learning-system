package com.hcmut.lms.coachingchatbot.application.mapper;

import com.hcmut.lms.coachingchatbot.application.dto.response.ChatHistoryResponse;
import com.hcmut.lms.coachingchatbot.application.dto.response.ChatResponse;
import com.hcmut.lms.coachingchatbot.application.service.KnowledgeSearchService;
import com.hcmut.lms.coachingchatbot.domain.entity.chatMessage.ChatMessage;
import com.hcmut.lms.coachingchatbot.domain.entity.chatMessage.MessageRole;
import com.hcmut.lms.coachingchatbot.domain.entity.chatSession.ChatSession;
import com.hcmut.lms.coachingchatbot.domain.entity.messageKnowledgeSource.MessageKnowledgeSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for converting chat entities to DTOs and vice versa
 */
@Component
public class ChatMapper {

    /**
     * Create ChatMessage entity from user question
     */
    public ChatMessage toUserMessage(ChatSession session, String question) {
        return ChatMessage.builder()
                .messageId(UUID.randomUUID())
                .session(session)
                .role(MessageRole.user)
                .content(question)
                .isError(false)
                .build();
    }

    /**
     * Create ChatMessage entity from assistant answer
     */
    public ChatMessage toAssistantMessage(ChatSession session, String answer, String languageCode, boolean isError) {
        return ChatMessage.builder()
                .messageId(UUID.randomUUID())
                .session(session)
                .role(MessageRole.assistant)
                .content(answer)
                .languageCode(languageCode)
                .isError(isError)
                .build();
    }

    /**
     * Convert knowledge search results to knowledge sources DTO
     */
    public List<ChatResponse.KnowledgeSource> toKnowledgeSources(List<KnowledgeSearchService.SearchResult> searchResults) {
        return searchResults.stream()
                .map(this::toKnowledgeSource)
                .collect(Collectors.toList());
    }

    /**
     * Convert single search result to knowledge source DTO
     */
    public ChatResponse.KnowledgeSource toKnowledgeSource(KnowledgeSearchService.SearchResult searchResult) {
        return ChatResponse.KnowledgeSource.builder()
                .chunkId(searchResult.chunkId())
                .chunkIndex(searchResult.chunkIndex())
                .pageNumber(searchResult.pageNumber())
                .startTimeSeconds(searchResult.startTimeSeconds())
                .endTimeSeconds(searchResult.endTimeSeconds())
                .formattedLocation(formatLocation(searchResult.startTimeSeconds(),
                        searchResult.endTimeSeconds(), searchResult.pageNumber()))
                .build();
    }

    /**
     * Convert MessageKnowledgeSource entity to KnowledgeSource DTO
     */
    public ChatResponse.KnowledgeSource toKnowledgeSourceFromEntity(MessageKnowledgeSource entity) {
        return ChatResponse.KnowledgeSource.builder()
                .chunkId(entity.getChunkId())
                .chunkIndex(entity.getChunkIndex())
                .pageNumber(entity.getPageNumber())
                .startTimeSeconds(entity.getStartTimeSeconds())
                .endTimeSeconds(entity.getEndTimeSeconds())
                .formattedLocation(formatLocation(entity.getStartTimeSeconds(),
                        entity.getEndTimeSeconds(), entity.getPageNumber()))
                .build();
    }

    /**
     * Format location string based on available metadata (video timestamp or page number)
     * @param startTimeSeconds start time for video
     * @param endTimeSeconds end time for video
     * @param pageNumber page number for document
     * @return formatted location string like "Video: 02:30-03:00" or "Trang: 5"
     */
    private String formatLocation(Integer startTimeSeconds, Integer endTimeSeconds, Integer pageNumber) {
        // Video timestamp takes priority
        if (startTimeSeconds != null && endTimeSeconds != null) {
            String startFormatted = formatTimestamp(startTimeSeconds, endTimeSeconds);
            String endFormatted = formatTimestamp(endTimeSeconds, endTimeSeconds);
            return String.format("Video: %s-%s", startFormatted, endFormatted);
        }

        // Document page number
        if (pageNumber != null) {
            return String.format("Trang: %d", pageNumber);
        }

        return null;
    }

    /**
     * Format seconds to mm:ss or hh:mm:ss based on duration
     * @param seconds the time in seconds to format
     * @param maxSeconds the maximum time to determine format (>= 3600 uses hh:mm:ss)
     * @return formatted time string
     */
    private String formatTimestamp(Integer seconds, Integer maxSeconds) {
        if (seconds == null) return "00:00";

        int hrs = seconds / 3600;
        int mins = (seconds % 3600) / 60;
        int secs = seconds % 60;

        // Use hh:mm:ss format if video is >= 1 hour
        if (maxSeconds != null && maxSeconds >= 3600) {
            return String.format("%02d:%02d:%02d", hrs, mins, secs);
        }

        // Use mm:ss format for shorter videos
        return String.format("%02d:%02d", (seconds / 60), secs);
    }

    /**
     * Convert knowledge search result to MessageKnowledgeSource entity
     */
    public MessageKnowledgeSource toMessageKnowledgeSource(ChatMessage message, KnowledgeSearchService.SearchResult searchResult) {
        return MessageKnowledgeSource.builder()
                .id(UUID.randomUUID())
                .message(message)
                .chunkId(searchResult.chunkId())
                .chunkIndex(searchResult.chunkIndex())
                .pageNumber(searchResult.pageNumber())
                .startTimeSeconds(searchResult.startTimeSeconds())
                .endTimeSeconds(searchResult.endTimeSeconds())
                .build();
    }

    /**
     * Convert list of search results to MessageKnowledgeSource entities
     */
    public List<MessageKnowledgeSource> toMessageKnowledgeSources(ChatMessage message, List<KnowledgeSearchService.SearchResult> searchResults) {
        return searchResults.stream()
                .map(sr -> toMessageKnowledgeSource(message, sr))
                .collect(Collectors.toList());
    }

    /**
     * Convert ChatMessage entity to MessageDto, including sources for assistant messages
     */
    public ChatHistoryResponse.MessageDto toMessageDto(ChatMessage message) {
        List<ChatResponse.KnowledgeSource> sources = null;

        // Only include sources for assistant messages (not user messages)
        if (message.getRole() == MessageRole.assistant && message.getKnowledgeSources() != null) {
            sources = message.getKnowledgeSources().stream()
                    .map(this::toKnowledgeSourceFromEntity)
                    .collect(Collectors.toList());
        }

        return ChatHistoryResponse.MessageDto.builder()
                .messageId(message.getMessageId())
                .role(message.getRole().name())
                .content(message.getContent())
                .languageCode(message.getLanguageCode())
                .isError(message.getIsError())
                .createdAt(message.getCreatedAt())
                .sources(sources)
                .build();
    }

    /**
     * Convert list of ChatMessage entities to ChatHistoryResponse (legacy, without pagination)
     */
    public ChatHistoryResponse toChatHistoryResponse(ChatSession session, List<ChatMessage> messages) {
        return ChatHistoryResponse.builder()
                .sessionId(session.getSessionId())
                .studentId(session.getStudentId())
                .lectureId(session.getLectureId())
                .messages(messages.stream()
                        .map(this::toMessageDto)
                        .collect(Collectors.toList()))
                .totalMessages((long) messages.size())
                .page(0)
                .size(messages.size())
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }

    /**
     * Convert Page of ChatMessage to paginated ChatHistoryResponse
     */
    public ChatHistoryResponse toPaginatedChatHistoryResponse(ChatSession session, Page<ChatMessage> messagePage) {
        // Reverse to show oldest first within the page
        List<ChatHistoryResponse.MessageDto> messages = messagePage.getContent().stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());
        Collections.reverse(messages);

        return ChatHistoryResponse.builder()
                .sessionId(session.getSessionId())
                .studentId(session.getStudentId())
                .lectureId(session.getLectureId())
                .messages(messages)
                .page(messagePage.getNumber())
                .size(messagePage.getSize())
                .totalMessages(messagePage.getTotalElements())
                .totalPages(messagePage.getTotalPages())
                .hasNext(messagePage.hasNext())
                .hasPrevious(messagePage.hasPrevious())
                .build();
    }
}
