package com.hcmut.lms.coachingchatbot.application.service;

import com.hcmut.lms.coachingchatbot.application.dto.request.TranscriptionCallbackBatchRequest;
import com.hcmut.lms.coachingchatbot.application.dto.request.TranscriptionCallbackRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.TranscriptionCallbackResponse;

/**
 * Service for handling transcription callbacks from AWS Fargate workers.
 *
 * Flow:
 * 1. Receive transcription result from Fargate worker
 * 2. Save transcript to course-management-service
 * 3. Start transcript enrichment asynchronously
 */
public interface TranscriptionCallbackService {

    /**
     * Process transcription callback from Fargate worker (single segment - legacy)
     *
     * @param request Transcription callback request containing transcript data
     * @return TranscriptionCallbackResponse with processing status
     */
    TranscriptionCallbackResponse processTranscriptionCallback(TranscriptionCallbackRequest request);

    /**
     * Process transcription callback from Fargate worker (batch - all segments in one request)
     *
     * @param request Transcription callback batch request containing transcript data for all segments
     * @return TranscriptionCallbackResponse with processing status
     */
    TranscriptionCallbackResponse processTranscriptionCallbackBatch(TranscriptionCallbackBatchRequest request);
}
