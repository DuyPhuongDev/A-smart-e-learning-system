package com.hcmut.lms.assessment.dto.request.question;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CodingQuestionRequest extends QuestionRequest {

    private String problemDescription;

    private int executionTimeLimit;

    private int executionMemoryLimit;

    @NotBlank(message = "Language must not be blank")
    private String language;

    private String initialCode;

    @Valid
    private List<TestCaseRequest> testCases;
}
