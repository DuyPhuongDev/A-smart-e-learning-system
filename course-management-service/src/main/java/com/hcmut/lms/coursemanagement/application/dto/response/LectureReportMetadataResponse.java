package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LectureReportMetadataResponse {
    private UUID lectureId;
    private String title;
    private Integer order;
    private Integer estimateTimeSpent;
    private Integer viewCount;
}
