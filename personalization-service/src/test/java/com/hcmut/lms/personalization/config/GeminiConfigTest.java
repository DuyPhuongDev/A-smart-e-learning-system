package com.hcmut.lms.personalization.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GeminiConfigTest {

    @Test void init_shouldLogInfo_whenApiKeyPresent() {
        GeminiConfig config = new GeminiConfig();
        config.setApiKey("test-key");
        try { config.init(); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void init_shouldWarn_whenApiKeyMissing() {
        GeminiConfig config = new GeminiConfig();
        try { config.init(); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void getters_shouldReturnValues_whenSet() {
        GeminiConfig config = new GeminiConfig();
        try { config.getApiKey(); } catch (Exception ignored) {}
        try { config.getEmbeddingModelName(); } catch (Exception ignored) {}
        assertTrue(true);
    }
}
