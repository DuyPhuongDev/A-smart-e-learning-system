package com.hcmut.lms.coachingchatbot.config;

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
 * Configuration for Document Processing
 * Manages temp directory and processing settings for PDF, DOCX, PPTX documents
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "document-processing")
public class DocumentProcessingConfig {

    /**
     * Temporary directory for downloaded documents
     * Default: /app/temp/documents (in Docker) or system temp directory
     */
    private String tempDir = "/app/temp/documents";

    /**
     * Timeout for downloading documents in seconds
     */
    private int downloadTimeoutSeconds = 300;

    /**
     * Target tokens per segment for chunking (~500 tokens)
     */
    private int targetTokensPerSegment = 500;

    /**
     * Minimum text length to consider extraction successful
     */
    private int minTextLengthForSuccess = 50;

    @PostConstruct
    public void init() {
        // Ensure temp directory exists
        Path tempPath = Paths.get(tempDir);
        try {
            if (!Files.exists(tempPath)) {
                Files.createDirectories(tempPath);
                log.info("Created document temp directory: {}", tempDir);
            }
        } catch (IOException e) {
            log.warn("Failed to create document temp directory {}, using system temp", tempDir);
            tempDir = System.getProperty("java.io.tmpdir") + "/documents";
            // Try to create fallback directory
            try {
                Files.createDirectories(Paths.get(tempDir));
            } catch (IOException ex) {
                log.error("Failed to create fallback temp directory: {}", tempDir);
            }
        }

        log.info("Document Processing configuration loaded successfully");
        log.info("Temp directory: {}", tempDir);
        log.info("Download timeout: {} seconds", downloadTimeoutSeconds);
        log.info("Target tokens per segment: {}", targetTokensPerSegment);
        log.info("Min text length for success: {}", minTextLengthForSuccess);
    }

    /**
     * Get temp directory path
     */
    public Path getTempDirPath() {
        return Paths.get(tempDir);
    }
}
