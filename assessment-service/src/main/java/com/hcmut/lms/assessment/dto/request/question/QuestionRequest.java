package com.hcmut.lms.assessment.dto.request.question;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hcmut.lms.assessment.domain.entity.question.DifficultLevel;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "questionType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = McqQuestionRequest.class, name = "MCQ"),
        @JsonSubTypes.Type(value = CodingQuestionRequest.class, name = "CODING"),
        @JsonSubTypes.Type(value = EssayQuestionRequest.class, name = "ESSAY")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class QuestionRequest {

    @NotNull(message = "questionType is required")
    private QuestionType questionType;

    private DifficultLevel difficultLevel;

    @NotEmpty(message = "Question must have content")
    private String content;

    private boolean required;
}
