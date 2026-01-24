package com.hcmut.lms.learning.application.service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Service for transcribing audio to text using AssemblyAI
 * Uses file upload approach for transcription
 */
public interface TranscriptionService {

    /**
     * Transcribe audio file to text
     * Uploads the file to AssemblyAI and returns the transcript
     *
     * @param audioFile The audio file to transcribe
     * @return Transcribed text
     */
    String transcribe(File audioFile);

    /**
     * Transcribe audio file to text
     *
     * @param audioFilePath Path to the audio file
     * @return Transcribed text
     */
    String transcribe(Path audioFilePath);

    /**
     * Transcribe audio file with language specification
     *
     * @param audioFile The audio file to transcribe
     * @param languageCode Language code (e.g., "en", "vi")
     * @return Transcribed text
     */
    String transcribe(File audioFile, String languageCode);

    /**
     * Get transcription with timestamps
     *
     * @param audioFile The audio file to transcribe
     * @return Transcription result with timing information
     */
    TranscriptionResult transcribeWithTimestamps(File audioFile);

    /**
     * Get transcription with timestamps
     *
     * @param audioFilePath Path to the audio file
     * @return Transcription result with timing information
     */
    TranscriptionResult transcribeWithTimestamps(Path audioFilePath);

    /**
     * Result object containing transcription with timestamps
     */
    record TranscriptionResult(
            String fullText,
            List<Utterance> utterances,
            Integer audioDurationSeconds,
            Integer wordCount
    ) {
        public record Utterance(
                String text,
                long startMs,
                long endMs,
                double confidence
        ) {}
    }
}
