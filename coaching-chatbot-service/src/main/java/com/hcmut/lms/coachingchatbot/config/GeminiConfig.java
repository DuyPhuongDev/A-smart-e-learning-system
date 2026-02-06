package com.hcmut.lms.coachingchatbot.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuration for Google Gemini AI API
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "gemini")
public class GeminiConfig {

    /**
     * Gemini API Key
     */
    private String apiKey;

    /**
     * Model name for text generation (default: gemini-3-flash-preview)
     */
    private String modelName = "gemini-3-flash-preview";

    /**
     * Model name for embeddings (default: gemini-embedding-001 for 768-dim vectors)
     */
    private String embeddingModelName = "models/gemini-embedding-001";

    /**
     * Temperature for text generation (0.0 - 1.0)
     */
    private double temperature = 0.5;

    /**
     * Maximum output tokens for text generation
     */
    private int maxOutputTokens = 8192;

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API Key is missing. AI features may fail.");
        } else {
            log.info("Gemini configuration loaded successfully");
            log.info("Text generation model: {}", modelName);
            log.info("Embedding model: {}", embeddingModelName);
        }
    }
}
