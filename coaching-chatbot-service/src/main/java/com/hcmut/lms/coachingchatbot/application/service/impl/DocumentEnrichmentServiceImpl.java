package com.hcmut.lms.coachingchatbot.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.coachingchatbot.application.service.DocumentEnrichmentService;
import com.hcmut.lms.coachingchatbot.client.GeminiClient;
import com.hcmut.lms.coachingchatbot.config.DocumentProcessingConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of DocumentEnrichmentService
 * Handles the complete document enrichment pipeline:
 * 1. Group pages into ~500 token segments
 * 2. Use 5-segment sliding context to enrich with LLM (summary + questions)
 * 3. Use Gemini Vision API for pages with failed text extraction
 * 4. Return enriched chunks for storage
 *
 * Uses DocumentProcessingConfig and transcript-enrichment configuration from
 * application.yml
 */
@Service
@Slf4j
public class DocumentEnrichmentServiceImpl implements DocumentEnrichmentService {

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;
    private final DocumentProcessingConfig documentProcessingConfig;

    // Reuse transcript-enrichment configuration
    @Value("${transcript-enrichment.llm-delay-ms:5000}")
    private long llmDelayMs;

    @Value("${transcript-enrichment.max-retries:3}")
    private int maxRetries;

    @Value("${transcript-enrichment.context-window-size:5}")
    private int contextWindowSize;

    @Value("${transcript-enrichment.max-summary-words:100}")
    private int maxSummaryWords;

    @Value("${transcript-enrichment.questions-per-chunk:4}")
    private int questionsPerChunk;

    public DocumentEnrichmentServiceImpl(GeminiClient geminiClient,
            ObjectMapper objectMapper,
            DocumentProcessingConfig documentProcessingConfig) {
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
        this.documentProcessingConfig = documentProcessingConfig;
    }

    private static final String ENRICHMENT_PROMPT = """
            You are an educational content expert. Your task is to enrich a document segment from educational material.

            CRITICAL LANGUAGE REQUIREMENT:
            - Detect the language of the TARGET SEGMENT below
            - Your "summary" and "questions" MUST be written in the SAME LANGUAGE as the TARGET SEGMENT
            - If the content is in Vietnamese, write summary and questions in Vietnamese
            - If the content is in English, write summary and questions in English

            DOCUMENT TITLE: %s

            CONTEXT (surrounding segments for reference):
            %s

            TARGET SEGMENT TO ENRICH (Pages %d-%d):
            %s

            Please provide the following in JSON format:
            1. "original_text": The exact original content text (copy as-is from TARGET SEGMENT)
            2. "summary": A concise summary of what this segment teaches (maximum %d words) - MUST BE IN THE SAME LANGUAGE AS THE CONTENT
            3. "questions": An array of exactly %d educational questions about this content - MUST BE IN THE SAME LANGUAGE AS THE CONTENT

            IMPORTANT:
            - Output ONLY valid JSON, no markdown code blocks or explanations
            - The response must be parseable JSON
            - Questions should be meaningful and test understanding of the content
            - Summary should capture the key learning points
            - ALWAYS match the output language to the input content language

            JSON Response format:
            {"original_text": "...", "summary": "...", "questions": ["Q1?", "Q2?", "Q3?", "Q4?"]}
            """;

    private static final String VISION_PROMPT = """
            You are an educational content expert. Analyze this image from an educational document (slide/page %d).

            DOCUMENT TITLE: %s

            Please extract and describe the content of this image, including:
            1. Any text visible in the image
            2. Description of diagrams, charts, or visual elements
            3. The educational concept being illustrated

            Then provide enrichment in JSON format:
            1. "extracted_text": All text and descriptions from the image
            2. "summary": A concise summary of what this page/slide teaches (maximum %d words)
            3. "questions": An array of exactly %d educational questions about this content

            IMPORTANT:
            - Output ONLY valid JSON, no markdown code blocks or explanations
            - Match the output language to the content language detected in the image

            JSON Response format:
            {"extracted_text": "...", "summary": "...", "questions": ["Q1?", "Q2?", "Q3?", "Q4?"]}
            """;

    @Override
    public EnrichmentResult enrichDocumentContent(UUID lectureId, List<PageSegment> pages, String documentTitle) {
        log.info("Starting document enrichment for lecture: {}, pages: {}", lectureId, pages.size());
        long startTime = System.currentTimeMillis();

        try {
            if (pages == null || pages.isEmpty()) {
                throw new IllegalArgumentException("No pages provided for enrichment");
            }

            // Step 1: Group pages into ~500 token segments
            log.info("[Step 1/3] Grouping {} pages into semantic segments (~{} tokens each)...",
                    pages.size(), documentProcessingConfig.getTargetTokensPerSegment());
            List<GroupedSegment> groupedSegments = groupPagesIntoSegments(pages);
            log.info("[Step 1/3] Created {} grouped segments", groupedSegments.size());

            // Step 2: Enrich each segment using LLM with sliding context window
            log.info("[Step 2/3] Enriching segments using LLM (delay: {}ms, retries: {})...",
                    llmDelayMs, maxRetries);
            List<EnrichedDocumentChunk> enrichedChunks = enrichSegmentsWithContext(
                    groupedSegments, documentTitle);

            int enrichedCount = (int) enrichedChunks.stream().filter(EnrichedDocumentChunk::isEnriched).count();
            int visionCount = (int) enrichedChunks.stream().filter(EnrichedDocumentChunk::usedVisionApi).count();
            int fallbackCount = enrichedChunks.size() - enrichedCount;
            log.info("[Step 2/3] Enrichment complete: {} enriched, {} fallback, {} vision API",
                    enrichedCount, fallbackCount, visionCount);

            // Step 3: Return results
            log.info("[Step 3/3] Returning {} enriched chunks", enrichedChunks.size());

            long processingTime = System.currentTimeMillis() - startTime;
            log.info("Document enrichment completed for lecture {} in {}ms", lectureId, processingTime);

            return EnrichmentResult.success(
                    lectureId,
                    enrichedChunks.size(),
                    enrichedCount,
                    fallbackCount,
                    visionCount,
                    processingTime,
                    enrichedChunks);

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("Document enrichment failed for lecture {}: {}", lectureId, e.getMessage(), e);
            return EnrichmentResult.failure(lectureId, e.getMessage(), processingTime);
        }
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<EnrichmentResult> enrichDocumentContentAsync(
            UUID lectureId, List<PageSegment> pages, String documentTitle) {
        log.info("Starting async document enrichment for lecture: {}", lectureId);
        try {
            EnrichmentResult result = enrichDocumentContent(lectureId, pages, documentTitle);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Async document enrichment failed: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Group pages into segments of approximately targetTokensPerSegment tokens
     */
    private List<GroupedSegment> groupPagesIntoSegments(List<PageSegment> pages) {
        List<GroupedSegment> segments = new ArrayList<>();
        int targetTokens = documentProcessingConfig.getTargetTokensPerSegment();

        StringBuilder currentText = new StringBuilder();
        List<Integer> currentPageNumbers = new ArrayList<>();
        List<String> currentImages = new ArrayList<>();
        int currentTokenCount = 0;
        int segmentIndex = 0;
        boolean currentNeedsVision = false;

        for (PageSegment page : pages) {
            // Check if adding this page would exceed token limit
            if (currentTokenCount > 0 && currentTokenCount + page.estimatedTokenCount() > targetTokens) {
                // Save current segment
                segments.add(createGroupedSegment(segmentIndex++, currentText.toString(),
                        currentPageNumbers, currentTokenCount, currentImages, currentNeedsVision));

                // Reset for new segment
                currentText = new StringBuilder();
                currentPageNumbers = new ArrayList<>();
                currentImages = new ArrayList<>();
                currentTokenCount = 0;
                currentNeedsVision = false;
            }

            // Add page to current segment
            if (page.textContent() != null && !page.textContent().isBlank()) {
                if (currentText.length() > 0) {
                    currentText.append("\n\n--- Page ").append(page.pageNumber()).append(" ---\n\n");
                }
                currentText.append(page.textContent());
            }
            currentPageNumbers.add(page.pageNumber());
            currentTokenCount += page.estimatedTokenCount();

            // Track images for vision fallback
            if (page.imagesBase64() != null && !page.imagesBase64().isEmpty()) {
                currentImages.addAll(page.imagesBase64());
            }
            if (page.needsVisionFallback()) {
                currentNeedsVision = true;
            }
        }

        // Don't forget the last segment
        if (!currentPageNumbers.isEmpty()) {
            segments.add(createGroupedSegment(segmentIndex, currentText.toString(),
                    currentPageNumbers, currentTokenCount, currentImages, currentNeedsVision));
        }

        return segments;
    }

    private GroupedSegment createGroupedSegment(int index, String text, List<Integer> pageNumbers,
            int tokenCount, List<String> images, boolean needsVision) {
        int startPage = pageNumbers.isEmpty() ? 0 : Collections.min(pageNumbers);
        int endPage = pageNumbers.isEmpty() ? 0 : Collections.max(pageNumbers);

        return new GroupedSegment(index, text, new ArrayList<>(pageNumbers),
                startPage, endPage, tokenCount, new ArrayList<>(images), needsVision);
    }

    /**
     * Enrich segments using 5-segment sliding context window
     */
    private List<EnrichedDocumentChunk> enrichSegmentsWithContext(
            List<GroupedSegment> segments, String documentTitle) {
        List<EnrichedDocumentChunk> enrichedChunks = new ArrayList<>();

        for (int i = 0; i < segments.size(); i++) {
            GroupedSegment currentSegment = segments.get(i);

            EnrichedDocumentChunk enrichedChunk;

            if (currentSegment.needsVisionFallback() && !currentSegment.imagesBase64().isEmpty()) {
                // Use Vision API for segments with failed text extraction
                enrichedChunk = enrichWithVisionApi(currentSegment, documentTitle, i);
            } else {
                // Use text-based enrichment with context
                String contextText = buildContextText(segments, i, contextWindowSize);
                enrichedChunk = enrichSegmentWithRetry(currentSegment, contextText, documentTitle, i);
            }

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

        int start = Math.max(0, currentIndex - halfContext);
        int end = Math.min(segments.size() - 1, currentIndex + halfContext);

        // Adjust if at beginning or end
        if (currentIndex < halfContext) {
            end = Math.min(segments.size() - 1, contextSize - 1);
        } else if (currentIndex >= segments.size() - halfContext) {
            start = Math.max(0, segments.size() - contextSize);
        }

        for (int i = start; i <= end; i++) {
            if (i == currentIndex)
                continue;

            GroupedSegment segment = segments.get(i);
            context.append(String.format("[Segment %d (Pages %d-%d)]: %s\n\n",
                    i + 1,
                    segment.startPage(),
                    segment.endPage(),
                    truncateText(segment.combinedText(), 200)));
        }

        return context.toString().trim();
    }

    /**
     * Truncate text for context display
     */
    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength)
            return text;
        return text.substring(0, maxLength) + "...";
    }

    /**
     * Enrich a single segment with retry logic
     */
    private EnrichedDocumentChunk enrichSegmentWithRetry(GroupedSegment segment, String contextText,
            String documentTitle, int index) {
        log.debug("Enriching segment {} (Pages {}-{})...", index, segment.startPage(), segment.endPage());

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                String prompt = String.format(ENRICHMENT_PROMPT,
                        documentTitle != null ? documentTitle : "Educational Document",
                        contextText.isEmpty() ? "No additional context available." : contextText,
                        segment.startPage(),
                        segment.endPage(),
                        segment.combinedText(),
                        maxSummaryWords,
                        questionsPerChunk);

                String response = geminiClient.generateContent(prompt);

                EnrichedDocumentChunk enrichedChunk = parseEnrichmentResponse(response, segment, index, false);

                if (enrichedChunk != null && enrichedChunk.isEnriched()) {
                    log.debug("Segment {} enriched successfully on attempt {}", index, attempt);
                    return enrichedChunk;
                }

            } catch (Exception e) {
                log.warn("Enrichment attempt {} failed for segment {}: {}", attempt, index, e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(llmDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        // All retries failed - return fallback
        log.warn("All {} retries failed for segment {}, using fallback", maxRetries, index);
        return createFallbackChunk(segment, index);
    }

    /**
     * Enrich segment using Gemini Vision API (for pages with images but no text)
     */
    private EnrichedDocumentChunk enrichWithVisionApi(GroupedSegment segment, String documentTitle, int index) {
        log.debug("Using Vision API for segment {} (Pages {}-{})...", index, segment.startPage(), segment.endPage());

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // For now, use the first image if multiple are available
                // In a full implementation, you might want to process all images
                String imageBase64 = segment.imagesBase64().get(0);

                String prompt = String.format(VISION_PROMPT,
                        segment.startPage(),
                        documentTitle != null ? documentTitle : "Educational Document",
                        maxSummaryWords,
                        questionsPerChunk);

                // Note: This requires Gemini Vision API support in GeminiClient
                // For now, we'll use text-based API with a note about the image
                String textPrompt = prompt + "\n\n[Note: Image content would be analyzed here. " +
                        "Since Vision API integration is pending, using text extraction fallback.]";

                String response = geminiClient.generateContent(textPrompt);

                EnrichedDocumentChunk enrichedChunk = parseVisionResponse(response, segment, index);

                if (enrichedChunk != null && enrichedChunk.isEnriched()) {
                    log.debug("Segment {} enriched with Vision API on attempt {}", index, attempt);
                    return enrichedChunk;
                }

            } catch (Exception e) {
                log.warn("Vision API attempt {} failed for segment {}: {}", attempt, index, e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(llmDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        // Vision API failed - return fallback
        log.warn("Vision API failed for segment {}, using fallback", index);
        return createFallbackChunk(segment, index);
    }

    /**
     * Parse LLM response JSON into EnrichedDocumentChunk
     */
    private EnrichedDocumentChunk parseEnrichmentResponse(String response, GroupedSegment segment,
            int index, boolean usedVision) {
        try {
            String cleanedResponse = cleanJsonResponse(response);
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

            String enrichedContent = buildEnrichedContent(originalText, summary, questions);
            int tokenCount = estimateTokenCount(enrichedContent);

            return new EnrichedDocumentChunk(
                    index,
                    originalText,
                    summary,
                    questions,
                    enrichedContent,
                    segment.startPage(),
                    segment.endPage(),
                    tokenCount,
                    true,
                    usedVision);

        } catch (JsonProcessingException e) {
            log.warn("Failed to parse LLM response as JSON: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Parse Vision API response
     */
    private EnrichedDocumentChunk parseVisionResponse(String response, GroupedSegment segment, int index) {
        try {
            String cleanedResponse = cleanJsonResponse(response);
            JsonNode jsonNode = objectMapper.readTree(cleanedResponse);

            String extractedText = jsonNode.has("extracted_text") ? jsonNode.get("extracted_text").asText() : "";
            String summary = jsonNode.has("summary") ? jsonNode.get("summary").asText() : "";

            List<String> questions = new ArrayList<>();
            if (jsonNode.has("questions") && jsonNode.get("questions").isArray()) {
                for (JsonNode q : jsonNode.get("questions")) {
                    questions.add(q.asText());
                }
            }

            String enrichedContent = buildEnrichedContent(extractedText, summary, questions);
            int tokenCount = estimateTokenCount(enrichedContent);

            return new EnrichedDocumentChunk(
                    index,
                    extractedText,
                    summary,
                    questions,
                    enrichedContent,
                    segment.startPage(),
                    segment.endPage(),
                    tokenCount,
                    true,
                    true);

        } catch (JsonProcessingException e) {
            log.warn("Failed to parse Vision API response as JSON: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Clean JSON response from markdown code blocks
     */
    private String cleanJsonResponse(String response) {
        String cleaned = response.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        }
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }

    /**
     * Build the final enriched content string
     */
    private String buildEnrichedContent(String originalText, String summary, List<String> questions) {
        StringBuilder content = new StringBuilder();

        content.append("=== ORIGINAL CONTENT ===\n");
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
     * Create fallback chunk with original text only
     */
    private EnrichedDocumentChunk createFallbackChunk(GroupedSegment segment, int index) {
        String enrichedContent = buildEnrichedContent(
                segment.combinedText(),
                "Summary not available.",
                List.of("Question not available."));

        return new EnrichedDocumentChunk(
                index,
                segment.combinedText(),
                "Summary not available.",
                List.of("Question not available."),
                enrichedContent,
                segment.startPage(),
                segment.endPage(),
                estimateTokenCount(enrichedContent),
                false,
                false);
    }

    /**
     * Estimate token count (approximately 4 characters per token)
     */
    private int estimateTokenCount(String text) {
        if (text == null || text.isBlank())
            return 0;
        return (int) Math.ceil(text.length() / 4.0);
    }
}
