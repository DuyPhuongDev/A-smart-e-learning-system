package com.hcmut.lms.coachingchatbot.application.strategy.content_process.impl;

import com.hcmut.lms.coachingchatbot.application.dto.internal.ExtractedContent;
import com.hcmut.lms.coachingchatbot.application.dto.internal.ProcessingContext;
import com.hcmut.lms.coachingchatbot.application.service.DocumentEnrichmentService;
import com.hcmut.lms.coachingchatbot.application.service.DocumentEnrichmentService.EnrichedDocumentChunk;
import com.hcmut.lms.coachingchatbot.application.service.DocumentEnrichmentService.EnrichmentResult;
import com.hcmut.lms.coachingchatbot.application.service.DocumentEnrichmentService.PageSegment;
import com.hcmut.lms.coachingchatbot.application.strategy.content_process.ContentProcessingException;
import com.hcmut.lms.coachingchatbot.application.strategy.content_process.ContentProcessor;
import com.hcmut.lms.coachingchatbot.client.CourseManagementClient;
import com.hcmut.lms.coachingchatbot.client.dto.TextLectureContentResponse;
import com.hcmut.lms.coachingchatbot.config.DocumentProcessingConfig;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processor for text lecture content (plain text, HTML, Markdown)
 *
 * Pipeline:
 * 1. Get text content from course-management-service (or use from context)
 * 2. Clean and normalize text based on format
 * 3. Split into logical "pages" (by paragraphs or ~500 tokens)
 * 4. Enrich content using DocumentEnrichmentService (with Gemini LLM)
 * 5. Return enriched content with page number metadata
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TextLectureProcessor implements ContentProcessor {

    private final CourseManagementClient courseManagementClient;
    private final DocumentEnrichmentService documentEnrichmentService;
    private final DocumentProcessingConfig documentProcessingConfig;

    @Override
    public ContentType[] getSupportedTypes() {
        return new ContentType[] {
                ContentType.TEXT_CONTENT,
                ContentType.TEXT_HTML,
                ContentType.TEXT_MARKDOWN
        };
    }

    @Override
    public String getProcessorName() {
        return "TextLectureProcessor";
    }

    @Override
    public ExtractedContent extractContent(ProcessingContext context) throws ContentProcessingException {
        log.info("[{}] Starting text content extraction for lecture: {}",
                getProcessorName(), context.getLectureId());

        try {
            // Step 1: Get text content (from context or fetch from service)
            String textContent = context.getTextContent();
            String formatType = context.getFormatType();
            String title = context.getLectureTitle();

            if (textContent == null || textContent.isBlank()) {
                log.info("[{}] Fetching text content from course-management-service", getProcessorName());
                TextLectureContentResponse response = courseManagementClient
                        .getTextLectureContent(context.getLectureId());
                textContent = response.getContent();
                formatType = response.getFormatType();
                title = response.getTitle();
            }

            if (textContent == null || textContent.isBlank()) {
                throw new ContentProcessingException(getProcessorName(), "validation",
                        "Text content is required for text lecture processing");
            }

            // Step 2: Determine and clean text format
            if (formatType == null) {
                formatType = detectFormat(textContent);
            }
            log.info("[{}] Processing text format: {}", getProcessorName(), formatType);

            String cleanedText = cleanText(textContent, formatType);
            log.info("[{}] Text cleaned, {} characters", getProcessorName(), cleanedText.length());

            // Step 3: Split text into logical pages
            List<PageSegment> pageSegments = splitTextIntoPages(cleanedText);
            log.info("[{}] Split text into {} logical pages", getProcessorName(), pageSegments.size());

            // Step 4: Enrich content using DocumentEnrichmentService
            log.info("[{}] Enriching text content with LLM...", getProcessorName());
            EnrichmentResult enrichmentResult = documentEnrichmentService.enrichDocumentContent(
                    context.getLectureId(),
                    pageSegments,
                    title);

            if (!"SUCCESS".equals(enrichmentResult.status())) {
                throw new ContentProcessingException(getProcessorName(), "enrichment",
                        "Text enrichment failed: " + enrichmentResult.message());
            }

            log.info("[{}] Text enrichment completed: {} chunks ({} enriched, {} fallback)",
                    getProcessorName(),
                    enrichmentResult.totalChunks(),
                    enrichmentResult.enrichedChunks(),
                    enrichmentResult.fallbackChunks());

            // Step 5: Build ExtractedContent with enriched chunks and metadata
            return buildExtractedContent(context, cleanedText, formatType, enrichmentResult, pageSegments.size());

        } catch (ContentProcessingException e) {
            throw e;
        } catch (Exception e) {
            log.error("[{}] Failed to process text content: {}", getProcessorName(), e.getMessage(), e);
            throw new ContentProcessingException(getProcessorName(), "extraction",
                    "Failed to extract text content: " + e.getMessage(), e);
        }
    }

    /**
     * Split text into logical pages based on paragraphs and token count
     */
    private List<PageSegment> splitTextIntoPages(String text) {
        List<PageSegment> pages = new ArrayList<>();
        int targetTokens = documentProcessingConfig.getTargetTokensPerSegment();

        // Split by double newlines (paragraphs) or by token count
        String[] paragraphs = text.split("\\n\\n+");

        StringBuilder currentPage = new StringBuilder();
        int currentTokenCount = 0;
        int pageNumber = 1;

        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            if (paragraph.isEmpty())
                continue;

            int paragraphTokens = estimateTokenCount(paragraph);

            // Check if adding this paragraph would exceed target
            if (currentTokenCount > 0 && currentTokenCount + paragraphTokens > targetTokens) {
                // Save current page
                pages.add(createPageSegment(pageNumber++, currentPage.toString(), currentTokenCount));
                currentPage = new StringBuilder();
                currentTokenCount = 0;
            }

            // Add paragraph to current page
            if (currentPage.length() > 0) {
                currentPage.append("\n\n");
            }
            currentPage.append(paragraph);
            currentTokenCount += paragraphTokens;
        }

        // Don't forget the last page
        if (currentPage.length() > 0) {
            pages.add(createPageSegment(pageNumber, currentPage.toString(), currentTokenCount));
        }

        // Ensure at least one page
        if (pages.isEmpty()) {
            pages.add(createPageSegment(1, text, estimateTokenCount(text)));
        }

        return pages;
    }

    private PageSegment createPageSegment(int pageNumber, String text, int tokenCount) {
        return new PageSegment(
                pageNumber,
                text,
                List.of(), // No images for text content
                true, // Text extraction always successful for text lectures
                tokenCount);
    }

    /**
     * Build ExtractedContent from enrichment result
     */
    private ExtractedContent buildExtractedContent(ProcessingContext context,
            String cleanedText,
            String formatType,
            EnrichmentResult enrichmentResult,
            int totalPages) {
        StringBuilder combinedContent = new StringBuilder();
        Map<String, Object> metadata = new HashMap<>();

        metadata.put("sourceType", "text");
        metadata.put("formatType", formatType);
        metadata.put("originalLength", cleanedText.length());
        metadata.put("wordCount", countWords(cleanedText));
        metadata.put("numPages", totalPages);
        metadata.put("totalChunks", enrichmentResult.totalChunks());
        metadata.put("enrichedChunks", enrichmentResult.enrichedChunks());
        metadata.put("processingMethod", "text-paragraph-split");

        // Store page number mapping for each chunk
        List<EnrichedDocumentChunk> chunks = enrichmentResult.chunks();
        for (int i = 0; i < chunks.size(); i++) {
            EnrichedDocumentChunk chunk = chunks.get(i);

            if (combinedContent.length() > 0) {
                combinedContent.append("\n\n---\n\n");
            }
            combinedContent.append(chunk.enrichedContent());

            // Store page number metadata
            metadata.put("pageNumber_" + i, chunk.startPage());
            metadata.put("startPage_" + i, chunk.startPage());
            metadata.put("endPage_" + i, chunk.endPage());
            metadata.put("isEnriched_" + i, chunk.isEnriched());
        }

        return ExtractedContent.builder()
                .lectureId(context.getLectureId())
                .sourceType("text_" + formatType.toLowerCase())
                .rawContent(cleanedText)
                .enhancedContent(combinedContent.toString())
                .metadata(metadata)
                .build();
    }

    /**
     * Detect text format from content
     */
    private String detectFormat(String content) {
        if (content == null)
            return "plain";

        if (content.contains("<html") || content.contains("<body") ||
                content.contains("<div") || content.contains("<p>")) {
            return "html";
        }

        if (content.contains("# ") || content.contains("## ") ||
                content.contains("```") || content.contains("**")) {
            return "markdown";
        }

        return "plain";
    }

    /**
     * Clean text based on format type
     */
    private String cleanText(String content, String formatType) {
        if (content == null)
            return "";

        String cleaned = content;

        switch (formatType.toLowerCase()) {
            case "html":
                cleaned = content.replaceAll("<[^>]+>", " ");
                cleaned = cleaned.replaceAll("&nbsp;", " ");
                cleaned = cleaned.replaceAll("&amp;", "&");
                cleaned = cleaned.replaceAll("&lt;", "<");
                cleaned = cleaned.replaceAll("&gt;", ">");
                break;
            case "markdown":
                cleaned = content.replaceAll("\\*\\*(.+?)\\*\\*", "$1");
                cleaned = cleaned.replaceAll("\\*(.+?)\\*", "$1");
                cleaned = cleaned.replaceAll("`(.+?)`", "$1");
                break;
            default:
                break;
        }

        // Normalize whitespace but preserve paragraph breaks
        cleaned = cleaned.replaceAll("[ \\t]+", " ");
        cleaned = cleaned.replaceAll("\\n{3,}", "\n\n");
        cleaned = cleaned.trim();

        return cleaned;
    }

    /**
     * Count words in text
     */
    private int countWords(String text) {
        if (text == null || text.isBlank())
            return 0;
        return text.split("\\s+").length;
    }

    /**
     * Estimate token count
     */
    private int estimateTokenCount(String text) {
        if (text == null || text.isBlank())
            return 0;
        return (int) Math.ceil(text.length() / 4.0);
    }
}
