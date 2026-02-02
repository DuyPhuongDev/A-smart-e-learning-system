package com.hcmut.lms.learning.application.strategy.document_parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory for selecting the appropriate document page extractor based on file format
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentParserFactory {

    private final List<DocumentPageExtractor> extractors;

    /**
     * Get the appropriate extractor for the given file format
     *
     * @param fileFormat The file format (e.g., "pdf", "docx", "pptx")
     * @return The appropriate document page extractor
     * @throws IllegalArgumentException if no extractor found for the format
     */
    public DocumentPageExtractor getExtractor(String fileFormat) {
        log.debug("Finding extractor for file format: {}", fileFormat);

        return extractors.stream()
                .filter(extractor -> extractor.supports(fileFormat))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No extractor found for file format: " + fileFormat));
    }

    /**
     * Check if an extractor exists for the given file format
     */
    public boolean hasExtractor(String fileFormat) {
        return extractors.stream().anyMatch(extractor -> extractor.supports(fileFormat));
    }

    /**
     * Get a file format from URL or filename
     */
    public String detectFileFormat(String urlOrFilename) {
        if (urlOrFilename == null || urlOrFilename.isBlank()) {
            return "unknown";
        }

        String lowerUrl = urlOrFilename.toLowerCase();

        // Remove query parameters if present
        int queryIndex = lowerUrl.indexOf('?');
        if (queryIndex > 0) {
            lowerUrl = lowerUrl.substring(0, queryIndex);
        }

        if (lowerUrl.endsWith(".pdf")) return "pdf";
        if (lowerUrl.endsWith(".docx")) return "docx";
        if (lowerUrl.endsWith(".doc")) return "doc";
        if (lowerUrl.endsWith(".pptx")) return "pptx";
        if (lowerUrl.endsWith(".ppt")) return "ppt";
        if (lowerUrl.endsWith(".xlsx")) return "xlsx";
        if (lowerUrl.endsWith(".xls")) return "xls";
        if (lowerUrl.endsWith(".txt")) return "txt";

        return "unknown";
    }
}
