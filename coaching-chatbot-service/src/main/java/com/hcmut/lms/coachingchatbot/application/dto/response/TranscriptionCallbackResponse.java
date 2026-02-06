package com.hcmut.lms.coachingchatbot.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO for transcription callback
 * Sent back to AWS Fargate worker to confirm receipt and processing status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TranscriptionCallbackResponse {

    /**
     * Video lecture ID that was processed
     */
    private UUID lectureId;

    /**
     * Processing status: "SUCCESS" or "FAILED"
     */
    private String status;

    /**
     * Human-readable message about the processing result
     */
    private String message;

    /**
     * ID of the saved transcript (if successful)
     */
    private UUID transcriptId;

    /**
     * Whether transcript enrichment was started in background
     */
    private Boolean enrichmentStarted;
}
