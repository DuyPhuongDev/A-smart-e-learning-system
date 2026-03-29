package com.hcmut.lms.assessment.dto.request.question;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class McqQuestionRequest extends QuestionRequest {

    private boolean allowMultiAnswer;
    private boolean shuffleOption;

    @NotEmpty(message = "MCQ must have at least one answer option")
    @Valid
    private List<AnswerOptionRequest> answerOptions;
}
