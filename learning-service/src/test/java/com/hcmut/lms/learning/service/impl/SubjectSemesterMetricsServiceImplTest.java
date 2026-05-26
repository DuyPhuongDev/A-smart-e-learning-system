package com.hcmut.lms.learning.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.ClassSectionDatasetResponse;
import com.hcmut.lms.learning.client.dto.SemesterResponse;
import com.hcmut.lms.learning.entity.enrollment.Enrollment;
import com.hcmut.lms.learning.entity.subject.SubjectSemesterMetrics;
import com.hcmut.lms.learning.repository.EnrollmentRepository;
import com.hcmut.lms.learning.repository.SubjectSemesterMetricsRepository;
import com.hcmut.lms.learning.service.GradeSemesterMetricsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class SubjectSemesterMetricsServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private SubjectSemesterMetricsRepository metricsRepository;

    @Mock
    private CourseManagementClient courseManagementClient;

    @Mock
    private GradeSemesterMetricsService gradeSemesterMetricsService;

    @InjectMocks
    private SubjectSemesterMetricsServiceImpl subjectSemesterMetricsService;

    private final UUID subjectId = UUID.randomUUID();
    private final UUID semesterId = UUID.randomUUID();
    private final UUID classId = UUID.randomUUID();

    @Test
    void computeAndSaveAllMetrics_shouldProcessEnrollments_whenDataAvailable() {
        // Enrollment with grade
        Enrollment enrollment = Enrollment.builder()
                .studentId(UUID.randomUUID()).classId(classId).finalGrade(8.0).build();
        when(enrollmentRepository.findByFinalGradeIsNotNull()).thenReturn(List.of(enrollment));

        // Class metadata
        ClassSectionDatasetResponse classMeta = ClassSectionDatasetResponse.builder()
                .classId(classId).subjectId(subjectId).semesterId(semesterId)
                .semKey(20231).credits(3).curriculumSectionId(UUID.randomUUID()).build();
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(classMeta));

        // All subjects and semesters
        when(courseManagementClient.getAllSubjectIds()).thenReturn(List.of(subjectId));
        SemesterResponse semesterResponse = SemesterResponse.builder()
                .id(semesterId).semKey(20231).semesterCode("231").build();
        when(courseManagementClient.getAllSemesters()).thenReturn(List.of(semesterResponse));
        when(courseManagementClient.getCurrentSemester()).thenReturn(semesterResponse);

        // Batch upsert for semester global metrics
        doNothing().when(gradeSemesterMetricsService).upsertAll(any());
        // Batch upsert for subject semester metrics
        doNothing().when(metricsRepository).batchUpsert(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());

        int result = subjectSemesterMetricsService.computeAndSaveAllMetrics();
        assertTrue(result > 0);
        assertTrue(true);
    }

    @Test
    void computeAndSaveAllMetrics_shouldReturnZero_whenNoEnrollmentsAndNoSubjects() {
        when(enrollmentRepository.findByFinalGradeIsNotNull()).thenReturn(List.of());
        when(courseManagementClient.getAllSubjectIds()).thenReturn(List.of());

        int result = subjectSemesterMetricsService.computeAndSaveAllMetrics();
        assertTrue(result == 0);
        assertTrue(true);
    }

    @Test
    void findBySubjectIdAndSemesterId_shouldReturnMetrics_whenExists() {
        SubjectSemesterMetrics metrics = new SubjectSemesterMetrics();
        when(metricsRepository.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));

        Optional<SubjectSemesterMetrics> result = subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId);
        assertTrue(result.isPresent());
        assertTrue(true);
    }

    @Test
    void findBySubjectIdAndSemesterId_shouldReturnEmpty_whenNotFound() {
        when(metricsRepository.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.empty());

        Optional<SubjectSemesterMetrics> result = subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getMetricsHistoryForSubject_shouldReturnList_whenHistoryExists() {
        SubjectSemesterMetrics metrics = new SubjectSemesterMetrics();
        when(metricsRepository.findBySubjectIdOrderByCreatedAtAsc(subjectId)).thenReturn(List.of(metrics));

        List<SubjectSemesterMetrics> result = subjectSemesterMetricsService.getMetricsHistoryForSubject(subjectId);
        assertTrue(!result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getMetricsHistoryForSubject_shouldReturnEmptyList_whenNoHistory() {
        when(metricsRepository.findBySubjectIdOrderByCreatedAtAsc(subjectId)).thenReturn(List.of());

        List<SubjectSemesterMetrics> result = subjectSemesterMetricsService.getMetricsHistoryForSubject(subjectId);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void batchLoadMetrics_shouldReturnMap_whenValidInput() {
        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId).build();
        when(metricsRepository.findBySubjectIdsAndSemesterIds(any(), any())).thenReturn(List.of(metrics));

        Map<String, SubjectSemesterMetrics> result = subjectSemesterMetricsService.batchLoadMetrics(
                List.of(subjectId), List.of(semesterId));
        assertTrue(!result.isEmpty());
        assertTrue(true);
    }

    @Test
    void batchLoadMetrics_shouldReturnEmptyMap_whenEmptyInput() {
        Map<String, SubjectSemesterMetrics> result = subjectSemesterMetricsService.batchLoadMetrics(List.of(), List.of());
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getBatchDifficulty_shouldReturnMap_whenSubjectsExist() {
        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).meanGrade(7.0).build();
        when(metricsRepository.findBySubjectIdOrderByCreatedAtAsc(subjectId)).thenReturn(List.of(metrics));

        Map<UUID, String> result = subjectSemesterMetricsService.getBatchDifficulty(List.of(subjectId));
        assertNotNull(result);
        assertTrue(result.containsKey(subjectId));
        assertTrue(true);
    }

    @Test
    void getBatchDifficulty_shouldReturnEmptyMap_whenNullInput() {
        Map<UUID, String> result = subjectSemesterMetricsService.getBatchDifficulty(null);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getBatchDifficulty_shouldReturnMedium_whenNoMetrics() {
        when(metricsRepository.findBySubjectIdOrderByCreatedAtAsc(subjectId)).thenReturn(List.of());
        Map<UUID, String> result = subjectSemesterMetricsService.getBatchDifficulty(List.of(subjectId));
        assertTrue("medium".equals(result.get(subjectId)));
        assertTrue(true);
    }

    @Test
    void getBatchDifficulty_shouldReturnHard_whenMeanGradeLow() {
        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).meanGrade(4.0).build();
        when(metricsRepository.findBySubjectIdOrderByCreatedAtAsc(subjectId)).thenReturn(List.of(metrics));

        Map<UUID, String> result = subjectSemesterMetricsService.getBatchDifficulty(List.of(subjectId));
        assertTrue("hard".equals(result.get(subjectId)));
        assertTrue(true);
    }

    @Test
    void getBatchDifficulty_shouldReturnEasy_whenMeanGradeHigh() {
        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).meanGrade(9.0).build();
        when(metricsRepository.findBySubjectIdOrderByCreatedAtAsc(subjectId)).thenReturn(List.of(metrics));

        Map<UUID, String> result = subjectSemesterMetricsService.getBatchDifficulty(List.of(subjectId));
        assertTrue("easy".equals(result.get(subjectId)));
        assertTrue(true);
    }

    @Test
    void computeAndSaveAllMetrics_shouldReturnZero_whenFetchAllSubjectsFails() {
        Enrollment enrollment = Enrollment.builder()
                .studentId(UUID.randomUUID()).classId(classId).finalGrade(8.0).build();
        when(enrollmentRepository.findByFinalGradeIsNotNull()).thenReturn(List.of(enrollment));

        ClassSectionDatasetResponse classMeta = ClassSectionDatasetResponse.builder()
                .classId(classId).subjectId(subjectId).semesterId(semesterId)
                .semKey(20231).credits(3).curriculumSectionId(UUID.randomUUID()).build();
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(classMeta));
        when(courseManagementClient.getAllSubjectIds()).thenThrow(new RuntimeException("Service unavailable"));

        int result = subjectSemesterMetricsService.computeAndSaveAllMetrics();
        assertTrue(result == 0);
        assertTrue(true);
    }

    @Test
    void computeAndSaveAllMetrics_shouldReturnZero_whenFetchAllSemestersFails() {
        Enrollment enrollment = Enrollment.builder()
                .studentId(UUID.randomUUID()).classId(classId).finalGrade(8.0).build();
        when(enrollmentRepository.findByFinalGradeIsNotNull()).thenReturn(List.of(enrollment));

        ClassSectionDatasetResponse classMeta = ClassSectionDatasetResponse.builder()
                .classId(classId).subjectId(subjectId).semesterId(semesterId)
                .semKey(20231).credits(3).curriculumSectionId(UUID.randomUUID()).build();
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(classMeta));
        when(courseManagementClient.getAllSubjectIds()).thenReturn(List.of(subjectId));
        when(courseManagementClient.getAllSemesters()).thenThrow(new RuntimeException("Service unavailable"));

        int result = subjectSemesterMetricsService.computeAndSaveAllMetrics();
        assertTrue(result == 0);
        assertTrue(true);
    }

    @Test
    void computeAndSaveAllMetrics_shouldReturnZero_whenNoSubjects() {
        Enrollment enrollment = Enrollment.builder()
                .studentId(UUID.randomUUID()).classId(classId).finalGrade(8.0).build();
        when(enrollmentRepository.findByFinalGradeIsNotNull()).thenReturn(List.of(enrollment));

        ClassSectionDatasetResponse classMeta = ClassSectionDatasetResponse.builder()
                .classId(classId).subjectId(subjectId).semesterId(semesterId)
                .semKey(20231).credits(3).curriculumSectionId(UUID.randomUUID()).build();
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(classMeta));
        when(courseManagementClient.getAllSubjectIds()).thenReturn(List.of());
        when(courseManagementClient.getAllSemesters()).thenReturn(List.of());

        int result = subjectSemesterMetricsService.computeAndSaveAllMetrics();
        assertTrue(result == 0);
        assertTrue(true);
    }

    @Test
    void batchLoadMetrics_shouldReturnMap_whenDuplicateKeys() {
        UUID subjectId1 = UUID.randomUUID();
        UUID semesterId1 = UUID.randomUUID();
        SubjectSemesterMetrics m1 = SubjectSemesterMetrics.builder()
                .subjectId(subjectId1).semesterId(semesterId1).build();
        SubjectSemesterMetrics m2 = SubjectSemesterMetrics.builder()
                .subjectId(subjectId1).semesterId(semesterId1).build();
        when(metricsRepository.findBySubjectIdsAndSemesterIds(any(), any())).thenReturn(List.of(m1, m2));

        Map<String, SubjectSemesterMetrics> result = subjectSemesterMetricsService.batchLoadMetrics(
                List.of(subjectId1), List.of(semesterId1));
        assertTrue(result.size() == 1);
        assertTrue(true);
    }

    @Test
    void getBatchDifficulty_shouldReturnEmpty_whenEmptyList() {
        Map<UUID, String> result = subjectSemesterMetricsService.getBatchDifficulty(List.of());
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void computeAndSaveAllMetrics_shouldReturnZero_whenBothEmpty() {
        when(enrollmentRepository.findByFinalGradeIsNotNull()).thenReturn(List.of());
        when(courseManagementClient.getAllSubjectIds()).thenReturn(List.of());

        int result = subjectSemesterMetricsService.computeAndSaveAllMetrics();
        assertTrue(result == 0);
        assertTrue(true);
    }

    @Test
    void computeAndSaveAllMetrics_shouldComputeWithMultipleSemesters() {
        UUID subjectId2 = UUID.randomUUID();
        UUID semesterId2 = UUID.randomUUID();
        UUID classId2 = UUID.randomUUID();
        UUID studentId2 = UUID.randomUUID();

        Enrollment e1 = Enrollment.builder()
                .studentId(UUID.randomUUID()).classId(classId).finalGrade(8.0).build();
        Enrollment e2 = Enrollment.builder()
                .studentId(studentId2).classId(classId2).finalGrade(6.0).build();
        when(enrollmentRepository.findByFinalGradeIsNotNull()).thenReturn(List.of(e1, e2));

        ClassSectionDatasetResponse meta1 = ClassSectionDatasetResponse.builder()
                .classId(classId).subjectId(subjectId).semesterId(semesterId)
                .semKey(20221).credits(3).curriculumSectionId(UUID.randomUUID()).build();
        ClassSectionDatasetResponse meta2 = ClassSectionDatasetResponse.builder()
                .classId(classId2).subjectId(subjectId2).semesterId(semesterId2)
                .semKey(20231).credits(3).curriculumSectionId(UUID.randomUUID()).build();
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(meta1, meta2));

        when(courseManagementClient.getAllSubjectIds()).thenReturn(List.of(subjectId, subjectId2));

        SemesterResponse sem1 = SemesterResponse.builder()
                .id(semesterId).semKey(20221).semesterCode("221").build();
        SemesterResponse sem2 = SemesterResponse.builder()
                .id(semesterId2).semKey(20231).semesterCode("231").build();
        when(courseManagementClient.getAllSemesters()).thenReturn(List.of(sem1, sem2));
        when(courseManagementClient.getCurrentSemester()).thenReturn(sem2);

        doNothing().when(gradeSemesterMetricsService).upsertAll(any());
        doNothing().when(metricsRepository).batchUpsert(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());

        int result = subjectSemesterMetricsService.computeAndSaveAllMetrics();
        assertTrue(result > 0);
        assertTrue(true);
    }

    @Test
    void computeAndSaveAllMetrics_shouldHandleNullSemKey() {
        Enrollment enrollment = Enrollment.builder()
                .studentId(UUID.randomUUID()).classId(classId).finalGrade(8.0).build();
        when(enrollmentRepository.findByFinalGradeIsNotNull()).thenReturn(List.of(enrollment));

        ClassSectionDatasetResponse classMeta = ClassSectionDatasetResponse.builder()
                .classId(classId).subjectId(subjectId).semesterId(semesterId)
                .semKey(20231).credits(3).curriculumSectionId(UUID.randomUUID()).build();
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(classMeta));

        when(courseManagementClient.getAllSubjectIds()).thenReturn(List.of(subjectId));

        // Semester with null semKey
        SemesterResponse sem = SemesterResponse.builder()
                .id(semesterId).semKey(null).semesterCode("231").build();
        when(courseManagementClient.getAllSemesters()).thenReturn(List.of(sem));
        when(courseManagementClient.getCurrentSemester()).thenReturn(sem);

        int result = subjectSemesterMetricsService.computeAndSaveAllMetrics();
        assertTrue(result == 0);
        assertTrue(true);
    }
}
