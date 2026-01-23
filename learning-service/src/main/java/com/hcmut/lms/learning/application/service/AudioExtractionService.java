package com.hcmut.lms.learning.application.service;

/**
 * Service for extracting audio from video sources (YouTube, S3)
 */
public interface AudioExtractionService {

    /**
     * Get audio URL from video URL
     * For YouTube: returns the original URL (AssemblyAI handles extraction)
     * For S3: returns direct URL or extracts audio file
     *
     * @param videoUrl The video URL
     * @return Audio URL that can be used for transcription
     */
    String getAudioUrl(String videoUrl);

    /**
     * Check if the video source is supported
     *
     * @param videoUrl The video URL
     * @return true if supported
     */
    boolean isSupported(String videoUrl);
}
