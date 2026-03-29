package com.hcmut.lms.assessment.dto.response;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import com.hcmut.lms.assessment.domain.entity.assessment.GradingRule;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponse {
    private UUID id;
    private UUID classId;
    private String title;
    private AssessmentType assessmentType;
    private AssessmentStatus assessmentStatus;
    private GradingRule gradingRule;
    private int maxAttempts;
    private int timeLimit;
    private int passingScore;
    private LocalDateTime startTime;
    private LocalDateTime closeTime;
    private boolean canReview;
    private boolean showCorrectAnswers;
    private Instant createdAt;
    private Instant updatedAt;
}
