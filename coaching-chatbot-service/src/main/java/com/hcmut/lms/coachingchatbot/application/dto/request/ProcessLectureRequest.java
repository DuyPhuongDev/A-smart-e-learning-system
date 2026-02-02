package com.hcmut.lms.coachingchatbot.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for processing a lecture
 * Contains all necessary information for the document processing pipeline
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessLectureRequest {

    @NotNull(message = "Lecture ID is required")
    private UUID lectureId;

    @NotNull(message = "Lecture knowledge ID is required")
    private UUID lectureKnowledgeId;

    private String lectureTitle;
    private String lectureDescription;

    // Content type information
    private String contentType; // VIDEO, DOCUMENT, TEXT
    private String lectureType; // For VIDEO: YOUTUBE, S3_VIDEO; For DOCUMENT: PDF, SLIDE, WORD, etc.

    // Video specific
    private String videoUrl;
    private Integer videoDuration; // In seconds

    // Document specific
    private String fileUrl;
    private String fileFormat; // pdf, docx, pptx, etc.
    private Integer numPages;

    // Text specific
    private String textContent;
    private String formatType; // markdown, html, plain_text

    // Processing options
    @Builder.Default
    private boolean forceReprocess = false;

    @Builder.Default
    private String embeddingModel = "all-MiniLM-L6-v2";

    @Builder.Default
    private Integer chunkSize = 512;

    @Builder.Default
    private Integer chunkOverlap = 50;
}
