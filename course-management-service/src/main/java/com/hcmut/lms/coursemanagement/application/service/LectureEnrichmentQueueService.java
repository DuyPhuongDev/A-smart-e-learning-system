package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.LectureEnrichmentJobRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.LectureEnrichmentJobResponse;

/**
 * Unified service for queueing lecture enrichment jobs to appropriate SQS queues
 * Routes lectures to correct queue based on type:
 * - VIDEO → video-transcription-queue
 * - DOCUMENT/TEXT → document-enrichment-queue
 */
public interface LectureEnrichmentQueueService {

    /**
     * Queue lectures for enrichment/transcription processing by AWS Fargate workers
     * Automatically routes to appropriate queue based on lecture type
     *
     * @param request Contains list of lecture IDs to process
     * @return Response with queued and failed jobs, broken down by type
     */
    LectureEnrichmentJobResponse queueEnrichmentJobs(LectureEnrichmentJobRequest request);
}
