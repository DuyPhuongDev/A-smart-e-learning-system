package com.hcmut.lms.personalization.client;

import com.hcmut.lms.personalization.client.dto.BatchGradePredictionRequest;
import com.hcmut.lms.personalization.client.dto.BatchGradePredictionResponse;
import com.hcmut.lms.personalization.client.dto.GradePredictionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "learning-service", path = "/api/learning/internal/prediction")
public interface LearningServiceClient {

  @GetMapping("/grade")
  GradePredictionResponse predictGrade(
      @RequestParam UUID studentId, @RequestParam UUID subjectId,
      @RequestParam(required = false) Integer plannedSemesterCredits, @RequestParam(required = false) Double threshold);

  @PostMapping("/grade/batch")
  BatchGradePredictionResponse predictGradeBatch(@RequestBody BatchGradePredictionRequest request);
}
