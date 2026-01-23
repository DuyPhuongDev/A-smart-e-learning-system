package com.hcmut.lms.learning.config;

import com.assemblyai.api.AssemblyAI;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "assemblyai")
public class AssemblyAiConfig {

    // 1. Khai báo biến cấu hình
    private String apiKey;

    // 2. Tạo Bean Client
    @Bean
    public AssemblyAI assemblyAiClient() {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AssemblyAI API Key is missing. Speech-to-text features may fail.");
        }

        log.info("Initializing AssemblyAI Client...");

        // Build Client sử dụng Builder Pattern
        AssemblyAI client = AssemblyAI.builder()
                .apiKey(apiKey)
                .build();

        // Test kết nối không khả dụng trực tiếp như Qdrant,
        // nhưng client object được tạo ra rất nhẹ.
        log.info("Successfully initialized AssemblyAI Client.");

        return client;
    }
}