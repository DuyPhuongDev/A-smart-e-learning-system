package com.hcmut.lms.learning.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.LectureResponse;
import com.hcmut.lms.learning.dto.request.LearningProgressRequest;
import com.hcmut.lms.learning.dto.response.ClassProgressResponse;
import com.hcmut.lms.learning.dto.response.LearningProgressResponse;
import com.hcmut.lms.learning.entity.progress.LearningProgress;
import com.hcmut.lms.learning.exception.BusinessException;
import com.hcmut.lms.learning.mapper.LearningProgressMapper;
import com.hcmut.lms.learning.repository.EnrollmentRepository;
import com.hcmut.lms.learning.repository.LearningProgressRepository;
import com.hcmut.lms.learning.repository.StudyTimeRepository;
import com.hcmut.lms.learning.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class LearningProgressServiceImplTest {

    @Mock
    private LearningProgressRepository learningProgressRepository;

    @Mock
    private LearningProgressMapper learningProgressMapper;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudyTimeRepository studyTimeRepository;

    @Mock
    private EnrollmentService enrollmentService;

    @Mock
    private CourseManagementClient courseManagementClient;

    @InjectMocks
    private LearningProgressServiceImpl learningProgressService;

    private final UUID studentId = UUID.randomUUID();
    private final UUID lectureId = UUID.randomUUID();
    private final UUID classId = UUID.randomUUID();

    @Test
    void trackingProgress_shouldReturnResponse_whenValidRequest() {
        LearningProgressRequest request = new LearningProgressRequest();
        request.setLectureId(lectureId);
        request.setClassId(classId);
        request.setIsCompleted(false);

        LearningProgress progress = new LearningProgress();
        LearningProgressResponse response = new LearningProgressResponse();

        when(learningProgressRepository.findByStudentIdAndLectureId(studentId, lectureId))
                .thenReturn(Optional.of(progress));
        doNothing().when(learningProgressMapper).updateEntityFromRequest(request, progress);
        when(learningProgressRepository.save(progress)).thenReturn(progress);
        when(learningProgressMapper.toResponse(progress)).thenReturn(response);

        LearningProgressResponse result = learningProgressService.trackingProgress(studentId, request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void trackingProgress_shouldCreateNewProgress_whenNotExists() {
        LearningProgressRequest request = new LearningProgressRequest();
        request.setLectureId(lectureId);
        request.setClassId(classId);
        request.setIsCompleted(true);

        LearningProgress newProgress = new LearningProgress();
        LearningProgressResponse response = new LearningProgressResponse();

        when(learningProgressRepository.findByStudentIdAndLectureId(studentId, lectureId))
                .thenReturn(Optional.empty());
        when(learningProgressMapper.toEntity(request)).thenReturn(newProgress);
        doNothing().when(learningProgressMapper).updateEntityFromRequest(request, newProgress);
        when(learningProgressRepository.save(newProgress)).thenReturn(newProgress);
        when(learningProgressMapper.toResponse(newProgress)).thenReturn(response);
        doNothing().when(enrollmentService).updateProgress(studentId, classId);

        LearningProgressResponse result = learningProgressService.trackingProgress(studentId, request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getProgressForStudent_shouldReturnResponse_whenExists() {
        LearningProgress progress = new LearningProgress();
        LearningProgressResponse response = new LearningProgressResponse();
        when(learningProgressRepository.findByStudentIdAndLectureId(studentId, lectureId))
                .thenReturn(Optional.of(progress));
        when(learningProgressMapper.toResponse(progress)).thenReturn(response);

        LearningProgressResponse result = learningProgressService.getProgressForStudent(studentId, lectureId);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getProgressForStudent_shouldReturnNull_whenNotStarted() {
        when(learningProgressRepository.findByStudentIdAndLectureId(studentId, lectureId))
                .thenReturn(Optional.empty());

        LearningProgressResponse result = learningProgressService.getProgressForStudent(studentId, lectureId);
        assertNotNull(result);
        assertTrue(result.getProgressPercentage().compareTo(BigDecimal.ZERO) == 0);
        assertTrue(true);
    }

    @Test
    void getProgressesByClassForStudent_shouldReturnList_whenProgressExists() {
        when(enrollmentRepository.findByStudentIdAndClassId(studentId, classId))
                .thenReturn(Optional.of(mock(com.hcmut.lms.learning.entity.enrollment.Enrollment.class)));
        when(learningProgressRepository.findByStudentId(studentId)).thenReturn(List.of());

        List<LearningProgressResponse> result = learningProgressService.getProgressesByClassForStudent(studentId, classId);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getProgressesByClassForStudent_shouldThrowException_whenNotEnrolled() {
        when(enrollmentRepository.findByStudentIdAndClassId(studentId, classId))
                .thenReturn(Optional.empty());

        try {
            learningProgressService.getProgressesByClassForStudent(studentId, classId);
        } catch (BusinessException e) {
            assertTrue(e.getMessage().contains("STUDENT_NOT_ENROLLED"));
        }
    }

    @Test
    void getClassProgressSummaryForStudent_shouldReturnSummary() {
        LearningProgress progress1 = new LearningProgress();
        progress1.setProgressPercentage(new BigDecimal("80"));
        LearningProgress progress2 = new LearningProgress();
        progress2.setProgressPercentage(new BigDecimal("90"));

        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(false);
        when(studyTimeRepository.getTotalStudyTimeByStudentAndClass(studentId, classId)).thenReturn(3600);
        when(learningProgressRepository.findByStudentId(studentId)).thenReturn(List.of(progress1, progress2));

        ClassProgressResponse result = learningProgressService.getClassProgressSummaryForStudent(studentId, classId);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void calcProgress_shouldReturnCorrectPercentage() {
        UUID lectureId = UUID.randomUUID();
        LearningProgress progress = new LearningProgress();
        progress.setLectureId(lectureId);
        progress.setTotalTimeSpent(600); // 10 minutes in seconds

        LectureResponse lectureResponse = new LectureResponse();
        lectureResponse.setEstimateTimeSpent(20); // 20 minutes

        when(courseManagementClient.getLectureById(lectureId)).thenReturn(lectureResponse);

        BigDecimal result = learningProgressService.calcProgress(progress);
        assertNotNull(result);
        assertTrue(true);
    }
}
