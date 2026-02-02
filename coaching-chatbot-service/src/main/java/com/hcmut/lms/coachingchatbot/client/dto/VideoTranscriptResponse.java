package com.hcmut.lms.coachingchatbot.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for video transcript from Course Management Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoTranscriptResponse {

    private UUID id;

    private UUID videoLectureId;

    private String transcriptText;

    private String languageCode;

    private Integer audioDuration;

    private Integer wordCount;

    private Integer startTimeSeconds;

    private Integer endTimeSeconds;

    private Integer segmentIndex;

    private Instant createdAt;

    private Instant updatedAt;
}
