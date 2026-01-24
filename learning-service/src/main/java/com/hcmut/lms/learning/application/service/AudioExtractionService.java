package com.hcmut.lms.learning.application.service;

import com.hcmut.lms.learning.application.util.AutoDeletingTempFile;

import java.nio.file.Path;

/**
 * Service for extracting audio from video files using FFmpeg
 */
public interface AudioExtractionService {

    /**
     * Extract audio from a video file using FFmpeg
     *
     * @param videoFilePath Path to the video file
     * @return AutoDeletingTempFile containing the extracted audio (MP3 format, auto-cleanup on close)
     */
    AutoDeletingTempFile extractAudio(Path videoFilePath);

    /**
     * Extract audio with custom output format
     *
     * @param videoFilePath Path to the video file
     * @param outputFormat Output format (e.g., "mp3", "wav", "m4a")
     * @return AutoDeletingTempFile containing the extracted audio (auto-cleanup on close)
     */
    AutoDeletingTempFile extractAudio(Path videoFilePath, String outputFormat);

    /**
     * Check if the video file is supported for audio extraction
     *
     * @param videoFilePath Path to the video file
     * @return true if supported
     */
    boolean isSupported(Path videoFilePath);
}
