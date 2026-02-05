package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.DocumentEnrichmentJobRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.DocumentEnrichmentJobResponse;

/**
 * Service for queueing document/text lecture enrichment jobs to SQS
 * for processing by AWS Fargate workers
 */
public interface DocumentEnrichmentQueueService {

    /**
     * Send document/text lecture enrichment jobs to SQS queue for processing by Fargate workers
     *
     * @param request Contains list of lecture IDs to process
     * @return Response with queued and failed jobs
     */
    DocumentEnrichmentJobResponse queueEnrichmentJobs(DocumentEnrichmentJobRequest request);
}
