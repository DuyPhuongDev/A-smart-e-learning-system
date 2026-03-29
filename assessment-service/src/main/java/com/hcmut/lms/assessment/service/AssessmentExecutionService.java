package com.hcmut.lms.assessment.service;

import com.hcmut.lms.assessment.dto.response.GradingResponse;
import com.hcmut.lms.assessment.handler.dto.SubmissionDto;

import java.util.UUID;

public interface AssessmentExecutionService {

    GradingResponse submitAnswer(UUID questionId, SubmissionDto submission);
}
