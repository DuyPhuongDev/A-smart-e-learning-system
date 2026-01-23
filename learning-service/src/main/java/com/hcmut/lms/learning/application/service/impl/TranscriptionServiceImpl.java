package com.hcmut.lms.learning.application.service.impl;

import com.assemblyai.api.AssemblyAI;
import com.assemblyai.api.resources.transcripts.types.Transcript;
import com.assemblyai.api.resources.transcripts.types.TranscriptOptionalParams;
import com.assemblyai.api.resources.transcripts.types.TranscriptStatus;
import com.assemblyai.api.resources.transcripts.types.TranscriptUtterance;
import com.hcmut.lms.learning.application.service.TranscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of TranscriptionService using AssemblyAI
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptionServiceImpl implements TranscriptionService {

    private final AssemblyAI assemblyAI;

    @Override
    public String transcribe(String audioUrl) {
        return transcribe(audioUrl, null);
    }

    @Override
    public String transcribe(String audioUrl, String languageCode) {
        log.info("Starting transcription for URL: {}", audioUrl);

        try {
            // Build transcript params
            TranscriptOptionalParams.Builder paramsBuilder = TranscriptOptionalParams.builder();

            if (languageCode != null && !languageCode.isBlank()) {
                // Note: AssemblyAI uses specific language codes
                // "en" for English, "vi" for Vietnamese, etc.
                log.info("Using language code: {}", languageCode);
            }

            // Submit transcription request
            Transcript transcript = assemblyAI.transcripts().transcribe(audioUrl, paramsBuilder.build());

            // Check status
            if (transcript.getStatus() == TranscriptStatus.ERROR) {
                String error = transcript.getError().orElse("Unknown error");
                log.error("Transcription failed: {}", error);
                throw new RuntimeException("Transcription failed: " + error);
            }

            // Get the text
            String text = transcript.getText().orElse("");
            log.info("Transcription completed successfully, length: {} characters", text.length());

            return text;

        } catch (Exception e) {
            log.error("Failed to transcribe audio: {}", e.getMessage(), e);
            throw new RuntimeException("Transcription failed: " + e.getMessage(), e);
        }
    }

    @Override
    public TranscriptionResult transcribeWithTimestamps(String audioUrl) {
        log.info("Starting transcription with timestamps for URL: {}", audioUrl);

        try {
            // Enable utterances for timestamp data
            TranscriptOptionalParams params = TranscriptOptionalParams.builder()
                    .build();

            Transcript transcript = assemblyAI.transcripts().transcribe(audioUrl, params);

            if (transcript.getStatus() == TranscriptStatus.ERROR) {
                String error = transcript.getError().orElse("Unknown error");
                log.error("Transcription failed: {}", error);
                throw new RuntimeException("Transcription failed: " + error);
            }

            String fullText = transcript.getText().orElse("");
            List<TranscriptionResult.Utterance> utterances = new ArrayList<>();

            // Extract utterances if available
            if (transcript.getUtterances().isPresent()) {
                for (TranscriptUtterance u : transcript.getUtterances().get()) {
                    utterances.add(new TranscriptionResult.Utterance(
                            u.getText(),
                            u.getStart(),
                            u.getEnd(),
                            u.getConfidence()
                    ));
                }
            }

            log.info("Transcription with timestamps completed, {} utterances", utterances.size());
            return new TranscriptionResult(fullText, utterances);

        } catch (Exception e) {
            log.error("Failed to transcribe audio with timestamps: {}", e.getMessage(), e);
            throw new RuntimeException("Transcription with timestamps failed: " + e.getMessage(), e);
        }
    }
}
