package com.hcmut.lms.learning.controller.internal;

import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.learning.dto.internal.InternalBatchClassStudentIdsRequest;
import com.hcmut.lms.learning.dto.internal.InternalBatchClassStudentIdsResponse;
import com.hcmut.lms.learning.dto.internal.InternalClassStudentIdsResponse;
import com.hcmut.lms.learning.dto.request.CreateTestEnrollmentRequest;
import com.hcmut.lms.learning.dto.response.StudentEnrollmentResponse;
import com.hcmut.lms.learning.dto.response.StudentEnrollmentWithSubjectResponse;
import com.hcmut.lms.learning.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @GetMapping("/student/{studentId}/with-subjects")
  public ResponseEntity<List<StudentEnrollmentWithSubjectResponse>> getStudentEnrollmentsWithSubjectIds(
      @PathVariable UUID studentId) {
    return ResponseEntity.ok(enrollmentService.getStudentEnrollmentsWithSubjectIds(studentId));
  }

  @GetMapping("/class/{classId}/students")
  public ResponseEntity<List<UUID>> resolveStudentsByClass(@PathVariable UUID classId) {
    return ResponseEntity.ok(enrollmentService.getStudentIdsByClassId(classId));
  }

  @PostMapping("/classes/students")
  public ResponseEntity<InternalBatchClassStudentIdsResponse> resolveStudentsByClassBatch(
      @RequestBody InternalBatchClassStudentIdsRequest request) {
    List<InternalClassStudentIdsResponse> items = enrollmentService.getStudentIdsByClassIds(
        request != null ? request.getClassIds() : List.of());
    return ResponseEntity.ok(InternalBatchClassStudentIdsResponse.builder().items(items).build());
  }

  @GetMapping("/course/{courseId}/students")
  public ResponseEntity<List<UUID>> resolveStudentsByCourse(@PathVariable UUID courseId) {
    return ResponseEntity.ok(enrollmentService.getStudentIdsByCourseId(courseId));
  }

  /**
   * Check if current user is enrolled in a class
   */
  @GetMapping("/check/{classId}")
  public ResponseEntity<Boolean> checkEnrollment(
      @CurrentUser CurrentUserInfo currentUser,
      @PathVariable UUID classId) {
    return ResponseEntity.ok(enrollmentService.isEnrolled(currentUser.getId(), classId));
  }

  @PostMapping("/test-data")
  public ResponseEntity<Void> createTestEnrollment(@RequestBody CreateTestEnrollmentRequest request) {
    enrollmentService.createTestEnrollment(request);
    return ResponseEntity.ok().build();
  }
}
