package com.hcmut.lms.coachingchatbot.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

/**
 * Request DTO for transcription callback from AWS Fargate worker
 *
 * Matches the payload sent by callback.py in the Fargate worker:
 * {
 *   "videoLectureId": "uuid",
 *   "transcriptText": "full transcript",
 *   "languageCode": "en-US",
 *   "audioDuration": 300,
 *   "wordCount": 1500,
 *   "startTimeSeconds": 0,
 *   "endTimeSeconds": 300,
 *   "segmentIndex": 0
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TranscriptionCallbackRequest {

    /**
     * Video lecture ID (UUID)
     */
    @NotNull(message = "Video lecture ID is required")
    private UUID videoLectureId;

    /**
     * Full transcript text
     */
    @NotBlank(message = "Transcript text is required")
    private String transcriptText;

    /**
     * Language code (e.g., "en-US", "vi-VN")
     */
    private String languageCode;

    /**
     * Total audio duration in seconds
     */
    private Integer audioDuration;

    /**
     * Total word count
     */
    private Integer wordCount;

    /**
     * Start time of the transcript segment in seconds
     * Default: 0 for full transcript
     */
    @Builder.Default
    private Integer startTimeSeconds = 0;

    /**
     * End time of the transcript segment in seconds
     */
    private Integer endTimeSeconds;

    /**
     * Segment index (0 for full transcript)
     */
    @Builder.Default
    private Integer segmentIndex = 0;
}
