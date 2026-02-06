package com.hcmut.lms.coachingchatbot.application.service;

import com.hcmut.lms.coachingchatbot.application.dto.response.ChatHistoryResponse;
import com.hcmut.lms.coachingchatbot.application.dto.response.ChatResponse;

import java.util.UUID;

/**
 * Service for coaching chatbot RAG pipeline
 */
public interface CoachingChatbotService {

    /**
     * Ask a question to the coaching chatbot
     * @param studentId the student ID
     * @param lectureId the lecture ID
     * @param question the student's question
     * @return ChatResponse with answer and sources
     */
    ChatResponse askQuestion(UUID studentId, UUID lectureId, String question);

    /**
     * Get chat history for a student and lecture (legacy, without pagination)
     * @param studentId the student ID
     * @param lectureId the lecture ID
     * @param limit maximum number of messages to return
     * @return ChatHistoryResponse with message history
     */
    ChatHistoryResponse getChatHistory(UUID studentId, UUID lectureId, Integer limit);

    /**
     * Get paginated chat history for a student and lecture (for lazy loading)
     * @param studentId the student ID
     * @param lectureId the lecture ID
     * @param page page number (0-based)
     * @param size page size
     * @return ChatHistoryResponse with paginated message history
     */
    ChatHistoryResponse getPaginatedChatHistory(UUID studentId, UUID lectureId, int page, int size);

    /**
     * Delete a chat session and all its messages
     * @param sessionId the session ID to delete
     */
    void deleteSession(UUID sessionId);
}
