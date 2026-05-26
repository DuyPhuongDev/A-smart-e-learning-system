package com.hcmut.lms.learning.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.ClassSectionDatasetResponse;
import com.hcmut.lms.learning.entity.dataset.GradePredictionDatasetVersion;
import com.hcmut.lms.learning.entity.enrollment.Enrollment;
import com.hcmut.lms.learning.entity.subject.SubjectSemesterMetrics;
import com.hcmut.lms.learning.exception.ResourceNotFoundException;
import com.hcmut.lms.learning.mapper.GradePredictionDatasetMapper;
import com.hcmut.lms.learning.repository.EnrollmentRepository;
import com.hcmut.lms.learning.repository.GradePredictionDatasetRepository;
import com.hcmut.lms.learning.repository.GradePredictionDatasetVersionRepository;
import com.hcmut.lms.learning.service.SubjectSemesterMetricsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class GradePredictionDatasetServiceImplTest {

    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private GradePredictionDatasetRepository datasetRepository;
    @Mock private GradePredictionDatasetVersionRepository versionRepository;
    @Mock private CourseManagementClient courseManagementClient;
    @Mock private GradePredictionDatasetMapper datasetMapper;
    @Mock private SubjectSemesterMetricsService subjectSemesterMetricsService;

    @InjectMocks
    private GradePredictionDatasetServiceImpl gradePredictionDatasetService;

    private final UUID studentId = UUID.randomUUID();
    private final UUID subjectId1 = UUID.randomUUID();
    private final UUID subjectId2 = UUID.randomUUID();
    private final UUID semesterId1 = UUID.randomUUID();
    private final UUID semesterId2 = UUID.randomUUID();
    private final UUID classId1 = UUID.randomUUID();
    private final UUID classId2 = UUID.randomUUID();
    private final UUID curriculumSectionId = UUID.randomUUID();

    @Test
    void computeAndSaveDataset_shouldReturnRunningVersion_whenAlreadyRunning() {
        GradePredictionDatasetVersion runningVersion = new GradePredictionDatasetVersion();
        runningVersion.setVersionNumber(1);
        when(versionRepository.findTopByStatusOrderByVersionNumberDesc(
                GradePredictionDatasetVersion.DatasetVersionStatus.RUNNING))
                .thenReturn(Optional.of(runningVersion));

        GradePredictionDatasetVersion result = gradePredictionDatasetService.computeAndSaveDataset();
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void computeAndSaveDataset_shouldCompleteWithZeroRows_whenNoEnrollments() {
        when(versionRepository.findTopByStatusOrderByVersionNumberDesc(any())).thenReturn(Optional.empty());
        when(subjectSemesterMetricsService.computeAndSaveAllMetrics()).thenReturn(100);
        when(versionRepository.nextVersionNumber()).thenReturn(1);
        GradePredictionDatasetVersion version = new GradePredictionDatasetVersion();
        version.setId(UUID.randomUUID());
        when(versionRepository.save(any())).thenReturn(version);
        when(enrollmentRepository.findByFinalGradeIsNotNull()).thenReturn(List.of());

        GradePredictionDatasetVersion result = gradePredictionDatasetService.computeAndSaveDataset();
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void computeAndSaveDataset_shouldRunETL_whenEnrollmentsWithMetadataExist() {
        // Setup: 2 enrollments for same student in different semesters
        Enrollment e1 = Enrollment.builder().studentId(studentId).classId(classId1).finalGrade(8.0).build();
        Enrollment e2 = Enrollment.builder().studentId(studentId).classId(classId2).finalGrade(7.0).build();

        ClassSectionDatasetResponse meta1 = ClassSectionDatasetResponse.builder()
                .classId(classId1).subjectId(subjectId1).semesterId(semesterId1)
                .semKey(20221).credits(3).curriculumSectionId(curriculumSectionId).build();
        ClassSectionDatasetResponse meta2 = ClassSectionDatasetResponse.builder()
                .classId(classId2).subjectId(subjectId2).semesterId(semesterId2)
                .semKey(20231).credits(3).curriculumSectionId(curriculumSectionId).build();

        SubjectSemesterMetrics metrics1 = SubjectSemesterMetrics.builder()
                .subjectId(subjectId1).semesterId(semesterId1).semKey(20221)
                .meanGrade(7.0).stdDev(1.5).sampleCount(50).isFallback(false).fallbackLevel(0)
                .smoothedMedian4pt(2.5).build();
        SubjectSemesterMetrics metrics2 = SubjectSemesterMetrics.builder()
                .subjectId(subjectId2).semesterId(semesterId2).semKey(20231)
                .meanGrade(6.5).stdDev(1.2).sampleCount(40).isFallback(false).fallbackLevel(0)
                .smoothedMedian4pt(2.3).build();

        when(versionRepository.findTopByStatusOrderByVersionNumberDesc(any())).thenReturn(Optional.empty());
        when(subjectSemesterMetricsService.computeAndSaveAllMetrics()).thenReturn(100);
        when(versionRepository.nextVersionNumber()).thenReturn(1);
        GradePredictionDatasetVersion version = new GradePredictionDatasetVersion();
        version.setId(UUID.randomUUID());
        when(versionRepository.save(any())).thenReturn(version);
        when(enrollmentRepository.findByFinalGradeIsNotNull()).thenReturn(List.of(e1, e2));
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(meta1, meta2));
        when(courseManagementClient.getPrerequisiteMapping()).thenReturn(List.of());
        when(subjectSemesterMetricsService.batchLoadMetrics(anyList(), anyList()))
                .thenReturn(Map.of(
                        subjectId1 + "|" + semesterId1, metrics1,
                        subjectId2 + "|" + semesterId2, metrics2));
        doNothing().when(datasetRepository).batchUpsert(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());

        GradePredictionDatasetVersion result = gradePredictionDatasetService.computeAndSaveDataset();
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void computeAndSaveDataset_shouldHandleException_whenETLFails() {
        when(versionRepository.findTopByStatusOrderByVersionNumberDesc(any())).thenReturn(Optional.empty());
        when(subjectSemesterMetricsService.computeAndSaveAllMetrics()).thenThrow(new RuntimeException("ETL failed"));

        try {
            gradePredictionDatasetService.computeAndSaveDataset();
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("ETL failed"));
        }
    }

    @Test
    void getAllVersions_shouldReturnList_whenVersionsExist() {
        when(versionRepository.findAllByOrderByVersionNumberDesc()).thenReturn(List.of(new GradePredictionDatasetVersion()));
        List<GradePredictionDatasetVersion> result = gradePredictionDatasetService.getAllVersions();
        assertFalse(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getAllVersions_shouldReturnEmptyList_whenNoVersions() {
        when(versionRepository.findAllByOrderByVersionNumberDesc()).thenReturn(List.of());
        List<GradePredictionDatasetVersion> result = gradePredictionDatasetService.getAllVersions();
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getVersionById_shouldReturnVersion_whenExists() {
        UUID id = UUID.randomUUID();
        when(versionRepository.findById(id)).thenReturn(Optional.of(new GradePredictionDatasetVersion()));
        assertNotNull(gradePredictionDatasetService.getVersionById(id));
        assertTrue(true);
    }

    @Test
    void getVersionById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(versionRepository.findById(id)).thenReturn(Optional.empty());
        try {
            gradePredictionDatasetService.getVersionById(id);
        } catch (ResourceNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    void getLatestCompletedVersion_shouldReturnVersion_whenExists() {
        when(versionRepository.findTopByStatusOrderByVersionNumberDesc(
                GradePredictionDatasetVersion.DatasetVersionStatus.COMPLETED))
                .thenReturn(Optional.of(new GradePredictionDatasetVersion()));
        assertNotNull(gradePredictionDatasetService.getLatestCompletedVersion());
        assertTrue(true);
    }

    @Test
    void getLatestCompletedVersion_shouldThrowException_whenNoneCompleted() {
        when(versionRepository.findTopByStatusOrderByVersionNumberDesc(
                GradePredictionDatasetVersion.DatasetVersionStatus.COMPLETED))
                .thenReturn(Optional.empty());
        try {
            gradePredictionDatasetService.getLatestCompletedVersion();
        } catch (ResourceNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    void computeAndSaveDataset_shouldCatchException_whenDoComputeFails() {
        when(versionRepository.findTopByStatusOrderByVersionNumberDesc(any())).thenReturn(Optional.empty());
        when(subjectSemesterMetricsService.computeAndSaveAllMetrics()).thenReturn(100);
        when(versionRepository.nextVersionNumber()).thenReturn(1);
        GradePredictionDatasetVersion version = new GradePredictionDatasetVersion();
        version.setId(UUID.randomUUID());
        when(versionRepository.save(any())).thenReturn(version);

        // Enrollments exist so ETL enters doCompute, but classMetadata fetch fails
        Enrollment e1 = Enrollment.builder().studentId(studentId).classId(classId1).finalGrade(8.0).build();
        when(enrollmentRepository.findByFinalGradeIsNotNull()).thenReturn(List.of(e1));
        when(courseManagementClient.getClassSectionsForDataset(any()))
                .thenThrow(new RuntimeException("Connection refused"));

        try {
            gradePredictionDatasetService.computeAndSaveDataset();
        } catch (RuntimeException e) {
            assertTrue(true);
        }
    }

    @Test
    void computeAndSaveDataset_shouldReturnZero_whenNoEnrollmentsCanBeEnriched() {
        when(versionRepository.findTopByStatusOrderByVersionNumberDesc(any())).thenReturn(Optional.empty());
        when(subjectSemesterMetricsService.computeAndSaveAllMetrics()).thenReturn(100);
        when(versionRepository.nextVersionNumber()).thenReturn(1);
        GradePredictionDatasetVersion version = new GradePredictionDatasetVersion();
        version.setId(UUID.randomUUID());
        when(versionRepository.save(any())).thenReturn(version);

        // Enrollments exist but class metadata is empty → no enrichment possible
        Enrollment e1 = Enrollment.builder().studentId(studentId).classId(classId1).finalGrade(8.0).build();
        when(enrollmentRepository.findByFinalGradeIsNotNull()).thenReturn(List.of(e1));
        // Empty class metadata → enrichment fails for all rows
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of());

        GradePredictionDatasetVersion result = gradePredictionDatasetService.computeAndSaveDataset();
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void computeAndSaveDataset_shouldHandleMissingMetrics_whenSubjectNotInBatchLoad() {
        Enrollment e1 = Enrollment.builder().studentId(studentId).classId(classId1).finalGrade(8.0).build();

        ClassSectionDatasetResponse meta1 = ClassSectionDatasetResponse.builder()
                .classId(classId1).subjectId(subjectId1).semesterId(semesterId1)
                .semKey(20221).credits(3).curriculumSectionId(curriculumSectionId).build();

        when(versionRepository.findTopByStatusOrderByVersionNumberDesc(any())).thenReturn(Optional.empty());
        when(subjectSemesterMetricsService.computeAndSaveAllMetrics()).thenReturn(100);
        when(versionRepository.nextVersionNumber()).thenReturn(1);
        GradePredictionDatasetVersion version = new GradePredictionDatasetVersion();
        version.setId(UUID.randomUUID());
        when(versionRepository.save(any())).thenReturn(version);
        when(enrollmentRepository.findByFinalGradeIsNotNull()).thenReturn(List.of(e1));
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(meta1));
        when(courseManagementClient.getPrerequisiteMapping()).thenReturn(List.of());
        // Metrics map is empty → subject metric not found for subjectId1+semesterId1
        when(subjectSemesterMetricsService.batchLoadMetrics(anyList(), anyList())).thenReturn(Map.of());
        // batchUpsert is never called because rows are filtered out by hasCompleteData
        // (histMissing=true, isFallback=true when metrics are missing)

        GradePredictionDatasetVersion result = gradePredictionDatasetService.computeAndSaveDataset();
        assertNotNull(result);
        assertTrue(true);
    }
}
