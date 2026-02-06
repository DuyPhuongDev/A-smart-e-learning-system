package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating/updating video transcript
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoTranscriptRequest {

    @NotNull(message = "Video lecture ID is required")
    private UUID videoLectureId;

    @NotBlank(message = "Transcript text is required")
    private String transcriptText;

    @NotBlank(message = "Language code is required")
    private String languageCode;

    private Integer audioDuration;

    private Integer wordCount;

    /**
     * Thời gian bắt đầu đoạn transcript trong video (tính bằng giây)
     * Default value: 0
     */
    @Builder.Default
    private Integer startTimeSeconds = 0;

    /**
     * Thời gian kết thúc đoạn transcript trong video (tính bằng giây)
     */
    private Integer endTimeSeconds;

    /**
     * Chỉ số của đoạn/phần trong video (0, 1, 2, ...)
     */
    private Integer segmentIndex;
}
