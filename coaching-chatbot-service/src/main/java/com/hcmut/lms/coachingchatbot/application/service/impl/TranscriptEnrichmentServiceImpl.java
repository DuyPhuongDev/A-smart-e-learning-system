package com.hcmut.lms.coachingchatbot.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.coachingchatbot.application.service.TranscriptEnrichmentService;
import com.hcmut.lms.coachingchatbot.client.CourseManagementClient;
import com.hcmut.lms.coachingchatbot.client.GeminiClient;
import com.hcmut.lms.coachingchatbot.client.dto.VideoTranscriptResponse;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.SyncStatus;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledgeChunk.LectureKnowledgeChunk;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeChunkRepository;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of TranscriptEnrichmentService
 * Handles the complete transcript enrichment pipeline:
 * 1. Fetch transcript segments from course-management-service
 * 2. Group segments into ~90 second windows
 * 3. Use 5-segment sliding context to enrich with LLM (summary + questions)
 * 4. Save enriched chunks to lecture_knowledge_chunks table
 *
 * Self-contained configuration - reads directly from application.yml
 */
@Service
@Slf4j
public class TranscriptEnrichmentServiceImpl implements TranscriptEnrichmentService {

    private final CourseManagementClient courseManagementClient;
    private final LectureKnowledgeRepository lectureKnowledgeRepository;
    private final LectureKnowledgeChunkRepository lectureKnowledgeChunkRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    // Transcript Enrichment Configuration - read directly from application.yml
    @Value("${transcript-enrichment.llm-delay-ms:5000}")
    private long llmDelayMs;

    @Value("${transcript-enrichment.max-retries:3}")
    private int maxRetries;

    @Value("${transcript-enrichment.segment-duration-seconds:90}")
    private int segmentDurationSeconds;

    @Value("${transcript-enrichment.context-window-size:5}")
    private int contextWindowSize;

    @Value("${transcript-enrichment.max-summary-words:100}")
    private int maxSummaryWords;

    @Value("${transcript-enrichment.questions-per-chunk:4}")
    private int questionsPerChunk;

    @Getter
    private static final String embeddingModel = "gemini-text-embedding-004";

    public TranscriptEnrichmentServiceImpl(
            CourseManagementClient courseManagementClient,
            LectureKnowledgeRepository lectureKnowledgeRepository,
            LectureKnowledgeChunkRepository lectureKnowledgeChunkRepository,
            GeminiClient geminiClient,
            ObjectMapper objectMapper) {
        this.courseManagementClient = courseManagementClient;
        this.lectureKnowledgeRepository = lectureKnowledgeRepository;
        this.lectureKnowledgeChunkRepository = lectureKnowledgeChunkRepository;
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    private static final String ENRICHMENT_PROMPT = """
            You are an educational content expert. Your task is to enrich a video lecture transcript segment.

            CRITICAL LANGUAGE REQUIREMENT:
            - Detect the language of the TARGET SEGMENT below
            - Your "summary" and "questions" MUST be written in the SAME LANGUAGE as the TARGET SEGMENT
            - If the transcript is in Vietnamese, write summary and questions in Vietnamese
            - If the transcript is in English, write summary and questions in English

            CONTEXT (surrounding segments for reference):
            %s

            TARGET SEGMENT TO ENRICH (this is the main segment you need to process):
            %s

            Please provide the following in JSON format:
            1. "original_text": The exact original transcript text (copy as-is from TARGET SEGMENT)
            2. "summary": A concise summary of what this segment teaches (maximum %d words) - MUST BE IN THE SAME LANGUAGE AS THE TRANSCRIPT
            3. "questions": An array of exactly %d educational questions about this content - MUST BE IN THE SAME LANGUAGE AS THE TRANSCRIPT

            IMPORTANT:
            - Output ONLY valid JSON, no markdown code blocks or explanations
            - The response must be parseable JSON
            - Questions should be meaningful and test understanding of the content
            - Summary should capture the key learning points
            - ALWAYS match the output language to the input transcript language

            JSON Response format:
            {"original_text": "...", "summary": "...", "questions": ["Q1?", "Q2?", "Q3?", "Q4?"]}
            """;

    @Override
    @Transactional
    public EnrichmentResult enrichTranscriptsForLecture(UUID lectureId) {
        log.info("Starting transcript enrichment for lecture: {}", lectureId);
        long startTime = System.currentTimeMillis();

        try {
            // Step 1: Fetch transcript segments from course-management-service
            log.info("[Step 1/5] Fetching transcript segments for lecture: {}", lectureId);
            List<VideoTranscriptResponse> transcripts = courseManagementClient.getTranscriptSegments(lectureId);

            if (transcripts == null || transcripts.isEmpty()) {
                throw new IllegalStateException("No transcript segments found for lecture: " + lectureId);
            }
            log.info("[Step 1/5] Fetched {} transcript segments", transcripts.size());

            // Step 2: Auto-create or get LectureKnowledge
            log.info("[Step 2/5] Creating/updating LectureKnowledge record...");
            LectureKnowledge lectureKnowledge = getOrCreateLectureKnowledge(lectureId);
            lectureKnowledge.setSyncStatus(SyncStatus.PROCESSING);
            lectureKnowledge.setContentType("VIDEO");
            lectureKnowledgeRepository.save(lectureKnowledge);

            // Step 3: Group transcripts into ~90 second windows
            log.info("[Step 3/5] Grouping transcripts into {} second windows...", segmentDurationSeconds);
            List<GroupedSegment> groupedSegments = groupTranscriptsByDuration(transcripts);
            log.info("[Step 3/5] Created {} grouped segments", groupedSegments.size());

            // Step 4: Enrich each grouped segment using LLM with sliding context window
            log.info("[Step 4/5] Enriching segments using LLM (delay: {}ms, retries: {})...",
                    llmDelayMs, maxRetries);
            List<EnrichedChunk> enrichedChunks = enrichSegmentsWithContext(groupedSegments);

            int enrichedCount = (int) enrichedChunks.stream().filter(EnrichedChunk::isEnriched).count();
            int fallbackCount = enrichedChunks.size() - enrichedCount;
            log.info("[Step 4/5] Enrichment complete: {} enriched, {} fallback", enrichedCount, fallbackCount);

            // Step 5: Save enriched chunks to database
            log.info("[Step 5/5] Saving {} chunks to database...", enrichedChunks.size());
            deleteExistingChunks(lectureKnowledge.getLectureKnowledgeId());
            saveEnrichedChunks(lectureKnowledge, enrichedChunks);

            // Update LectureKnowledge status
            lectureKnowledge.setSyncStatus(SyncStatus.COMPLETED);
            lectureKnowledge.setTotalChunks(enrichedChunks.size());
            lectureKnowledge.setLastSyncedAt(Instant.now());
            lectureKnowledge.setEmbeddingModel(embeddingModel);
            lectureKnowledgeRepository.save(lectureKnowledge);

            long processingTime = System.currentTimeMillis() - startTime;
            log.info("Transcript enrichment completed for lecture {} in {}ms", lectureId, processingTime);

            return EnrichmentResult.success(
                    lectureId,
                    lectureKnowledge.getLectureKnowledgeId(),
                    enrichedChunks.size(),
                    enrichedCount,
                    fallbackCount,
                    processingTime);

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("Transcript enrichment failed for lecture {}: {}", lectureId, e.getMessage(), e);

            // Update status to FAILED if LectureKnowledge exists
            try {
                LectureKnowledge lectureKnowledge = lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId);
                if (lectureKnowledge != null) {
                    lectureKnowledge.setSyncStatus(SyncStatus.FAILED);
                    lectureKnowledge.setErrorMessage(e.getMessage());
                    lectureKnowledgeRepository.save(lectureKnowledge);
                }
            } catch (Exception ex) {
                log.error("Failed to update LectureKnowledge status: {}", ex.getMessage());
            }

            return EnrichmentResult.failure(lectureId, e.getMessage(), processingTime);
        }
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<EnrichmentResult> enrichTranscriptsForLectureAsync(UUID lectureId) {
        log.info("Starting async transcript enrichment for lecture: {}", lectureId);
        try {
            // Note: Cannot use this.enrichTranscriptsForLecture() due to @Transactional
            // proxy limitation
            // The enrichTranscriptsForLecture method already handles its own transaction
            EnrichmentResult result = doEnrichTranscripts(lectureId);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Async transcript enrichment failed: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Internal method for enrichment logic - called by both sync and async methods
     */
    private EnrichmentResult doEnrichTranscripts(UUID lectureId) {
        return enrichTranscriptsForLecture(lectureId);
    }

    /**
     * Get existing LectureKnowledge or create new one with PROCESSING status
     */
    private LectureKnowledge getOrCreateLectureKnowledge(UUID lectureId) {
        LectureKnowledge existing = lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId);

        if (existing != null) {
            log.info("Found existing LectureKnowledge for lecture: {}", lectureId);
            return existing;
        }

        log.info("Creating new LectureKnowledge for lecture: {}", lectureId);
        LectureKnowledge newLectureKnowledge = LectureKnowledge.builder()
                .lectureKnowledgeId(lectureId)
                .syncStatus(SyncStatus.PROCESSING)
                .embeddingModel(embeddingModel)
                .totalChunks(0)
                .build();

        return lectureKnowledgeRepository.save(newLectureKnowledge);
    }

    /**
     * Group transcript segments into approximately 90-second windows
     */
    private List<GroupedSegment> groupTranscriptsByDuration(List<VideoTranscriptResponse> transcripts) {
        List<GroupedSegment> groupedSegments = new ArrayList<>();

        if (transcripts.isEmpty()) {
            return groupedSegments;
        }

        // Sort by segment index to ensure correct order
        transcripts.sort(Comparator.comparingInt(t -> t.getSegmentIndex() != null ? t.getSegmentIndex() : 0));

        StringBuilder currentText = new StringBuilder();
        int currentStartTime = transcripts.get(0).getStartTimeSeconds() != null
                ? transcripts.get(0).getStartTimeSeconds()
                : 0;
        int currentEndTime = currentStartTime;
        int segmentIndex = 0;
        int wordCount = 0;

        for (VideoTranscriptResponse transcript : transcripts) {
            int startTime = transcript.getStartTimeSeconds() != null ? transcript.getStartTimeSeconds()
                    : currentEndTime;
            int endTime = transcript.getEndTimeSeconds() != null ? transcript.getEndTimeSeconds() : startTime;
            String text = transcript.getTranscriptText() != null ? transcript.getTranscriptText() : "";
            int transcriptWords = text.isEmpty() ? 0 : text.split("\\s+").length;

            // Check if adding this transcript would exceed the duration limit
            int durationSoFar = currentEndTime - currentStartTime;

            if (durationSoFar >= segmentDurationSeconds && !currentText.isEmpty()) {
                // Save current group and start new one
                groupedSegments.add(new GroupedSegment(
                        segmentIndex++,
                        currentText.toString().trim(),
                        currentStartTime,
                        currentEndTime,
                        wordCount));

                // Start new group
                currentText = new StringBuilder();
                currentStartTime = startTime;
                wordCount = 0;
            }

            // Add transcript to current group
            if (!currentText.isEmpty()) {
                currentText.append(" ");
            }
            currentText.append(text);
            currentEndTime = endTime;
            wordCount += transcriptWords;
        }

        // Don't forget the last group
        if (!currentText.isEmpty()) {
            groupedSegments.add(new GroupedSegment(
                    segmentIndex,
                    currentText.toString().trim(),
                    currentStartTime,
                    currentEndTime,
                    wordCount));
        }

        return groupedSegments;
    }

    /**
     * Enrich segments using 5-segment sliding context window
     * For first/last segments, adjust context window accordingly
     */
    private List<EnrichedChunk> enrichSegmentsWithContext(List<GroupedSegment> segments) {
        List<EnrichedChunk> enrichedChunks = new ArrayList<>();

        for (int i = 0; i < segments.size(); i++) {
            GroupedSegment currentSegment = segments.get(i);

            // Build context window
            String contextText = buildContextText(segments, i, contextWindowSize);

            // Enrich with LLM (with retry logic)
            EnrichedChunk enrichedChunk = enrichSegmentWithRetry(currentSegment, contextText, i);
            enrichedChunks.add(enrichedChunk);

            // Add delay between LLM calls to avoid rate limiting
            if (i < segments.size() - 1) {
                try {
                    Thread.sleep(llmDelayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Interrupted during LLM delay");
                }
            }
        }

        return enrichedChunks;
    }

    /**
     * Build context text from surrounding segments
     */
    private String buildContextText(List<GroupedSegment> segments, int currentIndex, int contextSize) {
        StringBuilder context = new StringBuilder();
        int halfContext = contextSize / 2;

        // Calculate start and end indices for context window
        int start = Math.max(0, currentIndex - halfContext);
        int end = Math.min(segments.size() - 1, currentIndex + halfContext);

        // Adjust if we're at the beginning or end
        if (currentIndex < halfContext) {
            end = Math.min(segments.size() - 1, contextSize - 1);
        } else if (currentIndex >= segments.size() - halfContext) {
            start = Math.max(0, segments.size() - contextSize);
        }

        for (int i = start; i <= end; i++) {
            if (i == currentIndex)
                continue; // Skip current segment (it's passed separately)

            GroupedSegment segment = segments.get(i);
            context.append(String.format("[Segment %d (%d-%ds)]: %s\n\n",
                    i + 1,
                    segment.startTimeSeconds(),
                    segment.endTimeSeconds(),
                    segment.combinedText()));
        }

        return context.toString().trim();
    }

    /**
     * Enrich a single segment with retry logic
     * Falls back to original text if all retries fail
     */
    private EnrichedChunk enrichSegmentWithRetry(GroupedSegment segment, String contextText, int index) {
        log.debug("Enriching segment {} ({}-{}s)...", index, segment.startTimeSeconds(), segment.endTimeSeconds());

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                String prompt = String.format(ENRICHMENT_PROMPT,
                        contextText.isEmpty() ? "No additional context available." : contextText,
                        segment.combinedText(),
                        maxSummaryWords,
                        questionsPerChunk);

                String response = geminiClient.chat(prompt);

                // Parse LLM response
                EnrichedChunk enrichedChunk = parseEnrichmentResponse(response, segment, index);

                if (enrichedChunk != null && enrichedChunk.isEnriched()) {
                    log.debug("Segment {} enriched successfully on attempt {}", index, attempt);
                    return enrichedChunk;
                }

            } catch (Exception e) {
                log.warn("Enrichment attempt {} failed for segment {}: {}", attempt, index, e.getMessage());

                if (attempt < maxRetries) {
                    // Wait before retry
                    try {
                        Thread.sleep(llmDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        // All retries failed - return fallback with original text
        log.warn("All {} retries failed for segment {}, using fallback (original text)",
                maxRetries, index);
        return createFallbackChunk(segment, index);
    }

    /**
     * Parse LLM response JSON into EnrichedChunk
     */
    private EnrichedChunk parseEnrichmentResponse(String response, GroupedSegment segment, int index) {
        try {
            // Clean response - remove markdown code blocks if present
            String cleanedResponse = response.trim();
            if (cleanedResponse.startsWith("```json")) {
                cleanedResponse = cleanedResponse.substring(7);
            }
            if (cleanedResponse.startsWith("```")) {
                cleanedResponse = cleanedResponse.substring(3);
            }
            if (cleanedResponse.endsWith("```")) {
                cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
            }
            cleanedResponse = cleanedResponse.trim();

            JsonNode jsonNode = objectMapper.readTree(cleanedResponse);

            String originalText = jsonNode.has("original_text") ? jsonNode.get("original_text").asText()
                    : segment.combinedText();
            String summary = jsonNode.has("summary") ? jsonNode.get("summary").asText() : "";

            List<String> questions = new ArrayList<>();
            if (jsonNode.has("questions") && jsonNode.get("questions").isArray()) {
                for (JsonNode q : jsonNode.get("questions")) {
                    questions.add(q.asText());
                }
            }

            // Build enriched content
            String enrichedContent = buildEnrichedContent(originalText, summary, questions);
            int tokenCount = estimateTokenCount(enrichedContent);

            return new EnrichedChunk(
                    index,
                    originalText,
                    summary,
                    questions,
                    enrichedContent,
                    segment.startTimeSeconds(),
                    segment.endTimeSeconds(),
                    tokenCount,
                    true);

        } catch (JsonProcessingException e) {
            log.warn("Failed to parse LLM response as JSON: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Build the final enriched content string
     * Format: original_text + summary + questions (~450 words)
     */
    private String buildEnrichedContent(String originalText, String summary, List<String> questions) {
        StringBuilder content = new StringBuilder();

        content.append("=== ORIGINAL TRANSCRIPT ===\n");
        content.append(originalText);
        content.append("\n\n");

        content.append("=== SUMMARY ===\n");
        content.append(summary);
        content.append("\n\n");

        content.append("=== REVIEW QUESTIONS ===\n");
        for (int i = 0; i < questions.size(); i++) {
            content.append(String.format("%d. %s\n", i + 1, questions.get(i)));
        }

        return content.toString();
    }

    /**
     * Create fallback chunk with original text only (no enrichment)
     */
    private EnrichedChunk createFallbackChunk(GroupedSegment segment, int index) {
        String enrichedContent = buildEnrichedContent(
                segment.combinedText(),
                "Summary not available.",
                List.of("Question not available."));

        return new EnrichedChunk(
                index,
                segment.combinedText(),
                "Summary not available.",
                List.of("Question not available."),
                enrichedContent,
                segment.startTimeSeconds(),
                segment.endTimeSeconds(),
                estimateTokenCount(enrichedContent),
                false);
    }

    /**
     * Delete existing chunks for a lecture knowledge
     */
    private void deleteExistingChunks(UUID lectureKnowledgeId) {
        lectureKnowledgeChunkRepository.deleteByLectureKnowledgeLectureKnowledgeId(lectureKnowledgeId);
        log.info("Deleted existing chunks for lectureKnowledge: {}", lectureKnowledgeId);
    }

    /**
     * Save enriched chunks to database
     */
    private void saveEnrichedChunks(LectureKnowledge lectureKnowledge, List<EnrichedChunk> enrichedChunks) {
        List<LectureKnowledgeChunk> chunks = new ArrayList<>();

        for (EnrichedChunk enriched : enrichedChunks) {
            LectureKnowledgeChunk chunk = LectureKnowledgeChunk.builder()
                    .id(UUID.randomUUID())
                    .lectureKnowledge(lectureKnowledge)
                    .chunkIndex(enriched.index())
                    .chunkContent(enriched.enrichedContent())
                    .qdrantPointId(UUID.randomUUID()) // Will be updated when syncing to Qdrant
                    .startTimeSeconds(enriched.startTimeSeconds())
                    .endTimeSeconds(enriched.endTimeSeconds())
                    .tokenCount(enriched.tokenCount())
                    .build();

            chunks.add(chunk);
        }

        lectureKnowledgeChunkRepository.saveAll(chunks);
        log.info("Saved {} enriched chunks to database", chunks.size());
    }

    /**
     * Estimate token count (approximate: 4 chars per token)
     */
    private int estimateTokenCount(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return (int) Math.ceil(text.length() / 4.0);
    }
}
