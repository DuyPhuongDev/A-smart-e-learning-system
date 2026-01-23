package com.hcmut.lms.learning.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for creating a lecture knowledge chunk
 * Represents a single chunk of processed content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLectureKnowledgeChunkRequest {

    @NotNull(message = "Lecture knowledge ID is required")
    private UUID lectureKnowledgeId;

    @NotNull(message = "Chunk index is required")
    private Integer chunkIndex;

    @NotBlank(message = "Chunk content is required")
    private String chunkContent;

    private String chunkType;  // e.g., "paragraph", "section", "heading"

    private Integer tokenCount;

    // Location tracking - Video (Transcript)
    private Integer startTimeSeconds;
    private Integer endTimeSeconds;

    // Location tracking - Document (PDF/Slide/Doc)
    private Integer pageNumber;

    // Metadata for the chunk
    private Map<String, Object> metadata;

    // Embedding vector (optional, may be generated server-side)
    private String embeddingModel;
}

