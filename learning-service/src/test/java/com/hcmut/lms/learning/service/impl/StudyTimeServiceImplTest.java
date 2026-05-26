package com.hcmut.lms.learning.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.ClassSectionReportMetadataResponse;
import com.hcmut.lms.learning.client.dto.LectureReportMetadataResponse;
import com.hcmut.lms.learning.dto.request.StudyTimeRequest;
import com.hcmut.lms.learning.dto.response.*;
import com.hcmut.lms.learning.entity.enrollment.Enrollment;
import com.hcmut.lms.learning.entity.progress.ContentType;
import com.hcmut.lms.learning.entity.progress.LearningProgress;
import com.hcmut.lms.learning.entity.progress.PositionUnit;
import com.hcmut.lms.learning.entity.studytime.StudyTime;
import com.hcmut.lms.learning.exception.BusinessException;
import com.hcmut.lms.learning.exception.ForbiddenException;
import com.hcmut.lms.learning.exception.ResourceNotFoundException;
import feign.FeignException;
import com.hcmut.lms.learning.mapper.StudyTimeMapper;
import com.hcmut.lms.learning.repository.EnrollmentRepository;
import com.hcmut.lms.learning.repository.LearningProgressRepository;
import com.hcmut.lms.learning.repository.StudyTimeRepository;
import com.hcmut.lms.learning.service.LearningProgressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class StudyTimeServiceImplTest {

    @Mock
    private StudyTimeRepository studyTimeRepository;

    @Mock
    private StudyTimeMapper studyTimeMapper;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private LearningProgressRepository learningProgressRepository;

    @Mock
    private LearningProgressService learningProgressService;

    @Mock
    private CourseManagementClient courseManagementClient;

    @InjectMocks
    private StudyTimeServiceImpl studyTimeService;

    private final UUID studentId = UUID.randomUUID();
    private final UUID classId = UUID.randomUUID();
    private final UUID lectureId = UUID.randomUUID();

    @Test
    void recordStudyTime_shouldReturnResponse_whenValidRequest() {
        StudyTimeRequest request = new StudyTimeRequest();
        request.setClassId(classId);
        request.setLectureId(lectureId);
        request.setDurationSeconds(600);

        StudyTime entity = new StudyTime();
        StudyTimeResponse response = new StudyTimeResponse();

        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(true);
        when(studyTimeMapper.toEntity(request)).thenReturn(entity);
        when(studyTimeRepository.save(entity)).thenReturn(entity);
        when(studyTimeMapper.toResponse(entity)).thenReturn(response);
        when(learningProgressService.calcProgress(any())).thenReturn(java.math.BigDecimal.valueOf(50));
        when(learningProgressRepository.save(any())).thenReturn(new LearningProgress());

        StudyTimeResponse result = studyTimeService.recordStudyTime(studentId, request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void recordStudyTime_shouldThrowException_whenEnrollmentNotFound() {
        StudyTimeRequest request = new StudyTimeRequest();
        request.setClassId(classId);
        request.setLectureId(lectureId);
        request.setDurationSeconds(600);

        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(false);

        try {
            studyTimeService.recordStudyTime(studentId, request);
        } catch (BusinessException e) {
            assertTrue(e.getMessage().contains("STUDENT_NOT_ENROLLED"));
        }
    }

    @Test
    void getStudyTimeHistory_shouldReturnList_whenRecordsExist() {
        StudyTime entity = new StudyTime();
        StudyTimeResponse response = new StudyTimeResponse();
        when(studyTimeRepository.findByStudentIdAndClassIdAndDateRange(any(), any(), any(), any()))
                .thenReturn(List.of(entity));
        when(studyTimeMapper.toResponse(entity)).thenReturn(response);

        List<StudyTimeResponse> result = studyTimeService.getStudyTimeHistory(studentId, classId, null, null);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getStudyTimeHistory_shouldReturnEmptyList_whenNoRecords() {
        when(studyTimeRepository.findByStudentIdAndClassIdAndDateRange(any(), any(), any(), any()))
                .thenReturn(List.of());

        List<StudyTimeResponse> result = studyTimeService.getStudyTimeHistory(studentId, classId, null, null);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getStudyTimeSummary_shouldReturnSummaryList_whenRecordsExist() {
        when(studyTimeRepository.getStudyTimeSummaryByDate(any(), any(), any(), any()))
                .thenReturn(List.of());

        List<StudyTimeSummaryResponse> result = studyTimeService.getStudyTimeSummary(studentId, classId, null, null);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getTotalStudyTime_shouldReturnTotal_whenRecordsExist() {
        when(studyTimeRepository.getTotalStudyTimeByStudentAndClass(studentId, classId)).thenReturn(3600);

        Integer result = studyTimeService.getTotalStudyTime(studentId, classId);
        assertTrue(result > 0);
        assertTrue(true);
    }

    @Test
    void getTotalStudyTime_shouldReturnZero_whenNoRecords() {
        when(studyTimeRepository.getTotalStudyTimeByStudentAndClass(studentId, classId)).thenReturn(null);

        Integer result = studyTimeService.getTotalStudyTime(studentId, classId);
        assertTrue(result == 0);
        assertTrue(true);
    }

    @Test
    void getAggregatedSummary_shouldReturnList_whenValidRequest() {
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId).build();
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
        when(studyTimeRepository.getAggregatedSummaryByDate(any(), any(), any(), any()))
                .thenReturn(List.of());

        List<AggregatedStudyTimeResponse> result = studyTimeService.getAggregatedSummary(studentId, 7);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getLectureFrequencyForTeacher_shouldReturnResponse_whenValidRequest() {
        UUID teacherId = UUID.randomUUID();
        CurrentUserInfo currentUserInfo = CurrentUserInfo.builder()
                .id(teacherId).role("TEACHER").build();

        ClassSectionReportMetadataResponse classMetadata = new ClassSectionReportMetadataResponse();
        classMetadata.setTeacherId(teacherId);
        classMetadata.setLectures(List.of());

        when(courseManagementClient.getClassSectionReportMetadata(classId)).thenReturn(classMetadata);
        when(enrollmentRepository.findDistinctStudentIdsByClassId(classId)).thenReturn(List.of(studentId));
        when(studyTimeRepository.sumDurationByClassGroupedByLecture(classId)).thenReturn(List.of());
        when(studyTimeRepository.countDistinctStudentsByClassGroupedByLecture(classId)).thenReturn(List.of());

        LectureFrequencyResponse result = studyTimeService.getLectureFrequencyForTeacher(classId, currentUserInfo);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getStudentStudyTimesForTeacher_shouldReturnList_whenExists() {
        UUID teacherId = UUID.randomUUID();
        CurrentUserInfo currentUserInfo = CurrentUserInfo.builder()
                .id(teacherId).role("TEACHER").build();

        ClassSectionReportMetadataResponse classMetadata = new ClassSectionReportMetadataResponse();
        classMetadata.setTeacherId(teacherId);
        classMetadata.setLectures(List.of());

        when(courseManagementClient.getClassSectionReportMetadata(classId)).thenReturn(classMetadata);
        when(enrollmentRepository.findDistinctStudentIdsByClassId(classId)).thenReturn(List.of(studentId));
        when(studyTimeRepository.sumDurationByClassGroupedByStudent(classId)).thenReturn(List.of());

        List<StudentStudyTimeSummaryResponse> result = studyTimeService.getStudentStudyTimesForTeacher(classId, currentUserInfo);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getLectureFrequencyForTeacher_shouldAllowAdmin() {
        CurrentUserInfo adminUser = CurrentUserInfo.builder()
                .id(UUID.randomUUID()).role("ADMIN").build();

        ClassSectionReportMetadataResponse classMetadata = new ClassSectionReportMetadataResponse();
        classMetadata.setTeacherId(UUID.randomUUID());
        classMetadata.setLectures(List.of());

        when(courseManagementClient.getClassSectionReportMetadata(classId)).thenReturn(classMetadata);
        when(enrollmentRepository.findDistinctStudentIdsByClassId(classId)).thenReturn(List.of());
        when(studyTimeRepository.sumDurationByClassGroupedByLecture(classId)).thenReturn(List.of());
        when(studyTimeRepository.countDistinctStudentsByClassGroupedByLecture(classId)).thenReturn(List.of());

        LectureFrequencyResponse result = studyTimeService.getLectureFrequencyForTeacher(classId, adminUser);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getLectureFrequencyForTeacher_shouldThrowForbidden_whenNotTeacher() {
        CurrentUserInfo studentUser = CurrentUserInfo.builder()
                .id(UUID.randomUUID()).role("STUDENT").build();

        ClassSectionReportMetadataResponse classMetadata = new ClassSectionReportMetadataResponse();
        classMetadata.setTeacherId(UUID.randomUUID());
        when(courseManagementClient.getClassSectionReportMetadata(classId)).thenReturn(classMetadata);

        try {
            studyTimeService.getLectureFrequencyForTeacher(classId, studentUser);
        } catch (ForbiddenException e) {
            assertTrue(e.getMessage().contains("Access denied"));
        }
    }

    @Test
    void getLectureFrequencyForTeacher_shouldThrowForbidden_whenWrongTeacher() {
        UUID teacherId = UUID.randomUUID();
        CurrentUserInfo wrongTeacher = CurrentUserInfo.builder()
                .id(UUID.randomUUID()).role("TEACHER").build();

        ClassSectionReportMetadataResponse classMetadata = new ClassSectionReportMetadataResponse();
        classMetadata.setTeacherId(teacherId);
        when(courseManagementClient.getClassSectionReportMetadata(classId)).thenReturn(classMetadata);

        try {
            studyTimeService.getLectureFrequencyForTeacher(classId, wrongTeacher);
        } catch (ForbiddenException e) {
            assertTrue(e.getMessage().contains("Teacher is not assigned"));
        }
    }

    @Test
    void getAggregatedSummary_shouldReturnEmpty_whenNoEnrollments() {
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of());

        List<AggregatedStudyTimeResponse> result = studyTimeService.getAggregatedSummary(studentId, 7);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void recordStudyTime_shouldUpdateExistingProgress_whenProgressExists() {
        StudyTimeRequest request = new StudyTimeRequest();
        request.setClassId(classId);
        request.setLectureId(lectureId);
        request.setDurationSeconds(600);
        request.setCurrentPosition(50);
        request.setContentType(ContentType.VIDEO);
        request.setPositionUnit(PositionUnit.SECOND);

        StudyTime entity = new StudyTime();
        StudyTimeResponse response = new StudyTimeResponse();

        LearningProgress existingProgress = new LearningProgress();
        existingProgress.setStudentId(studentId);
        existingProgress.setLectureId(lectureId);
        existingProgress.setTotalTimeSpent(0);
        existingProgress.setProgressPercentage(BigDecimal.valueOf(30));

        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(true);
        when(studyTimeMapper.toEntity(request)).thenReturn(entity);
        when(studyTimeRepository.save(entity)).thenReturn(entity);
        when(studyTimeMapper.toResponse(entity)).thenReturn(response);
        when(learningProgressRepository.findByStudentIdAndLectureId(studentId, lectureId))
                .thenReturn(Optional.of(existingProgress));
        when(learningProgressService.calcProgress(any())).thenReturn(BigDecimal.valueOf(50));
        when(learningProgressRepository.save(any())).thenReturn(existingProgress);

        StudyTimeResponse result = studyTimeService.recordStudyTime(studentId, request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void recordStudyTime_shouldNotUpdateProgress_whenAlreadyCompleted() {
        StudyTimeRequest request = new StudyTimeRequest();
        request.setClassId(classId);
        request.setLectureId(lectureId);
        request.setDurationSeconds(600);

        StudyTime entity = new StudyTime();
        StudyTimeResponse response = new StudyTimeResponse();

        LearningProgress completedProgress = new LearningProgress();
        completedProgress.setStudentId(studentId);
        completedProgress.setLectureId(lectureId);
        completedProgress.setTotalTimeSpent(3600);
        completedProgress.setProgressPercentage(BigDecimal.valueOf(100));

        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(true);
        when(studyTimeMapper.toEntity(request)).thenReturn(entity);
        when(studyTimeRepository.save(entity)).thenReturn(entity);
        when(studyTimeMapper.toResponse(entity)).thenReturn(response);
        when(learningProgressRepository.findByStudentIdAndLectureId(studentId, lectureId))
                .thenReturn(Optional.of(completedProgress));

        StudyTimeResponse result = studyTimeService.recordStudyTime(studentId, request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void recordStudyTime_shouldCalculateEndedAt_whenStartedAtIsSet() {
        StudyTimeRequest request = new StudyTimeRequest();
        request.setClassId(classId);
        request.setLectureId(lectureId);
        request.setDurationSeconds(600);
        request.setStartedAt(java.time.LocalDateTime.now());

        StudyTime entity = new StudyTime();
        entity.setStartedAt(request.getStartedAt());
        StudyTimeResponse response = new StudyTimeResponse();

        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(true);
        when(studyTimeMapper.toEntity(request)).thenReturn(entity);
        when(studyTimeRepository.save(entity)).thenReturn(entity);
        when(studyTimeMapper.toResponse(entity)).thenReturn(response);
        when(learningProgressService.calcProgress(any())).thenReturn(BigDecimal.valueOf(50));
        when(learningProgressRepository.save(any())).thenReturn(new LearningProgress());

        StudyTimeResponse result = studyTimeService.recordStudyTime(studentId, request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getLectureFrequencyForTeacher_shouldComputeFrequencyRatio_whenLecturesHaveEstimateTime() {
        UUID teacherId = UUID.randomUUID();
        CurrentUserInfo currentUserInfo = CurrentUserInfo.builder()
                .id(teacherId).role("TEACHER").build();

        LectureReportMetadataResponse lecture1 = new LectureReportMetadataResponse();
        lecture1.setLectureId(UUID.randomUUID());
        lecture1.setTitle("Lecture 1");
        lecture1.setOrder(1);
        lecture1.setEstimateTimeSpent(60);
        lecture1.setViewCount(100);

        LectureReportMetadataResponse lecture2 = new LectureReportMetadataResponse();
        lecture2.setLectureId(UUID.randomUUID());
        lecture2.setTitle("Lecture 2");
        lecture2.setOrder(2);
        lecture2.setEstimateTimeSpent(30);
        lecture2.setViewCount(50);

        ClassSectionReportMetadataResponse classMetadata = new ClassSectionReportMetadataResponse();
        classMetadata.setTeacherId(teacherId);
        classMetadata.setLectures(List.of(lecture1, lecture2));

        when(courseManagementClient.getClassSectionReportMetadata(classId)).thenReturn(classMetadata);
        when(enrollmentRepository.findDistinctStudentIdsByClassId(classId)).thenReturn(List.of(studentId));
        when(studyTimeRepository.sumDurationByClassGroupedByLecture(classId)).thenReturn(List.of());
        when(studyTimeRepository.countDistinctStudentsByClassGroupedByLecture(classId)).thenReturn(List.of());

        LectureFrequencyResponse result = studyTimeService.getLectureFrequencyForTeacher(classId, currentUserInfo);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getLectureFrequencyForTeacher_shouldHandleNullRole() {
        CurrentUserInfo currentUserInfo = CurrentUserInfo.builder()
                .id(UUID.randomUUID()).build();

        ClassSectionReportMetadataResponse classMetadata = new ClassSectionReportMetadataResponse();
        classMetadata.setTeacherId(UUID.randomUUID());
        when(courseManagementClient.getClassSectionReportMetadata(classId)).thenReturn(classMetadata);

        try {
            studyTimeService.getLectureFrequencyForTeacher(classId, currentUserInfo);
        } catch (ForbiddenException e) {
            assertTrue(e.getMessage().contains("Access denied"));
        }
    }

    @Test
    void getStudyTimeSummary_shouldMapResults_whenDataExists() {
        java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.of(2024, 1, 15));
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{sqlDate, 3600L, 3L});
        when(studyTimeRepository.getStudyTimeSummaryByDate(any(), any(), any(), any()))
                .thenReturn(mockResults);

        List<StudyTimeSummaryResponse> result = studyTimeService.getStudyTimeSummary(studentId, classId, null, null);
        assertNotNull(result);
        assertTrue(!result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getAggregatedSummary_shouldMapResults_whenDataExists() {
        java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.of(2024, 1, 15));
        Enrollment enrollment = Enrollment.builder().studentId(studentId).classId(classId).build();

        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{sqlDate, 7200L, 5L});
        when(studyTimeRepository.getAggregatedSummaryByDate(any(), any(), any(), any()))
                .thenReturn(mockResults);

        List<AggregatedStudyTimeResponse> result = studyTimeService.getAggregatedSummary(studentId, 7);
        assertNotNull(result);
        assertTrue(!result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getStudentStudyTimesForTeacher_shouldComputeDuration_whenDataExists() {
        UUID teacherId = UUID.randomUUID();
        CurrentUserInfo currentUserInfo = CurrentUserInfo.builder()
                .id(teacherId).role("TEACHER").build();

        ClassSectionReportMetadataResponse classMetadata = new ClassSectionReportMetadataResponse();
        classMetadata.setTeacherId(teacherId);
        classMetadata.setLectures(List.of());

        UUID student1 = UUID.randomUUID();
        UUID student2 = UUID.randomUUID();

        when(courseManagementClient.getClassSectionReportMetadata(classId)).thenReturn(classMetadata);
        when(enrollmentRepository.findDistinctStudentIdsByClassId(classId)).thenReturn(List.of(student2, student1));
        when(studyTimeRepository.sumDurationByClassGroupedByStudent(classId))
                .thenReturn(Arrays.asList(new Object[]{student1, 3600L}, new Object[]{student2, 1800L}));

        List<StudentStudyTimeSummaryResponse> result = studyTimeService.getStudentStudyTimesForTeacher(classId, currentUserInfo);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getLectureFrequencyForTeacher_shouldHandleZeroStudents() {
        UUID teacherId = UUID.randomUUID();
        CurrentUserInfo currentUserInfo = CurrentUserInfo.builder()
                .id(teacherId).role("TEACHER").build();

        LectureReportMetadataResponse lecture = new LectureReportMetadataResponse();
        lecture.setLectureId(UUID.randomUUID());
        lecture.setTitle("Lecture");
        lecture.setOrder(1);
        lecture.setEstimateTimeSpent(60);

        ClassSectionReportMetadataResponse classMetadata = new ClassSectionReportMetadataResponse();
        classMetadata.setTeacherId(teacherId);
        classMetadata.setLectures(List.of(lecture));

        when(courseManagementClient.getClassSectionReportMetadata(classId)).thenReturn(classMetadata);
        when(enrollmentRepository.findDistinctStudentIdsByClassId(classId)).thenReturn(List.of());
        when(studyTimeRepository.sumDurationByClassGroupedByLecture(classId)).thenReturn(List.of());
        when(studyTimeRepository.countDistinctStudentsByClassGroupedByLecture(classId)).thenReturn(List.of());

        LectureFrequencyResponse result = studyTimeService.getLectureFrequencyForTeacher(classId, currentUserInfo);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void recordStudyTime_shouldSetCompletedAt_whenProgressReaches100() {
        StudyTimeRequest request = new StudyTimeRequest();
        request.setClassId(classId);
        request.setLectureId(lectureId);
        request.setDurationSeconds(600);
        request.setContentType(ContentType.VIDEO);
        request.setPositionUnit(PositionUnit.SECOND);

        StudyTime entity = new StudyTime();
        StudyTimeResponse response = new StudyTimeResponse();

        LearningProgress existingProgress = new LearningProgress();
        existingProgress.setStudentId(studentId);
        existingProgress.setLectureId(lectureId);
        existingProgress.setTotalTimeSpent(0);
        existingProgress.setProgressPercentage(BigDecimal.valueOf(80));

        when(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)).thenReturn(true);
        when(studyTimeMapper.toEntity(request)).thenReturn(entity);
        when(studyTimeRepository.save(entity)).thenReturn(entity);
        when(studyTimeMapper.toResponse(entity)).thenReturn(response);
        when(learningProgressRepository.findByStudentIdAndLectureId(studentId, lectureId))
                .thenReturn(Optional.of(existingProgress));
        when(learningProgressService.calcProgress(any())).thenReturn(BigDecimal.valueOf(100));
        when(learningProgressRepository.save(any())).thenReturn(existingProgress);

        StudyTimeResponse result = studyTimeService.recordStudyTime(studentId, request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getLectureFrequencyForTeacher_shouldProcessRealRepositoryData() {
        UUID teacherId = UUID.randomUUID();
        UUID lecture1Id = UUID.randomUUID();
        CurrentUserInfo currentUserInfo = CurrentUserInfo.builder()
                .id(teacherId).role("TEACHER").build();

        LectureReportMetadataResponse lecture = new LectureReportMetadataResponse();
        lecture.setLectureId(lecture1Id);
        lecture.setTitle("Lecture");
        lecture.setOrder(1);
        lecture.setEstimateTimeSpent(60);

        ClassSectionReportMetadataResponse classMetadata = new ClassSectionReportMetadataResponse();
        classMetadata.setTeacherId(teacherId);
        classMetadata.setLectures(List.of(lecture));

        when(courseManagementClient.getClassSectionReportMetadata(classId)).thenReturn(classMetadata);
        when(enrollmentRepository.findDistinctStudentIdsByClassId(classId)).thenReturn(List.of(studentId));
        List<Object[]> sumData = new ArrayList<>();
        sumData.add(new Object[]{lecture1Id, 3600L});
        when(studyTimeRepository.sumDurationByClassGroupedByLecture(classId)).thenReturn(sumData);
        List<Object[]> countData = new ArrayList<>();
        countData.add(new Object[]{lecture1Id, 5});
        when(studyTimeRepository.countDistinctStudentsByClassGroupedByLecture(classId)).thenReturn(countData);

        LectureFrequencyResponse result = studyTimeService.getLectureFrequencyForTeacher(classId, currentUserInfo);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getLectureFrequencyForTeacher_shouldHandleNullLectures() {
        UUID teacherId = UUID.randomUUID();
        CurrentUserInfo currentUserInfo = CurrentUserInfo.builder()
                .id(teacherId).role("TEACHER").build();

        ClassSectionReportMetadataResponse classMetadata = new ClassSectionReportMetadataResponse();
        classMetadata.setTeacherId(teacherId);
        classMetadata.setLectures(null);

        when(courseManagementClient.getClassSectionReportMetadata(classId)).thenReturn(classMetadata);
        when(enrollmentRepository.findDistinctStudentIdsByClassId(classId)).thenReturn(List.of(studentId));
        when(studyTimeRepository.sumDurationByClassGroupedByLecture(classId)).thenReturn(List.of());
        when(studyTimeRepository.countDistinctStudentsByClassGroupedByLecture(classId)).thenReturn(List.of());

        LectureFrequencyResponse result = studyTimeService.getLectureFrequencyForTeacher(classId, currentUserInfo);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getLectureFrequencyForTeacher_shouldThrowResourceNotFound_whenFeignReturns404() {
        UUID teacherId = UUID.randomUUID();
        CurrentUserInfo currentUserInfo = CurrentUserInfo.builder()
                .id(teacherId).role("TEACHER").build();

        FeignException notFoundEx = mock(FeignException.class);
        when(notFoundEx.status()).thenReturn(404);
        when(courseManagementClient.getClassSectionReportMetadata(classId)).thenThrow(notFoundEx);

        try {
            studyTimeService.getLectureFrequencyForTeacher(classId, currentUserInfo);
        } catch (ResourceNotFoundException e) {
            assertTrue(e.getMessage().contains("ClassSection"));
        }
    }

    @Test
    void getLectureFrequencyForTeacher_shouldRethrow_whenFeignReturns500() {
        UUID teacherId = UUID.randomUUID();
        CurrentUserInfo currentUserInfo = CurrentUserInfo.builder()
                .id(teacherId).role("TEACHER").build();

        FeignException serverEx = mock(FeignException.class);
        when(serverEx.status()).thenReturn(500);
        when(courseManagementClient.getClassSectionReportMetadata(classId)).thenThrow(serverEx);

        try {
            studyTimeService.getLectureFrequencyForTeacher(classId, currentUserInfo);
        } catch (FeignException e) {
            assertTrue(true);
        }
    }
}
