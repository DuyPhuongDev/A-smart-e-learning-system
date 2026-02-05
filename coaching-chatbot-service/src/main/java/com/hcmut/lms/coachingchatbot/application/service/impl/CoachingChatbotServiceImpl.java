package com.hcmut.lms.coachingchatbot.application.service.impl;

import com.hcmut.lms.coachingchatbot.application.dto.response.ChatHistoryResponse;
import com.hcmut.lms.coachingchatbot.application.dto.response.ChatResponse;
import com.hcmut.lms.coachingchatbot.application.mapper.ChatMapper;
import com.hcmut.lms.coachingchatbot.application.service.CoachingChatbotService;
import com.hcmut.lms.coachingchatbot.application.service.KnowledgeSearchService;
import com.hcmut.lms.coachingchatbot.client.GeminiClient;
import com.hcmut.lms.coachingchatbot.domain.entity.chatMessage.ChatMessage;
import com.hcmut.lms.coachingchatbot.domain.entity.chatSession.ChatSession;
import com.hcmut.lms.coachingchatbot.domain.repository.ChatMessageRepository;
import com.hcmut.lms.coachingchatbot.domain.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of CoachingChatbotService with RAG pipeline
 * Pipeline: Load context → Query refinement → Knowledge search → Answer generation → Save history
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoachingChatbotServiceImpl implements CoachingChatbotService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final KnowledgeSearchService knowledgeSearchService;
    private final GeminiClient geminiClient;
    private final ChatMapper chatMapper;

    @Value("${chatbot.max-context-messages:6}")
    private int maxContextMessages;

    @Value("${chatbot.max-knowledge-chunks:5}")
    private int maxKnowledgeChunks;

    @Value("${chatbot.error-fallback-message-vi:Xin lỗi, tôi không thể trả lời câu hỏi lúc này. Vui lòng thử lại sau.}")
    private String errorFallbackVi;

    @Value("${chatbot.error-fallback-message-en:Sorry, I cannot answer your question right now. Please try again later.}")
    private String errorFallbackEn;

    private static final String QUERY_REFINEMENT_PROMPT = """
            You are a search query optimizer. Extract the key search terms from the student's question.
            Focus on the main concepts, technical terms, and important keywords.
            Return ONLY the refined search query as plain text, nothing else.
            
            Student question: %s
            
            Refined search query:""";

    private static final String ANSWER_GENERATION_PROMPT = """
            You are a friendly AI coaching assistant helping students learn. Your task is to answer the student's question using the provided knowledge context.
            
            CRITICAL LANGUAGE INSTRUCTION:
            - Detect the language of the STUDENT'S QUESTION below
            - Your answer MUST be in the SAME LANGUAGE as the question
            - If the question is in Vietnamese, answer in Vietnamese
            - If the question is in English, answer in English
            
            CONVERSATION HISTORY (for context):
            %s
            
            KNOWLEDGE CONTEXT (from lecture materials):
            %s
            
            STUDENT'S QUESTION:
            %s
            
            INSTRUCTIONS:
            1. Analyze the question carefully and understand what the student is asking
            2. Use the knowledge context provided to formulate an accurate answer
            3. If the conversation history is relevant, use it to provide continuity
            4. Format your answer in clean Markdown with proper headings, lists, and code blocks if needed
            5. Use a friendly, encouraging coaching tone
            6. If the knowledge context doesn't contain enough information, acknowledge this and suggest what the student could explore
            7. Keep the answer concise but complete (aim for 200-400 words)
            8. REMEMBER: Match the language of your answer to the language of the question
            
            Your answer in Markdown:""";

    @Override
    @Transactional
    public ChatResponse askQuestion(UUID studentId, UUID lectureId, String question) {
        long startTime = System.currentTimeMillis();
        log.info("Processing question from student {} for lecture {}: '{}'", studentId, lectureId, question);

        try {
            // Step 1: Get or create chat session
            ChatSession session = getOrCreateSession(studentId, lectureId);

            // Step 2: Load recent conversation history (excluding error messages)
            List<ChatMessage> contextMessages = loadContextMessages(session);
            log.debug("Loaded {} context messages for session {}", contextMessages.size(), session.getSessionId());

            // Step 3: Refine query using LLM
            String refinedQuery = refineQuery(question);
            log.debug("Refined query: '{}'", refinedQuery);

            // Step 4: Search for relevant knowledge chunks
            List<KnowledgeSearchService.SearchResult> searchResults = knowledgeSearchService.searchInLecture(
                    lectureId, refinedQuery, maxKnowledgeChunks);
            log.info("Found {} relevant knowledge chunks", searchResults.size());

            // Step 5: Generate answer using LLM with context
            String answer = generateAnswer(question, contextMessages, searchResults);
            log.debug("Generated answer length: {} characters", answer.length());

            // Step 6: Save user question
            ChatMessage userMessage = chatMapper.toUserMessage(session, question);
            messageRepository.save(userMessage);

            // Step 7: Save assistant answer (non-error)
            ChatMessage assistantMessage = chatMapper.toAssistantMessage(session, answer, detectLanguage(question), false);
            messageRepository.save(assistantMessage);

            // Step 8: Update session timestamp
            session.setUpdatedAt(java.time.Instant.now());
            sessionRepository.save(session);

            long processingTime = System.currentTimeMillis() - startTime;
            log.info("Successfully processed question in {}ms", processingTime);

            // Step 9: Build response
            return ChatResponse.builder()
                    .sessionId(session.getSessionId())
                    .answer(answer)
                    .sources(chatMapper.toKnowledgeSources(searchResults))
                    .processingTimeMs(processingTime)
                    .languageDetected(detectLanguage(question))
                    .build();

        } catch (Exception e) {
            log.error("Failed to process question: {}", e.getMessage(), e);
            return handleError(studentId, lectureId, question, e, startTime);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ChatHistoryResponse getChatHistory(UUID studentId, UUID lectureId, Integer limit) {
        log.info("Fetching chat history for student {} and lecture {}, limit: {}", studentId, lectureId, limit);

        ChatSession session = sessionRepository.findByStudentIdAndLectureId(studentId, lectureId)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        List<ChatMessage> messages = messageRepository.findBySessionOrderByCreatedAtAsc(session);

        // Apply limit if specified
        if (limit != null && limit > 0 && messages.size() > limit) {
            messages = messages.subList(Math.max(0, messages.size() - limit), messages.size());
        }

        return chatMapper.toChatHistoryResponse(session, messages);
    }

    @Override
    @Transactional
    public void deleteSession(UUID sessionId) {
        log.info("Deleting chat session: {}", sessionId);

        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        messageRepository.deleteBySession(session);
        sessionRepository.delete(session);

        log.info("Chat session deleted successfully");
    }

    /**
     * Get existing session or create new one
     */
    private ChatSession getOrCreateSession(UUID studentId, UUID lectureId) {
        return sessionRepository.findByStudentIdAndLectureId(studentId, lectureId)
                .orElseGet(() -> {
                    log.info("Creating new chat session for student {} and lecture {}", studentId, lectureId);
                    ChatSession newSession = ChatSession.builder()
                            .sessionId(UUID.randomUUID())
                            .studentId(studentId)
                            .lectureId(lectureId)
                            .build();
                    return sessionRepository.save(newSession);
                });
    }

    /**
     * Load recent non-error messages for context (3 Q&A pairs = 6 messages max)
     */
    private List<ChatMessage> loadContextMessages(ChatSession session) {
        List<ChatMessage> messages = messageRepository.findTop6BySessionAndIsErrorFalseOrderByCreatedAtDesc(session);
        // Reverse to get chronological order (oldest first)
        Collections.reverse(messages);
        if (messages.size() > maxContextMessages) {
            messages = messages.subList(messages.size() - maxContextMessages, messages.size());
        }
        return messages;
    }

    /**
     * Refine query using LLM to extract search keywords
     */
    private String refineQuery(String question) {
        try {
            String prompt = String.format(QUERY_REFINEMENT_PROMPT, question);
            String refined = geminiClient.generateContent(prompt).trim();
            return refined.isEmpty() ? question : refined;
        } catch (Exception e) {
            log.warn("Query refinement failed, using original question: {}", e.getMessage());
            return question;
        }
    }

    /**
     * Generate answer using LLM with conversation history and knowledge context
     */
    private String generateAnswer(String question, List<ChatMessage> contextMessages,
            List<KnowledgeSearchService.SearchResult> searchResults) {

        // Build conversation history section
        String conversationHistory = buildConversationHistory(contextMessages);

        // Build knowledge context section
        String knowledgeContext = buildKnowledgeContext(searchResults);

        // Build final prompt
        String prompt = String.format(ANSWER_GENERATION_PROMPT, conversationHistory, knowledgeContext, question);

        // Generate answer
        return geminiClient.generateContent(prompt);
    }

    /**
     * Build conversation history string from messages
     */
    private String buildConversationHistory(List<ChatMessage> messages) {
        if (messages.isEmpty()) {
            return "(No previous conversation)";
        }

        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : messages) {
            sb.append(msg.getRole().name().toUpperCase()).append(": ");
            sb.append(msg.getContent()).append("\n\n");
        }
        return sb.toString().trim();
    }

    /**
     * Build knowledge context string from search results
     */
    private String buildKnowledgeContext(List<KnowledgeSearchService.SearchResult> searchResults) {
        if (searchResults.isEmpty()) {
            return "(No relevant knowledge found in lecture materials)";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < searchResults.size(); i++) {
            KnowledgeSearchService.SearchResult result = searchResults.get(i);
            sb.append(String.format("--- Knowledge Chunk %d (Relevance: %.2f) ---\n", i + 1, result.score()));
            sb.append(result.content()).append("\n\n");
        }
        return sb.toString().trim();
    }

    /**
     * Simple language detection (Vietnamese vs English)
     */
    private String detectLanguage(String text) {
        // Check for Vietnamese-specific characters
        if (text.matches(".*[àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ].*")) {
            return "vi";
        }
        return "en";
    }

    /**
     * Handle error and save error message
     */
    private ChatResponse handleError(UUID studentId, UUID lectureId, String question, Exception error, long startTime) {
        try {
            ChatSession session = getOrCreateSession(studentId, lectureId);

            // Save user question
            ChatMessage userMessage = chatMapper.toUserMessage(session, question);
            messageRepository.save(userMessage);

            // Determine error message based on language
            String language = detectLanguage(question);
            String errorMessage = "vi".equals(language) ? errorFallbackVi : errorFallbackEn;

            // Save error response with is_error=true flag
            ChatMessage errorAssistantMessage = chatMapper.toAssistantMessage(session, errorMessage, language, true);
            messageRepository.save(errorAssistantMessage);

            long processingTime = System.currentTimeMillis() - startTime;

            return ChatResponse.builder()
                    .sessionId(session.getSessionId())
                    .answer(errorMessage)
                    .sources(Collections.emptyList())
                    .processingTimeMs(processingTime)
                    .languageDetected(language)
                    .build();

        } catch (Exception e) {
            log.error("Failed to save error message: {}", e.getMessage(), e);
            throw new RuntimeException("Critical error in chatbot service", error);
        }
    }
}
