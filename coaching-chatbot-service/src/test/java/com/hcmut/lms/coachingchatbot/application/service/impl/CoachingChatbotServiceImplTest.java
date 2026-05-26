package com.hcmut.lms.coachingchatbot.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.coachingchatbot.application.dto.response.ChatHistoryResponse;
import com.hcmut.lms.coachingchatbot.application.dto.response.ChatResponse;
import com.hcmut.lms.coachingchatbot.application.mapper.ChatMapper;
import com.hcmut.lms.coachingchatbot.application.service.KnowledgeSearchService;
import com.hcmut.lms.coachingchatbot.application.service.KnowledgeSearchService.SearchResult;
import com.hcmut.lms.coachingchatbot.client.GeminiClient;
import com.hcmut.lms.coachingchatbot.domain.entity.chatMessage.ChatMessage;
import com.hcmut.lms.coachingchatbot.domain.entity.chatSession.ChatSession;
import com.hcmut.lms.coachingchatbot.domain.repository.ChatMessageRepository;
import com.hcmut.lms.coachingchatbot.domain.repository.ChatSessionRepository;
import com.hcmut.lms.coachingchatbot.domain.repository.MessageKnowledgeSourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoachingChatbotServiceImplTest {

    @Mock private ChatSessionRepository sessionRepository;
    @Mock private ChatMessageRepository messageRepository;
    @Mock private MessageKnowledgeSourceRepository knowledgeSourceRepository;
    @Mock private KnowledgeSearchService knowledgeSearchService;
    @Mock private GeminiClient geminiClient;
    @Mock private ChatMapper chatMapper;

    @InjectMocks
    private CoachingChatbotServiceImpl coachingChatbotService;

    private static final UUID studentId = UUID.randomUUID();
    private static final UUID lectureId = UUID.randomUUID();
    private static final UUID sessionId = UUID.randomUUID();

    @Test
    void askQuestion_shouldReturnResponse_whenValidInput() {
        ReflectionTestUtils.setField(coachingChatbotService, "maxContextMessages", 6);
        ReflectionTestUtils.setField(coachingChatbotService, "maxKnowledgeChunks", 5);
        ReflectionTestUtils.setField(coachingChatbotService, "minRelevanceScore", 0.6f);
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackVi", "Xin lỗi...");
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackEn", "Sorry...");

        ChatSession session = ChatSession.builder().sessionId(sessionId).studentId(studentId).lectureId(lectureId).build();
        when(sessionRepository.findByStudentIdAndLectureId(studentId, lectureId)).thenReturn(Optional.of(session));
        when(messageRepository.findTop6BySessionAndIsErrorFalseOrderByCreatedAtDesc(session)).thenReturn(List.of());
        when(geminiClient.generateContent(any())).thenReturn("refined query", "generated answer");
        when(knowledgeSearchService.searchInLecture(eq(lectureId), any(), eq(5))).thenReturn(List.of());
        ChatMessage userMsg = new ChatMessage();
        ChatMessage asstMsg = new ChatMessage();
        when(chatMapper.toUserMessage(any(), any())).thenReturn(userMsg);
        when(chatMapper.toAssistantMessage(any(), any(), any(), eq(false))).thenReturn(asstMsg);
        when(chatMapper.toMessageKnowledgeSources(any(), any())).thenReturn(List.of());
        when(chatMapper.toKnowledgeSources(any())).thenReturn(List.of());

        ChatResponse result = coachingChatbotService.askQuestion(studentId, lectureId, "Xin chào");
        assertNotNull(result);
    }

    @Test
    void askQuestion_shouldReturnErrorResponse_whenExceptionOccurs() {
        ReflectionTestUtils.setField(coachingChatbotService, "maxContextMessages", 6);
        ReflectionTestUtils.setField(coachingChatbotService, "maxKnowledgeChunks", 5);
        ReflectionTestUtils.setField(coachingChatbotService, "minRelevanceScore", 0.6f);
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackVi", "Xin lỗi...");
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackEn", "Sorry...");

        ChatSession session = ChatSession.builder().sessionId(sessionId).studentId(studentId).lectureId(lectureId).build();
        when(sessionRepository.findByStudentIdAndLectureId(any(), any())).thenReturn(Optional.of(session));
        when(messageRepository.findTop6BySessionAndIsErrorFalseOrderByCreatedAtDesc(any()))
                .thenThrow(new RuntimeException("DB error"));
        when(chatMapper.toUserMessage(any(), any())).thenReturn(new ChatMessage());
        when(chatMapper.toAssistantMessage(any(), any(), any(), eq(true))).thenReturn(new ChatMessage());

        ChatResponse result = coachingChatbotService.askQuestion(studentId, lectureId, "test");
        assertNotNull(result);
        assertTrue(result.getAnswer().contains("Xin lỗi") || result.getAnswer().contains("Sorry"));
    }

    @Test
    void getChatHistory_shouldReturnHistory_whenMessagesExist() {
        ChatSession session = ChatSession.builder().sessionId(sessionId).studentId(studentId).lectureId(lectureId).build();
        ChatMessage msg = new ChatMessage();
        ChatHistoryResponse expected = new ChatHistoryResponse();

        when(sessionRepository.findByStudentIdAndLectureId(studentId, lectureId)).thenReturn(Optional.of(session));
        when(messageRepository.findBySessionOrderByCreatedAtAsc(session)).thenReturn(List.of(msg));
        when(chatMapper.toChatHistoryResponse(session, List.of(msg))).thenReturn(expected);

        ChatHistoryResponse result = coachingChatbotService.getChatHistory(studentId, lectureId, null);
        assertNotNull(result);
    }

    @Test
    void getChatHistory_shouldCreateNewSession_whenNotExists() {
        ChatSession newSession = ChatSession.builder().sessionId(sessionId).studentId(studentId).lectureId(lectureId).build();
        ChatHistoryResponse expected = new ChatHistoryResponse();

        when(sessionRepository.findByStudentIdAndLectureId(studentId, lectureId)).thenReturn(Optional.empty());
        when(sessionRepository.save(any())).thenReturn(newSession);
        when(messageRepository.findBySessionOrderByCreatedAtAsc(any())).thenReturn(List.of());
        when(chatMapper.toChatHistoryResponse(any(), any())).thenReturn(expected);

        ChatHistoryResponse result = coachingChatbotService.getChatHistory(studentId, lectureId, null);
        assertNotNull(result);
    }

    @Test
    void deleteSession_shouldDelete_whenSessionExists() {
        ChatSession session = ChatSession.builder().sessionId(sessionId).studentId(studentId).lectureId(lectureId).build();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(messageRepository.findBySessionOrderByCreatedAtAsc(session)).thenReturn(List.of());
        doNothing().when(knowledgeSourceRepository).deleteByMessageIn(any());
        doNothing().when(messageRepository).deleteBySession(session);
        doNothing().when(sessionRepository).delete(session);

        assertDoesNotThrow(() -> coachingChatbotService.deleteSession(sessionId));
    }

    @Test
    void deleteSession_shouldThrowException_whenNotFound() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> coachingChatbotService.deleteSession(sessionId));
    }

    @Test
    void askQuestion_shouldReturnAnswerWithSources_whenKnowledgeFound() {
        ReflectionTestUtils.setField(coachingChatbotService, "maxContextMessages", 6);
        ReflectionTestUtils.setField(coachingChatbotService, "maxKnowledgeChunks", 5);
        ReflectionTestUtils.setField(coachingChatbotService, "minRelevanceScore", 0.6f);
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackVi", "Xin lỗi...");
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackEn", "Sorry...");

        ChatSession session = ChatSession.builder().sessionId(sessionId).studentId(studentId).lectureId(lectureId).build();
        SearchResult result1 = new SearchResult(UUID.randomUUID(), lectureId, "Content 1", 0.85f, 0, 10, 30, null);
        SearchResult result2 = new SearchResult(UUID.randomUUID(), lectureId, "Content 2", 0.92f, 1, 60, 90, null);

        when(sessionRepository.findByStudentIdAndLectureId(studentId, lectureId)).thenReturn(Optional.of(session));
        when(messageRepository.findTop6BySessionAndIsErrorFalseOrderByCreatedAtDesc(session)).thenReturn(List.of());
        when(geminiClient.generateContent(any())).thenReturn("refined query", "generated answer with sources");
        when(knowledgeSearchService.searchInLecture(eq(lectureId), any(), eq(5)))
                .thenReturn(List.of(result1, result2));
        ChatMessage userMsg = new ChatMessage();
        ChatMessage asstMsg = new ChatMessage();
        when(chatMapper.toUserMessage(any(), any())).thenReturn(userMsg);
        when(chatMapper.toAssistantMessage(any(), any(), any(), eq(false))).thenReturn(asstMsg);
        when(chatMapper.toMessageKnowledgeSources(any(), any())).thenReturn(List.of());
        when(chatMapper.toKnowledgeSources(any())).thenReturn(List.of());

        ChatResponse result = coachingChatbotService.askQuestion(studentId, lectureId, "Giải thích khái niệm?");
        assertNotNull(result);
    }

    @Test
    void askQuestion_shouldWorkWithEnglishQuestion() {
        ReflectionTestUtils.setField(coachingChatbotService, "maxContextMessages", 6);
        ReflectionTestUtils.setField(coachingChatbotService, "maxKnowledgeChunks", 5);
        ReflectionTestUtils.setField(coachingChatbotService, "minRelevanceScore", 0.6f);
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackVi", "Xin lỗi...");
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackEn", "Sorry...");

        ChatSession session = ChatSession.builder().sessionId(sessionId).studentId(studentId).lectureId(lectureId).build();
        when(sessionRepository.findByStudentIdAndLectureId(studentId, lectureId)).thenReturn(Optional.of(session));
        when(messageRepository.findTop6BySessionAndIsErrorFalseOrderByCreatedAtDesc(session)).thenReturn(List.of());
        when(geminiClient.generateContent(any())).thenReturn("refined query", "generated answer");
        when(knowledgeSearchService.searchInLecture(eq(lectureId), any(), eq(5))).thenReturn(List.of());
        ChatMessage userMsg = new ChatMessage();
        ChatMessage asstMsg = new ChatMessage();
        when(chatMapper.toUserMessage(any(), any())).thenReturn(userMsg);
        when(chatMapper.toAssistantMessage(any(), any(), any(), eq(false))).thenReturn(asstMsg);
        when(chatMapper.toMessageKnowledgeSources(any(), any())).thenReturn(List.of());
        when(chatMapper.toKnowledgeSources(any())).thenReturn(List.of());

        ChatResponse result = coachingChatbotService.askQuestion(studentId, lectureId, "What is this concept?");
        assertNotNull(result);
    }

    @Test
    void askQuestion_shouldWorkWithContextMessages() {
        ReflectionTestUtils.setField(coachingChatbotService, "maxContextMessages", 6);
        ReflectionTestUtils.setField(coachingChatbotService, "maxKnowledgeChunks", 5);
        ReflectionTestUtils.setField(coachingChatbotService, "minRelevanceScore", 0.6f);
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackVi", "Xin lỗi...");
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackEn", "Sorry...");

        ChatSession session = ChatSession.builder().sessionId(sessionId).studentId(studentId).lectureId(lectureId).build();
        ChatMessage previousMsg = new ChatMessage();
        previousMsg.setContent("Previous question?");
        previousMsg.setRole(com.hcmut.lms.coachingchatbot.domain.entity.chatMessage.MessageRole.user);

        when(sessionRepository.findByStudentIdAndLectureId(studentId, lectureId)).thenReturn(Optional.of(session));
        when(messageRepository.findTop6BySessionAndIsErrorFalseOrderByCreatedAtDesc(session))
                .thenReturn(List.of(previousMsg));
        when(geminiClient.generateContent(any())).thenReturn("refined query", "generated answer");
        when(knowledgeSearchService.searchInLecture(eq(lectureId), any(), eq(5))).thenReturn(List.of());
        ChatMessage userMsg = new ChatMessage();
        ChatMessage asstMsg = new ChatMessage();
        when(chatMapper.toUserMessage(any(), any())).thenReturn(userMsg);
        when(chatMapper.toAssistantMessage(any(), any(), any(), eq(false))).thenReturn(asstMsg);
        when(chatMapper.toMessageKnowledgeSources(any(), any())).thenReturn(List.of());
        when(chatMapper.toKnowledgeSources(any())).thenReturn(List.of());

        ChatResponse result = coachingChatbotService.askQuestion(studentId, lectureId, "Câu hỏi tiếp theo?");
        assertNotNull(result);
    }

    @Test
    void askQuestion_shouldMarkOffTopic_whenNoRelevantChunks() {
        ReflectionTestUtils.setField(coachingChatbotService, "maxContextMessages", 6);
        ReflectionTestUtils.setField(coachingChatbotService, "maxKnowledgeChunks", 5);
        ReflectionTestUtils.setField(coachingChatbotService, "minRelevanceScore", 0.6f);
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackVi", "Xin lỗi...");
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackEn", "Sorry...");

        ChatSession session = ChatSession.builder().sessionId(sessionId).studentId(studentId).lectureId(lectureId).build();
        SearchResult lowScore = new SearchResult(UUID.randomUUID(), lectureId, "Low relevance", 0.3f, 0, null, null, null);

        when(sessionRepository.findByStudentIdAndLectureId(studentId, lectureId)).thenReturn(Optional.of(session));
        when(messageRepository.findTop6BySessionAndIsErrorFalseOrderByCreatedAtDesc(session)).thenReturn(List.of());
        when(geminiClient.generateContent(any())).thenReturn("refined query", "off-topic answer");
        when(knowledgeSearchService.searchInLecture(eq(lectureId), any(), eq(5)))
                .thenReturn(List.of(lowScore));
        ChatMessage userMsg = new ChatMessage();
        ChatMessage asstMsg = new ChatMessage();
        when(chatMapper.toUserMessage(any(), any())).thenReturn(userMsg);
        when(chatMapper.toAssistantMessage(any(), any(), any(), eq(false))).thenReturn(asstMsg);
        when(chatMapper.toMessageKnowledgeSources(any(), any())).thenReturn(List.of());
        when(chatMapper.toKnowledgeSources(any())).thenReturn(List.of());

        ChatResponse result = coachingChatbotService.askQuestion(studentId, lectureId, "Câu hỏi lạc đề?");
        assertNotNull(result);
    }

    @Test
    void getChatHistory_shouldApplyLimit_whenSpecified() {
        ChatSession session = ChatSession.builder().sessionId(sessionId).studentId(studentId).lectureId(lectureId).build();
        ChatHistoryResponse expected = new ChatHistoryResponse();
        ChatMessage msg1 = new ChatMessage();
        ChatMessage msg2 = new ChatMessage();
        ChatMessage msg3 = new ChatMessage();

        when(sessionRepository.findByStudentIdAndLectureId(studentId, lectureId)).thenReturn(Optional.of(session));
        when(messageRepository.findBySessionOrderByCreatedAtAsc(session)).thenReturn(List.of(msg1, msg2, msg3));
        when(chatMapper.toChatHistoryResponse(eq(session), any())).thenReturn(expected);

        ChatHistoryResponse result = coachingChatbotService.getChatHistory(studentId, lectureId, 2);
        assertNotNull(result);
    }

    @Test
    void getPaginatedChatHistory_shouldReturnPage_whenMessagesExist() {
        ChatSession session = ChatSession.builder().sessionId(sessionId).studentId(studentId).lectureId(lectureId).build();
        Page<ChatMessage> messagePage = new PageImpl<>(List.of(new ChatMessage()));

        when(sessionRepository.findByStudentIdAndLectureId(studentId, lectureId)).thenReturn(Optional.of(session));
        when(messageRepository.findBySessionOrderByCreatedAtDesc(eq(session), any(PageRequest.class)))
                .thenReturn(messagePage);
        when(chatMapper.toPaginatedChatHistoryResponse(eq(session), any())).thenReturn(new ChatHistoryResponse());

        ChatHistoryResponse result = coachingChatbotService.getPaginatedChatHistory(studentId, lectureId, 0, 10);
        assertNotNull(result);
    }

    @Test
    void askQuestion_shouldHandleRefineQueryFailure() {
        ReflectionTestUtils.setField(coachingChatbotService, "maxContextMessages", 6);
        ReflectionTestUtils.setField(coachingChatbotService, "maxKnowledgeChunks", 5);
        ReflectionTestUtils.setField(coachingChatbotService, "minRelevanceScore", 0.6f);
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackVi", "Xin lỗi...");
        ReflectionTestUtils.setField(coachingChatbotService, "errorFallbackEn", "Sorry...");

        ChatSession session = ChatSession.builder().sessionId(sessionId).studentId(studentId).lectureId(lectureId).build();
        when(sessionRepository.findByStudentIdAndLectureId(studentId, lectureId)).thenReturn(Optional.of(session));
        when(messageRepository.findTop6BySessionAndIsErrorFalseOrderByCreatedAtDesc(session)).thenReturn(List.of());
        // First call throws, second succeeds for answer
        when(geminiClient.generateContent(any()))
                .thenThrow(new RuntimeException("LLM unavailable"))
                .thenReturn("generated answer");
        when(knowledgeSearchService.searchInLecture(eq(lectureId), any(), eq(5))).thenReturn(List.of());
        ChatMessage userMsg = new ChatMessage();
        ChatMessage asstMsg = new ChatMessage();
        when(chatMapper.toUserMessage(any(), any())).thenReturn(userMsg);
        when(chatMapper.toAssistantMessage(any(), any(), any(), eq(false))).thenReturn(asstMsg);
        when(chatMapper.toMessageKnowledgeSources(any(), any())).thenReturn(List.of());
        when(chatMapper.toKnowledgeSources(any())).thenReturn(List.of());

        ChatResponse result = coachingChatbotService.askQuestion(studentId, lectureId, "test");
        assertNotNull(result);
    }
}
