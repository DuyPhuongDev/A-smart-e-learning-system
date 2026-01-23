package com.hcmut.lms.learning.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TranscriptionService Tests")
class TranscriptionServiceTest {

    @Test
    @DisplayName("Should verify TranscriptionResult record structure")
    void testTranscriptionResultRecord() {
        // Arrange
        String fullText = "Welcome to this lecture about machine learning.";
        var utterances = new java.util.ArrayList<TranscriptionService.TranscriptionResult.Utterance>();

        var utterance = new TranscriptionService.TranscriptionResult.Utterance(
                "Welcome to this lecture",
                0,
                5000,
                0.95
        );
        utterances.add(utterance);

        // Act
        var result = new TranscriptionService.TranscriptionResult(fullText, utterances);

        // Assert
        assertNotNull(result);
        assertEquals("Welcome to this lecture about machine learning.", result.fullText());
        assertNotNull(result.utterances());
        assertEquals(1, result.utterances().size());

        var firstUtterance = result.utterances().getFirst();
        assertEquals("Welcome to this lecture", firstUtterance.text());
        assertEquals(0, firstUtterance.startMs());
        assertEquals(5000, firstUtterance.endMs());
        assertEquals(0.95, firstUtterance.confidence());
    }

    @Test
    @DisplayName("Should verify Utterance record structure")
    void testUtteranceRecord() {
        // Arrange
        String text = "Machine learning is powerful";
        long startMs = 5000;
        long endMs = 10000;
        double confidence = 0.92;

        // Act
        var utterance = new TranscriptionService.TranscriptionResult.Utterance(
                text, startMs, endMs, confidence
        );

        // Assert
        assertNotNull(utterance);
        assertEquals("Machine learning is powerful", utterance.text());
        assertEquals(5000, utterance.startMs());
        assertEquals(10000, utterance.endMs());
        assertEquals(0.92, utterance.confidence());
    }

    @Test
    @DisplayName("Should verify empty utterances list")
    void testEmptyUtterances() {
        // Arrange
        String fullText = "Test transcription";
        var utterances = new java.util.ArrayList<TranscriptionService.TranscriptionResult.Utterance>();

        // Act
        var result = new TranscriptionService.TranscriptionResult(fullText, utterances);

        // Assert
        assertNotNull(result);
        assertEquals("Test transcription", result.fullText());
        assertNotNull(result.utterances());
        assertEquals(0, result.utterances().size());
    }

    @Test
    @DisplayName("Should verify multiple utterances")
    void testMultipleUtterances() {
        // Arrange
        String fullText = "First sentence. Second sentence. Third sentence.";
        var utterances = new java.util.ArrayList<TranscriptionService.TranscriptionResult.Utterance>();

        utterances.add(new TranscriptionService.TranscriptionResult.Utterance(
                "First sentence", 0, 3000, 0.95
        ));
        utterances.add(new TranscriptionService.TranscriptionResult.Utterance(
                "Second sentence", 3000, 6000, 0.93
        ));
        utterances.add(new TranscriptionService.TranscriptionResult.Utterance(
                "Third sentence", 6000, 9000, 0.94
        ));

        // Act
        var result = new TranscriptionService.TranscriptionResult(fullText, utterances);

        // Assert
        assertNotNull(result);
        assertEquals("First sentence. Second sentence. Third sentence.", result.fullText());
        assertEquals(3, result.utterances().size());

        assertEquals("First sentence", result.utterances().get(0).text());
        assertEquals("Second sentence", result.utterances().get(1).text());
        assertEquals("Third sentence", result.utterances().get(2).text());
    }
}
