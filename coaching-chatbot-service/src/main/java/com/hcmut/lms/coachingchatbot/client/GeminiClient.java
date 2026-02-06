package com.hcmut.lms.coachingchatbot.client;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.hcmut.lms.coachingchatbot.config.GeminiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * REST client for Google Gemini AI API
 * Uses a google-genai library with Gemini model
 * Handles text generation operations
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GeminiClient {

    private final GeminiConfig geminiConfig;

    private Client client;

    @PostConstruct
    public void init() {
        String apiKey = geminiConfig.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.error("Gemini API key is not configured! Set gemini.api-key in application.yml");
            throw new IllegalStateException("Gemini API key is required");
        }

        log.info("Initializing Gemini Client with model: {}", geminiConfig.getModelName());
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
        log.info("Gemini Client initialized successfully");
    }

    /**
     * Get the model name from config
     */
    public String getModelName() {
        return geminiConfig.getModelName();
    }

    /**
     * Get the temperature from config
     */
    public double getTemperature() {
        return geminiConfig.getTemperature();
    }

    /**
     * Get the max output tokens from config
     */
    public int getMaxOutputTokens() {
        return geminiConfig.getMaxOutputTokens();
    }

    /**
     * Generate text content using Gemini AI
     *
     * @param prompt The input prompt
     * @return Generated text response
     */
    public String generateContent(String prompt) {
        log.debug("Generating content with prompt length: {} characters", prompt.length());

        try {
            GenerateContentResponse response = client.models.generateContent(
                    geminiConfig.getModelName(), prompt, null);

            if (response == null || response.text() == null) {
                throw new RuntimeException("Empty response from Gemini API");
            }

            String result = response.text();
            log.debug("Content generated successfully, response length: {} characters", result.length());
            return result;

        } catch (Exception e) {
            log.error("Failed to generate content: {}", e.getMessage(), e);
            throw new RuntimeException("Gemini API call failed: " + e.getMessage(), e);
        }
    }

    /**
     * Generate text content with custom configuration
     *
     * @param prompt            The input prompt
     * @param customTemperature Custom temperature for this request
     * @param customMaxTokens   Custom max output tokens for this request
     * @return Generated text response
     */
    public String generateContent(String prompt, double customTemperature, int customMaxTokens) {
        log.debug("Generating content with custom config - temp: {}, maxTokens: {}",
                customTemperature, customMaxTokens);

        try {
            // Build config with custom parameters
            com.google.genai.types.GenerateContentConfig config = com.google.genai.types.GenerateContentConfig.builder()
                    .temperature((float) customTemperature)
                    .maxOutputTokens(customMaxTokens)
                    .build();

            GenerateContentResponse response = client.models.generateContent(
                    geminiConfig.getModelName(), prompt, config);

            if (response == null || response.text() == null) {
                throw new RuntimeException("Empty response from Gemini API");
            }

            String result = response.text();
            log.debug("Content generated successfully with custom config, response length: {} characters",
                    result.length());
            return result;

        } catch (Exception e) {
            log.error("Failed to generate content with custom config: {}", e.getMessage(), e);
            throw new RuntimeException("Gemini API call failed: " + e.getMessage(), e);
        }
    }

    /**
     * Chat method - simple wrapper for generateContent
     * Provides compatibility with existing code using chat() method
     *
     * @param message The chat message/prompt
     * @return Generated response text
     */
    public String chat(String message) {
        return generateContent(message);
    }

    /**
     * Check if the client is properly initialized
     *
     * @return true if client is ready
     */
    public boolean isReady() {
        String apiKey = geminiConfig.getApiKey();
        return client != null && apiKey != null && !apiKey.isBlank();
    }
}
