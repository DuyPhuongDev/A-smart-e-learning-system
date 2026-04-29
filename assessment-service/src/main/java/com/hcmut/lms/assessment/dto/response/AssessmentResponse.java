package com.hcmut.lms.assessment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import com.hcmut.lms.assessment.domain.entity.assessment.GradingRule;
import com.hcmut.lms.assessment.domain.entity.assessment.TimeCanReview;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentResponse {
    private UUID id;
    private UUID classId;
    private BigDecimal weight;
    private AssessmentStatus assessmentStatus;
    private AssessmentType assessmentType;
    private String title;
    private GradingRule gradingRule;
    private int maxAttempts;
    private int timeLimit;
    private int passingScore;
    private Instant startTime;
    private Instant closeTime;
    private boolean canReview;
    private boolean showCorrectAnswers;
    private int numberQuestions;
    private TimeCanReview timeCanReview;

    private Instant createdAt;
    private Instant updatedAt;
}
