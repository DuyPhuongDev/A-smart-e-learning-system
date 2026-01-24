package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.response.VideoDownloadUrlResponse;

import java.util.UUID;

/**
 * Service for video lecture operations
 * Provides download URL generation for S3 and YouTube videos
 */
public interface VideoLectureService {

    /**
     * Get download URL for a video lecture
     * For S3 videos: returns pre-signed URL
     * For YouTube videos: returns original URL
     *
     * @param lectureId The video lecture ID
     * @return VideoDownloadUrlResponse containing the download URL and source type
     */
    VideoDownloadUrlResponse getDownloadUrl(UUID lectureId);
}
