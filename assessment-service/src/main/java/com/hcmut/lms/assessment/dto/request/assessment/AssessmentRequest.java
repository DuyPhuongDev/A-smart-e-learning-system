package com.hcmut.lms.assessment.dto.request.assessment;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import com.hcmut.lms.assessment.domain.entity.assessment.GradingRule;
import com.hcmut.lms.assessment.dto.request.question.QuestionRequest;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    @NotNull(message = "assessmentType is required")
    private AssessmentType assessmentType;

    private GradingRule gradingRule;

    private int maxAttempts;

    private int timeLimit;

    private int passingScore;

    private LocalDateTime startTime;

    private LocalDateTime closeTime;

    private boolean canReview;

    private boolean showCorrectAnswers;

    @Valid
    List<QuestionRequest> questions;
}
