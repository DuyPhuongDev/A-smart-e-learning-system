package com.hcmut.lms.coachingchatbot.application.service.impl;

import com.hcmut.lms.coachingchatbot.application.dto.request.TranscriptionCallbackRequest;
import com.hcmut.lms.coachingchatbot.application.dto.request.TranscriptionCallbackBatchRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.TranscriptionCallbackResponse;
import com.hcmut.lms.coachingchatbot.application.mapper.TranscriptionCallbackMapper;
import com.hcmut.lms.coachingchatbot.application.service.TranscriptionCallbackService;
import com.hcmut.lms.coachingchatbot.application.service.TranscriptEnrichmentService;
import com.hcmut.lms.coachingchatbot.client.CourseManagementClient;
import com.hcmut.lms.coachingchatbot.client.dto.VideoTranscriptRequest;
import com.hcmut.lms.coachingchatbot.client.dto.VideoTranscriptResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of TranscriptionCallbackService
 * Handles callbacks from AWS Fargate worker after transcription is complete.
 *
 * Flow:
 * 1. Receive EACH SEGMENT from Fargate worker (already grouped with word-level timestamps)
 * 2. Save segment directly to course-management-service
 * 3. Trigger enrichment (idempotent - can be called multiple times)
 *
 * Note: Fargate worker groups words into segments (~3s) using same logic as TranscriptionServiceImpl
 * and sends MULTIPLE callback requests - one per segment.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptionCallbackServiceImpl implements TranscriptionCallbackService {

    private final CourseManagementClient courseManagementClient;
    private final TranscriptEnrichmentService transcriptEnrichmentService;
    private final TranscriptionCallbackMapper transcriptionCallbackMapper;

    @Override
    public TranscriptionCallbackResponse processTranscriptionCallback(TranscriptionCallbackRequest request) {
        UUID lectureId = request.getVideoLectureId();
        int segmentIndex = request.getSegmentIndex() != null ? request.getSegmentIndex() : 0;

        log.info("Processing transcription callback for lecture: {}, segment: {}, time: {}s-{}s",
                lectureId, segmentIndex, request.getStartTimeSeconds(), request.getEndTimeSeconds());

        try {
            // Save transcript segment
            VideoTranscriptRequest transcriptRequest = transcriptionCallbackMapper.toVideoTranscriptRequest(request);

            VideoTranscriptResponse transcriptResponse = courseManagementClient.createVideoTranscript(transcriptRequest);
            log.info("Saved segment {} for lecture {}, transcriptId: {}", segmentIndex, lectureId, transcriptResponse.getId());

            // Trigger enrichment asynchronously (idempotent - will only start when all segments are saved)
            triggerEnrichmentAsync(lectureId);

            return buildSuccessResponse(lectureId, segmentIndex, request.getStartTimeSeconds(),
                    request.getEndTimeSeconds(), transcriptResponse.getId());

        } catch (Exception e) {
            log.error("Failed to process transcription callback for lecture {}, segment {}: {}",
                    lectureId, segmentIndex, e.getMessage(), e);
            return buildFailureResponse(lectureId, e.getMessage());
        }
    }

    @Override
    public TranscriptionCallbackResponse processTranscriptionCallbackBatch(TranscriptionCallbackBatchRequest request) {
        UUID lectureId = request.getVideoLectureId();
        int segmentCount = request.getSegments() != null ? request.getSegments().size() : 0;

        log.info("Processing batch transcription callback for lecture: {}, segments: {}", lectureId, segmentCount);

        try {
            // Build list of transcript requests for batch save
            List<VideoTranscriptRequest> transcriptRequests = request.getSegments().stream()
                    .map(segment -> transcriptionCallbackMapper.toVideoTranscriptRequest(request, segment))
                    .toList();

            // Save all segments in one batch call
            List<VideoTranscriptResponse> savedSegments = courseManagementClient.createVideoTranscripts(transcriptRequests);
            log.info("Successfully saved {} segments for lecture: {} in batch", savedSegments.size(), lectureId);

            // Trigger enrichment asynchronously
            triggerEnrichmentAsync(lectureId);

            return TranscriptionCallbackResponse.builder()
                    .lectureId(lectureId)
                    .status("SUCCESS")
                    .message(String.format("Saved %d transcript segments and started enrichment", savedSegments.size()))
                    .transcriptId(!savedSegments.isEmpty() ? savedSegments.getFirst().getId() : null)
                    .enrichmentStarted(true)
                    .build();

        } catch (Exception e) {
            log.error("Failed to process batch transcription callback for lecture {}: {}", lectureId, e.getMessage(), e);
            return buildFailureResponse(lectureId, e.getMessage());
        }
    }


    /**
     * Trigger enrichment asynchronously (idempotent - will only start when all segments are saved)
     */
    private void triggerEnrichmentAsync(UUID lectureId) {
        log.info("Triggering transcript enrichment for lecture: {}", lectureId);
        transcriptEnrichmentService.enrichTranscriptsForLectureAsync(lectureId)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        log.error("Transcript enrichment failed for lecture {}: {}", lectureId, error.getMessage());
                    } else {
                        log.info("Transcript enrichment completed for lecture {}: status={}, chunks={}",
                                lectureId, result.status(), result.totalChunks());
                    }
                });
    }

    /**
     * Build success response for single segment
     */
    private TranscriptionCallbackResponse buildSuccessResponse(UUID lectureId, int segmentIndex,
                                                               Integer startTime, Integer endTime, UUID transcriptId) {
        return TranscriptionCallbackResponse.builder()
                .lectureId(lectureId)
                .status("SUCCESS")
                .message(String.format("Saved segment %d with timestamps %ds-%ds", segmentIndex, startTime, endTime))
                .transcriptId(transcriptId)
                .enrichmentStarted(true)
                .build();
    }

    /**
     * Build failure response
     */
    private TranscriptionCallbackResponse buildFailureResponse(UUID lectureId, String errorMessage) {
        return TranscriptionCallbackResponse.builder()
                .lectureId(lectureId)
                .status("FAILED")
                .message("Failed to process segments: " + errorMessage)
                .transcriptId(null)
                .enrichmentStarted(false)
                .build();
    }
}
