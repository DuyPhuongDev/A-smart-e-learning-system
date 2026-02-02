package com.hcmut.lms.coachingchatbot.controller;

import com.hcmut.lms.coachingchatbot.application.service.TranscriptEnrichmentService;
import com.hcmut.lms.coachingchatbot.application.service.TranscriptEnrichmentService.EnrichmentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller for testing transcript enrichment pipeline
 * Provides endpoints to:
 * - Enrich transcripts synchronously
 * - Enrich transcripts asynchronously
 * - Check async processing status
 */
@RestController
@RequestMapping("${prefix-api:}/transcript-enrichment")
@RequiredArgsConstructor
@Slf4j
public class TranscriptEnrichmentController {

    private final TranscriptEnrichmentService transcriptEnrichmentService;

    // Store async job results (in production, use Redis or database)
    private final Map<UUID, EnrichmentResult> asyncResults = new ConcurrentHashMap<>();
    private final Map<UUID, String> asyncStatus = new ConcurrentHashMap<>();

    /**
     * Enrich transcripts for a lecture synchronously
     * This endpoint will block until processing is complete
     *
     * @param lectureId The video lecture ID
     * @return EnrichmentResult with processing details
     *
     *         Example: POST /api/learning/v1/transcript-enrichment/sync/{lectureId}
     */
    @PostMapping("/sync/{lectureId}")
    public ResponseEntity<EnrichmentResult> enrichTranscriptsSync(@PathVariable UUID lectureId) {
        log.info("Received sync transcript enrichment request for lecture: {}", lectureId);

        EnrichmentResult result = transcriptEnrichmentService.enrichTranscriptsForLecture(lectureId);

        if ("SUCCESS".equals(result.status())) {
            log.info("Sync transcript enrichment completed successfully for lecture: {}", lectureId);
            return ResponseEntity.ok(result);
        } else {
            log.error("Sync transcript enrichment failed for lecture: {}", lectureId);
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * Enrich transcripts for a lecture asynchronously
     * Returns immediately with a job ID to check status later
     *
     * @param lectureId The video lecture ID
     * @return Job ID for status checking
     *
     *         Example: POST
     *         /api/learning/v1/transcript-enrichment/async/{lectureId}
     */
    @PostMapping("/async/{lectureId}")
    public ResponseEntity<AsyncJobResponse> enrichTranscriptsAsync(@PathVariable UUID lectureId) {
        log.info("Received async transcript enrichment request for lecture: {}", lectureId);

        // Generate job ID
        UUID jobId = UUID.randomUUID();
        asyncStatus.put(jobId, "PROCESSING");

        // Start async processing
        CompletableFuture<EnrichmentResult> future = transcriptEnrichmentService
                .enrichTranscriptsForLectureAsync(lectureId);

        // Handle completion
        future.whenComplete((result, error) -> {
            if (error != null) {
                log.error("Async enrichment failed for job {}: {}", jobId, error.getMessage());
                asyncStatus.put(jobId, "FAILED");
                asyncResults.put(jobId, EnrichmentResult.failure(lectureId, error.getMessage(), 0));
            } else {
                log.info("Async enrichment completed for job {}: {}", jobId, result.status());
                asyncStatus.put(jobId, result.status());
                asyncResults.put(jobId, result);
            }
        });

        return ResponseEntity.accepted().body(new AsyncJobResponse(
                jobId,
                lectureId,
                "PROCESSING",
                "Transcript enrichment started. Use GET /api/learning/v1/transcript-enrichment/status/{jobId} to check status."));
    }

    /**
     * Check async processing status
     *
     * @param jobId The job ID returned from async endpoint
     * @return Current status and result if completed
     *
     *         Example: GET /api/learning/v1/transcript-enrichment/status/{jobId}
     */
    @GetMapping("/status/{jobId}")
    public ResponseEntity<AsyncStatusResponse> checkStatus(@PathVariable UUID jobId) {
        String status = asyncStatus.get(jobId);

        if (status == null) {
            return ResponseEntity.notFound().build();
        }

        EnrichmentResult result = asyncResults.get(jobId);

        return ResponseEntity.ok(new AsyncStatusResponse(
                jobId,
                status,
                result));
    }

    /**
     * Health check endpoint for testing
     *
     * Example: GET /api/learning/v1/transcript-enrichment/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "transcript-enrichment",
                "message", "Transcript enrichment service is running",
                "endpoints", Map.of(
                        "sync", "POST /api/learning/v1/transcript-enrichment/sync/{lectureId}",
                        "async", "POST /api/learning/v1/transcript-enrichment/async/{lectureId}",
                        "status", "GET /api/learning/v1/transcript-enrichment/status/{jobId}")));
    }

    // Response DTOs
    public record AsyncJobResponse(
            UUID jobId,
            UUID lectureId,
            String status,
            String message) {
    }

    public record AsyncStatusResponse(
            UUID jobId,
            String status,
            EnrichmentResult result) {
    }
}
