package com.hcmut.lms.learning.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for lecture knowledge chunk
 * Represents a single chunk of processed content from a lecture
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LectureKnowledgeChunkResponse {

    private UUID id;
    private UUID lectureKnowledgeId;
    private Integer chunkIndex;

    private String chunkContent;
    private String chunkType;

    // Vector store reference
    private UUID qdrantPointId;

    // Location tracking
    private Integer startTimeSeconds;
    private Integer endTimeSeconds;
    private Integer pageNumber;

    // Content metrics
    private Integer tokenCount;
    private Integer contentLength;

    // Embedding information
    private List<Float> embedding;
    private String embeddingModel;

    // Metadata
    private Map<String, Object> metadata;

    // Timestamps
    private Instant createdAt;
}
