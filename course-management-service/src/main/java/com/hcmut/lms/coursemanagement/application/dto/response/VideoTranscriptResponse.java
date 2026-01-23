package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for video transcript
 * Maps from VideoTranscript entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoTranscriptResponse {
    private UUID id;

    private UUID videoLectureId;

    private String transcriptText;

    private String languageCode;

    private Integer audioDuration;

    private Integer wordCount;

    /**
     * Thời gian bắt đầu đoạn transcript trong video (tính bằng giây)
     */
    private Integer startTimeSeconds;

    /**
     * Thời gian kết thúc đoạn transcript trong video (tính bằng giây)
     */
    private Integer endTimeSeconds;

    /**
     * Chỉ số của đoạn/phần trong video (0, 1, 2, ...)
     */
    private Integer segmentIndex;

    private Instant createdAt;

    private Instant updatedAt;
}
