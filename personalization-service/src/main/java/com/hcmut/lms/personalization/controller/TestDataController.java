package com.hcmut.lms.personalization.controller;

import com.hcmut.lms.personalization.application.dto.response.TestDataGenerationResponse;
import com.hcmut.lms.personalization.application.service.TestDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/learning-paths")
@RequiredArgsConstructor
public class TestDataController {

  private final TestDataService testDataService;

  @PostMapping("/test-data/generate")
  public ResponseEntity<TestDataGenerationResponse> generateTestData(
      @RequestParam UUID studentId,
      @RequestParam UUID learningPathId,
      @RequestParam(defaultValue = "MIXED") String mode) {
    return ResponseEntity.ok(testDataService.generateTestData(studentId, learningPathId, mode));
  }
}
