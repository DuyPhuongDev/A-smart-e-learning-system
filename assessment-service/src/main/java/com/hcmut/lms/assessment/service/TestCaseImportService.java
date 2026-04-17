package com.hcmut.lms.assessment.service;

import com.hcmut.lms.assessment.domain.entity.answer.TestCase;
import com.hcmut.lms.assessment.dto.request.question.TestCaseRequest;
import com.hcmut.lms.assessment.dto.response.TestCaseImportResultResponse;
import com.hcmut.lms.assessment.dto.response.TestCaseResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface TestCaseImportService {
    TestCaseImportResultResponse importTestCases(UUID questionId, MultipartFile file);

    TestCaseResponse createTestCase(UUID questionId, TestCaseRequest request);
}
