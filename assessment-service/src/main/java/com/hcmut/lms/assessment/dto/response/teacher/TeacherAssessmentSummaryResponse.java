package com.hcmut.lms.assessment.dto.response.teacher;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import com.hcmut.lms.assessment.domain.entity.assessment.GradingRule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TeacherAssessmentSummaryResponse {
    private UUID assessmentId;
    private String title;
    private AssessmentType assessmentType;
    private GradingRule gradingRule;
    private Integer maxAttempts;
    private Integer timeLimit;
    private Instant startTime;
    private Instant closeTime;
}
