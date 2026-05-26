package com.hcmut.lms.learning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.learning.dto.request.LearningProgressRequest;
import com.hcmut.lms.learning.dto.response.ClassProgressResponse;
import com.hcmut.lms.learning.dto.response.LearningProgressResponse;
import com.hcmut.lms.learning.service.LearningProgressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class LearningProgressControllerTest {

    @Mock
    private LearningProgressService learningProgressService;

    @InjectMocks
    private LearningProgressController controller;

    private final UUID studentId = UUID.randomUUID();
    private final UUID lectureId = UUID.randomUUID();
    private final UUID classId = UUID.randomUUID();
    private final CurrentUserInfo currentUser = CurrentUserInfo.builder()
            .id(studentId).email("test@hcmut.edu.vn").role("STUDENT").build();

    @Test
    void trackingProgress_shouldReturnResponse() {
        LearningProgressRequest request = new LearningProgressRequest();
        request.setLectureId(lectureId);
        request.setClassId(classId);

        when(learningProgressService.trackingProgress(studentId, request))
                .thenReturn(new LearningProgressResponse());

        ResponseEntity<LearningProgressResponse> result = controller.trackingProgress(currentUser, request);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getProgress_shouldReturnResponse() {
        when(learningProgressService.getProgressForStudent(studentId, lectureId))
                .thenReturn(new LearningProgressResponse());

        ResponseEntity<LearningProgressResponse> result = controller.getProgress(currentUser, lectureId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getProgressesByClass_shouldReturnList() {
        when(learningProgressService.getProgressesByClassForStudent(studentId, classId))
                .thenReturn(List.of(new LearningProgressResponse()));

        ResponseEntity<List<LearningProgressResponse>> result = controller.getProgressesByClass(currentUser, classId);
        assertEquals(1, result.getBody().size());
        assertTrue(true);
    }

    @Test
    void getClassProgressSummary_shouldReturnSummary() {
        when(learningProgressService.getClassProgressSummaryForStudent(studentId, classId))
                .thenReturn(new ClassProgressResponse());

        ResponseEntity<ClassProgressResponse> result = controller.getClassProgressSummary(currentUser, classId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }
}
