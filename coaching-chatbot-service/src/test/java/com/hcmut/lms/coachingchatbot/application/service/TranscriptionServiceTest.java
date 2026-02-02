package com.hcmut.lms.coachingchatbot.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TranscriptionService Tests")
class TranscriptionServiceTest {

        @Test
        @DisplayName("Should verify TranscriptionResult record structure with all fields")
        void testTranscriptionResultRecord() {
                // Arrange
                String fullText = "Welcome to this lecture about machine learning.";
                var utterances = new java.util.ArrayList<TranscriptionService.TranscriptionResult.Utterance>();

                var utterance = new TranscriptionService.TranscriptionResult.Utterance(
                                "Welcome to this lecture",
                                0,
                                5000,
                                0.95);
                utterances.add(utterance);

                Integer audioDuration = 3600; // 1 hour
                Integer wordCount = 50;

                // Act
                var result = new TranscriptionService.TranscriptionResult(
                                fullText,
                                utterances,
                                audioDuration,
                                wordCount);

                // Assert
                assertNotNull(result);
                assertEquals("Welcome to this lecture about machine learning.", result.fullText());
                assertNotNull(result.utterances());
                assertEquals(1, result.utterances().size());
                assertEquals(3600, result.audioDurationSeconds());
                assertEquals(50, result.wordCount());

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
                                text, startMs, endMs, confidence);

                // Assert
                assertNotNull(utterance);
                assertEquals("Machine learning is powerful", utterance.text());
                assertEquals(5000, utterance.startMs());
                assertEquals(10000, utterance.endMs());
                assertEquals(0.92, utterance.confidence());
        }

        @Test
        @DisplayName("Should verify empty utterances list with metadata")
        void testEmptyUtterances() {
                // Arrange
                String fullText = "Test transcription";
                var utterances = new java.util.ArrayList<TranscriptionService.TranscriptionResult.Utterance>();
                Integer audioDuration = 120;
                Integer wordCount = 2;

                // Act
                var result = new TranscriptionService.TranscriptionResult(
                                fullText,
                                utterances,
                                audioDuration,
                                wordCount);

                // Assert
                assertNotNull(result);
                assertEquals("Test transcription", result.fullText());
                assertNotNull(result.utterances());
                assertEquals(0, result.utterances().size());
                assertEquals(120, result.audioDurationSeconds());
                assertEquals(2, result.wordCount());
        }

        @Test
        @DisplayName("Should verify multiple utterances with complete metadata")
        void testMultipleUtterances() {
                // Arrange
                String fullText = "First sentence. Second sentence. Third sentence.";
                var utterances = new java.util.ArrayList<TranscriptionService.TranscriptionResult.Utterance>();

                utterances.add(new TranscriptionService.TranscriptionResult.Utterance(
                                "First sentence", 0, 3000, 0.95));
                utterances.add(new TranscriptionService.TranscriptionResult.Utterance(
                                "Second sentence", 3000, 6000, 0.93));
                utterances.add(new TranscriptionService.TranscriptionResult.Utterance(
                                "Third sentence", 6000, 9000, 0.94));

                Integer audioDuration = 9; // 9 seconds
                Integer wordCount = 9; // 9 words

                // Act
                var result = new TranscriptionService.TranscriptionResult(
                                fullText,
                                utterances,
                                audioDuration,
                                wordCount);

                // Assert
                assertNotNull(result);
                assertEquals("First sentence. Second sentence. Third sentence.", result.fullText());
                assertEquals(3, result.utterances().size());
                assertEquals(9, result.audioDurationSeconds());
                assertEquals(9, result.wordCount());

                assertEquals("First sentence", result.utterances().get(0).text());
                assertEquals("Second sentence", result.utterances().get(1).text());
                assertEquals("Third sentence", result.utterances().get(2).text());
        }

        @Test
        @DisplayName("Should verify TranscriptionResult with null audio duration and word count")
        void testTranscriptionResultWithNullMetadata() {
                // Arrange
                String fullText = "Test content without metadata";
                var utterances = new java.util.ArrayList<TranscriptionService.TranscriptionResult.Utterance>();

                // Act
                var result = new TranscriptionService.TranscriptionResult(
                                fullText,
                                utterances,
                                null, // audioDurationSeconds
                                null // wordCount
                );

                // Assert
                assertNotNull(result);
                assertEquals("Test content without metadata", result.fullText());
                assertNull(result.audioDurationSeconds());
                assertNull(result.wordCount());
        }

        @Test
        @DisplayName("Should verify word count calculation from full text")
        void testWordCountCalculation() {
                // Arrange
                String fullText = "The quick brown fox jumps over the lazy dog";
                var utterances = new java.util.ArrayList<TranscriptionService.TranscriptionResult.Utterance>();
                int expectedWordCount = fullText.split("\\s+").length; // 9 words

                // Act
                var result = new TranscriptionService.TranscriptionResult(
                                fullText,
                                utterances,
                                30,
                                expectedWordCount);

                // Assert
                assertEquals(9, result.wordCount());
                assertEquals(expectedWordCount, result.wordCount());
        }

        @Test
        @DisplayName("Should verify long audio duration in seconds")
        void testLongAudioDuration() {
                // Arrange
                String fullText = "Extended lecture content";
                var utterances = new java.util.ArrayList<TranscriptionService.TranscriptionResult.Utterance>();
                Integer longDuration = 7200; // 2 hours in seconds

                // Act
                var result = new TranscriptionService.TranscriptionResult(
                                fullText,
                                utterances,
                                longDuration,
                                100);

                // Assert
                assertEquals(7200, result.audioDurationSeconds());
                assertEquals(2, result.audioDurationSeconds() / 3600); // Should be 2 hours
        }
}
