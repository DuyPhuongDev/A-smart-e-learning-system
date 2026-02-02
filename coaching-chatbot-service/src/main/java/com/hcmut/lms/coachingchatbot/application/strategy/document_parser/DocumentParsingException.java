package com.hcmut.lms.coachingchatbot.application.strategy.document_parser;

import lombok.Getter;

/**
 * Exception thrown when document parsing fails
 */
@Getter
public class DocumentParsingException extends RuntimeException {

    private final String extractorName;
    private final String phase;

    public DocumentParsingException(String extractorName, String phase, String message) {
        super(String.format("[%s] %s failed: %s", extractorName, phase, message));
        this.extractorName = extractorName;
        this.phase = phase;
    }

    public DocumentParsingException(String extractorName, String phase, String message, Throwable cause) {
        super(String.format("[%s] %s failed: %s", extractorName, phase, message), cause);
        this.extractorName = extractorName;
        this.phase = phase;
    }

}
