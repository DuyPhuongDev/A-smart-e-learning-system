package com.hcmut.lms.assessment.service;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentQuestion;
import com.hcmut.lms.assessment.dto.request.student.RunTestcaseRequest;
import com.hcmut.lms.assessment.dto.response.GradingResponse;
import com.hcmut.lms.assessment.handler.dto.SubmissionDto;
import com.hcmut.lms.assessment.service.judge.dto.CodingJudgeEvaluation;

import java.math.BigDecimal;
import java.util.UUID;

public interface AssessmentExecutionService {

    GradingResponse submitAnswer(UUID questionId, SubmissionDto submission, BigDecimal maxScore);

    CodingJudgeEvaluation preCheckTestcase(UUID questionId, RunTestcaseRequest runTestcaseRequest);
}
