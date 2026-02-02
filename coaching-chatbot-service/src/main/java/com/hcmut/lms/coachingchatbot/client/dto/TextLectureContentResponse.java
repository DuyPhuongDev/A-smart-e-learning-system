package com.hcmut.lms.coachingchatbot.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for receiving text lecture content from Course Management Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextLectureContentResponse {

    private UUID lectureId;

    /**
     * The text content of the lecture
     */
    private String content;

    /**
     * Word count of the content
     */
    private Integer wordCount;

    /**
     * Format type: "plain", "markdown", "html", etc.
     */
    private String formatType;

    /**
     * Lecture title
     */
    private String title;

    /**
     * Lecture description
     */
    private String description;
}
