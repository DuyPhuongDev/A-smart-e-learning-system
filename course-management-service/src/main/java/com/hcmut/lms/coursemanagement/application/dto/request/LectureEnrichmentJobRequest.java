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
 * Unified request DTO for queueing lecture enrichment jobs
 * Handles VIDEO (transcription), DOCUMENT, and TEXT lectures
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureEnrichmentJobRequest {

    @NotNull(message = "Lecture IDs are required")
    @NotEmpty(message = "Lecture IDs cannot be empty")
    private List<UUID> lectureIds;

    /**
     * Optional lecture type filter: VIDEO, DOCUMENT, TEXT
     * If null, will process all types based on actual lecture type
     */
    private String lectureType;

    /**
     * Optional callback URL override
     * If not provided, will use default from configuration
     */
    private String callbackUrl;
}
