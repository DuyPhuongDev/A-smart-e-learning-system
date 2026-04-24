package com.hcmut.lms.assessment.dto.response.student;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.assessment.service.judge.dto.JudgeVerdict;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RunTestcaseResponse {
    private boolean compileSuccess;
    private JudgeVerdict verdict;
    private String input;
    private String expected;
    private String output;
    private String error;
    private String compileError;
    private Integer executionTimeMs;
    private Integer memoryUsedKb;
}
