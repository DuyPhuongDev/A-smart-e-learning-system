package com.hcmut.lms.coursemanagement.client;

import com.hcmut.lms.coursemanagement.client.dto.StudentEnrollmentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "learning-service")
public interface LearningServiceClient {

  @GetMapping("/api/learning/internal/enrollments/student/{studentId}")
  List<StudentEnrollmentResponse> getStudentEnrollments(@PathVariable UUID studentId);
}
