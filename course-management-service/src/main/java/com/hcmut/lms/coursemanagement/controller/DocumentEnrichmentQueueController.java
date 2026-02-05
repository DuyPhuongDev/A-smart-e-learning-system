package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.request.DocumentEnrichmentJobRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.DocumentEnrichmentJobResponse;
import com.hcmut.lms.coursemanagement.application.service.DocumentEnrichmentQueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for document/text lecture enrichment queue operations
 * Provides API to queue document/text lectures for enrichment processing by AWS Fargate workers
 */
@RestController
@RequestMapping("${prefix-api}/document-enrichment")
@RequiredArgsConstructor
@Slf4j
public class DocumentEnrichmentQueueController {

    private final DocumentEnrichmentQueueService documentEnrichmentQueueService;

    /**
     * Queue document/text lectures for enrichment processing by AWS Fargate workers
     *
     * POST /api/courses/v1/document-enrichment/queue
     * Body: { "lectureIds": ["uuid1", "uuid2", ...], "lectureType": "DOCUMENT" | "TEXT" | null }
     *
     * @param request Contains list of lecture IDs to process and optional lecture type filter
     * @return Response with queued and failed jobs
     */
    @PostMapping("/queue")
    public ResponseEntity<DocumentEnrichmentJobResponse> queueEnrichmentJobs(
            @Valid @RequestBody DocumentEnrichmentJobRequest request) {
        log.info("Received request to queue {} document/text enrichment jobs, lectureType filter: {}",
                request.getLectureIds().size(), request.getLectureType());

        DocumentEnrichmentJobResponse response = documentEnrichmentQueueService.queueEnrichmentJobs(request);

        return ResponseEntity.ok(response);
    }
}
