package com.hcmut.lms.learning.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating video transcript via Course Management Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoTranscriptRequest {

    private UUID videoLectureId;

    private String transcriptText;

    private String languageCode;

    private Integer audioDuration;

    private Integer wordCount;

    /**
     * Start time of the transcript segment in seconds
     */
    @Builder.Default
    private Integer startTimeSeconds = 0;

    /**
     * End time of the transcript segment in seconds
     */
    private Integer endTimeSeconds;

    /**
     * Segment index (0, 1, 2, ...)
     */
    private Integer segmentIndex;
}
