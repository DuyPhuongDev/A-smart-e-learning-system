package com.hcmut.lms.personalization.application.service.impl.learning_path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService.SemesterSlot;
import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService.SubjectCandidate;
import com.hcmut.lms.personalization.application.service.impl.validation.StudentProgressDataService;
import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.BatchGradePredictionResponse;
import com.hcmut.lms.personalization.client.dto.GradePredictionResponse;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPath;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSection;
import com.hcmut.lms.personalization.repository.LearningPathRepository;
import com.hcmut.lms.personalization.repository.LearningPathSectionRepository;
import com.hcmut.lms.personalization.repository.LearningPathSubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LearningPathPersistenceServiceTest {

    @Mock private LearningServiceClient learningServiceClient;
    @Mock private LearningPathRepository learningPathRepository;
    @Mock private LearningPathSectionRepository learningPathSectionRepository;
    @Mock private LearningPathSubjectRepository learningPathSubjectRepository;

    @InjectMocks
    private LearningPathPersistenceService persistenceService;

    private static StudentProgressDataService.StudentProgressData emptyProgress() {
        return new StudentProgressDataService.StudentProgressData(
            BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyMap(), null);
    }

    @Test void buildAndPersistLearningPath_shouldReturnPath_whenEmptySchedule() {
        when(learningPathRepository.save(any())).thenReturn(new LearningPath());
        when(learningPathSectionRepository.saveAll(any())).thenReturn(Collections.emptyList());
        when(learningPathSubjectRepository.saveAll(any())).thenReturn(Collections.emptyList());

        var result = persistenceService.buildAndPersistLearningPath(
            UUID.randomUUID(), UUID.randomUUID(), "DT", Collections.emptyList(),
            "LOW", emptyProgress(), 0, Collections.emptyList());
        assertNotNull(result);
    }

    @Test void buildAndPersistLearningPath_shouldPersist_whenScheduleWithSubjects() {
        UUID studentId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();
        UUID academicYearId = UUID.randomUUID();

        when(learningPathRepository.save(any())).thenReturn(new LearningPath());

        SemesterResponse semInfo = SemesterResponse.builder()
            .id(semId).semKey(20241).semesterCode("HK241")
            .academicYearId(academicYearId).build();

        SubjectCandidate candidate = SubjectCandidate.builder()
            .subjectId(subjectId).subjectCode("CS101").subjectName("Intro to CS")
            .credits(3).priority1(2).priority2(5).isRequired(true)
            .prerequisites(Collections.emptyList())
            .parallels(Collections.emptyList())
            .build();

        SemesterSlot slot = SemesterSlot.builder()
            .semesterOrder(1).totalCredits(3).isSummer(false)
            .semesterInfo(semInfo).subjects(List.of(candidate)).build();

        when(learningPathSectionRepository.saveAll(any())).thenReturn(List.of(new LearningPathSection()));
        when(learningPathSubjectRepository.saveAll(any())).thenReturn(Collections.emptyList());

        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred = new GradePredictionResponse();
        pred.setSubjectId(subjectId);
        pred.setCorrectedPredictedGrade(7.5);
        batchResponse.setPredictions(List.of(pred));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        var result = persistenceService.buildAndPersistLearningPath(
            studentId, UUID.randomUUID(), "DT", List.of(slot),
            "LOW", emptyProgress(), 0, Collections.emptyList());
        assertNotNull(result);
    }

    @Test void buildAndPersistLearningPath_shouldIncludePastSemesters_whenProvided() {
        UUID studentId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();
        UUID academicYearId = UUID.randomUUID();
        UUID pastSemId = UUID.randomUUID();
        UUID pastAcademicYearId = UUID.randomUUID();

        when(learningPathRepository.save(any())).thenReturn(new LearningPath());

        String today = LocalDate.now().toString();
        String yesterday = LocalDate.now().minusDays(1).toString();
        SemesterResponse pastSem = SemesterResponse.builder()
            .id(pastSemId).semKey(20231).semesterCode("HK231")
            .academicYearId(pastAcademicYearId)
            .startDate("2023-09-01").endDate(yesterday).build();

        SemesterResponse futureSemInfo = SemesterResponse.builder()
            .id(semId).semKey(20253).semesterCode("HK251")
            .academicYearId(academicYearId)
            .startDate(today).endDate("2025-12-31").build();

        SubjectCandidate candidate = SubjectCandidate.builder()
            .subjectId(subjectId).subjectCode("CS101").subjectName("Intro to CS")
            .credits(3).priority1(2).priority2(5).isRequired(true)
            .prerequisites(Collections.emptyList())
            .parallels(Collections.emptyList())
            .build();

        SemesterSlot slot = SemesterSlot.builder()
            .semesterOrder(1).totalCredits(3).isSummer(false)
            .semesterInfo(futureSemInfo).subjects(List.of(candidate)).build();

        when(learningPathSectionRepository.saveAll(any())).thenReturn(
            List.of(new LearningPathSection(), new LearningPathSection()));
        when(learningPathSubjectRepository.saveAll(any())).thenReturn(Collections.emptyList());

        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred = new GradePredictionResponse();
        pred.setSubjectId(subjectId);
        pred.setCorrectedPredictedGrade(7.5);
        batchResponse.setPredictions(List.of(pred));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        var result = persistenceService.buildAndPersistLearningPath(
            studentId, UUID.randomUUID(), "DT", List.of(slot),
            "MEDIUM", emptyProgress(), 10, List.of(pastSem));
        assertNotNull(result);
    }

    @Test void buildAndPersistLearningPath_shouldSkipEmptySummerPastSemesters() {
        UUID studentId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();
        UUID academicYearId = UUID.randomUUID();
        UUID pastSummerId = UUID.randomUUID();
        UUID pastSummerAcademicYearId = UUID.randomUUID();

        when(learningPathRepository.save(any())).thenReturn(new LearningPath());

        String yesterday = LocalDate.now().minusDays(1).toString();
        // Summer semester with no completed subjects - should be skipped
        SemesterResponse pastSummer = SemesterResponse.builder()
            .id(pastSummerId).semKey(20233).semesterCode("HK233")
            .academicYearId(pastSummerAcademicYearId)
            .startDate("2023-06-01").endDate(yesterday).build();

        SemesterResponse futureSemInfo = SemesterResponse.builder()
            .id(semId).semKey(20253).semesterCode("HK251")
            .academicYearId(academicYearId).build();

        SubjectCandidate candidate = SubjectCandidate.builder()
            .subjectId(subjectId).subjectCode("CS101").subjectName("Intro to CS")
            .credits(3).priority1(2).priority2(5).isRequired(true)
            .prerequisites(Collections.emptyList())
            .parallels(Collections.emptyList())
            .build();

        SemesterSlot slot = SemesterSlot.builder()
            .semesterOrder(1).totalCredits(3).isSummer(false)
            .semesterInfo(futureSemInfo).subjects(List.of(candidate)).build();

        when(learningPathSectionRepository.saveAll(any())).thenReturn(List.of(new LearningPathSection()));
        when(learningPathSubjectRepository.saveAll(any())).thenReturn(Collections.emptyList());

        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred = new GradePredictionResponse();
        pred.setSubjectId(subjectId);
        pred.setCorrectedPredictedGrade(7.5);
        batchResponse.setPredictions(List.of(pred));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        var result = persistenceService.buildAndPersistLearningPath(
            studentId, UUID.randomUUID(), "DT", List.of(slot),
            "LOW", emptyProgress(), 0, List.of(pastSummer));
        assertNotNull(result);
    }

    @Test void buildAndPersistLearningPath_shouldHandlePredictionFailure() {
        UUID studentId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();
        UUID academicYearId = UUID.randomUUID();

        when(learningPathRepository.save(any())).thenReturn(new LearningPath());

        SemesterResponse semInfo = SemesterResponse.builder()
            .id(semId).semKey(20241).semesterCode("HK241")
            .academicYearId(academicYearId).build();

        SubjectCandidate candidate = SubjectCandidate.builder()
            .subjectId(subjectId).subjectCode("CS101").subjectName("Intro to CS")
            .credits(3).priority1(2).priority2(5).isRequired(true)
            .prerequisites(Collections.emptyList())
            .parallels(Collections.emptyList())
            .build();

        SemesterSlot slot = SemesterSlot.builder()
            .semesterOrder(1).totalCredits(3).isSummer(false)
            .semesterInfo(semInfo).subjects(List.of(candidate)).build();

        when(learningPathSectionRepository.saveAll(any())).thenReturn(List.of(new LearningPathSection()));
        when(learningPathSubjectRepository.saveAll(any())).thenReturn(Collections.emptyList());

        when(learningServiceClient.predictGradeBatch(any()))
            .thenThrow(new RuntimeException("Prediction service down"));

        var result = persistenceService.buildAndPersistLearningPath(
            studentId, UUID.randomUUID(), "DT", List.of(slot),
            "LOW", emptyProgress(), 0, Collections.emptyList());
        assertNotNull(result);
    }

    @Test void buildAndPersistLearningPath_shouldIncludePastWithCompletedSubjects() {
        UUID studentId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();
        UUID academicYearId = UUID.randomUUID();
        UUID pastSemId = UUID.randomUUID();
        UUID pastAcademicYearId = UUID.randomUUID();
        UUID completedSubjectId = UUID.randomUUID();

        when(learningPathRepository.save(any())).thenReturn(new LearningPath());

        String yesterday = LocalDate.now().minusDays(1).toString();
        SemesterResponse pastSem = SemesterResponse.builder()
            .id(pastSemId).semKey(20211).semesterCode("HK211")
            .academicYearId(pastAcademicYearId)
            .startDate("2021-09-01").endDate(yesterday).build();

        SemesterResponse futureSemInfo = SemesterResponse.builder()
            .id(semId).semKey(20253).semesterCode("HK251")
            .academicYearId(academicYearId).build();

        SubjectCandidate candidate = SubjectCandidate.builder()
            .subjectId(subjectId).subjectCode("CS101").subjectName("Intro to CS")
            .credits(3).priority1(2).priority2(5).isRequired(true)
            .prerequisites(Collections.emptyList())
            .parallels(Collections.emptyList())
            .build();

        SemesterSlot slot = SemesterSlot.builder()
            .semesterOrder(1).totalCredits(3).isSummer(false)
            .semesterInfo(futureSemInfo).subjects(List.of(candidate)).build();

        StudentProgressDataService.CompletedSubjectDetail completedDetail =
            new StudentProgressDataService.CompletedSubjectDetail(
                completedSubjectId, pastSemId, pastAcademicYearId, 1, 1,
                "PH101", "Physics 1", 4, 1, 7.0, "B", 3.0, 1, true, true);

        StudentProgressDataService.StudentProgressData progressWithCompleted =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.valueOf(3.0), 4, 0, List.of(completedSubjectId), Collections.emptyList(),
                List.of(completedDetail), Collections.emptyMap(), null);

        when(learningPathSectionRepository.saveAll(any())).thenReturn(
            List.of(new LearningPathSection(), new LearningPathSection()));
        when(learningPathSubjectRepository.saveAll(any())).thenReturn(Collections.emptyList());

        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred = new GradePredictionResponse();
        pred.setSubjectId(subjectId);
        pred.setCorrectedPredictedGrade(7.5);
        batchResponse.setPredictions(List.of(pred));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        var result = persistenceService.buildAndPersistLearningPath(
            studentId, UUID.randomUUID(), "DT", List.of(slot),
            "LOW", progressWithCompleted, 4, List.of(pastSem));
        assertNotNull(result);
    }

    @Test void buildAndPersistLearningPath_shouldHandleNullPredictionGrade() {
        UUID studentId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();
        UUID academicYearId = UUID.randomUUID();

        when(learningPathRepository.save(any())).thenReturn(new LearningPath());

        SemesterResponse semInfo = SemesterResponse.builder()
            .id(semId).semKey(20241).semesterCode("HK241")
            .academicYearId(academicYearId).build();

        SubjectCandidate candidate = SubjectCandidate.builder()
            .subjectId(subjectId).subjectCode("CS101").subjectName("Intro to CS")
            .credits(3).priority1(2).priority2(5).isRequired(true)
            .prerequisites(Collections.emptyList())
            .parallels(Collections.emptyList())
            .build();

        SemesterSlot slot = SemesterSlot.builder()
            .semesterOrder(1).totalCredits(3).isSummer(false)
            .semesterInfo(semInfo).subjects(List.of(candidate)).build();

        when(learningPathSectionRepository.saveAll(any())).thenReturn(List.of(new LearningPathSection()));
        when(learningPathSubjectRepository.saveAll(any())).thenReturn(Collections.emptyList());

        // correctedPredictedGrade is null — should be filtered out
        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred = new GradePredictionResponse();
        pred.setSubjectId(subjectId);
        pred.setCorrectedPredictedGrade(null);
        batchResponse.setPredictions(List.of(pred));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        var result = persistenceService.buildAndPersistLearningPath(
            studentId, UUID.randomUUID(), "DT", List.of(slot),
            "LOW", emptyProgress(), 0, Collections.emptyList());
        assertNotNull(result);
    }

    @Test void buildAndPersistLearningPath_shouldFallback_whenNoPastSemestersButHasCompleted() {
        UUID studentId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();
        UUID academicYearId = UUID.randomUUID();
        UUID completedSubjectId = UUID.randomUUID();
        UUID completedSemId = UUID.randomUUID();
        UUID completedAcademicYearId = UUID.randomUUID();

        when(learningPathRepository.save(any())).thenReturn(new LearningPath());

        SemesterResponse futureSemInfo = SemesterResponse.builder()
            .id(semId).semKey(20253).semesterCode("HK251")
            .academicYearId(academicYearId).build();

        SubjectCandidate candidate = SubjectCandidate.builder()
            .subjectId(subjectId).subjectCode("CS101").subjectName("Intro to CS")
            .credits(3).priority1(2).priority2(5).isRequired(true)
            .prerequisites(Collections.emptyList())
            .parallels(Collections.emptyList())
            .build();

        SemesterSlot slot = SemesterSlot.builder()
            .semesterOrder(1).totalCredits(3).isSummer(false)
            .semesterInfo(futureSemInfo).subjects(List.of(candidate)).build();

        StudentProgressDataService.CompletedSubjectDetail completedDetail =
            new StudentProgressDataService.CompletedSubjectDetail(
                completedSubjectId, completedSemId, completedAcademicYearId, 1, 1,
                "PH101", "Physics 1", 4, 1, 7.0, "B", 3.0, 1, true, true);

        StudentProgressDataService.StudentProgressData progressWithCompleted =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.valueOf(3.0), 4, 0, List.of(completedSubjectId), Collections.emptyList(),
                List.of(completedDetail), Collections.emptyMap(), null);

        when(learningPathSectionRepository.saveAll(any())).thenReturn(
            List.of(new LearningPathSection(), new LearningPathSection()));
        when(learningPathSubjectRepository.saveAll(any())).thenReturn(Collections.emptyList());

        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred = new GradePredictionResponse();
        pred.setSubjectId(subjectId);
        pred.setCorrectedPredictedGrade(7.5);
        batchResponse.setPredictions(List.of(pred));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        // No past semesters but has completed subjects → triggers fallback path
        var result = persistenceService.buildAndPersistLearningPath(
            studentId, UUID.randomUUID(), "DT", List.of(slot),
            "LOW", progressWithCompleted, 4, Collections.emptyList());
        assertNotNull(result);
    }

    @Test void buildAndPersistLearningPath_shouldIncludeSummerSlotWithSubjects() {
        UUID studentId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();
        UUID academicYearId = UUID.randomUUID();

        when(learningPathRepository.save(any())).thenReturn(new LearningPath());

        SemesterResponse semInfo = SemesterResponse.builder()
            .id(semId).semKey(20253).semesterCode("HK253")
            .academicYearId(academicYearId).build();

        SubjectCandidate candidate = SubjectCandidate.builder()
            .subjectId(subjectId).subjectCode("CS101").subjectName("Intro to CS")
            .credits(3).priority1(2).priority2(5).isRequired(true)
            .prerequisites(Collections.emptyList())
            .parallels(Collections.emptyList())
            .build();

        SemesterSlot slot = SemesterSlot.builder()
            .semesterOrder(1).totalCredits(3).isSummer(true)
            .creditCap(8)
            .semesterInfo(semInfo).subjects(List.of(candidate)).build();

        when(learningPathSectionRepository.saveAll(any())).thenReturn(List.of(new LearningPathSection()));
        when(learningPathSubjectRepository.saveAll(any())).thenReturn(Collections.emptyList());

        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred = new GradePredictionResponse();
        pred.setSubjectId(subjectId);
        pred.setCorrectedPredictedGrade(7.5);
        batchResponse.setPredictions(List.of(pred));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        var result = persistenceService.buildAndPersistLearningPath(
            studentId, UUID.randomUUID(), "DT", List.of(slot),
            "LOW", emptyProgress(), 0, Collections.emptyList());
        assertNotNull(result);
    }
}
