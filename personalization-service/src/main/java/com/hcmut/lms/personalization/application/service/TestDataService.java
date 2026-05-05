package com.hcmut.lms.personalization.application.service;

import com.hcmut.lms.personalization.application.dto.response.TestDataGenerationResponse;

import java.util.UUID;

public interface TestDataService {
  TestDataGenerationResponse generateTestData(UUID studentId, UUID learningPathId, String mode);
}
