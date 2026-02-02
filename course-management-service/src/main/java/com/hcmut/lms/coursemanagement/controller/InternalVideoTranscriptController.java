package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.request.VideoTranscriptRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.VideoTranscriptResponse;
import com.hcmut.lms.coursemanagement.application.service.VideoTranscriptSegmentService;
import com.hcmut.lms.coursemanagement.application.service.VideoTranscriptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Internal controller for service-to-service communication
 * These endpoints should NOT be exposed through API Gateway
 */
@RestController
@RequestMapping("${prefix-api}/internal/video-transcripts")
@RequiredArgsConstructor
public class InternalVideoTranscriptController {

    private final VideoTranscriptService videoTranscriptService;
    private final VideoTranscriptSegmentService videoTranscriptSegmentService;

    /**
     * Create video transcript
     * Used by learning-service to create transcript after AI processing
     */
    @PostMapping
    public VideoTranscriptResponse createTranscript(@Valid @RequestBody VideoTranscriptRequest request) {
        return videoTranscriptService.createTranscript(request);
    }

    /**
     * Create multiple video transcripts (batch)
     * Used by learning-service to create transcript segments after AI processing
     */
    @PostMapping("/batch")
    public List<VideoTranscriptResponse> createTranscripts(@Valid @RequestBody List<VideoTranscriptRequest> requests) {
        return videoTranscriptService.createTranscripts(requests);
    }

    /**
     * Check if transcript exists for a video lecture
     * Used by learning-service to check before processing
     */
    @GetMapping("/video-lecture/{videoLectureId}/exists")
    public Boolean existsTranscript(@PathVariable UUID videoLectureId) {
        return videoTranscriptService.existsByVideoLectureId(videoLectureId);
    }

    /**
     * Get all transcript segments for a video lecture, ordered by segment index
     * Used by learning-service to fetch transcripts for enrichment processing
     */
    @GetMapping("/video-lecture/{videoLectureId}/segments")
    public List<VideoTranscriptResponse> getTranscriptSegments(@PathVariable UUID videoLectureId) {
        return videoTranscriptSegmentService.getTranscriptSegments(videoLectureId);
    }
}
