package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.request.VideoTranscriptionJobRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.VideoTranscriptionJobResponse;
import com.hcmut.lms.coursemanagement.application.service.VideoTranscriptionQueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for video transcription queue operations
 * Provides API to queue video lectures for transcription processing by AWS Fargate workers
 */
@RestController
@RequestMapping("${prefix-api}/video-transcription")
@RequiredArgsConstructor
@Slf4j
public class VideoTranscriptionQueueController {

    private final VideoTranscriptionQueueService videoTranscriptionQueueService;

    /**
     * Queue video lectures for transcription processing by AWS Fargate workers
     *
     * POST /api/courses/v1/video-transcription/queue
     * Body: { "lectureIds": ["uuid1", "uuid2", ...] }
     *
     * @param request Contains list of lecture IDs to process
     * @return Response with queued and failed jobs
     */
    @PostMapping("/queue")
    public ResponseEntity<VideoTranscriptionJobResponse> queueTranscriptionJobs(
            @Valid @RequestBody VideoTranscriptionJobRequest request) {
        log.info("Received request to queue {} video transcription jobs", request.getLectureIds().size());

        VideoTranscriptionJobResponse response = videoTranscriptionQueueService.queueTranscriptionJobs(request);

        return ResponseEntity.ok(response);
    }
}
