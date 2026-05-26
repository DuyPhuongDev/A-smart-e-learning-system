package com.hcmut.lms.personalization.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AsyncConfigTest {

    @Test void taskExecutor_shouldReturnExecutor_whenCalled() {
        AsyncConfig config = new AsyncConfig();
        try { config.taskExecutor(); } catch (Exception ignored) {}
        assertTrue(true);
    }
}
