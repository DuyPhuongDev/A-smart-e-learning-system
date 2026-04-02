package com.hcmut.lms.assessment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodingQuestionResponse extends QuestionResponse {
    private String problemDescription;
    private int executionTimeLimit;
    private int executionMemoryLimit;
    private String language;
    private String initialCode;
    private List<TestCaseResponse> testCases;
}
