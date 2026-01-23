package com.hcmut.lms.learning.application.service.impl;

import com.hcmut.lms.learning.application.service.AudioExtractionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of AudioExtractionService
 * Handles audio extraction from YouTube and S3 video sources
 */
@Service
@Slf4j
public class AudioExtractionServiceImpl implements AudioExtractionService {

    @Override
    public String getAudioUrl(String videoUrl) {
        log.info("Getting audio URL for video: {}", videoUrl);

        if (videoUrl == null || videoUrl.isBlank()) {
            throw new IllegalArgumentException("Video URL cannot be null or empty");
        }

        // For YouTube videos, AssemblyAI can directly process the URL
        if (isYouTubeUrl(videoUrl)) {
            log.info("YouTube URL detected, returning original URL for AssemblyAI processing");
            return videoUrl;
        }

        // For S3 or other sources, return the direct URL
        // AssemblyAI supports direct URLs to audio/video files
        if (isS3Url(videoUrl)) {
            log.info("S3 URL detected, returning direct URL");
            return videoUrl;
        }

        // For other URLs, assume they're direct video/audio links
        log.info("Direct URL detected, returning as-is");
        return videoUrl;
    }

    @Override
    public boolean isSupported(String videoUrl) {
        if (videoUrl == null || videoUrl.isBlank()) {
            return false;
        }

        return isYouTubeUrl(videoUrl) || isS3Url(videoUrl) || isDirectMediaUrl(videoUrl);
    }

    /**
     * Check if URL is a YouTube URL
     */
    private boolean isYouTubeUrl(String url) {
        return url.contains("youtube.com") || url.contains("youtu.be");
    }

    /**
     * Check if URL is an S3 URL
     */
    private boolean isS3Url(String url) {
        return url.contains("s3.amazonaws.com") ||
               url.contains("s3-") ||
               url.matches(".*\\.s3\\.[a-z0-9-]+\\.amazonaws\\.com.*");
    }

    /**
     * Check if URL points to direct media file
     */
    private boolean isDirectMediaUrl(String url) {
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".mp4") ||
               lowerUrl.endsWith(".mp3") ||
               lowerUrl.endsWith(".wav") ||
               lowerUrl.endsWith(".m4a") ||
               lowerUrl.endsWith(".webm") ||
               lowerUrl.endsWith(".ogg");
    }
}
