package com.hcmut.lms.learning.application.strategy.content_process;

import com.hcmut.lms.learning.application.dto.internal.ExtractedContent;
import com.hcmut.lms.learning.application.dto.internal.ProcessingContext;
import com.hcmut.lms.learning.domain.entity.lectureKnowledge.ContentType;

/**
 * Strategy interface for processing different types of lecture content
 * Each implementation handles a specific content type (video, document, text)
 */
public interface ContentProcessor {

    /**
     * Get the content types this processor can handle
     */
    ContentType[] getSupportedTypes();

    /**
     * Check if this processor supports the given content type
     */
    default boolean supports(ContentType contentType) {
        for (ContentType type : getSupportedTypes()) {
            if (type == contentType) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extract raw content from the source (video, document, or text)
     *
     * @param context Processing context containing source information
     * @return Extracted content with raw text
     * @throws ContentProcessingException if extraction fails
     */
    ExtractedContent extractContent(ProcessingContext context) throws ContentProcessingException;

    /**
     * Get processor name for logging
     */
    String getProcessorName();
}
