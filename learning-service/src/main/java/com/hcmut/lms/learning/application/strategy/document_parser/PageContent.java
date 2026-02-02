package com.hcmut.lms.learning.application.strategy.document_parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents extracted content from a single page of a document
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageContent {

    /**
     * Page number (1-based index)
     */
    private int pageNumber;

    /**
     * Extracted text content from the page
     */
    private String textContent;

    /**
     * Images extracted from the page (as base64 encoded strings)
     * Only populated when text extraction fails or for slides
     */
    private List<String> imagesBase64;

    /**
     * Whether text extraction was successful
     * If false, images should be used for Gemini Vision API
     */
    private boolean textExtractionSuccessful;

    /**
     * Estimated token count of the text content
     */
    private int estimatedTokenCount;

    /**
     * Check if this page has meaningful content
     */
    public boolean hasContent() {
        return (textContent != null && !textContent.isBlank()) ||
               (imagesBase64 != null && !imagesBase64.isEmpty());
    }

    /**
     * Check if fallback to vision API is needed
     */
    public boolean needsVisionFallback() {
        return !textExtractionSuccessful && imagesBase64 != null && !imagesBase64.isEmpty();
    }
}
