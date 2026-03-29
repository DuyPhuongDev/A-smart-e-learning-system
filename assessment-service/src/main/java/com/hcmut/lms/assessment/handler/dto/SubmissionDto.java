package com.hcmut.lms.assessment.handler.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "questionType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = McqSubmissionDto.class, name = "MCQ"),
        @JsonSubTypes.Type(value = CodingSubmissionDto.class, name = "CODING"),
        @JsonSubTypes.Type(value = EssaySubmissionDto.class, name = "ESSAY")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class SubmissionDto {
    private UUID questionId;
    private UUID studentId;
    private QuestionType questionType;
}
