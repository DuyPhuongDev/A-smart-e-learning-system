package com.hcmut.lms.learning.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for processing status
 * Contains current status and progress information for a lecture's processing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessingStatusResponse {

    private UUID lectureId;
    private UUID lectureKnowledgeId;
    private String lectureTitle;

    // Status information
    private String status;  // PENDING, PROCESSING, COMPLETED, FAILED, OUTDATED
    private String currentStep;  // Current processing step

    // Progress tracking
    private Integer totalChunks;
    private Integer processedChunks;
    private Integer failedChunks;
    private Double progressPercentage;

    // Timing information
    private Instant startedAt;
    private Instant completedAt;
    private Long processingTimeMs;

    // Error handling
    private String errorMessage;
    private String errorDetails;

    // Processing details
    private String embeddingModel;
    private Integer chunkSize;
    private Integer chunkOverlap;

    // Vector store information
    private String collectionName;
    private Integer vectorDimension;

    // Metadata
    private Instant createdAt;
    private Instant updatedAt;
}
