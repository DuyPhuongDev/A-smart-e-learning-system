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
 * Request DTO for queueing video transcription jobs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoTranscriptionJobRequest {

    @NotNull(message = "Lecture IDs are required")
    @NotEmpty(message = "Lecture IDs cannot be empty")
    private List<UUID> lectureIds;

    /**
     * Callback URL for Fargate worker to send results
     * Default: will be auto-generated from service configuration
     */
    private String callbackUrl;
}
