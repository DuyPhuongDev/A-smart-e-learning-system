package com.hcmut.lms.assessment.service.judge.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder

public class TestCaseJudgeResult {
    private UUID testCaseId;
    private JudgeVerdict verdict;
    private boolean pass;
    private String output;
    private String error;
    private Integer executionTimeMs;
}

