package com.hcmut.lms.assessment.service.judge.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CodingJudgeEvaluation {
    private int passedCount;
    private int totalCount;
    private JudgeVerdict overallVerdict;
    private String detail;
    private List<TestCaseJudgeResult> testCaseResults;
}

