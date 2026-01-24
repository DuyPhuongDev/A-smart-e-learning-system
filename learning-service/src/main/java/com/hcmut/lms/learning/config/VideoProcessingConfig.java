package com.hcmut.lms.learning.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration for video processing
 * Manages temp directory, FFmpeg and yt-dlp settings
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "video-processing")
public class VideoProcessingConfig {

    /**
     * Temporary directory for video processing
     * Default: /app/temp (in Docker) or system temp directory
     */
    private String tempDir = "/app/temp";

    /**
     * Download timeout in seconds
     */
    private int downloadTimeoutSeconds = 300;

    /**
     * FFmpeg timeout in seconds
     */
    private int ffmpegTimeoutSeconds = 600;

    private String ffmpegPath = "ffmpeg";
    private String ytDlpPath = "yt-dlp";

    @PostConstruct
    public void init() {
        // Ensure temp directory exists
        Path tempPath = Paths.get(tempDir);
        try {
            if (!Files.exists(tempPath)) {
                Files.createDirectories(tempPath);
                log.info("Created temp directory: {}", tempDir);
            }
        } catch (IOException e) {
            log.warn("Failed to create temp directory {}, using system temp", tempDir);
            tempDir = System.getProperty("java.io.tmpdir");
        }

        // Verify FFmpeg is available (respect configured path)
        if (isExecutableAvailable(ffmpegPath)) {
            log.info("FFmpeg found at configured path: {}", ffmpegPath);
        } else if (isCommandAvailable("ffmpeg")) {
            log.info("FFmpeg is available in system PATH");
        } else {
            log.warn("FFmpeg not found at configured path {} nor in system PATH. Video processing may fail.", ffmpegPath);
        }

        // Verify yt-dlp is available (respect configured path)
        if (isExecutableAvailable(ytDlpPath)) {
            log.info("yt-dlp found at configured path: {}", ytDlpPath);
        } else if (isCommandAvailable("yt-dlp")) {
            log.info("yt-dlp is available in system PATH");
        } else {
            log.warn("yt-dlp not found at configured path {} nor in system PATH. YouTube download may fail.", ytDlpPath);
        }
    }

    /**
     * Check if a command is available in system PATH
     */
    private boolean isCommandAvailable(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder();
            // Use "where" on Windows, "which" on Unix
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("win")) {
                pb.command("where", command);
            } else {
                pb.command("which", command);
            }
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isExecutableAvailable(String pathString) {
        Path path = Paths.get(pathString);
        return Files.exists(path) && Files.isRegularFile(path);
    }

    /**
     * Get temp directory path
     */
    public Path getTempDirPath() {
        return Paths.get(tempDir);
    }
}
