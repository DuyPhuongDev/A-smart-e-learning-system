package com.hcmut.lms.assessment.dto.request.assessment;

import com.hcmut.lms.assessment.dto.request.question.QuestionRequest;
import jakarta.validation.Valid;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentQuestionRequest {
    @Valid
    private QuestionRequest question;

    private int orderIndex;

    private BigDecimal point;
}
