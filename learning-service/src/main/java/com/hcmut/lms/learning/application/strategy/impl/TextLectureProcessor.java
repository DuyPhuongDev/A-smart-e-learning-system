package com.hcmut.lms.learning.application.strategy.impl;

import com.hcmut.lms.learning.application.dto.internal.ExtractedContent;
import com.hcmut.lms.learning.application.dto.internal.ProcessingContext;
import com.hcmut.lms.learning.application.strategy.ContentProcessingException;
import com.hcmut.lms.learning.application.strategy.ContentProcessor;
import com.hcmut.lms.learning.domain.entity.lectureKnowledge.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Processor for text lecture content (plain text, HTML, Markdown)
 * Directly processes text content without external services
 */
@Component
@Slf4j
public class TextLectureProcessor implements ContentProcessor {

    @Override
    public ContentType[] getSupportedTypes() {
        return new ContentType[]{
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

        String textContent = context.getTextContent();
        if (textContent == null || textContent.isBlank()) {
            throw new ContentProcessingException(getProcessorName(), "validation",
                    "Text content is required for text lecture processing");
        }

        try {
            // Step 1: Determine text format
            String formatType = context.getFormatType();
            if (formatType == null) {
                formatType = detectFormat(textContent);
            }
            log.info("[{}] Processing text format: {}", getProcessorName(), formatType);

            // Step 2: Clean and normalize text based on format
            String cleanedText = cleanText(textContent, formatType);
            log.info("[{}] Text cleaned, {} characters", getProcessorName(), cleanedText.length());

            // Step 3: Build metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sourceType", "text");
            metadata.put("formatType", formatType);
            metadata.put("originalLength", textContent.length());
            metadata.put("cleanedLength", cleanedText.length());
            metadata.put("wordCount", countWords(cleanedText));

            // Step 4: Build and return extracted content
            return ExtractedContent.builder()
                    .lectureId(context.getLectureId())
                    .sourceType("text_" + formatType.toLowerCase())
                    .rawContent(cleanedText)
                    .metadata(metadata)
                    .build();

        } catch (Exception e) {
            log.error("[{}] Failed to process text content: {}", getProcessorName(), e.getMessage(), e);
            throw new ContentProcessingException(getProcessorName(), "extraction",
                    "Failed to extract text content: " + e.getMessage(), e);
        }
    }

    /**
     * Detect text format from content
     */
    private String detectFormat(String content) {
        if (content == null) return "plain";

        // Check for HTML
        if (content.contains("<html") || content.contains("<body") ||
            content.contains("<div") || content.contains("<p>")) {
            return "html";
        }

        // Check for Markdown
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
        if (content == null) return "";

        String cleaned = content;

        switch (formatType.toLowerCase()) {
            case "html":
                // Remove HTML tags but preserve text
                cleaned = content.replaceAll("<[^>]+>", " ");
                cleaned = cleaned.replaceAll("&nbsp;", " ");
                cleaned = cleaned.replaceAll("&amp;", "&");
                cleaned = cleaned.replaceAll("&lt;", "<");
                cleaned = cleaned.replaceAll("&gt;", ">");
                break;
            case "markdown":
                // Keep markdown mostly intact, just clean up excessive whitespace
                cleaned = content.replaceAll("\\*\\*(.+?)\\*\\*", "$1");
                cleaned = cleaned.replaceAll("\\*(.+?)\\*", "$1");
                cleaned = cleaned.replaceAll("`(.+?)`", "$1");
                break;
            default:
                // Plain text - just normalize whitespace
                break;
        }

        // Common cleaning for all formats
        cleaned = cleaned.replaceAll("\\s+", " ");
        cleaned = cleaned.trim();

        return cleaned;
    }

    /**
     * Count words in text
     */
    private int countWords(String text) {
        if (text == null || text.isBlank()) return 0;
        return text.split("\\s+").length;
    }
}
