package com.hcmut.lms.learning.application.strategy;

import com.hcmut.lms.learning.domain.entity.lectureKnowledge.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory for selecting appropriate content processor based on content type
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContentProcessorFactory {

    private final List<ContentProcessor> processors;

    /**
     * Get the appropriate processor for the given content type
     *
     * @param contentType The type of content to process
     * @return The appropriate content processor
     * @throws IllegalArgumentException if no processor found for the content type
     */
    public ContentProcessor getProcessor(ContentType contentType) {
        log.debug("Finding processor for content type: {}", contentType);

        return processors.stream()
                .filter(processor -> processor.supports(contentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No processor found for content type: " + contentType));
    }

    /**
     * Get the appropriate processor for the given content type string
     *
     * @param lectureType The lecture type (VIDEO, DOCUMENT, TEXT)
     * @param videoUrl The video URL (for determining YouTube vs S3)
     * @param fileFormat The file format (for documents)
     * @return The appropriate content processor
     */
    public ContentProcessor getProcessor(String lectureType, String videoUrl, String fileFormat) {
        ContentType contentType = ContentType.fromLectureType(lectureType, videoUrl, fileFormat);
        return getProcessor(contentType);
    }

    /**
     * Check if a processor exists for the given content type
     */
    public boolean hasProcessor(ContentType contentType) {
        return processors.stream().anyMatch(processor -> processor.supports(contentType));
    }
}
