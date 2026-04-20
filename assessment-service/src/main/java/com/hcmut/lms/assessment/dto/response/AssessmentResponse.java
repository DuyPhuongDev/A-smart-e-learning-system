package com.hcmut.lms.assessment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import com.hcmut.lms.assessment.domain.entity.assessment.GradingRule;
import lombok.*;

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
    private int numberQuestions;
    private Instant createdAt;
    private Instant updatedAt;

    /** Weight (%) this assessment contributes to the final grade.
     *  Computed for QUIZ, MIDTERM, and FINAL types: gradingComponentWeight / countOfSameTypeAssessments.
     *  Null for PRACTICE, EXAM, and ASSIGNMENT types. */
    private Float gradingWeight;
}
