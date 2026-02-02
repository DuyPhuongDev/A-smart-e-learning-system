package com.hcmut.lms.coachingchatbot.controller;

import com.hcmut.lms.coachingchatbot.application.service.VideoProcessingService;
import com.hcmut.lms.coachingchatbot.application.service.VideoProcessingService.VideoProcessingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller for testing video processing pipeline
 * Provides endpoints to:
 * - Process video synchronously
 * - Process video asynchronously
 * - Check async processing status
 */
@RestController
@RequestMapping("${prefix-api:}/video-processing")
@RequiredArgsConstructor
@Slf4j
public class VideoProcessingController {

    private final VideoProcessingService videoProcessingService;

    // Store async job results (in production, use Redis or database)
    private final Map<UUID, VideoProcessingResult> asyncResults = new ConcurrentHashMap<>();
    private final Map<UUID, String> asyncStatus = new ConcurrentHashMap<>();

    /**
     * Process video lecture synchronously
     * This endpoint will block until processing is complete
     *
     * @param lectureId The video lecture ID
     * @return VideoProcessingResult with transcript and metadata
     *
     *         Example: POST /api/v1/video-processing/sync/{lectureId}
     */
    @PostMapping("/sync/{lectureId}")
    public ResponseEntity<VideoProcessingResult> processVideoSync(@PathVariable UUID lectureId) {
        log.info("Received sync video processing request for lecture: {}", lectureId);

        VideoProcessingResult result = videoProcessingService.processVideo(lectureId);

        if ("SUCCESS".equals(result.status())) {
            log.info("Sync video processing completed successfully for lecture: {}", lectureId);
            return ResponseEntity.ok(result);
        } else {
            log.error("Sync video processing failed for lecture: {}", lectureId);
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * Process video lecture asynchronously
     * Returns immediately with a job ID to check status later
     *
     * @param lectureId The video lecture ID
     * @return Job ID for status checking
     *
     *         Example: POST /api/v1/video-processing/async/{lectureId}
     */
    @PostMapping("/async/{lectureId}")
    public ResponseEntity<AsyncJobResponse> processVideoAsync(@PathVariable UUID lectureId) {
        log.info("Received async video processing request for lecture: {}", lectureId);

        // Generate job ID (using lecture ID for simplicity)
        UUID jobId = UUID.randomUUID();
        asyncStatus.put(jobId, "PROCESSING");

        // Start async processing
        CompletableFuture<VideoProcessingResult> future = videoProcessingService.processVideoAsync(lectureId);

        // Handle completion
        future.whenComplete((result, error) -> {
            if (error != null) {
                log.error("Async processing failed for job {}: {}", jobId, error.getMessage());
                asyncStatus.put(jobId, "FAILED");
                asyncResults.put(jobId, VideoProcessingResult.failure(lectureId, error.getMessage(), 0));
            } else {
                log.info("Async processing completed for job {}: {}", jobId, result.status());
                asyncStatus.put(jobId, result.status());
                asyncResults.put(jobId, result);
            }
        });

        return ResponseEntity.accepted().body(new AsyncJobResponse(
                jobId,
                lectureId,
                "PROCESSING",
                "Video processing started. Use GET /api/v1/video-processing/status/{jobId} to check status."));
    }

    /**
     * Check async processing status
     *
     * @param jobId The job ID returned from async endpoint
     * @return Current status and result if completed
     *
     *         Example: GET /api/v1/video-processing/status/{jobId}
     */
    @GetMapping("/status/{jobId}")
    public ResponseEntity<AsyncStatusResponse> checkStatus(@PathVariable UUID jobId) {
        String status = asyncStatus.get(jobId);

        if (status == null) {
            return ResponseEntity.notFound().build();
        }

        VideoProcessingResult result = asyncResults.get(jobId);

        return ResponseEntity.ok(new AsyncStatusResponse(
                jobId,
                status,
                result));
    }

    /**
     * Health check endpoint for testing
     *
     * Example: GET /api/v1/video-processing/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "video-processing",
                "message", "Video processing service is running",
                "endpoints", Map.of(
                        "sync", "POST /api/v1/video-processing/sync/{lectureId}",
                        "async", "POST /api/v1/video-processing/async/{lectureId}",
                        "status", "GET /api/v1/video-processing/status/{jobId}")));
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
            VideoProcessingResult result) {
    }
}
