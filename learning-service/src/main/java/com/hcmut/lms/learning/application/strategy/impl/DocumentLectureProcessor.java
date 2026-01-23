package com.hcmut.lms.learning.application.strategy.impl;

import com.hcmut.lms.learning.application.dto.internal.ExtractedContent;
import com.hcmut.lms.learning.application.dto.internal.ProcessingContext;
import com.hcmut.lms.learning.application.service.DocumentParsingService;
import com.hcmut.lms.learning.application.strategy.ContentProcessingException;
import com.hcmut.lms.learning.application.strategy.ContentProcessor;
import com.hcmut.lms.learning.domain.entity.lectureKnowledge.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Processor for document lecture content (PDF, DOCX, PPTX)
 * Extracts text using Apache Tika
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentLectureProcessor implements ContentProcessor {

    private final DocumentParsingService documentParsingService;

    @Override
    public ContentType[] getSupportedTypes() {
        return new ContentType[]{
                ContentType.DOCUMENT_PDF,
                ContentType.DOCUMENT_DOCX,
                ContentType.DOCUMENT_PPTX
        };
    }

    @Override
    public String getProcessorName() {
        return "DocumentLectureProcessor";
    }

    @Override
    public ExtractedContent extractContent(ProcessingContext context) throws ContentProcessingException {
        log.info("[{}] Starting document content extraction for lecture: {}",
                getProcessorName(), context.getLectureId());

        String fileUrl = context.getFileUrl();
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new ContentProcessingException(getProcessorName(), "validation",
                    "File URL is required for document lecture processing");
        }

        try {
            // Step 1: Determine document type
            String fileFormat = context.getFileFormat();
            if (fileFormat == null) {
                fileFormat = detectFileFormat(fileUrl);
            }
            log.info("[{}] Processing document format: {}", getProcessorName(), fileFormat);

            // Step 2: Parse document and extract text
            log.info("[{}] Parsing document from URL: {}", getProcessorName(), fileUrl);
            String extractedText = documentParsingService.parseDocument(fileUrl);
            log.info("[{}] Document parsed successfully, extracted {} characters",
                    getProcessorName(), extractedText.length());

            // Step 3: Build metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sourceType", "document");
            metadata.put("fileUrl", fileUrl);
            metadata.put("fileFormat", fileFormat);
            metadata.put("numPages", context.getNumPages());
            metadata.put("parsingMethod", "apache-tika");

            // Step 4: Build and return extracted content
            return ExtractedContent.builder()
                    .lectureId(context.getLectureId())
                    .sourceType("document_" + fileFormat.toLowerCase())
                    .rawContent(extractedText)
                    .metadata(metadata)
                    .build();

        } catch (Exception e) {
            log.error("[{}] Failed to process document content: {}", getProcessorName(), e.getMessage(), e);
            throw new ContentProcessingException(getProcessorName(), "extraction",
                    "Failed to extract content from document: " + e.getMessage(), e);
        }
    }

    /**
     * Detect file format from URL
     */
    private String detectFileFormat(String url) {
        if (url == null) return "unknown";

        String lowerUrl = url.toLowerCase();
        if (lowerUrl.endsWith(".pdf")) return "pdf";
        if (lowerUrl.endsWith(".docx") || lowerUrl.endsWith(".doc")) return "docx";
        if (lowerUrl.endsWith(".pptx") || lowerUrl.endsWith(".ppt")) return "pptx";
        if (lowerUrl.endsWith(".xlsx") || lowerUrl.endsWith(".xls")) return "xlsx";
        if (lowerUrl.endsWith(".txt")) return "txt";

        return "unknown";
    }
}
