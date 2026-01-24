package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.response.VideoDownloadUrlResponse;
import com.hcmut.lms.coursemanagement.application.service.VideoLectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Internal controller for service-to-service communication
 * These endpoints should NOT be exposed through API Gateway
 */
@RestController
@RequestMapping("${prefix-api}/internal/video-lectures")
@RequiredArgsConstructor
public class InternalVideoLectureController {

    private final VideoLectureService videoLectureService;

    /**
     * Get download URL for a video lecture
     * For S3 videos: returns pre-signed URL
     * For YouTube videos: returns original URL
     * Used by learning-service to get video download URL
     */
    @GetMapping("/{id}/download-url")
    public VideoDownloadUrlResponse getDownloadUrl(@PathVariable UUID id) {
        return videoLectureService.getDownloadUrl(id);
    }
}
