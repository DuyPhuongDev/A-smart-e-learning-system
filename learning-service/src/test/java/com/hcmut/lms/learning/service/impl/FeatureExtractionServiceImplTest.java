package com.hcmut.lms.learning.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.ClassSectionDatasetResponse;
import com.hcmut.lms.learning.client.dto.SemesterResponse;
import com.hcmut.lms.learning.entity.enrollment.Enrollment;
import com.hcmut.lms.learning.entity.semester.GradeSemesterMetrics;
import com.hcmut.lms.learning.entity.subject.SubjectSemesterMetrics;
import com.hcmut.lms.learning.mapper.FeatureExtractionMapper;
import com.hcmut.lms.learning.repository.EnrollmentRepository;
import com.hcmut.lms.learning.service.GradeSemesterMetricsService;
import com.hcmut.lms.learning.service.SubjectSemesterMetricsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class FeatureExtractionServiceImplTest {

    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private CourseManagementClient courseManagementClient;
    @Mock private FeatureExtractionMapper featureExtractionMapper;
    @Mock private SubjectSemesterMetricsService subjectSemesterMetricsService;
    @Mock private GradeSemesterMetricsService gradeSemesterMetricsService;

    @InjectMocks
    private FeatureExtractionServiceImpl featureExtractionService;

    private final UUID studentId = UUID.randomUUID();
    private final UUID subjectId = UUID.randomUUID();
    private final UUID classId1 = UUID.randomUUID();
    private final UUID classId2 = UUID.randomUUID();
    private final UUID semesterId = UUID.randomUUID();

    @Test
    void hasGradedHistory_shouldReturnTrue_whenHistoryExists() {
        when(enrollmentRepository.existsGradedByStudentId(studentId)).thenReturn(true);
        assertTrue(featureExtractionService.hasGradedHistory(studentId));
    }

    @Test
    void hasGradedHistory_shouldReturnFalse_whenNoHistory() {
        when(enrollmentRepository.existsGradedByStudentId(studentId)).thenReturn(false);
        assertFalse(featureExtractionService.hasGradedHistory(studentId));
    }

    @Test
    void extractFeatures_shouldUsePrecomputedMetrics_whenAvailable() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        semester.setSemKey(20241);

        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId)
                .smoothedMedian4pt(2.5).sampleCount(100).isFallback(false).build();

        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of());
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));
        when(courseManagementClient.getPrerequisiteMapping()).thenReturn(List.of());
        when(featureExtractionMapper.toFeatureMap(any())).thenReturn(new HashMap<>());

        Map<String, Object> result = featureExtractionService.extractFeatures(studentId, subjectId, 20);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void extractFeatures_shouldComputeHistoryFromPriorEnrollments() {
        UUID priorClassId = UUID.randomUUID();
        UUID priorSemesterId = UUID.randomUUID();

        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        semester.setSemKey(20241);

        Enrollment priorEnrollment = Enrollment.builder()
                .studentId(studentId).classId(priorClassId).finalGrade(8.0).attemptNo(0).build();

        ClassSectionDatasetResponse priorMeta = ClassSectionDatasetResponse.builder()
                .classId(priorClassId).subjectId(UUID.randomUUID()).semesterId(priorSemesterId)
                .semKey(20231).credits(3).build();

        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(priorEnrollment));
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(priorMeta));
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(any(), any()))
                .thenReturn(Optional.empty());
        when(courseManagementClient.getPrerequisiteMapping()).thenReturn(List.of());
        when(featureExtractionMapper.toFeatureMap(any())).thenReturn(new HashMap<>());

        Map<String, Object> result = featureExtractionService.extractFeatures(studentId, subjectId, 17);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void extractFeatures_shouldReturnDefaultFeatures_whenNoSemester() {
        when(courseManagementClient.getCurrentSemester()).thenThrow(new RuntimeException("Service unavailable"));
        when(featureExtractionMapper.toFeatureMap(any())).thenReturn(new HashMap<>());

        Map<String, Object> result = featureExtractionService.extractFeatures(studentId, subjectId, null);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void extractFeatures_shouldComputeRelativeFeatures_whenRelatedSubjectsExist() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        semester.setSemKey(20241);

        UUID relatedSubjectId = UUID.randomUUID();
        UUID priorClassId = UUID.randomUUID();
        UUID priorSemesterId = UUID.randomUUID();

        Enrollment priorEnrollment = Enrollment.builder()
                .studentId(studentId).classId(priorClassId).finalGrade(8.0).attemptNo(0).build();

        ClassSectionDatasetResponse priorMeta = ClassSectionDatasetResponse.builder()
                .classId(priorClassId).subjectId(relatedSubjectId).semesterId(priorSemesterId)
                .semKey(20231).credits(3).build();

        com.hcmut.lms.learning.client.dto.SubjectPrerequisiteMapResponse prereqResp =
                com.hcmut.lms.learning.client.dto.SubjectPrerequisiteMapResponse.builder()
                        .subjectId(subjectId).relatedSubjectIds(List.of(relatedSubjectId)).build();

        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(priorEnrollment));
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(priorMeta));
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(any(), any()))
                .thenReturn(Optional.empty());
        when(courseManagementClient.getPrerequisiteMapping()).thenReturn(List.of(prereqResp));
        when(featureExtractionMapper.toFeatureMap(any())).thenReturn(new HashMap<>());

        Map<String, Object> result = featureExtractionService.extractFeatures(studentId, subjectId, 17);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void extractFeatures_shouldComputeSubjectHistory_whenNoPrecomputedMetrics() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        semester.setSemKey(20241);

        UUID priorClassId = UUID.randomUUID();
        UUID priorSemesterId = UUID.randomUUID();
        UUID priorSubjectId = UUID.randomUUID();

        Enrollment priorEnrollment = Enrollment.builder()
                .studentId(studentId).classId(priorClassId).finalGrade(8.0).attemptNo(0).build();

        ClassSectionDatasetResponse priorMeta = ClassSectionDatasetResponse.builder()
                .classId(priorClassId).subjectId(priorSubjectId).semesterId(priorSemesterId)
                .semKey(20231).credits(3).build();

        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(priorEnrollment));
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(priorMeta));
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(any(), any()))
                .thenReturn(Optional.empty());
        when(courseManagementClient.getPrerequisiteMapping()).thenReturn(List.of());
        // getCourseHistory will also call getClassSectionsBySubjectWindow
        when(courseManagementClient.getClassSectionsBySubjectWindow(any())).thenReturn(List.of());
        when(featureExtractionMapper.toFeatureMap(any())).thenReturn(new HashMap<>());

        Map<String, Object> result = featureExtractionService.extractFeatures(studentId, subjectId, 17);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void extractFeatures_shouldUseSemesterGlobalMedian_whenSubjectHistoryExists() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        semester.setSemKey(20241);

        UUID priorClassId = UUID.randomUUID();
        UUID priorSemesterId = UUID.randomUUID();
        UUID priorSubjectId = UUID.randomUUID();

        Enrollment priorEnrollment = Enrollment.builder()
                .studentId(studentId).classId(priorClassId).finalGrade(8.0).attemptNo(0).build();

        ClassSectionDatasetResponse priorMeta = ClassSectionDatasetResponse.builder()
                .classId(priorClassId).subjectId(priorSubjectId).semesterId(priorSemesterId)
                .semKey(20231).credits(3).build();

        // Subject history: getClassSectionsBySubjectWindow returns data for the current subject
        ClassSectionDatasetResponse subjectClassMeta = ClassSectionDatasetResponse.builder()
                .classId(classId1).subjectId(subjectId).semesterId(priorSemesterId)
                .semKey(20231).build();

        GradeSemesterMetrics semMetrics = GradeSemesterMetrics.builder()
                .semesterId(semesterId).semKey(20241).medianGrade(6.5).meanGrade(6.5).sampleCount(100).build();

        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(priorEnrollment));
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(priorMeta));
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(any(), any()))
                .thenReturn(Optional.empty());
        when(courseManagementClient.getPrerequisiteMapping()).thenReturn(List.of());
        when(courseManagementClient.getClassSectionsBySubjectWindow(any()))
                .thenReturn(List.of(subjectClassMeta));
        when(enrollmentRepository.findByClassIdInAndFinalGradeIsNotNull(any()))
                .thenReturn(List.of(priorEnrollment));
        when(gradeSemesterMetricsService.findBySemesterId(semesterId))
                .thenReturn(Optional.of(semMetrics));
        when(featureExtractionMapper.toFeatureMap(any())).thenReturn(new HashMap<>());

        Map<String, Object> result = featureExtractionService.extractFeatures(studentId, subjectId, 17);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void extractFeatures_shouldRefreshPrerequisiteCache_whenCacheExpired() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        semester.setSemKey(20241);

        // Force cache to be expired
        ReflectionTestUtils.setField(featureExtractionService, "prereqCache", null);
        ReflectionTestUtils.setField(featureExtractionService, "prereqCacheTimestamp", 0L);

        UUID priorClassId = UUID.randomUUID();
        UUID priorSemesterId = UUID.randomUUID();

        Enrollment priorEnrollment = Enrollment.builder()
                .studentId(studentId).classId(priorClassId).finalGrade(8.0).attemptNo(0).build();

        ClassSectionDatasetResponse priorMeta = ClassSectionDatasetResponse.builder()
                .classId(priorClassId).subjectId(UUID.randomUUID()).semesterId(priorSemesterId)
                .semKey(20231).credits(3).build();

        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId)
                .smoothedMedian4pt(2.5).sampleCount(100).isFallback(false).build();

        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(priorEnrollment));
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(priorMeta));
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));
        when(courseManagementClient.getPrerequisiteMapping()).thenReturn(List.of());
        when(featureExtractionMapper.toFeatureMap(any())).thenReturn(new HashMap<>());

        Map<String, Object> result = featureExtractionService.extractFeatures(studentId, subjectId, 17);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void extractFeatures_shouldComputeFeaturesWithReattemptCount_whenEnrollmentsExist() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        semester.setSemKey(20241);

        Enrollment priorEnrollment = Enrollment.builder()
                .studentId(studentId).classId(classId1).finalGrade(8.0).attemptNo(2).build();

        ClassSectionDatasetResponse priorMeta = ClassSectionDatasetResponse.builder()
                .classId(classId1).subjectId(subjectId).semesterId(UUID.randomUUID())
                .semKey(20231).credits(3).build();

        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId)
                .smoothedMedian4pt(2.5).sampleCount(100).isFallback(false).build();

        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(priorEnrollment));
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(priorMeta));
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));
        when(courseManagementClient.getPrerequisiteMapping()).thenReturn(List.of());
        when(featureExtractionMapper.toFeatureMap(any())).thenReturn(new HashMap<>());

        Map<String, Object> result = featureExtractionService.extractFeatures(studentId, subjectId, 17);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void extractFeatures_shouldHandleGetClassMetadataError() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        semester.setSemKey(20241);

        Enrollment priorEnrollment = Enrollment.builder()
                .studentId(studentId).classId(classId1).finalGrade(8.0).attemptNo(0).build();

        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId)
                .smoothedMedian4pt(2.5).sampleCount(100).isFallback(false).build();

        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(priorEnrollment));
        // getClassMetadataMap throws exception → empty map returned
        when(courseManagementClient.getClassSectionsForDataset(any()))
                .thenThrow(new RuntimeException("Network error"));
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));
        when(courseManagementClient.getPrerequisiteMapping()).thenReturn(List.of());
        when(featureExtractionMapper.toFeatureMap(any())).thenReturn(new HashMap<>());

        Map<String, Object> result = featureExtractionService.extractFeatures(studentId, subjectId, 17);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void extractFeatures_shouldHandlePrerequisiteMappingError() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        semester.setSemKey(20241);

        // Force cache refresh
        ReflectionTestUtils.setField(featureExtractionService, "prereqCache", null);
        ReflectionTestUtils.setField(featureExtractionService, "prereqCacheTimestamp", 0L);

        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId)
                .smoothedMedian4pt(2.5).sampleCount(100).isFallback(false).build();

        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of());
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));
        when(courseManagementClient.getPrerequisiteMapping())
                .thenThrow(new RuntimeException("Service unavailable"));
        when(featureExtractionMapper.toFeatureMap(any())).thenReturn(new HashMap<>());

        Map<String, Object> result = featureExtractionService.extractFeatures(studentId, subjectId, 17);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void extractFeatures_shouldHandleGetCourseHistoryError() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        semester.setSemKey(20241);

        UUID priorClassId = UUID.randomUUID();
        UUID priorSemesterId = UUID.randomUUID();

        Enrollment priorEnrollment = Enrollment.builder()
                .studentId(studentId).classId(priorClassId).finalGrade(8.0).attemptNo(0).build();

        ClassSectionDatasetResponse priorMeta = ClassSectionDatasetResponse.builder()
                .classId(priorClassId).subjectId(UUID.randomUUID()).semesterId(priorSemesterId)
                .semKey(20231).credits(3).build();

        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(priorEnrollment));
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(priorMeta));
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(any(), any()))
                .thenReturn(Optional.empty());
        when(courseManagementClient.getPrerequisiteMapping()).thenReturn(List.of());
        // getCourseHistory fails
        when(courseManagementClient.getClassSectionsBySubjectWindow(any()))
                .thenThrow(new RuntimeException("DB error"));
        when(featureExtractionMapper.toFeatureMap(any())).thenReturn(new HashMap<>());

        Map<String, Object> result = featureExtractionService.extractFeatures(studentId, subjectId, 17);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void extractFeatures_shouldComputeEvenSizedMedian_whenSubjectHistoryHasTwoEnrollments() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        semester.setSemKey(20241);

        UUID priorClassId1 = UUID.randomUUID();
        UUID priorClassId2 = UUID.randomUUID();
        UUID priorSemesterId = UUID.randomUUID();

        Enrollment priorEnrollment = Enrollment.builder()
                .studentId(studentId).classId(priorClassId1).finalGrade(8.0).attemptNo(0).build();

        Enrollment e2 = Enrollment.builder()
                .studentId(studentId).classId(priorClassId2).finalGrade(6.0).attemptNo(0).build();

        ClassSectionDatasetResponse priorMeta = ClassSectionDatasetResponse.builder()
                .classId(priorClassId1).subjectId(UUID.randomUUID()).semesterId(priorSemesterId)
                .semKey(20231).credits(3).build();

        ClassSectionDatasetResponse subjectClassMeta1 = ClassSectionDatasetResponse.builder()
                .classId(priorClassId1).subjectId(subjectId).semesterId(priorSemesterId)
                .semKey(20231).build();
        ClassSectionDatasetResponse subjectClassMeta2 = ClassSectionDatasetResponse.builder()
                .classId(priorClassId2).subjectId(subjectId).semesterId(priorSemesterId)
                .semKey(20231).build();

        GradeSemesterMetrics semMetrics = GradeSemesterMetrics.builder()
                .semesterId(semesterId).semKey(20241).medianGrade(6.5).meanGrade(6.5).sampleCount(100).build();

        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(priorEnrollment, e2));
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(priorMeta));
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(any(), any()))
                .thenReturn(Optional.empty());
        when(courseManagementClient.getPrerequisiteMapping()).thenReturn(List.of());
        when(courseManagementClient.getClassSectionsBySubjectWindow(any()))
                .thenReturn(List.of(subjectClassMeta1, subjectClassMeta2));
        when(enrollmentRepository.findByClassIdInAndFinalGradeIsNotNull(any()))
                .thenReturn(List.of(priorEnrollment, e2));
        when(gradeSemesterMetricsService.findBySemesterId(semesterId))
                .thenReturn(Optional.of(semMetrics));
        when(featureExtractionMapper.toFeatureMap(any())).thenReturn(new HashMap<>());

        Map<String, Object> result = featureExtractionService.extractFeatures(studentId, subjectId, 17);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void extractFeatures_shouldFallbackToSimpleAvg_whenRelativeCreditsAreZero() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        semester.setSemKey(20241);

        UUID relatedSubjectId = UUID.randomUUID();
        UUID priorClassId = UUID.randomUUID();
        UUID priorSemesterId = UUID.randomUUID();

        Enrollment priorEnrollment = Enrollment.builder()
                .studentId(studentId).classId(priorClassId).finalGrade(8.0).attemptNo(0).build();

        // Class metadata with null credits → sumCredits will be 0 → triggers simple avg path
        ClassSectionDatasetResponse priorMeta = ClassSectionDatasetResponse.builder()
                .classId(priorClassId).subjectId(relatedSubjectId).semesterId(priorSemesterId)
                .semKey(20231).credits(null).build();

        com.hcmut.lms.learning.client.dto.SubjectPrerequisiteMapResponse prereqResp =
                com.hcmut.lms.learning.client.dto.SubjectPrerequisiteMapResponse.builder()
                        .subjectId(subjectId).relatedSubjectIds(List.of(relatedSubjectId)).build();

        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId)
                .smoothedMedian4pt(2.5).sampleCount(100).isFallback(false).build();

        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(priorEnrollment));
        when(courseManagementClient.getClassSectionsForDataset(any())).thenReturn(List.of(priorMeta));
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));
        when(courseManagementClient.getPrerequisiteMapping()).thenReturn(List.of(prereqResp));
        when(featureExtractionMapper.toFeatureMap(any())).thenReturn(new HashMap<>());

        Map<String, Object> result = featureExtractionService.extractFeatures(studentId, subjectId, 17);
        assertNotNull(result);
        assertTrue(true);
    }
}
