package com.hcmut.lms.learning.application.strategy.content_process;

/**
 * Custom exception for content processing errors
 */
public class ContentProcessingException extends RuntimeException {

    private final String processorName;
    private final String phase;

    public ContentProcessingException(String message) {
        super(message);
        this.processorName = "Unknown";
        this.phase = "Unknown";
    }

    public ContentProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.processorName = "Unknown";
        this.phase = "Unknown";
    }

    public ContentProcessingException(String processorName, String phase, String message) {
        super(String.format("[%s] %s: %s", processorName, phase, message));
        this.processorName = processorName;
        this.phase = phase;
    }

    public ContentProcessingException(String processorName, String phase, String message, Throwable cause) {
        super(String.format("[%s] %s: %s", processorName, phase, message), cause);
        this.processorName = processorName;
        this.phase = phase;
    }

    public String getProcessorName() {
        return processorName;
    }

    public String getPhase() {
        return phase;
    }
}
