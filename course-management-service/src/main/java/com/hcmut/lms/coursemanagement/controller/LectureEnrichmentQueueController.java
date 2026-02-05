package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.request.LectureEnrichmentJobRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.LectureEnrichmentJobResponse;
import com.hcmut.lms.coursemanagement.application.service.LectureEnrichmentQueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Unified controller for lecture enrichment queue operations
 * Handles VIDEO (transcription), DOCUMENT, and TEXT lecture processing
 * Routes to appropriate SQS queue based on lecture type
 */
@RestController
@RequestMapping("${prefix-api}/lecture-enrichment")
@RequiredArgsConstructor
@Slf4j
public class LectureEnrichmentQueueController {

    private final LectureEnrichmentQueueService lectureEnrichmentQueueService;

    /**
     * Queue lectures for enrichment/transcription processing by AWS Fargate workers
     * Automatically routes to appropriate queue based on lecture type:
     * - VIDEO → video-transcription-queue
     * - DOCUMENT/TEXT → document-enrichment-queue
     *
     * POST /api/courses/v1/lecture-enrichment/queue
     * Body: {
     *   "lectureIds": ["uuid1", "uuid2", ...],
     *   "lectureType": "VIDEO" | "DOCUMENT" | "TEXT" | null  // optional filter
     * }
     *
     * @param request Contains list of lecture IDs to process and optional lecture type filter
     * @return Response with queued and failed jobs, broken down by type
     */
    @PostMapping("/queue")
    public ResponseEntity<LectureEnrichmentJobResponse> queueEnrichmentJobs(
            @Valid @RequestBody LectureEnrichmentJobRequest request) {
        log.info("Received unified enrichment request: {} lectures, lectureType filter: {}",
                request.getLectureIds().size(), request.getLectureType());

        LectureEnrichmentJobResponse response = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        log.info("Unified enrichment result: queued={}, failed={}, VIDEO={}/{}, DOCUMENT={}/{}, TEXT={}/{}",
                response.getSuccessfullyQueued(), response.getFailed(),
                response.getVideoJobs().getQueued(), response.getVideoJobs().getRequested(),
                response.getDocumentJobs().getQueued(), response.getDocumentJobs().getRequested(),
                response.getTextJobs().getQueued(), response.getTextJobs().getRequested());

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Lecture Enrichment Queue Service is running");
    }
}
