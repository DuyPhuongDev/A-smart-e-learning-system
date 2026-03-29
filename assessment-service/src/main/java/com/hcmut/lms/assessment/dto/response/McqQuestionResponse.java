package com.hcmut.lms.assessment.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class McqQuestionResponse extends QuestionResponse {
    private boolean allowMultiAnswer;
    private boolean shuffleOption;
    private List<AnswerOptionResponse> answerOptions;
}
