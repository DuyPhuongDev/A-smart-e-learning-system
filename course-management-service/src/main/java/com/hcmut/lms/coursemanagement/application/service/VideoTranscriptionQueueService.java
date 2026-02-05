package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.VideoTranscriptionJobRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.VideoTranscriptionJobResponse;

/**
 * Service for queueing video transcription jobs to SQS
 * for processing by AWS Fargate workers
 */
public interface VideoTranscriptionQueueService {

    /**
     * Send video transcription jobs to SQS queue for processing by Fargate workers
     *
     * @param request Contains list of lecture IDs to process
     * @return Response with queued and failed jobs
     */
    VideoTranscriptionJobResponse queueTranscriptionJobs(VideoTranscriptionJobRequest request);
}
