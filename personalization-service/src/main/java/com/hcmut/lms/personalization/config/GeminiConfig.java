package com.hcmut.lms.personalization.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "gemini")
public class  GeminiConfig {

    /** API key for Gemini */
    private String apiKey;

    /** Embedding model name (default Gemini embedding 001) */
    private String embeddingModelName = "models/gemini-embedding-001";

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API Key is missing. Embedding features may fail.");
        } else {
            log.info("Gemini configuration loaded. Embedding model: {}", embeddingModelName);
        }
    }
}
