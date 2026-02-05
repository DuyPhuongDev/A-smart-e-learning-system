package com.hcmut.lms.coachingchatbot.controller;

import com.hcmut.lms.coachingchatbot.application.dto.request.ChatRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.ChatHistoryResponse;
import com.hcmut.lms.coachingchatbot.application.dto.response.ChatResponse;
import com.hcmut.lms.coachingchatbot.application.service.CoachingChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for coaching chatbot endpoints
 * Provides RAG-based Q&A and chat history management
 */
@RestController
@RequestMapping("${prefix-api}/chat")
@RequiredArgsConstructor
@Slf4j
public class CoachingChatbotController {

    private final CoachingChatbotService coachingChatbotService;

    /**
     * Ask a question to the coaching chatbot
     * POST /api/coaching-chatbot/v1/chat/ask
     */
    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> askQuestion(@Valid @RequestBody ChatRequest request) {
        log.info("Received chat request from student {} for lecture {}",
                request.getStudentId(), request.getLectureId());

        ChatResponse response = coachingChatbotService.askQuestion(
                request.getStudentId(),
                request.getLectureId(),
                request.getQuestion());

        return ResponseEntity.ok(response);
    }

    /**
     * Get chat history for a student and lecture
     * GET /api/coaching-chatbot/v1/chat/history?studentId={studentId}&lectureId={lectureId}&limit={limit}
     */
    @GetMapping("/history")
    public ResponseEntity<ChatHistoryResponse> getChatHistory(
            @RequestParam UUID studentId,
            @RequestParam UUID lectureId,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {

        log.info("Fetching chat history for student {} and lecture {}, limit: {}",
                studentId, lectureId, limit);

        ChatHistoryResponse response = coachingChatbotService.getChatHistory(studentId, lectureId, limit);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a chat session
     * DELETE /api/coaching-chatbot/v1/chat/session/{sessionId}
     */
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable UUID sessionId) {
        log.info("Deleting chat session: {}", sessionId);

        coachingChatbotService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Coaching Chatbot Service is running");
    }
}
