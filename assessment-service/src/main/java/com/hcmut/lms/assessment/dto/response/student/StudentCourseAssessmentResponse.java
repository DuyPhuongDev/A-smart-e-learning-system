package com.hcmut.lms.assessment.dto.response.student;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseAssessmentResponse {
    private UUID id;
    private String title;
    private AssessmentType assessmentType;
    private AssessmentStatus assessmentStatus;
    private Instant startTime;
    private Instant closeTime;
    private int maxAttempts;
    private int timeLimit;
    private int attemptsUsed;
    private BigDecimal bestScore;
    private String myStatus;
    private boolean canStart;
}
