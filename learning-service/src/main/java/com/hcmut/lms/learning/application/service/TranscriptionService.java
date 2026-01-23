package com.hcmut.lms.learning.application.service;

import java.util.List;

/**
 * Service for transcribing audio to text using AssemblyAI
 */
public interface TranscriptionService {

    /**
     * Transcribe audio from URL to text
     *
     * @param audioUrl URL of the audio/video file
     * @return Transcribed text
     */
    String transcribe(String audioUrl);

    /**
     * Transcribe audio with language specification
     *
     * @param audioUrl URL of the audio/video file
     * @param languageCode Language code (e.g., "en", "vi")
     * @return Transcribed text
     */
    String transcribe(String audioUrl, String languageCode);

    /**
     * Get transcription with timestamps
     *
     * @param audioUrl URL of the audio/video file
     * @return Transcription result with timing information
     */
    TranscriptionResult transcribeWithTimestamps(String audioUrl);

    /**
     * Result object containing transcription with timestamps
     */
    record TranscriptionResult(
            String fullText,
            List<Utterance> utterances
    ) {
        public record Utterance(
                String text,
                long startMs,
                long endMs,
                double confidence
        ) {}
    }
}
