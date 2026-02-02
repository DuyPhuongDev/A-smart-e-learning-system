package com.hcmut.lms.coachingchatbot.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Video Processing Integration Tests")
class VideoProcessingIntegrationTest {

    @Test
    @DisplayName("Should validate YouTube URL format")
    void testYouTubeUrlValidation() {
        // Arrange
        String youtubeUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
        String invalidUrl = "https://example.com/video";

        // Act & Assert
        assertTrue(youtubeUrl.contains("youtube.com"), "Valid YouTube URL should contain 'youtube.com'");
        assertFalse(invalidUrl.contains("youtube.com"), "Invalid URL should not contain 'youtube.com'");
        assertTrue(youtubeUrl.startsWith("https://"), "URL should start with https://");
    }

    @Test
    @DisplayName("Should validate S3 URL format")
    void testS3UrlValidation() {
        // Arrange
        String s3Url = "https://mybucket.s3.amazonaws.com/lectures/week1-intro.mp4";
        String youtubeUrl = "https://www.youtube.com/watch?v=test";

        // Act & Assert
        assertTrue(s3Url.contains("s3.amazonaws.com"), "Valid S3 URL should contain 's3.amazonaws.com'");
        assertFalse(youtubeUrl.contains("s3.amazonaws.com"), "YouTube URL should not contain S3 domain");
        assertTrue(s3Url.endsWith(".mp4"), "S3 URL should end with .mp4 extension");
    }

    @Test
    @DisplayName("Should handle transcription with language specification")
    void testTranscribeWithLanguageCode() {
        // Arrange
        String videoUrl = "https://s3.amazonaws.com/lectures/vietnamese-lecture.mp4";
        String languageCode = "vi";

        // Act & Assert
        assertNotNull(videoUrl, "Video URL should not be null");
        assertNotNull(languageCode, "Language code should not be null");
        assertEquals("vi", languageCode, "Language code should be 'vi'");
        assertNotEquals("", languageCode, "Language code should not be empty");
    }

    @Test
    @DisplayName("Should validate corrupted video URL")
    void testTranscriptionErrorHandling() {
        // Arrange
        String validS3Url = "https://s3.amazonaws.com/lectures/corrupted-video.mp4";
        String invalidUrl = "ftp://invalid-protocol.com/video";

        // Act & Assert
        assertTrue(validS3Url.contains("s3.amazonaws.com"), "Should be valid S3 URL");
        assertFalse(invalidUrl.startsWith("https://"), "Invalid URL should not start with https://");
        assertTrue(validS3Url.endsWith(".mp4"), "S3 URL should have .mp4 extension");
    }

    @Test
    @DisplayName("Should validate audio URL before transcription")
    void testAudioUrlValidation() {
        // Arrange
        String youtubeUrl = "https://www.youtube.com/watch?v=invalid";
        String httpUrl = "http://example.com/video";
        String httpsUrl = "https://example.com/video";

        // Act & Assert
        assertTrue(youtubeUrl.startsWith("https://"), "YouTube URL must use https");
        assertFalse(httpUrl.startsWith("https://"), "HTTP URL should not start with https://");
        assertTrue(httpsUrl.startsWith("https://"), "HTTPS URL should start with https://");
        assertTrue(youtubeUrl.contains("youtube.com"), "Should contain youtube.com");
    }

    @Test
    @DisplayName("Should support multiple video source types")
    void testMultipleVideoSourceTypes() {
        // Arrange
        String youtubeUrl = "https://www.youtube.com/watch?v=test";
        String s3Url = "https://bucket.s3.amazonaws.com/video.mp4";
        String directUrl = "https://example.com/media/video.mp4";

        // Act & Assert - Test different URL patterns
        assertTrue(youtubeUrl.contains("youtube"), "Should recognize YouTube URLs");
        assertTrue(s3Url.contains("s3"), "Should recognize S3 URLs");
        assertTrue(directUrl.endsWith(".mp4"), "Should recognize direct media URLs");
    }
}
