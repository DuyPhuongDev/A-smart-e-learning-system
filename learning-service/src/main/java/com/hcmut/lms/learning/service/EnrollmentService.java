package com.hcmut.lms.learning.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.learning.dto.internal.InternalClassStudentIdsResponse;
import com.hcmut.lms.learning.dto.request.CreateTestEnrollmentRequest;
import com.hcmut.lms.learning.dto.request.EnrollmentRequest;
import com.hcmut.lms.learning.dto.response.EnrolledClassCardResponse;
import com.hcmut.lms.learning.dto.response.EnrollmentResponse;
import com.hcmut.lms.learning.dto.response.StudentEnrollmentResponse;
import com.hcmut.lms.learning.dto.response.StudentEnrollmentWithSubjectResponse;
import com.hcmut.lms.learning.dto.response.UpcomingAssessmentResponse;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {

  /**
   * Enroll a student in a class
   */
  EnrollmentResponse makeEnrollment(EnrollmentRequest request);

  /**
   * Get enrolled classes for a student with optional filters
   *
   * @param studentId    Student ID
   * @param semesterCode Optional semester filter
   * @param searchTerm   Optional search term (by class name or subject name)
   * @param page         Page number
   * @param size         Page size
   * @return Paginated list of enrolled class cards
   */
  PageResponse<EnrolledClassCardResponse> getEnrolledClasses(
      UUID studentId, String semesterCode, String searchTerm,
      int page, int size);

  /**
   * Check if student is already enrolled in a class
   */
  boolean isEnrolled(UUID studentId, UUID classId);

  void updateProgress(UUID studentId, UUID classId);

  EnrollmentResponse changeClass(UUID id, EnrollmentRequest enrollmentRequest);

  void unEnroll(UUID id);

  /**
   * Get all enrollments for a student with subject information
   * Used by course-management-service for student progress tracking
   */
  List<StudentEnrollmentResponse> getStudentEnrollmentsWithSubjects(UUID studentId);

  /**
   * Get all enrollments for a student with resolved subject IDs.
   * Batch resolves classId -> subjectId via course-management-service in a single Feign call.
   */
  List<StudentEnrollmentWithSubjectResponse> getStudentEnrollmentsWithSubjectIds(UUID studentId);

  List<UUID> getStudentIdsByClassId(UUID classId);

  List<InternalClassStudentIdsResponse> getStudentIdsByClassIds(List<UUID> classIds);

  List<UUID> getStudentIdsByCourseId(UUID courseId);

  /**
   * Get upcoming assessments across all enrolled classes for a student.
   * Returns at most 5 assessments ordered by closest closeTime.
   */
  List<UpcomingAssessmentResponse> getUpcomingAssessments(UUID studentId);

  /**
   * Create a test enrollment directly with grade data, bypassing normal enrollment validation.
   * Used internally by personalization-service for test data generation.
   */
  void createTestEnrollment(CreateTestEnrollmentRequest request);
}
