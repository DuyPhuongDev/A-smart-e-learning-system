package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for queueing document/text lecture enrichment jobs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEnrichmentJobRequest {

    @NotNull(message = "Lecture IDs are required")
    @NotEmpty(message = "Lecture IDs cannot be empty")
    private List<UUID> lectureIds;

    /**
     * Lecture type filter: DOCUMENT, TEXT, or null for both
     */
    private String lectureType;

    /**
     * Callback URL for Fargate worker to send results
     * Default: will be auto-generated from service configuration
     */
    private String callbackUrl;
}
