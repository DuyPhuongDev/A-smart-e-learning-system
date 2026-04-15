package com.hcmut.lms.learning.client.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class LectureReportMetadataResponse {
    private UUID lectureId;
    private String title;
    private Integer order;
    private Integer estimateTimeSpent;
}

