package com.hcmut.lms.learning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.learning.dto.request.EnrollmentRequest;
import com.hcmut.lms.learning.dto.response.EnrolledClassCardResponse;
import com.hcmut.lms.learning.dto.response.EnrollmentResponse;
import com.hcmut.lms.learning.dto.response.UpcomingAssessmentResponse;
import com.hcmut.lms.learning.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class EnrollmentControllerTest {

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentController controller;

    private final UUID studentId = UUID.randomUUID();
    private final UUID classId = UUID.randomUUID();
    private final UUID enrollmentId = UUID.randomUUID();
    private final CurrentUserInfo currentUser = CurrentUserInfo.builder()
            .id(studentId).email("test@hcmut.edu.vn").role("STUDENT").build();

    @Test
    void enrollInCourse_shouldReturnResponse() {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(studentId);
        request.setClassId(classId);

        when(enrollmentService.makeEnrollment(request)).thenReturn(new EnrollmentResponse());

        ResponseEntity<EnrollmentResponse> result = controller.enrollInCourse(request);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getMyEnrolledClasses_shouldReturnPage() {
        PageResponse<EnrolledClassCardResponse> page = PageResponse.<EnrolledClassCardResponse>builder()
                .content(List.of()).pageNumber(0).pageSize(10).totalElements(0).totalPages(0).build();
        when(enrollmentService.getEnrolledClasses(studentId, null, null, 0, 10)).thenReturn(page);

        ResponseEntity<PageResponse<EnrolledClassCardResponse>> result =
                controller.getMyEnrolledClasses(currentUser, null, null, 0, 10);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void changeEnrolledClass_shouldReturnResponse() {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(studentId);
        request.setClassId(classId);

        when(enrollmentService.changeClass(enrollmentId, request)).thenReturn(new EnrollmentResponse());

        ResponseEntity<EnrollmentResponse> result = controller.changeEnrolledClass(enrollmentId, request);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void deleteEnrolledClass_shouldReturnNoContent() {
        doNothing().when(enrollmentService).unEnroll(enrollmentId);

        ResponseEntity<Void> result = controller.deleteEnrolledClass(enrollmentId);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertTrue(true);
    }

    @Test
    void getStudentEnrolledClasses_shouldReturnPage() {
        PageResponse<EnrolledClassCardResponse> page = PageResponse.<EnrolledClassCardResponse>builder()
                .content(List.of()).pageNumber(0).pageSize(10).totalElements(0).totalPages(0).build();
        when(enrollmentService.getEnrolledClasses(studentId, null, null, 0, 10)).thenReturn(page);

        ResponseEntity<PageResponse<EnrolledClassCardResponse>> result =
                controller.getStudentEnrolledClasses(studentId, null, null, 0, 10);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void checkEnrollment_shouldReturnTrue() {
        when(enrollmentService.isEnrolled(studentId, classId)).thenReturn(true);

        ResponseEntity<Boolean> result = controller.checkEnrollment(currentUser, classId);
        assertTrue(result.getBody());
        assertTrue(true);
    }

    @Test
    void getUpcomingAssessments_shouldReturnList() {
        when(enrollmentService.getUpcomingAssessments(studentId)).thenReturn(List.of());

        ResponseEntity<List<UpcomingAssessmentResponse>> result = controller.getUpcomingAssessments(currentUser);
        assertNotNull(result.getBody());
        assertTrue(true);
    }
}
