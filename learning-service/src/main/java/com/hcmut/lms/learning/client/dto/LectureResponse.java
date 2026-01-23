package com.hcmut.lms.learning.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for receiving lecture information from Course Management Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LectureResponse {
    private UUID id;
    private Integer orderIndex;
    private String title;
    private String description;
    private Boolean isMandatory;
    private String lectureType;
    private Float completionRate;
    private Integer viewCount;
    private Boolean allowPreview;
    private Boolean isDownloadable;
    private UUID chapterId;
    private Instant createdAt;
    private Instant updatedAt;

    // For video lectures
    private String videoUrl;
    private Integer duration;

    // For document lectures
    private String fileUrl;
    private Integer numPages;
    private String fileFormat;

    // For text lectures
    private String content;
    private Integer wordCount;
    private String formatType;
}
