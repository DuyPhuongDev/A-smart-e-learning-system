package com.hcmut.lms.learning.application.service;

import com.hcmut.lms.learning.application.util.AutoDeletingTempFile;

import java.util.UUID;

/**
 * Service for downloading videos from various sources (S3, YouTube)
 */
public interface VideoDownloadService {

    /**
     * Download video from S3 using pre-signed URL
     *
     * @param lectureId The lecture ID for file naming
     * @param presignedUrl The pre-signed S3 URL
     * @return AutoDeletingTempFile containing the downloaded video (auto-cleanup on close)
     */
    AutoDeletingTempFile downloadFromS3(UUID lectureId, String presignedUrl);

    /**
     * Download video from YouTube using yt-dlp
     *
     * @param lectureId The lecture ID for file naming
     * @param youtubeUrl The YouTube video URL
     * @return AutoDeletingTempFile containing the downloaded video (auto-cleanup on close)
     */
    AutoDeletingTempFile downloadFromYouTube(UUID lectureId, String youtubeUrl);

    /**
     * Download video based on source type
     *
     * @param lectureId The lecture ID
     * @param url The video URL
     * @param sourceType The source type (s3, youtube, direct)
     * @return AutoDeletingTempFile containing the downloaded video (auto-cleanup on close)
     */
    AutoDeletingTempFile downloadVideo(UUID lectureId, String url, String sourceType);
}
