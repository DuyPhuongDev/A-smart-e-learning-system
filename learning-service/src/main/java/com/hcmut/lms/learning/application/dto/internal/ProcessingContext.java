package com.hcmut.lms.learning.application.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Internal DTO for processing context passed through the pipeline
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingContext {

    private UUID lectureId;
    private UUID lectureKnowledgeId;
    private String lectureTitle;
    private String lectureDescription;
    private String lectureType;
    private String contentType;

    // Video specific
    private String videoUrl;
    private Integer videoDuration;

    // Document specific
    private String fileUrl;
    private String fileFormat;
    private Integer numPages;

    // Text specific
    private String textContent;
    private String formatType;

    // Processing state
    private String currentStatus;
    private long startTime;
    private String errorMessage;
    private Map<String, Object> additionalMetadata;
}
