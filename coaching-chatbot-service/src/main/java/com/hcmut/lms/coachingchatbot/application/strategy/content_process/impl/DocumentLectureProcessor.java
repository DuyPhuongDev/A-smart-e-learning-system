package com.hcmut.lms.coachingchatbot.application.strategy.content_process.impl;

import com.hcmut.lms.coachingchatbot.application.dto.internal.ExtractedContent;
import com.hcmut.lms.coachingchatbot.application.dto.internal.ProcessingContext;
import com.hcmut.lms.coachingchatbot.application.service.DocumentEnrichmentService;
import com.hcmut.lms.coachingchatbot.application.service.DocumentEnrichmentService.EnrichedDocumentChunk;
import com.hcmut.lms.coachingchatbot.application.service.DocumentEnrichmentService.EnrichmentResult;
import com.hcmut.lms.coachingchatbot.application.service.DocumentEnrichmentService.PageSegment;
import com.hcmut.lms.coachingchatbot.application.strategy.content_process.ContentProcessingException;
import com.hcmut.lms.coachingchatbot.application.strategy.content_process.ContentProcessor;
import com.hcmut.lms.coachingchatbot.application.strategy.document_parser.DocumentPageExtractor;
import com.hcmut.lms.coachingchatbot.application.strategy.document_parser.DocumentParserFactory;
import com.hcmut.lms.coachingchatbot.application.strategy.document_parser.PageContent;
import com.hcmut.lms.coachingchatbot.application.util.AutoDeletingTempFile;
import com.hcmut.lms.coachingchatbot.client.CourseManagementClient;
import com.hcmut.lms.coachingchatbot.client.dto.DocumentDownloadUrlResponse;
import com.hcmut.lms.coachingchatbot.config.DocumentProcessingConfig;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processor for document lecture content (PDF, DOCX, PPTX)
 *
 * Pipeline:
 * 1. Get download URL from course-management-service
 * 2. Download document to temp file
 * 3. Extract content page by page using DocumentPageExtractor
 * 4. Enrich content using DocumentEnrichmentService (with Gemini LLM)
 * 5. Return enriched content with page number metadata
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentLectureProcessor implements ContentProcessor {

    private final CourseManagementClient courseManagementClient;
    private final DocumentParserFactory documentParserFactory;
    private final DocumentEnrichmentService documentEnrichmentService;
    private final DocumentProcessingConfig documentProcessingConfig;

    private HttpClient httpClient;

    private HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .connectTimeout(Duration.ofSeconds(documentProcessingConfig.getDownloadTimeoutSeconds()))
                    .build();
        }
        return httpClient;
    }

    @Override
    public ContentType[] getSupportedTypes() {
        return new ContentType[] {
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

        try {
            // Step 1: Get download URL from course-management-service
            log.info("[{}] Getting download URL for document lecture: {}",
                    getProcessorName(), context.getLectureId());
            DocumentDownloadUrlResponse downloadResponse = courseManagementClient
                    .getDocumentDownloadUrl(context.getLectureId());

            String downloadUrl = downloadResponse.getDownloadUrl();
            String fileFormat = downloadResponse.getFileFormat();
            if (fileFormat == null || fileFormat.isBlank()) {
                fileFormat = documentParserFactory.detectFileFormat(downloadUrl);
            }
            log.info("[{}] Document format: {}, source: {}",
                    getProcessorName(), fileFormat, downloadResponse.getSourceType());

            // Step 2: Download document to temp file (auto-cleanup)
            String fileExtension = "." + fileFormat.toLowerCase();
            try (AutoDeletingTempFile tempFile = downloadDocumentToTemp(downloadUrl, fileExtension)) {
                log.info("[{}] Document downloaded to: {}", getProcessorName(), tempFile.getPath());

                // Step 3: Extract content page by page
                log.info("[{}] Extracting pages from document...", getProcessorName());
                DocumentPageExtractor extractor = documentParserFactory.getExtractor(fileFormat);
                List<PageContent> pages = extractor.extractPages(tempFile.getPath());
                log.info("[{}] Extracted {} pages from document", getProcessorName(), pages.size());

                // Step 4: Convert to PageSegment for enrichment service
                List<PageSegment> pageSegments = convertToPageSegments(pages);

                // Step 5: Enrich content using DocumentEnrichmentService
                log.info("[{}] Enriching document content with LLM...", getProcessorName());
                EnrichmentResult enrichmentResult = documentEnrichmentService.enrichDocumentContent(
                        context.getLectureId(),
                        pageSegments,
                        context.getLectureTitle());

                if (!"SUCCESS".equals(enrichmentResult.status())) {
                    throw new ContentProcessingException(getProcessorName(), "enrichment",
                            "Document enrichment failed: " + enrichmentResult.message());
                }

                log.info("[{}] Document enrichment completed: {} chunks ({} enriched, {} fallback, {} vision)",
                        getProcessorName(),
                        enrichmentResult.totalChunks(),
                        enrichmentResult.enrichedChunks(),
                        enrichmentResult.fallbackChunks(),
                        enrichmentResult.visionApiChunks());

                // Step 6: Build ExtractedContent with enriched chunks and metadata
                return buildExtractedContent(context, downloadResponse, enrichmentResult, pages.size());
            }

        } catch (ContentProcessingException e) {
            throw e;
        } catch (Exception e) {
            log.error("[{}] Failed to process document content: {}", getProcessorName(), e.getMessage(), e);
            throw new ContentProcessingException(getProcessorName(), "extraction",
                    "Failed to extract content from document: " + e.getMessage(), e);
        }
    }

    /**
     * Download document from URL to a temporary file
     */
    private AutoDeletingTempFile downloadDocumentToTemp(String url, String fileExtension) throws Exception {
        log.debug("[{}] Downloading document from: {}", getProcessorName(), url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(documentProcessingConfig.getDownloadTimeoutSeconds()))
                .GET()
                .build();

        HttpResponse<InputStream> response = getHttpClient().send(request,
                HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to download document, status: " + response.statusCode());
        }

        AutoDeletingTempFile tempFile = new AutoDeletingTempFile("doc_", fileExtension,
                documentProcessingConfig.getTempDirPath());
        try (InputStream inputStream = response.body()) {
            Files.copy(inputStream, tempFile.getPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        log.debug("[{}] Document downloaded, size: {} bytes", getProcessorName(), tempFile.size());
        return tempFile;
    }

    /**
     * Convert PageContent list to PageSegment list for enrichment service
     */
    private List<PageSegment> convertToPageSegments(List<PageContent> pages) {
        List<PageSegment> segments = new ArrayList<>();
        for (PageContent page : pages) {
            segments.add(new PageSegment(
                    page.getPageNumber(),
                    page.getTextContent(),
                    page.getImagesBase64(),
                    page.isTextExtractionSuccessful(),
                    page.getEstimatedTokenCount()));
        }
        return segments;
    }

    /**
     * Build ExtractedContent from enrichment result with page metadata
     */
    private ExtractedContent buildExtractedContent(ProcessingContext context,
            DocumentDownloadUrlResponse downloadResponse,
            EnrichmentResult enrichmentResult,
            int totalPages) {
        // Combine all enriched content
        StringBuilder combinedContent = new StringBuilder();
        Map<String, Object> metadata = new HashMap<>();

        metadata.put("sourceType", "document");
        metadata.put("fileUrl", downloadResponse.getOriginalUrl());
        metadata.put("fileFormat", downloadResponse.getFileFormat());
        metadata.put("numPages", totalPages);
        metadata.put("totalChunks", enrichmentResult.totalChunks());
        metadata.put("enrichedChunks", enrichmentResult.enrichedChunks());
        metadata.put("processingMethod", "page-by-page-extraction");

        // Store page number mapping for each chunk
        List<EnrichedDocumentChunk> chunks = enrichmentResult.chunks();
        for (int i = 0; i < chunks.size(); i++) {
            EnrichedDocumentChunk chunk = chunks.get(i);

            // Add chunk content to combined content
            if (combinedContent.length() > 0) {
                combinedContent.append("\n\n---\n\n");
            }
            combinedContent.append(chunk.enrichedContent());

            // Store page number metadata for this chunk
            metadata.put("pageNumber_" + i, chunk.startPage());
            metadata.put("startPage_" + i, chunk.startPage());
            metadata.put("endPage_" + i, chunk.endPage());
            metadata.put("isEnriched_" + i, chunk.isEnriched());
            metadata.put("usedVisionApi_" + i, chunk.usedVisionApi());
        }

        return ExtractedContent.builder()
                .lectureId(context.getLectureId())
                .sourceType("document_" + downloadResponse.getFileFormat().toLowerCase())
                .rawContent(combinedContent.toString())
                .enhancedContent(combinedContent.toString())
                .metadata(metadata)
                .build();
    }
}
