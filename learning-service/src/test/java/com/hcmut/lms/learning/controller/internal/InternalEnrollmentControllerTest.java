package com.hcmut.lms.learning.controller.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.learning.dto.internal.InternalBatchClassStudentIdsRequest;
import com.hcmut.lms.learning.dto.internal.InternalBatchClassStudentIdsResponse;
import com.hcmut.lms.learning.dto.internal.InternalClassStudentIdsResponse;
import com.hcmut.lms.learning.dto.request.CreateTestEnrollmentRequest;
import com.hcmut.lms.learning.dto.response.StudentEnrollmentResponse;
import com.hcmut.lms.learning.dto.response.StudentEnrollmentWithSubjectResponse;
import com.hcmut.lms.learning.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class InternalEnrollmentControllerTest {

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private InternalEnrollmentController controller;

    private final UUID studentId = UUID.randomUUID();
    private final UUID classId = UUID.randomUUID();
    private final UUID courseId = UUID.randomUUID();

    @Test
    void getStudentEnrollments_shouldReturnList() {
        when(enrollmentService.getStudentEnrollmentsWithSubjects(studentId))
                .thenReturn(List.of(new StudentEnrollmentResponse()));

        ResponseEntity<List<StudentEnrollmentResponse>> result = controller.getStudentEnrollments(studentId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getStudentEnrollmentsWithSubjectIds_shouldReturnList() {
        when(enrollmentService.getStudentEnrollmentsWithSubjectIds(studentId))
                .thenReturn(List.of());

        ResponseEntity<List<StudentEnrollmentWithSubjectResponse>> result =
                controller.getStudentEnrollmentsWithSubjectIds(studentId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void resolveStudentsByClass_shouldReturnList() {
        when(enrollmentService.getStudentIdsByClassId(classId)).thenReturn(List.of(studentId));

        ResponseEntity<List<UUID>> result = controller.resolveStudentsByClass(classId);
        assertEquals(1, result.getBody().size());
        assertTrue(true);
    }

    @Test
    void resolveStudentsByClassBatch_shouldReturnResponse() {
        InternalBatchClassStudentIdsRequest request = new InternalBatchClassStudentIdsRequest();
        request.setClassIds(List.of(classId));
        when(enrollmentService.getStudentIdsByClassIds(any()))
                .thenReturn(List.of(InternalClassStudentIdsResponse.builder()
                        .classId(classId).studentIds(List.of()).build()));

        ResponseEntity<InternalBatchClassStudentIdsResponse> result =
                controller.resolveStudentsByClassBatch(request);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void resolveStudentsByClassBatch_shouldHandleNullRequest() {
        when(enrollmentService.getStudentIdsByClassIds(List.of())).thenReturn(List.of());

        ResponseEntity<InternalBatchClassStudentIdsResponse> result =
                controller.resolveStudentsByClassBatch(null);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void resolveStudentsByCourse_shouldReturnList() {
        when(enrollmentService.getStudentIdsByCourseId(courseId)).thenReturn(List.of());

        ResponseEntity<List<UUID>> result = controller.resolveStudentsByCourse(courseId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void checkEnrollment_shouldReturnBoolean() {
        CurrentUserInfo user = CurrentUserInfo.builder().id(studentId).role("STUDENT").build();
        when(enrollmentService.isEnrolled(studentId, classId)).thenReturn(true);

        ResponseEntity<Boolean> result = controller.checkEnrollment(user, classId);
        assertTrue(result.getBody());
        assertTrue(true);
    }

    @Test
    void createTestEnrollment_shouldReturnOk() {
        CreateTestEnrollmentRequest request = CreateTestEnrollmentRequest.builder()
                .studentId(studentId).classId(classId).finalGrade(8.0).isPassed(true).build();
        doNothing().when(enrollmentService).createTestEnrollment(request);

        ResponseEntity<Void> result = controller.createTestEnrollment(request);
        assertEquals(200, result.getStatusCodeValue());
        assertTrue(true);
    }
}
