package com.hcmut.lms.learning.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class LectureFrequencyItemResponse {
    private UUID lectureId;
    private String title;
    private Integer order;
    private Integer estimateTimeMinutes;
    private int studentCount;
    private long totalSpentSeconds;
    private BigDecimal frequencyRatio;
    private BigDecimal frequencyPercent;
}

