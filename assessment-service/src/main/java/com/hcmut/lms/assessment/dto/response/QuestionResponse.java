package com.hcmut.lms.assessment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hcmut.lms.assessment.domain.entity.question.DifficultLevel;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "questionType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = McqQuestionResponse.class, name = "MCQ"),
        @JsonSubTypes.Type(value = CodingQuestionResponse.class, name = "CODING"),
        @JsonSubTypes.Type(value = EssayQuestionResponse.class, name = "ESSAY")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class QuestionResponse {
    private UUID id;
    private QuestionType questionType;
    private DifficultLevel difficultLevel;
    private BigDecimal point;
    private boolean required;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
}
