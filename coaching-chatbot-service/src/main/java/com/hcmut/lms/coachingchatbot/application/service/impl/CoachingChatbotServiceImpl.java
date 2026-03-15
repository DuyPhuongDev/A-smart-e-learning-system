package com.hcmut.lms.coachingchatbot.application.service.impl;

import com.hcmut.lms.coachingchatbot.application.dto.response.ChatHistoryResponse;
import com.hcmut.lms.coachingchatbot.application.dto.response.ChatResponse;
import com.hcmut.lms.coachingchatbot.application.mapper.ChatMapper;
import com.hcmut.lms.coachingchatbot.application.service.CoachingChatbotService;
import com.hcmut.lms.coachingchatbot.application.service.KnowledgeSearchService;
import com.hcmut.lms.coachingchatbot.client.GeminiClient;
import com.hcmut.lms.coachingchatbot.domain.entity.chatMessage.ChatMessage;
import com.hcmut.lms.coachingchatbot.domain.entity.chatSession.ChatSession;
import com.hcmut.lms.coachingchatbot.domain.entity.messageKnowledgeSource.MessageKnowledgeSource;
import com.hcmut.lms.coachingchatbot.domain.repository.ChatMessageRepository;
import com.hcmut.lms.coachingchatbot.domain.repository.ChatSessionRepository;
import com.hcmut.lms.coachingchatbot.domain.repository.MessageKnowledgeSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final MessageKnowledgeSourceRepository knowledgeSourceRepository;
    private final KnowledgeSearchService knowledgeSearchService;
    private final GeminiClient geminiClient;
    private final ChatMapper chatMapper;

    @Value("${chatbot.max-context-messages:6}")
    private int maxContextMessages;

    @Value("${chatbot.max-knowledge-chunks:5}")
    private int maxKnowledgeChunks;

    @Value("${chatbot.min-relevance-score:0.6}")
    private float minRelevanceScore;

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
            - You MUST respond in ONLY Vietnamese or English
            - Detect the language of the STUDENT'S QUESTION below
            - If the question is in Vietnamese → answer in Vietnamese
            - If the question is in English → answer in English
            - If the question is in ANY OTHER LANGUAGE (e.g., Chinese, Japanese, French, etc.) → answer in Vietnamese (default)
            
            CONVERSATION HISTORY (for context):
            %s
            
            KNOWLEDGE CONTEXT (from lecture materials):
            %s
            
            IS OFF-TOPIC FLAG: %s
            
            STUDENT'S QUESTION:
            %s
            
            QUESTION CLASSIFICATION & RESPONSE RULES:
            
            1. **OFF-TOPIC QUESTION** (IS OFF-TOPIC FLAG = true OR question unrelated to lecture content):
               - Politely decline to answer
               - Redirect the student back to the lecture topic
               - Vietnamese: "Xin lỗi, câu hỏi này nằm ngoài phạm vi nội dung bài giảng. Tôi chỉ có thể hỗ trợ bạn về [tóm tắt chủ đề bài giảng]. Bạn có câu hỏi nào khác về nội dung bài học không?"
               - English: "Sorry, this question is outside the scope of the lecture content. I can only assist you with [lecture topic summary]. Do you have any other questions about the lesson content?"
            
            2. **AMBIGUOUS QUESTION** (unclear intent, multiple possible meanings):
               - Do NOT answer directly
               - Provide 2-3 clarification options for the student to choose
               - Vietnamese: "Câu hỏi của bạn có thể hiểu theo nhiều cách. Bạn muốn hỏi về:\\n1. [Option 1]\\n2. [Option 2]\\n3. [Option 3]\\nVui lòng chọn hoặc làm rõ thêm!"
               - English: "Your question can be interpreted in multiple ways. Are you asking about:\\n1. [Option 1]\\n2. [Option 2]\\n3. [Option 3]\\nPlease select or clarify!"
            
            3. **FOCUSED QUESTION** (clear intent, answerable from knowledge context):
               - Answer comprehensively using the knowledge context
               - **MUST include inline citations** with the source location from each knowledge chunk
               - Citation format for VIDEO: [📍 Video: MM:SS-MM:SS] or [📍 Video: HH:MM:SS-HH:MM:SS]
               - Citation format for DOCUMENT: [📄 Trang: X] (Vietnamese) or [📄 Page: X] (English)
               - Place citations at the end of relevant sentences or paragraphs
               - Example: "Machine Learning là một nhánh của AI cho phép máy tính học từ dữ liệu [📍 Video: 02:30-03:15]."
            
            ADDITIONAL INSTRUCTIONS:
            1. Format your answer in clean Markdown with proper headings, lists, and code blocks if needed
            2. Use a friendly, encouraging coaching tone
            3. Keep the answer concise but complete (aim for 200-400 words for focused questions)
            4. If multiple knowledge chunks support the same point, you can combine citations: [📍 Video: 02:30-03:15, 05:00-05:30]
            5. REMEMBER: Only respond in Vietnamese or English. Default to Vietnamese if unsure.
            
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
            log.info("Found {} knowledge chunks from search", searchResults.size());

            // Step 4.1: Filter chunks with relevance score >= threshold
            List<KnowledgeSearchService.SearchResult> relevantResults = searchResults.stream()
                    .filter(result -> result.score() >= minRelevanceScore)
                    .toList();

            // Determine if question is off-topic (no relevant chunks found)
            boolean isOffTopic = relevantResults.isEmpty();
            if (isOffTopic) {
                log.warn("No chunks with score >= {} found. Marking as off-topic. Original scores: {}",
                        minRelevanceScore,
                        searchResults.stream().map(r -> String.format("%.3f", r.score())).toList());
            } else {
                log.info("Filtered to {} relevant chunks with score >= {}", relevantResults.size(), minRelevanceScore);
            }

            // Step 4.1: Deduplicate search results by page/timestamp to avoid redundant sources
            searchResults = deduplicateSearchResults(searchResults);
            log.debug("After deduplication: {} unique knowledge chunks", searchResults.size());

            // Step 5: Generate answer using LLM with context
            String answer = generateAnswer(question, contextMessages, relevantResults, isOffTopic);
            log.debug("Generated answer length: {} characters", answer.length());

            // Step 6: Save user question
            ChatMessage userMessage = chatMapper.toUserMessage(session, question);
            messageRepository.save(userMessage);

            // Step 7: Save assistant answer with knowledge sources
            ChatMessage assistantMessage = chatMapper.toAssistantMessage(session, answer, detectLanguage(question), false);

            // Step 7.1: Create knowledge sources for this assistant message (only relevant chunks)
            List<MessageKnowledgeSource> knowledgeSources = chatMapper.toMessageKnowledgeSources(assistantMessage, relevantResults);

            // Step 7.2: Set bidirectional relationship
            assistantMessage.getKnowledgeSources().addAll(knowledgeSources);

            // Step 7.3: Save message (will cascade save knowledge sources)
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
                    .sources(chatMapper.toKnowledgeSources(relevantResults))
                    .processingTimeMs(processingTime)
                    .languageDetected(detectLanguage(question))
                    .build();

        } catch (Exception e) {
            log.error("Failed to process question: {}", e.getMessage(), e);
            return handleError(studentId, lectureId, question, e, startTime);
        }
    }

    @Override
    @Transactional
    public ChatHistoryResponse getChatHistory(UUID studentId, UUID lectureId, Integer limit) {
        log.info("Fetching chat history for student {} and lecture {}, limit: {}", studentId, lectureId, limit);

        // Get existing session or create new one if not found
        ChatSession session = getOrCreateSession(studentId, lectureId);

        List<ChatMessage> messages = messageRepository.findBySessionOrderByCreatedAtAsc(session);

        // Apply limit if specified
        if (limit != null && limit > 0 && messages.size() > limit) {
            messages = messages.subList(Math.max(0, messages.size() - limit), messages.size());
        }

        return chatMapper.toChatHistoryResponse(session, messages);
    }

    @Override
    @Transactional
    public ChatHistoryResponse getPaginatedChatHistory(UUID studentId, UUID lectureId, int page, int size) {
        log.info("Fetching paginated chat history for student {} and lecture {}, page: {}, size: {}",
                studentId, lectureId, page, size);

        // Get existing session or create new one if not found
        ChatSession session = getOrCreateSession(studentId, lectureId);

        // Query with pagination (newest first for lazy loading older messages)
        Page<ChatMessage> messagePage = messageRepository.findBySessionOrderByCreatedAtDesc(
                session, PageRequest.of(page, size));

        return chatMapper.toPaginatedChatHistoryResponse(session, messagePage);
    }

    @Override
    @Transactional
    public void deleteSession(UUID sessionId) {
        log.info("Deleting chat session: {}", sessionId);

        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        // Delete knowledge sources for all messages in the session
        List<ChatMessage> messages = messageRepository.findBySessionOrderByCreatedAtAsc(session);
        knowledgeSourceRepository.deleteByMessageIn(messages);

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
            List<KnowledgeSearchService.SearchResult> searchResults, boolean isOffTopic) {

        // Build conversation history section
        String conversationHistory = buildConversationHistory(contextMessages);

        // Build knowledge context section
        String knowledgeContext = buildKnowledgeContext(searchResults);

        // Build final prompt with isOffTopic flag
        String prompt = String.format(ANSWER_GENERATION_PROMPT,
                conversationHistory,
                knowledgeContext,
                isOffTopic ? "true" : "false",
                question);

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
     * Build knowledge context string from search results with formatted location metadata
     */
    private String buildKnowledgeContext(List<KnowledgeSearchService.SearchResult> searchResults) {
        if (searchResults.isEmpty()) {
            return "(No relevant knowledge found in lecture materials)";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < searchResults.size(); i++) {
            KnowledgeSearchService.SearchResult result = searchResults.get(i);
            sb.append(String.format("--- Knowledge Chunk %d (Relevance: %.2f) ---\n", i + 1, result.score()));

            // Add location metadata for citation
            String locationTag = buildLocationTag(result);
            if (locationTag != null) {
                sb.append(String.format("📍 Source Location: %s\n", locationTag));
            }

            sb.append(result.content()).append("\n\n");
        }
        return sb.toString().trim();
    }

    /**
     * Deduplicate search results to avoid redundant sources
     * Removes duplicates based on page number or timestamp ranges
     */
    private List<KnowledgeSearchService.SearchResult> deduplicateSearchResults(
            List<KnowledgeSearchService.SearchResult> searchResults) {

        if (searchResults == null || searchResults.size() <= 1) {
            return searchResults;
        }

        List<KnowledgeSearchService.SearchResult> deduplicated = new java.util.ArrayList<>();
        java.util.Set<String> seenKeys = new java.util.HashSet<>();

        for (KnowledgeSearchService.SearchResult result : searchResults) {
            String key = buildDeduplicationKey(result);

            if (!seenKeys.contains(key)) {
                seenKeys.add(key);
                deduplicated.add(result);
            } else {
                log.debug("Skipping duplicate source: {}", key);
            }
        }

        return deduplicated;
    }

    /**
     * Build deduplication key based on page number or timestamp
     */
    private String buildDeduplicationKey(KnowledgeSearchService.SearchResult result) {
        // For document content: use page number
        if (result.pageNumber() != null) {
            return "page:" + result.pageNumber();
        }

        // For video/audio content: use timestamp range
        if (result.startTimeSeconds() != null && result.endTimeSeconds() != null) {
            return "time:" + result.startTimeSeconds() + "-" + result.endTimeSeconds();
        }

        // Fallback: use chunk ID (should be unique anyway)
        return "chunk:" + result.chunkId();
    }

    /**
     * Build location tag for a knowledge chunk (video timestamp or page number)
     */
    private String buildLocationTag(KnowledgeSearchService.SearchResult result) {
        // Video timestamp
        if (result.startTimeSeconds() != null && result.endTimeSeconds() != null) {
            String startFormatted = formatTimestamp(result.startTimeSeconds(), result.endTimeSeconds());
            String endFormatted = formatTimestamp(result.endTimeSeconds(), result.endTimeSeconds());
            return String.format("Video: %s-%s", startFormatted, endFormatted);
        }

        // Document page number
        if (result.pageNumber() != null) {
            return String.format("Trang: %d", result.pageNumber());
        }

        return null;
    }

    /**
     * Format seconds to mm:ss or hh:mm:ss based on video duration
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
     * Detect language of text - only supports Vietnamese and English
     * Returns "vi" for Vietnamese, "en" for English
     * Defaults to "vi" (Vietnamese) if language cannot be determined
     */
    private String detectLanguage(String text) {
        if (text == null || text.isBlank()) {
            return "vi"; // Default to Vietnamese
        }

        // Check for Vietnamese-specific characters (diacritics)
        if (text.matches(".*[àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ].*")) {
            return "vi";
        }

        // Check if text is primarily ASCII/Latin characters (likely English)
        // Count ASCII letters vs non-ASCII characters
        long asciiCount = text.chars().filter(c -> (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')).count();
        long totalLetters = text.chars().filter(Character::isLetter).count();

        if (totalLetters > 0 && (double) asciiCount / totalLetters >= 0.9) {
            return "en"; // Predominantly English/ASCII
        }

        // Default to Vietnamese for any other language (Chinese, Japanese, Korean, etc.)
        return "vi";
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
