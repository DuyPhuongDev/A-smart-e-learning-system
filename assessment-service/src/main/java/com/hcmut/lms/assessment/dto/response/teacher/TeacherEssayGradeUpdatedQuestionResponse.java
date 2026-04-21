package com.hcmut.lms.assessment.dto.response.teacher;

import com.hcmut.lms.assessment.domain.entity.submission.QuestionSubmissionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TeacherEssayGradeUpdatedQuestionResponse {
    private UUID questionId;
    private BigDecimal earnedPoints;
    private BigDecimal maxPoints;
    private QuestionSubmissionStatus status;
    private String feedback;
}
