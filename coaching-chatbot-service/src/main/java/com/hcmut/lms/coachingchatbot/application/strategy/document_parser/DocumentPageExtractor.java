package com.hcmut.lms.coachingchatbot.application.strategy.document_parser;

import java.nio.file.Path;
import java.util.List;

/**
 * Strategy interface for extracting content from documents page by page
 * Each implementation handles a specific document format (PDF, DOCX, PPTX)
 */
public interface DocumentPageExtractor {

    /**
     * Get the file formats this extractor supports
     * 
     * @return Array of supported file extensions (lowercase, without dot)
     */
    String[] getSupportedFormats();

    /**
     * Check if this extractor supports the given file format
     * 
     * @param fileFormat The file format to check (e.g., "pdf", "docx")
     * @return true if supported
     */
    default boolean supports(String fileFormat) {
        if (fileFormat == null)
            return false;
        String lowerFormat = fileFormat.toLowerCase().trim();
        for (String supported : getSupportedFormats()) {
            if (supported.equals(lowerFormat)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extract content from each page of the document
     *
     * @param filePath Path to the document file
     * @return List of PageContent, one per page
     * @throws DocumentParsingException if extraction fails
     */
    List<PageContent> extractPages(Path filePath) throws DocumentParsingException;

    /**
     * Get the total number of pages in the document
     *
     * @param filePath Path to the document file
     * @return Number of pages
     * @throws DocumentParsingException if counting fails
     */
    int getPageCount(Path filePath) throws DocumentParsingException;

    /**
     * Get the extractor name for logging
     */
    String getExtractorName();
}
