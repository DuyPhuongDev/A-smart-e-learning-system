package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class VideoTranscriptRequest {
    @NotBlank(message = "VideoLectureId is mandatory!")
    private UUID videoLectureId;

    private String transcriptText;
}
