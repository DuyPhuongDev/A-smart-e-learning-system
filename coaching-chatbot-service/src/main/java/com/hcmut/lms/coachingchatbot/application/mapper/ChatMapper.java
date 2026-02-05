package com.hcmut.lms.coachingchatbot.application.mapper;

import com.hcmut.lms.coachingchatbot.application.dto.response.ChatHistoryResponse;
import com.hcmut.lms.coachingchatbot.application.dto.response.ChatResponse;
import com.hcmut.lms.coachingchatbot.application.service.KnowledgeSearchService;
import com.hcmut.lms.coachingchatbot.domain.entity.chatMessage.ChatMessage;
import com.hcmut.lms.coachingchatbot.domain.entity.chatMessage.MessageRole;
import com.hcmut.lms.coachingchatbot.domain.entity.chatSession.ChatSession;
import org.springframework.stereotype.Component;

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
     * Convert knowledge search results to knowledge sources
     */
    public List<ChatResponse.KnowledgeSource> toKnowledgeSources(List<KnowledgeSearchService.SearchResult> searchResults) {
        return searchResults.stream()
                .map(this::toKnowledgeSource)
                .collect(Collectors.toList());
    }

    /**
     * Convert single search result to knowledge source
     */
    public ChatResponse.KnowledgeSource toKnowledgeSource(KnowledgeSearchService.SearchResult searchResult) {
        return ChatResponse.KnowledgeSource.builder()
                .chunkId(searchResult.chunkId())
                .content(searchResult.content())
                .relevanceScore((double) searchResult.score())
                .chunkIndex(searchResult.chunkIndex())
                .pageNumber(searchResult.pageNumber())
                .startTimeSeconds(searchResult.startTimeSeconds())
                .endTimeSeconds(searchResult.endTimeSeconds())
                .build();
    }

    /**
     * Convert ChatMessage entity to MessageDto
     */
    public ChatHistoryResponse.MessageDto toMessageDto(ChatMessage message) {
        return ChatHistoryResponse.MessageDto.builder()
                .messageId(message.getMessageId())
                .role(message.getRole().name())
                .content(message.getContent())
                .languageCode(message.getLanguageCode())
                .isError(message.getIsError())
                .createdAt(message.getCreatedAt())
                .build();
    }

    /**
     * Convert list of ChatMessage entities to ChatHistoryResponse
     */
    public ChatHistoryResponse toChatHistoryResponse(ChatSession session, List<ChatMessage> messages) {
        return ChatHistoryResponse.builder()
                .sessionId(session.getSessionId())
                .studentId(session.getStudentId())
                .lectureId(session.getLectureId())
                .messages(messages.stream()
                        .map(this::toMessageDto)
                        .collect(Collectors.toList()))
                .totalMessages(messages.size())
                .build();
    }
}
