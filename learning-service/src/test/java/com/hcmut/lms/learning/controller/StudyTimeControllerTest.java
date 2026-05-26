package com.hcmut.lms.learning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.learning.dto.request.StudyTimeRequest;
import com.hcmut.lms.learning.dto.response.*;
import com.hcmut.lms.learning.service.StudyTimeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class StudyTimeControllerTest {

    @Mock
    private StudyTimeService studyTimeService;

    @InjectMocks
    private StudyTimeController controller;

    private final UUID studentId = UUID.randomUUID();
    private final UUID classId = UUID.randomUUID();
    private final UUID lectureId = UUID.randomUUID();
    private final CurrentUserInfo currentUser = CurrentUserInfo.builder()
            .id(studentId).email("test@hcmut.edu.vn").role("STUDENT").build();

    @Test
    void recordStudyTime_shouldReturnResponse() {
        StudyTimeRequest request = new StudyTimeRequest();
        request.setClassId(classId);
        request.setLectureId(lectureId);
        request.setDurationSeconds(300);
        request.setStartedAt(LocalDateTime.now());

        when(studyTimeService.recordStudyTime(studentId, request)).thenReturn(new StudyTimeResponse());

        ResponseEntity<StudyTimeResponse> result = controller.recordStudyTime(currentUser, request);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getMyStudyTimeHistory_shouldReturnList() {
        when(studyTimeService.getStudyTimeHistory(studentId, classId, null, null))
                .thenReturn(List.of(new StudyTimeResponse()));

        ResponseEntity<List<StudyTimeResponse>> result = controller.getMyStudyTimeHistory(currentUser, classId, null, null);
        assertEquals(1, result.getBody().size());
        assertTrue(true);
    }

    @Test
    void getStudentStudyTimeHistory_shouldReturnList() {
        when(studyTimeService.getStudyTimeHistory(studentId, classId, null, null))
                .thenReturn(List.of());

        ResponseEntity<List<StudyTimeResponse>> result = controller.getStudentStudyTimeHistory(studentId, classId, null, null);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getMyStudyTimeSummary_shouldReturnList() {
        when(studyTimeService.getStudyTimeSummary(studentId, classId, null, null))
                .thenReturn(List.of());

        ResponseEntity<List<StudyTimeSummaryResponse>> result = controller.getMyStudyTimeSummary(currentUser, classId, null, null);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getStudentStudyTimeSummary_shouldReturnList() {
        when(studyTimeService.getStudyTimeSummary(studentId, classId, null, null))
                .thenReturn(List.of());

        ResponseEntity<List<StudyTimeSummaryResponse>> result = controller.getStudentStudyTimeSummary(studentId, classId, null, null);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getMyTotalStudyTime_shouldReturnTotal() {
        when(studyTimeService.getTotalStudyTime(studentId, classId)).thenReturn(3600);

        ResponseEntity<Integer> result = controller.getMyTotalStudyTime(currentUser, classId);
        assertEquals(3600, result.getBody());
        assertTrue(true);
    }

    @Test
    void getStudentTotalStudyTime_shouldReturnTotal() {
        when(studyTimeService.getTotalStudyTime(studentId, classId)).thenReturn(0);

        ResponseEntity<Integer> result = controller.getStudentTotalStudyTime(studentId, classId);
        assertEquals(0, result.getBody());
        assertTrue(true);
    }

    @Test
    void getLectureFrequencyForTeacher_shouldReturnResponse() {
        when(studyTimeService.getLectureFrequencyForTeacher(classId, currentUser))
                .thenReturn(LectureFrequencyResponse.builder().build());

        ResponseEntity<LectureFrequencyResponse> result = controller.getLectureFrequencyForTeacher(currentUser, classId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getStudentStudyTimesForTeacher_shouldReturnList() {
        when(studyTimeService.getStudentStudyTimesForTeacher(classId, currentUser))
                .thenReturn(List.of());

        ResponseEntity<List<StudentStudyTimeSummaryResponse>> result = controller.getStudentStudyTimesForTeacher(currentUser, classId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getAggregatedSummary_shouldReturnList() {
        when(studyTimeService.getAggregatedSummary(studentId, 7))
                .thenReturn(List.of());

        ResponseEntity<List<AggregatedStudyTimeResponse>> result = controller.getAggregatedSummary(currentUser, 7);
        assertNotNull(result.getBody());
        assertTrue(true);
    }
}
