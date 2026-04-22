package com.hcmut.lms.assessment.dto.request.assessment;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import com.hcmut.lms.assessment.domain.entity.assessment.GradingRule;
import com.hcmut.lms.assessment.domain.entity.assessment.TimeCanReview;
import com.hcmut.lms.assessment.dto.request.question.QuestionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AssessmentRequest {

    @NotNull(message = "classId is required")
    private UUID classId;

    @NotBlank(message = "title is required")
    private String title;

    private BigDecimal weight;

    @NotNull(message = "assessmentType is required")
    private AssessmentType assessmentType;

    private GradingRule gradingRule;

    private int maxAttempts;

    private int timeLimit;

    private int passingScore;

    private Instant startTime;

    private Instant closeTime;

    private boolean canReview;

    private boolean showCorrectAnswers;

    private TimeCanReview timeCanReview;
}
