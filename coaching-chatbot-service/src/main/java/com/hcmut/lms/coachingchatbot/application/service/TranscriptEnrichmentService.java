package com.hcmut.lms.coachingchatbot.application.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for enriching video transcripts using LLM
 * Groups transcript segments into ~90s windows, uses 5-segment sliding context
 * to generate summaries and questions, then saves enriched chunks to database
 */
public interface TranscriptEnrichmentService {

        /**
         * Enrich transcripts for a lecture synchronously
         *
         * @param lectureId The video lecture ID
         * @return EnrichmentResult containing processing details
         */
        EnrichmentResult enrichTranscriptsForLecture(UUID lectureId);

        /**
         * Enrich transcripts for a lecture asynchronously
         *
         * @param lectureId The video lecture ID
         * @return CompletableFuture with EnrichmentResult
         */
        CompletableFuture<EnrichmentResult> enrichTranscriptsForLectureAsync(UUID lectureId);

        /**
         * Result of transcript enrichment process
         */
        record EnrichmentResult(
                        UUID lectureId,
                        UUID lectureKnowledgeId,
                        int totalChunks,
                        int enrichedChunks,
                        int fallbackChunks,
                        String status,
                        String message,
                        long processingTimeMs) {
                public static EnrichmentResult success(UUID lectureId, UUID lectureKnowledgeId,
                                int totalChunks, int enrichedChunks, int fallbackChunks, long processingTimeMs) {
                        return new EnrichmentResult(
                                        lectureId,
                                        lectureKnowledgeId,
                                        totalChunks,
                                        enrichedChunks,
                                        fallbackChunks,
                                        "SUCCESS",
                                        String.format("Successfully processed %d chunks (%d enriched, %d fallback)",
                                                        totalChunks, enrichedChunks, fallbackChunks),
                                        processingTimeMs);
                }

                public static EnrichmentResult failure(UUID lectureId, String errorMessage, long processingTimeMs) {
                        return new EnrichmentResult(
                                        lectureId,
                                        null,
                                        0,
                                        0,
                                        0,
                                        "FAILED",
                                        errorMessage,
                                        processingTimeMs);
                }
        }

        /**
         * Represents a grouped transcript segment (approximately 90 seconds)
         */
        record GroupedSegment(
                        int index,
                        String combinedText,
                        int startTimeSeconds,
                        int endTimeSeconds,
                        int wordCount) {
        }

        /**
         * Represents an enriched chunk ready for storage
         */
        record EnrichedChunk(
                        int index,
                        String originalText,
                        String summary,
                        List<String> questions,
                        String enrichedContent,
                        int startTimeSeconds,
                        int endTimeSeconds,
                        int tokenCount,
                        boolean isEnriched) {
        }
}
