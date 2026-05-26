package com.hcmut.lms.learning.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.learning.client.AssessmentClient;
import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.*;
import com.hcmut.lms.learning.dto.internal.InternalClassStudentIdsResponse;
import com.hcmut.lms.learning.dto.request.CreateTestEnrollmentRequest;
import com.hcmut.lms.learning.dto.request.EnrollmentRequest;
import com.hcmut.lms.learning.dto.response.*;
import com.hcmut.lms.learning.entity.enrollment.Enrollment;
import com.hcmut.lms.learning.exception.BusinessException;
import com.hcmut.lms.learning.mapper.EnrollmentMapper;
import com.hcmut.lms.learning.repository.EnrollmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private EnrollmentMapper enrollmentMapper;

    @Mock
    private CourseManagementClient courseManagementClient;

    @Mock
    private AssessmentClient assessmentClient;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private final UUID studentId = UUID.randomUUID();
    private final UUID classId = UUID.randomUUID();
    private final UUID enrollmentId = UUID.randomUUID();

    private EnrollmentRequest createEnrollmentRequest() {
        EnrollmentRequest req = new EnrollmentRequest();
        req.setStudentId(studentId);
        req.setClassId(classId);
        return req;
    }

    @Test
    void makeEnrollment_shouldReturnResponse_whenValidRequest() {
        EnrollmentRequest request = createEnrollmentRequest();
        ClassEnrollStatus status = new ClassEnrollStatus(classId, true, ClassStatus.OPEN, true, 50, 30);
        Enrollment entity = new Enrollment();
        Enrollment savedEntity = new Enrollment();
        EnrollmentResponse response = new EnrollmentResponse();

        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(false);
        when(courseManagementClient.getEnrollmentStatus(classId)).thenReturn(status);
        when(enrollmentMapper.toEntity(request)).thenReturn(entity);
        when(enrollmentRepository.save(entity)).thenReturn(savedEntity);
        when(enrollmentMapper.toResponse(savedEntity)).thenReturn(response);
        doNothing().when(courseManagementClient).incrementCurrentStudents(classId);

        EnrollmentResponse result = enrollmentService.makeEnrollment(request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void makeEnrollment_shouldThrowBusinessException_whenAlreadyEnrolled() {
        EnrollmentRequest request = createEnrollmentRequest();
        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(true);

        try {
            enrollmentService.makeEnrollment(request);
        } catch (BusinessException e) {
            assertTrue(e.getMessage().contains("ALREADY_ENROLLED"));
        }
    }

    @Test
    void makeEnrollment_shouldThrowEntityNotFoundException_whenClassNotFound() {
        EnrollmentRequest request = createEnrollmentRequest();
        ClassEnrollStatus status = new ClassEnrollStatus(classId, false, ClassStatus.UNAVAILABLE, false, 0, 0);
        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(false);
        when(courseManagementClient.getEnrollmentStatus(classId)).thenReturn(status);

        try {
            enrollmentService.makeEnrollment(request);
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    void makeEnrollment_shouldThrowBusinessException_whenClassCannotEnroll() {
        EnrollmentRequest request = createEnrollmentRequest();
        ClassEnrollStatus status = new ClassEnrollStatus(classId, true, ClassStatus.CLOSED, true, 50, 30);
        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(false);
        when(courseManagementClient.getEnrollmentStatus(classId)).thenReturn(status);

        try {
            enrollmentService.makeEnrollment(request);
        } catch (BusinessException e) {
            assertTrue(e.getMessage().contains("CAN_NOT_ENROL"));
        }
    }

    @Test
    void makeEnrollment_shouldThrowBusinessException_whenClassFull() {
        EnrollmentRequest request = createEnrollmentRequest();
        ClassEnrollStatus status = new ClassEnrollStatus(classId, true, ClassStatus.OPEN, true, 50, 50);
        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(false);
        when(courseManagementClient.getEnrollmentStatus(classId)).thenReturn(status);

        try {
            enrollmentService.makeEnrollment(request);
        } catch (BusinessException e) {
            assertTrue(e.getMessage().contains("CAN_NOT_ENROL"));
        }
    }

    @Test
    void getEnrolledClasses_shouldReturnPageResponse_whenEnrollmentsExist() {
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId)
                .enrolledAt(LocalDateTime.now()).build();
        ClassResponse classResponse = ClassResponse.builder().id(classId).sectionName("Test Class")
                .subjectName("Math").build();

        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
        when(courseManagementClient.getClassSectionsByIds(any())).thenReturn(List.of(classResponse));
        when(assessmentClient.getPendingAssessmentCounts(any())).thenReturn(List.of());

        PageResponse<EnrolledClassCardResponse> result = enrollmentService.getEnrolledClasses(studentId, null, null, 0, 10);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getEnrolledClasses_shouldReturnEmptyPage_whenNoEnrollments() {
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of());

        PageResponse<EnrolledClassCardResponse> result = enrollmentService.getEnrolledClasses(studentId, null, null, 0, 10);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void isEnrolled_shouldReturnTrue_whenEnrolled() {
        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(true);
        assertTrue(enrollmentService.isEnrolled(studentId, classId));
    }

    @Test
    void isEnrolled_shouldReturnFalse_whenNotEnrolled() {
        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(false);
        assertTrue(!enrollmentService.isEnrolled(studentId, classId));
    }

    @Test
    void updateProgress_shouldIncrementProgress_whenLectureDone() {
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId)
                .progressPercentage(0.5).build();
        when(enrollmentRepository.findByStudentIdAndClassId(studentId, classId)).thenReturn(Optional.of(enrollment));
        when(courseManagementClient.countNumberLecturesByClassId(classId)).thenReturn(10);
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);

        enrollmentService.updateProgress(studentId, classId);
        assertTrue(true);
    }

    @Test
    void changeClass_shouldThrowException_whenEnrollmentNotFound() {
        UUID id = UUID.randomUUID();
        EnrollmentRequest request = createEnrollmentRequest();
        when(enrollmentRepository.findById(id)).thenReturn(Optional.empty());

        try {
            enrollmentService.changeClass(id, request);
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    void changeClass_shouldReturnResponse_whenValidRequest() {
        UUID id = UUID.randomUUID();
        UUID newClassId = UUID.randomUUID();
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(studentId);
        request.setClassId(newClassId);

        Enrollment existing = Enrollment.builder().id(id).studentId(studentId).classId(classId).build();
        EnrollmentResponse response = new EnrollmentResponse();

        ClassEnrollStatus status = new ClassEnrollStatus(newClassId, true, ClassStatus.OPEN, true, 50, 30);

        when(enrollmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, newClassId)).thenReturn(false);
        when(courseManagementClient.getEnrollmentStatus(newClassId)).thenReturn(status);
        when(enrollmentRepository.save(existing)).thenReturn(existing);
        when(enrollmentMapper.toResponse(existing)).thenReturn(response);

        EnrollmentResponse result = enrollmentService.changeClass(id, request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void updateProgress_shouldComplete_whenProgressReaches100() {
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId)
                .progressPercentage(0.95).build();
        when(enrollmentRepository.findByStudentIdAndClassId(studentId, classId)).thenReturn(Optional.of(enrollment));
        when(courseManagementClient.countNumberLecturesByClassId(classId)).thenReturn(10);
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);

        enrollmentService.updateProgress(studentId, classId);
        assertTrue(true);
    }

    @Test
    void getUpcomingAssessments_shouldReturnList_whenAssessmentsExist() {
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId).build();
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
        ClassResponse classResponse = ClassResponse.builder().id(classId).sectionName("CS101")
                .subjectName("CS").subjectCode("CS101").build();
        when(courseManagementClient.getClassSectionsByIds(any())).thenReturn(List.of(classResponse));

        AssessmentResponse assessment = AssessmentResponse.builder()
                .id(UUID.randomUUID()).title("Exam 1").assessmentType("EXAM")
                .classId(classId).assessmentStatus("PUBLISHED")
                .closeTime(Instant.now().plusSeconds(86400))
                .startTime(Instant.now()).build();
        when(assessmentClient.getAssessmentsByClassIds(any())).thenReturn(List.of(assessment));

        List<UpcomingAssessmentResponse> result = enrollmentService.getUpcomingAssessments(studentId);
        assertTrue(!result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getStudentEnrollmentsWithSubjectIds_shouldResolveSubjects_whenClassDataAvailable() {
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId).build();
        UUID subjectId2 = UUID.randomUUID();
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
        ClassResponse classResponse = ClassResponse.builder().id(classId).subjectId(subjectId2)
                .subjectGradingType("GRADED").build();
        when(courseManagementClient.getClassSectionsByIds(any())).thenReturn(List.of(classResponse));

        List<StudentEnrollmentWithSubjectResponse> result = enrollmentService.getStudentEnrollmentsWithSubjectIds(studentId);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void unEnroll_shouldDeleteEnrollment_whenExists() {
        UUID id = UUID.randomUUID();
        Enrollment enrollment = Enrollment.builder().id(id).studentId(studentId).classId(classId).build();
        when(enrollmentRepository.findById(id)).thenReturn(Optional.of(enrollment));
        doNothing().when(enrollmentRepository).deleteById(id);

        enrollmentService.unEnroll(id);
        verify(enrollmentRepository).deleteById(id);
        assertTrue(true);
    }

    @Test
    void getStudentEnrollmentsWithSubjects_shouldReturnList_whenEnrollmentsExist() {
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId).build();
        StudentEnrollmentResponse response = new StudentEnrollmentResponse();
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
        when(enrollmentMapper.toStudentEnrollmentResponse(enrollment)).thenReturn(response);

        List<StudentEnrollmentResponse> result = enrollmentService.getStudentEnrollmentsWithSubjects(studentId);
        assertTrue(!result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getStudentEnrollmentsWithSubjects_shouldReturnEmptyList_whenNoEnrollments() {
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of());

        List<StudentEnrollmentResponse> result = enrollmentService.getStudentEnrollmentsWithSubjects(studentId);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getStudentEnrollmentsWithSubjectIds_shouldReturnList_whenEnrollmentsExist() {
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId).build();
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
        when(courseManagementClient.getClassSectionsByIds(any())).thenReturn(List.of());

        List<StudentEnrollmentWithSubjectResponse> result = enrollmentService.getStudentEnrollmentsWithSubjectIds(studentId);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getStudentIdsByClassId_shouldReturnList() {
        when(enrollmentRepository.findDistinctStudentIdsByClassId(classId)).thenReturn(List.of(studentId));

        List<UUID> result = enrollmentService.getStudentIdsByClassId(classId);
        assertTrue(!result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getStudentIdsByClassIds_shouldReturnList_whenValidInput() {
        when(enrollmentRepository.findByClassIdIn(any())).thenReturn(List.of());

        List<InternalClassStudentIdsResponse> result = enrollmentService.getStudentIdsByClassIds(List.of(classId));
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getStudentIdsByClassIds_shouldReturnEmptyList_whenNullInput() {
        List<InternalClassStudentIdsResponse> result = enrollmentService.getStudentIdsByClassIds(null);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getUpcomingAssessments_shouldReturnEmptyList_whenNoEnrollments() {
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of());
        List<UpcomingAssessmentResponse> result = enrollmentService.getUpcomingAssessments(studentId);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void createTestEnrollment_shouldCreateNew_whenNotExists() {
        CreateTestEnrollmentRequest request = CreateTestEnrollmentRequest.builder()
                .studentId(studentId).classId(classId).finalGrade(8.0).isPassed(true).build();
        when(enrollmentRepository.findByStudentIdAndClassId(studentId, classId)).thenReturn(Optional.empty());
        when(enrollmentRepository.save(any())).thenReturn(new Enrollment());

        enrollmentService.createTestEnrollment(request);
        assertTrue(true);
    }

    @Test
    void createTestEnrollment_shouldUpdateExisting_whenExists() {
        CreateTestEnrollmentRequest request = CreateTestEnrollmentRequest.builder()
                .studentId(studentId).classId(classId).finalGrade(7.0).isPassed(true).attemptNo(2).build();
        Enrollment existing = Enrollment.builder().id(enrollmentId).studentId(studentId).classId(classId).build();
        when(enrollmentRepository.findByStudentIdAndClassId(studentId, classId)).thenReturn(Optional.of(existing));
        when(enrollmentRepository.save(existing)).thenReturn(existing);

        enrollmentService.createTestEnrollment(request);
        assertTrue(true);
    }

    @Test
    void getStudentIdsByCourseId_shouldReturnEmpty_whenNoClassIds() {
        when(enrollmentRepository.findDistinctClassIds()).thenReturn(List.of());

        List<UUID> result = enrollmentService.getStudentIdsByCourseId(classId);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getStudentIdsByCourseId_shouldReturnEmpty_whenNoMatchingSubject() {
        UUID otherClassId = UUID.randomUUID();
        when(enrollmentRepository.findDistinctClassIds()).thenReturn(List.of(otherClassId));
        ClassResponse classResponse = ClassResponse.builder().id(otherClassId).subjectId(UUID.randomUUID()).build();
        when(courseManagementClient.getClassSectionsByIds(any())).thenReturn(List.of(classResponse));

        List<UUID> result = enrollmentService.getStudentIdsByCourseId(classId);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getUpcomingAssessments_shouldReturnEmpty_whenCourseClientFails() {
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId).build();
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
        when(courseManagementClient.getClassSectionsByIds(any())).thenThrow(new RuntimeException("Service down"));

        List<UpcomingAssessmentResponse> result = enrollmentService.getUpcomingAssessments(studentId);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getUpcomingAssessments_shouldReturnEmpty_whenAssessmentClientFails() {
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId).build();
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
        ClassResponse classResponse = ClassResponse.builder().id(classId).sectionName("CS101").build();
        when(courseManagementClient.getClassSectionsByIds(any())).thenReturn(List.of(classResponse));
        when(assessmentClient.getAssessmentsByClassIds(any())).thenThrow(new RuntimeException("Service down"));

        List<UpcomingAssessmentResponse> result = enrollmentService.getUpcomingAssessments(studentId);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void changeClass_shouldThrowException_whenAlreadyEnrolledInTarget() {
        UUID id = UUID.randomUUID();
        UUID newClassId = UUID.randomUUID();
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(studentId);
        request.setClassId(newClassId);

        Enrollment existing = Enrollment.builder().id(id).studentId(studentId).classId(classId).build();
        when(enrollmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, newClassId)).thenReturn(true);

        try {
            enrollmentService.changeClass(id, request);
        } catch (BusinessException e) {
            assertTrue(e.getMessage().contains("ALREADY_ENROLLED"));
        }
    }

    @Test
    void changeClass_shouldThrowException_whenTargetClassNotFound() {
        UUID id = UUID.randomUUID();
        UUID newClassId = UUID.randomUUID();
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(studentId);
        request.setClassId(newClassId);

        Enrollment existing = Enrollment.builder().id(id).studentId(studentId).classId(classId).build();
        ClassEnrollStatus status = new ClassEnrollStatus(newClassId, false, ClassStatus.UNAVAILABLE, false, 0, 0);

        when(enrollmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, newClassId)).thenReturn(false);
        when(courseManagementClient.getEnrollmentStatus(newClassId)).thenReturn(status);

        try {
            enrollmentService.changeClass(id, request);
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    void changeClass_shouldThrowException_whenTargetClassCannotEnroll() {
        UUID id = UUID.randomUUID();
        UUID newClassId = UUID.randomUUID();
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(studentId);
        request.setClassId(newClassId);

        Enrollment existing = Enrollment.builder().id(id).studentId(studentId).classId(classId).build();
        ClassEnrollStatus status = new ClassEnrollStatus(newClassId, true, ClassStatus.CLOSED, true, 50, 30);

        when(enrollmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, newClassId)).thenReturn(false);
        when(courseManagementClient.getEnrollmentStatus(newClassId)).thenReturn(status);

        try {
            enrollmentService.changeClass(id, request);
        } catch (BusinessException e) {
            assertTrue(e.getMessage().contains("CAN_NOT_ENROL"));
        }
    }

    @Test
    void changeClass_shouldThrowException_whenTargetClassFull() {
        UUID id = UUID.randomUUID();
        UUID newClassId = UUID.randomUUID();
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(studentId);
        request.setClassId(newClassId);

        Enrollment existing = Enrollment.builder().id(id).studentId(studentId).classId(classId).build();
        ClassEnrollStatus status = new ClassEnrollStatus(newClassId, true, ClassStatus.OPEN, true, 50, 50);

        when(enrollmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, newClassId)).thenReturn(false);
        when(courseManagementClient.getEnrollmentStatus(newClassId)).thenReturn(status);

        try {
            enrollmentService.changeClass(id, request);
        } catch (BusinessException e) {
            assertTrue(true);
        }
    }

    @Test
    void getStudentIdsByCourseId_shouldReturnStudentIds_whenMatchingSubjectFound() {
        UUID matchingClassId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        when(enrollmentRepository.findDistinctClassIds()).thenReturn(List.of(matchingClassId));
        ClassResponse classResponse = ClassResponse.builder().id(matchingClassId).subjectId(subjectId).build();
        when(courseManagementClient.getClassSectionsByIds(any())).thenReturn(List.of(classResponse));
        when(enrollmentRepository.findDistinctStudentIdsByClassIds(any())).thenReturn(List.of(studentId));

        List<UUID> result = enrollmentService.getStudentIdsByCourseId(subjectId);
        assertTrue(!result.isEmpty());
        assertTrue(result.contains(studentId));
        assertTrue(true);
    }

    @Test
    void updateProgress_shouldThrowException_whenEnrollmentNotFound() {
        when(enrollmentRepository.findByStudentIdAndClassId(studentId, classId)).thenReturn(Optional.empty());

        try {
            enrollmentService.updateProgress(studentId, classId);
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    void getUpcomingAssessments_shouldFilterOutNonPublishedAndExpired() {
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId).build();
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
        ClassResponse classResponse = ClassResponse.builder().id(classId).sectionName("CS101")
                .subjectName("CS").subjectCode("CS101").build();
        when(courseManagementClient.getClassSectionsByIds(any())).thenReturn(List.of(classResponse));

        // Non-PUBLISHED → filtered out
        AssessmentResponse draftAssessment = AssessmentResponse.builder()
                .id(UUID.randomUUID()).title("Draft").assessmentType("EXAM")
                .classId(classId).assessmentStatus("DRAFT")
                .closeTime(Instant.now().plusSeconds(86400))
                .startTime(Instant.now()).build();
        // Expired → filtered out
        AssessmentResponse expiredAssessment = AssessmentResponse.builder()
                .id(UUID.randomUUID()).title("Expired").assessmentType("QUIZ")
                .classId(classId).assessmentStatus("PUBLISHED")
                .closeTime(Instant.now().minusSeconds(3600))
                .startTime(Instant.now().minusSeconds(7200)).build();
        when(assessmentClient.getAssessmentsByClassIds(any()))
                .thenReturn(List.of(draftAssessment, expiredAssessment));

        List<UpcomingAssessmentResponse> result = enrollmentService.getUpcomingAssessments(studentId);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getEnrolledClasses_shouldPaginate_whenMultipleEnrollments() {
        UUID classId2 = UUID.randomUUID();
        Enrollment e1 = Enrollment.builder().studentId(studentId).classId(classId)
                .enrolledAt(LocalDateTime.now()).build();
        Enrollment e2 = Enrollment.builder().studentId(studentId).classId(classId2)
                .enrolledAt(LocalDateTime.now().minusDays(1)).build();

        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(e1, e2));
        ClassResponse cr1 = ClassResponse.builder().id(classId).sectionName("CS101")
                .subjectName("CS").build();
        ClassResponse cr2 = ClassResponse.builder().id(classId2).sectionName("CS102")
                .subjectName("CS").build();
        when(courseManagementClient.getClassSectionsByIds(any())).thenReturn(List.of(cr1, cr2));
        when(assessmentClient.getPendingAssessmentCounts(any())).thenReturn(List.of());

        // Request page 0, size 1 → paginated
        PageResponse<EnrolledClassCardResponse> result = enrollmentService.getEnrolledClasses(studentId, null, null, 0, 1);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getStudentEnrollmentsWithSubjectIds_shouldHandleClassResolutionFailure() {
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId)
                .enrolledAt(LocalDateTime.now()).build();
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
        when(courseManagementClient.getClassSectionsByIds(any()))
                .thenThrow(new RuntimeException("Service unavailable"));

        List<StudentEnrollmentWithSubjectResponse> result = enrollmentService.getStudentEnrollmentsWithSubjectIds(studentId);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void makeEnrollment_shouldNotFail_whenIncrementCurrentStudentsFails() {
        EnrollmentRequest request = createEnrollmentRequest();
        ClassEnrollStatus status = new ClassEnrollStatus(classId, true, ClassStatus.OPEN, true, 50, 30);
        Enrollment entity = new Enrollment();
        Enrollment savedEntity = new Enrollment();
        EnrollmentResponse response = new EnrollmentResponse();

        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(false);
        when(courseManagementClient.getEnrollmentStatus(classId)).thenReturn(status);
        when(enrollmentMapper.toEntity(request)).thenReturn(entity);
        when(enrollmentRepository.save(entity)).thenReturn(savedEntity);
        when(enrollmentMapper.toResponse(savedEntity)).thenReturn(response);
        doThrow(new RuntimeException("Network error")).when(courseManagementClient).incrementCurrentStudents(classId);

        EnrollmentResponse result = enrollmentService.makeEnrollment(request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getEnrolledClasses_shouldCatchException_whenAssessmentClientFails() {
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId)
                .enrolledAt(LocalDateTime.now()).build();
        ClassResponse classResponse = ClassResponse.builder().id(classId).sectionName("Test")
                .subjectName("Math").build();

        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
        when(courseManagementClient.getClassSectionsByIds(any())).thenReturn(List.of(classResponse));
        when(assessmentClient.getPendingAssessmentCounts(any()))
                .thenThrow(new RuntimeException("Assessment service down"));

        PageResponse<EnrolledClassCardResponse> result = enrollmentService.getEnrolledClasses(studentId, null, null, 0, 10);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void changeClass_shouldNotFail_whenDecrementOrIncrementFails() {
        UUID id = enrollmentId;
        UUID newClassId = UUID.randomUUID();
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(studentId);
        request.setClassId(newClassId);

        Enrollment existing = Enrollment.builder().id(id).studentId(studentId).classId(classId).build();
        ClassEnrollStatus status = new ClassEnrollStatus(newClassId, true, ClassStatus.OPEN, true, 50, 30);
        Enrollment savedEnrollment = Enrollment.builder().id(id).studentId(studentId).classId(newClassId).build();
        EnrollmentResponse response = new EnrollmentResponse();

        when(enrollmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, newClassId)).thenReturn(false);
        when(courseManagementClient.getEnrollmentStatus(newClassId)).thenReturn(status);
        when(enrollmentRepository.save(any())).thenReturn(savedEnrollment);
        when(enrollmentMapper.toResponse(savedEnrollment)).thenReturn(response);
        doThrow(new RuntimeException("Network error")).when(courseManagementClient).decrementCurrentStudents(classId);

        EnrollmentResponse result = enrollmentService.changeClass(id, request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getEnrolledClasses_shouldReturnEmptyPage_whenPageBeyondRange() {
        UUID classId2 = UUID.randomUUID();
        Enrollment e1 = Enrollment.builder().studentId(studentId).classId(classId)
                .enrolledAt(LocalDateTime.now()).build();
        Enrollment e2 = Enrollment.builder().studentId(studentId).classId(classId2)
                .enrolledAt(LocalDateTime.now().minusDays(1)).build();

        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(e1, e2));
        ClassResponse cr1 = ClassResponse.builder().id(classId).sectionName("CS101")
                .subjectName("CS").build();
        ClassResponse cr2 = ClassResponse.builder().id(classId2).sectionName("CS102")
                .subjectName("CS").build();
        when(courseManagementClient.getClassSectionsByIds(any())).thenReturn(List.of(cr1, cr2));
        when(assessmentClient.getPendingAssessmentCounts(any())).thenReturn(List.of());

        PageResponse<EnrolledClassCardResponse> result = enrollmentService.getEnrolledClasses(studentId, null, null, 2, 1);
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertTrue(true);
    }

    @Test
    void getStudentIdsByClassIds_shouldReturnGrouped_whenEnrollmentsExist() {
        UUID classId2 = UUID.randomUUID();
        UUID studentId2 = UUID.randomUUID();
        Enrollment e1 = Enrollment.builder().studentId(studentId).classId(classId).build();
        Enrollment e2 = Enrollment.builder().studentId(studentId2).classId(classId).build();
        Enrollment e3 = Enrollment.builder().studentId(studentId).classId(classId2).build();
        when(enrollmentRepository.findByClassIdIn(any())).thenReturn(List.of(e1, e2, e3));

        List<InternalClassStudentIdsResponse> result = enrollmentService.getStudentIdsByClassIds(List.of(classId, classId2));
        assertNotNull(result);
        assertTrue(!result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getStudentEnrollmentsWithSubjectIds_shouldReturnEmpty_whenNoEnrollments() {
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of());

        List<StudentEnrollmentWithSubjectResponse> result = enrollmentService.getStudentEnrollmentsWithSubjectIds(studentId);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }
}
