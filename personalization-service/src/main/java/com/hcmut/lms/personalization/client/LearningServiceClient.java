package com.hcmut.lms.personalization.client;

import com.hcmut.lms.personalization.client.dto.BatchGradePredictionRequest;
import com.hcmut.lms.personalization.client.dto.BatchGradePredictionResponse;
import com.hcmut.lms.personalization.client.dto.GradePredictionResponse;
import com.hcmut.lms.personalization.client.dto.StudentEnrollmentWithSubjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "learning-service", path = "/api/learning/internal")
public interface LearningServiceClient {

  @GetMapping("/prediction/grade")
  GradePredictionResponse predictGrade(
      @RequestParam UUID studentId, @RequestParam UUID subjectId,
      @RequestParam(required = false) Integer plannedSemesterCredits, @RequestParam(required = false) Double threshold);

  @PostMapping("/prediction/grade/batch")
  BatchGradePredictionResponse predictGradeBatch(@RequestBody BatchGradePredictionRequest request);

  @GetMapping("/subject-metrics/batch/difficulty")
  Map<UUID, String> getBatchDifficulty(@RequestParam("subjectIds") List<UUID> subjectIds);

  @GetMapping("/enrollments/student/{studentId}/with-subjects")
  List<StudentEnrollmentWithSubjectResponse> getStudentEnrollmentsWithSubjects(@PathVariable UUID studentId);
}
