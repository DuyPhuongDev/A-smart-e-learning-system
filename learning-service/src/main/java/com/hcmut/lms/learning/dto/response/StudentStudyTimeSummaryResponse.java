package com.hcmut.lms.learning.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StudentStudyTimeSummaryResponse {
    private UUID studentId;
    private long totalSpentSeconds;
}
