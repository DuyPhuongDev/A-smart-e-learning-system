package com.hcmut.lms.learning.application.service.impl;

import com.hcmut.lms.learning.application.service.TranscriptionService;
import com.hcmut.lms.learning.client.AssemblyAiClient;
import com.hcmut.lms.learning.client.dto.assemblyai.AssemblyAiWord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptionServiceImpl implements TranscriptionService {

    private static final long SEGMENT_DURATION_MS = 3000; // 3 seconds per segment

    private final AssemblyAiClient assemblyAiClient;

    @Override
    public String transcribe(File audioFile) {
        return transcribe(audioFile, null);
    }

    @Override
    public String transcribe(Path audioFilePath) {
        return transcribe(audioFilePath.toFile(), null);
    }

    @Override
    public String transcribe(File audioFile, String languageCode) {
        log.info("Starting transcription for file: {}", audioFile.getName());

        var response = assemblyAiClient.uploadAndTranscribe(audioFile, languageCode);
        String text = response.getText() != null ? response.getText() : "";

        log.info("Transcription completed. Language: {}, Length: {} chars",
                response.getLanguageCode(), text.length());
        return text;
    }

    @Override
    public TranscriptionResult transcribeWithTimestamps(File audioFile) {
        log.info("Starting transcription with timestamps for file: {}", audioFile.getName());

        var response = assemblyAiClient.uploadAndTranscribe(audioFile, null);

        String fullText = response.getText() != null ? response.getText() : "";

        // Debug: log words count
        int wordsFromApi = response.getWords() != null ? response.getWords().size() : 0;
        log.info("Received {} words from AssemblyAI API", wordsFromApi);

        List<TranscriptionResult.Utterance> segments = groupWordsIntoSegments(response.getWords());
        int wordCount = fullText.isEmpty() ? 0 : fullText.split("\\s+").length;

        log.info("Transcription completed. Language: {}, Words from API: {}, Segments created: {}, Duration: {}s",
                response.getLanguageCode(), wordsFromApi, segments.size(), response.getAudioDuration());

        return new TranscriptionResult(fullText, segments, response.getAudioDuration(), wordCount);
    }

    @Override
    public TranscriptionResult transcribeWithTimestamps(Path audioFilePath) {
        return transcribeWithTimestamps(audioFilePath.toFile());
    }

    /**
     * Group words into segments of approximately SEGMENT_DURATION_MS (3 seconds).
     * Tries to end segments at sentence boundaries (. ! ?) when possible.
     */
    private List<TranscriptionResult.Utterance> groupWordsIntoSegments(List<AssemblyAiWord> words) {
        if (words == null || words.isEmpty()) {
            return Collections.emptyList();
        }

        List<TranscriptionResult.Utterance> segments = new ArrayList<>();
        StringBuilder segmentText = new StringBuilder();
        Long segmentStart = null;
        Long segmentEnd = null;
        double totalConfidence = 0;
        int wordCount = 0;

        for (AssemblyAiWord word : words) {
            if (segmentStart == null) {
                segmentStart = word.getStart();
            }

            if (!segmentText.isEmpty()) {
                segmentText.append(" ");
            }
            segmentText.append(word.getText());
            segmentEnd = word.getEnd();
            totalConfidence += word.getConfidence() != null ? word.getConfidence() : 0.0;
            wordCount++;

            long currentDuration = segmentEnd - segmentStart;
            boolean isSentenceEnd = isSentenceEnd(word.getText());

            // Create segment if: duration >= 3s AND at sentence end, OR duration >= 5s (force split)
            if ((currentDuration >= SEGMENT_DURATION_MS && isSentenceEnd) || currentDuration >= SEGMENT_DURATION_MS * 2) {
                segments.add(createUtterance(segmentText, segmentStart, segmentEnd, totalConfidence, wordCount));

                // Reset for next segment
                segmentText.setLength(0);
                segmentStart = null;
                totalConfidence = 0;
                wordCount = 0;
            }
        }

        // Add remaining words as final segment
        if (!segmentText.isEmpty() && segmentStart != null) {
            segments.add(createUtterance(segmentText, segmentStart, segmentEnd, totalConfidence, wordCount));
        }

        return segments;
    }

    private TranscriptionResult.Utterance createUtterance(StringBuilder text, Long start, Long end,
                                                          double totalConfidence, int wordCount) {
        return new TranscriptionResult.Utterance(
                text.toString().trim(),
                start,
                end,
                wordCount > 0 ? totalConfidence / wordCount : 0.0
        );
    }

    private boolean isSentenceEnd(String text) {
        return text != null && text.matches(".*[.!?]$");
    }
}
