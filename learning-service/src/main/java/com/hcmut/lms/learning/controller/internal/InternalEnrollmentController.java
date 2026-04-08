package com.hcmut.lms.learning.controller.internal;

import com.hcmut.lms.learning.dto.response.StudentEnrollmentResponse;
import com.hcmut.lms.learning.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Internal controller for enrollment operations
 * Used by other microservices to fetch enrollment data
 */
@RestController
@RequestMapping("/api/learning/internal/enrollments")
@RequiredArgsConstructor
public class InternalEnrollmentController {

  private final EnrollmentService enrollmentService;

  /**
   * Get all enrollments for a student with subject information
   * Used by course-management-service for student progress tracking
   */
  @GetMapping("/student/{studentId}")
  public ResponseEntity<List<StudentEnrollmentResponse>> getStudentEnrollments(
      @PathVariable UUID studentId) {
    return ResponseEntity.ok(enrollmentService.getStudentEnrollmentsWithSubjects(studentId));
  }
}
