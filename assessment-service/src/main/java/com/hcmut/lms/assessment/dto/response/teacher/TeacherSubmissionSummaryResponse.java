package com.hcmut.lms.assessment.dto.response.teacher;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TeacherSubmissionSummaryResponse {
    private UUID attemptId;
    private UUID studentId;
    private Integer attemptNo;
    private Instant submittedAt;
    private BigDecimal score;
    private String gradingStatus;
}
