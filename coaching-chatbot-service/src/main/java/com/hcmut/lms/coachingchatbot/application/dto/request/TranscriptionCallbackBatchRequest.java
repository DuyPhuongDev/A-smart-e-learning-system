package com.hcmut.lms.coachingchatbot.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for transcription callback from AWS Fargate worker (BATCH)
 * Contains all segments in a single request
 *
 * Payload format:
 * {
 *   "videoLectureId": "uuid",
 *   "languageCode": "en-US",
 *   "audioDuration": 300,
 *   "segments": [
 *     {
 *       "transcriptText": "First segment...",
 *       "startTimeSeconds": 0,
 *       "endTimeSeconds": 3,
 *       "wordCount": 5,
 *       "segmentIndex": 0
 *     },
 *     {
 *       "transcriptText": "Second segment...",
 *       "startTimeSeconds": 3,
 *       "endTimeSeconds": 6,
 *       "wordCount": 8,
 *       "segmentIndex": 1
 *     }
 *   ]
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TranscriptionCallbackBatchRequest {

    @NotNull(message = "Video lecture ID is required")
    private UUID videoLectureId;

    private String languageCode;

    private Integer audioDuration;

    @NotEmpty(message = "Segments list cannot be empty")
    @Valid
    private List<TranscriptSegment> segments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TranscriptSegment {

        @NotNull(message = "Transcript text is required")
        private String transcriptText;

        @NotNull(message = "Start time is required")
        private Integer startTimeSeconds;

        @NotNull(message = "End time is required")
        private Integer endTimeSeconds;

        private Integer wordCount;

        @NotNull(message = "Segment index is required")
        private Integer segmentIndex;
    }
}
