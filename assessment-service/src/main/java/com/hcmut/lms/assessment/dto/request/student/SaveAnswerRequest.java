package com.hcmut.lms.assessment.dto.request.student;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "questionType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SaveMcqAnswerRequest.class, name = "MCQ"),
        @JsonSubTypes.Type(value = SaveCodingAnswerRequest.class, name = "CODING"),
        @JsonSubTypes.Type(value = SaveEssayAnswerRequest.class, name = "ESSAY")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class SaveAnswerRequest {
    private QuestionType questionType;
}
