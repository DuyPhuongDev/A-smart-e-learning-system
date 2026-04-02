package com.hcmut.lms.assessment.dto.response.student;

import com.hcmut.lms.assessment.domain.entity.submission.QuestionSubmissionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitQuestionResultResponse {
    private UUID questionId;
    private BigDecimal earnedPoints;
    private BigDecimal maxPoints;
    private QuestionSubmissionStatus status;
    private String detail;
}
