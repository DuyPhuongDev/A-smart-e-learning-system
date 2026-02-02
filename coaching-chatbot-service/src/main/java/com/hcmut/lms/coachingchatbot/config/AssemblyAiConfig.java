package com.hcmut.lms.coachingchatbot.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "assemblyai")
public class AssemblyAiConfig {

    // Khai báo biến cấu hình
    private String apiKey;

    /**
     * RestTemplate bean configured for AssemblyAI API calls
     * With extended timeouts for file upload and transcription polling
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AssemblyAI API Key is missing. Speech-to-text features may fail.");
        }

        log.info("Initializing RestTemplate for AssemblyAI API...");

        RestTemplate restTemplate = builder
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofMinutes(10)) // Extended for large file uploads
                .build();

        log.info("Successfully initialized RestTemplate for AssemblyAI.");

        return restTemplate;
    }
}