package com.hcmut.lms.learning.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LectureFrequencyResponse {
    private UUID classId;
    private int totalStudents;
    private int totalLectures;
    private int eligibleLectures;
    private BigDecimal overallFrequencyRatio;
    private BigDecimal overallFrequencyPercent;
    private List<LectureFrequencyItemResponse> lectures;
    private Instant generatedAt;
}

