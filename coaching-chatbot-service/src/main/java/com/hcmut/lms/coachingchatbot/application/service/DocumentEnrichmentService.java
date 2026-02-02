package com.hcmut.lms.coachingchatbot.application.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for enriching document content using LLM
 * Groups document pages into semantic chunks (~500 tokens), uses 5-segment
 * sliding context
 * to generate summaries and questions, then returns enriched chunks for storage
 */
public interface DocumentEnrichmentService {

        /**
         * Enrich document content for a lecture synchronously
         *
         * @param lectureId     The document lecture ID
         * @param pages         List of page contents extracted from the document
         * @param documentTitle Title of the document for context
         * @return EnrichmentResult containing processing details
         */
        EnrichmentResult enrichDocumentContent(UUID lectureId, List<PageSegment> pages, String documentTitle);

        /**
         * Enrich document content for a lecture asynchronously
         *
         * @param lectureId     The document lecture ID
         * @param pages         List of page contents extracted from the document
         * @param documentTitle Title of the document for context
         * @return CompletableFuture with EnrichmentResult
         */
        CompletableFuture<EnrichmentResult> enrichDocumentContentAsync(UUID lectureId, List<PageSegment> pages,
                        String documentTitle);

        /**
         * Result of document enrichment process
         */
        record EnrichmentResult(
                        UUID lectureId,
                        int totalChunks,
                        int enrichedChunks,
                        int fallbackChunks,
                        int visionApiChunks,
                        String status,
                        String message,
                        long processingTimeMs,
                        List<EnrichedDocumentChunk> chunks) {
                public static EnrichmentResult success(UUID lectureId, int totalChunks, int enrichedChunks,
                                int fallbackChunks, int visionApiChunks, long processingTimeMs,
                                List<EnrichedDocumentChunk> chunks) {
                        return new EnrichmentResult(
                                        lectureId,
                                        totalChunks,
                                        enrichedChunks,
                                        fallbackChunks,
                                        visionApiChunks,
                                        "SUCCESS",
                                        String.format("Successfully processed %d chunks (%d enriched, %d fallback, %d vision)",
                                                        totalChunks, enrichedChunks, fallbackChunks, visionApiChunks),
                                        processingTimeMs,
                                        chunks);
                }

                public static EnrichmentResult failure(UUID lectureId, String errorMessage, long processingTimeMs) {
                        return new EnrichmentResult(
                                        lectureId,
                                        0,
                                        0,
                                        0,
                                        0,
                                        "FAILED",
                                        errorMessage,
                                        processingTimeMs,
                                        List.of());
                }
        }

        /**
         * Represents a page segment from document extraction
         */
        record PageSegment(
                        int pageNumber,
                        String textContent,
                        List<String> imagesBase64,
                        boolean textExtractionSuccessful,
                        int estimatedTokenCount) {
                public boolean needsVisionFallback() {
                        return !textExtractionSuccessful && imagesBase64 != null && !imagesBase64.isEmpty();
                }

                public boolean hasContent() {
                        return (textContent != null && !textContent.isBlank()) ||
                                        (imagesBase64 != null && !imagesBase64.isEmpty());
                }
        }

        /**
         * Represents a grouped segment (multiple pages combined to ~500 tokens)
         */
        record GroupedSegment(
                        int index,
                        String combinedText,
                        List<Integer> pageNumbers,
                        int startPage,
                        int endPage,
                        int tokenCount,
                        List<String> imagesBase64,
                        boolean needsVisionFallback) {
        }

        /**
         * Represents an enriched chunk ready for storage
         */
        record EnrichedDocumentChunk(
                        int index,
                        String originalText,
                        String summary,
                        List<String> questions,
                        String enrichedContent,
                        int startPage,
                        int endPage,
                        int tokenCount,
                        boolean isEnriched,
                        boolean usedVisionApi) {
        }
}
