package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO for text lecture content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
