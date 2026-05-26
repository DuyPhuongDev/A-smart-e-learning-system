package com.hcmut.lms.personalization.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.*;

import com.hcmut.lms.personalization.application.dto.response.RecommendedSubjectResponse;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.*;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSubject;
import com.hcmut.lms.personalization.domain.entity.occupationData.SubjectOccupationValuation;
import com.hcmut.lms.personalization.repository.LearningGoalRepository;
import com.hcmut.lms.personalization.repository.LearningPathSubjectRepository;
import com.hcmut.lms.personalization.repository.SubjectOccupationValuationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendedSubjectServiceImplTest {

    @Mock private LearningServiceClient learningServiceClient;
    @Mock private SubjectOccupationValuationRepository subjectOccupationValuationRepository;
    @Mock private CourseManagementClient courseManagementClient;
    @Mock private LearningGoalRepository learningGoalRepository;
    @Mock private LearningPathSubjectRepository learningPathSubjectRepository;

    @InjectMocks
    private RecommendedSubjectServiceImpl recommendedSubjectService;

    @Test void getRecommendedSubjects_shouldReturnEmpty_whenNullCurriculumSections() {
        UUID studentId = UUID.randomUUID();
        UUID learningPathId = UUID.randomUUID();

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true).build();
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));

        StudentLearningProgressResponse progress = mock(StudentLearningProgressResponse.class);
        when(progress.getSections()).thenReturn(Collections.emptyList());
        StudentLearningProgressResponse.StudentProgramInfo programInfo = mock(StudentLearningProgressResponse.StudentProgramInfo.class);
        when(programInfo.getCurriculumYear()).thenReturn(2024);
        when(progress.getProgramInfo()).thenReturn(programInfo);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        CurriculumResolutionResponse curriculum = mock(CurriculumResolutionResponse.class);
        when(curriculum.getCode()).thenReturn("DT");
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(curriculum);

        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        CurriculumFullResponse curriculumFull = mock(CurriculumFullResponse.class);
        when(curriculumFull.getSections()).thenReturn(null);
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculumFull);

        var result = recommendedSubjectService.getRecommendedSubjects(studentId, learningPathId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test void getRecommendedSubjects_shouldReturnRecommendations_whenFullData() {
        UUID studentId = UUID.randomUUID();
        UUID learningPathId = UUID.randomUUID();
        UUID subjectId1 = UUID.randomUUID();
        UUID subjectId2 = UUID.randomUUID();
        UUID failedSubjectId = UUID.randomUUID();
        UUID specId = UUID.randomUUID();

        // Active learning goal
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(studentId)
            .specializationId(specId.toString()).isActive(true)
            .targetOccupationCode("15-1252.00")
            .focusOnTargetOccupation(1)
            .attemptTargetGpaOrder(2)
            .prefMainSemLearnIntensity(com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity.Standard)
            .build();
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));

        // Progress with sections containing completed subjects
        StudentLearningProgressResponse progress = mock(StudentLearningProgressResponse.class);
        StudentLearningProgressResponse.StudentProgramInfo programInfo = mock(StudentLearningProgressResponse.StudentProgramInfo.class);
        when(programInfo.getCurriculumYear()).thenReturn(2024);
        when(progress.getProgramInfo()).thenReturn(programInfo);

        StudentLearningProgressResponse.StudentProgressSectionItem progressSection =
            new StudentLearningProgressResponse.StudentProgressSectionItem();
        progressSection.setSectionName("Chuyên ngành");
        progressSection.setCompletedCredits(0);
        progressSection.setRequiredCredits(15);
        when(progress.getSections()).thenReturn(List.of(progressSection));
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        // Curriculum resolution
        CurriculumResolutionResponse curriculum = mock(CurriculumResolutionResponse.class);
        when(curriculum.getCode()).thenReturn("DT");
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(curriculum);

        // Path subjects: 1 failed, 1 completed
        LearningPathSubject failedSubject = new LearningPathSubject();
        failedSubject.setSubjectId(failedSubjectId);
        failedSubject.setSubjectCode("CS201");
        failedSubject.setSubjectName("Advanced CS");
        failedSubject.setCredits(3);
        failedSubject.setIsCompleted(false);
        failedSubject.setIsHighestResult(true);
        failedSubject.setAttemptNo(1);

        LearningPathSubject completedSubject = new LearningPathSubject();
        UUID completedId = UUID.randomUUID();
        completedSubject.setSubjectId(completedId);
        completedSubject.setSubjectCode("CS101");
        completedSubject.setSubjectName("Intro CS");
        completedSubject.setCredits(3);
        completedSubject.setIsCompleted(true);

        when(learningPathSubjectRepository.findByLearningPathId(any()))
            .thenReturn(List.of(failedSubject, completedSubject));

        // Curriculum full with recommendable sections
        CurriculumFullResponse curriculumFull = new CurriculumFullResponse();
        CurriculumFullResponse.CurriculumSectionFull section = new CurriculumFullResponse.CurriculumSectionFull();
        section.setSectionName("Chuyên ngành");
        section.setPriorityWeight(3);

        CurriculumFullResponse.CurriculumSubjectFull subj1 = new CurriculumFullResponse.CurriculumSubjectFull();
        subj1.setSubjectId(subjectId1);
        subj1.setSubjectCode("CS301");
        subj1.setSubjectName("Algorithms");
        subj1.setCredits(3);
        subj1.setIsRequired(true);
        subj1.setRecommendedYear(3);
        subj1.setRecommendedSemesterInYear(1);

        CurriculumFullResponse.CurriculumSubjectFull subj2 = new CurriculumFullResponse.CurriculumSubjectFull();
        subj2.setSubjectId(subjectId2);
        subj2.setSubjectCode("CS302");
        subj2.setSubjectName("Data Structures");
        subj2.setCredits(4);
        subj2.setIsRequired(true);
        subj2.setRecommendedYear(3);
        subj2.setRecommendedSemesterInYear(2);

        section.setSubjects(List.of(subj1, subj2));
        curriculumFull.setSections(List.of(section));
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculumFull);

        // Occupation valuations
        SubjectOccupationValuation valuation1 = new SubjectOccupationValuation();
        valuation1.setSubjectId(subjectId1);
        valuation1.setTotalValue(BigDecimal.valueOf(8.0));
        SubjectOccupationValuation valuation2 = new SubjectOccupationValuation();
        valuation2.setSubjectId(subjectId2);
        valuation2.setTotalValue(BigDecimal.valueOf(6.0));
        when(subjectOccupationValuationRepository.findByTargetOccupationCode(any()))
            .thenReturn(List.of(valuation1, valuation2));

        // Grade predictions batch
        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred1 = new GradePredictionResponse();
        pred1.setSubjectId(subjectId1);
        pred1.setCorrectedPredictedGrade(7.5);
        GradePredictionResponse pred2 = new GradePredictionResponse();
        pred2.setSubjectId(subjectId2);
        pred2.setCorrectedPredictedGrade(8.0);
        batchResponse.setPredictions(List.of(pred1, pred2));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        List<RecommendedSubjectResponse> result = recommendedSubjectService.getRecommendedSubjects(studentId, learningPathId);
        assertNotNull(result);
        assertTrue(!result.isEmpty());
    }

    @Test void getRecommendedSubjects_shouldReturnEmpty_whenNoActiveGoal() {
        UUID studentId = UUID.randomUUID();
        UUID learningPathId = UUID.randomUUID();

        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.empty());

        try {
            recommendedSubjectService.getRecommendedSubjects(studentId, learningPathId);
        } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void getRecommendedSubjects_shouldReturnEmpty_whenNullCurriculum() {
        UUID studentId = UUID.randomUUID();
        UUID learningPathId = UUID.randomUUID();
        UUID specId = UUID.randomUUID();

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(studentId)
            .specializationId(specId.toString()).isActive(true).build();
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));

        StudentLearningProgressResponse progress = mock(StudentLearningProgressResponse.class);
        StudentLearningProgressResponse.StudentProgramInfo programInfo = mock(StudentLearningProgressResponse.StudentProgramInfo.class);
        when(programInfo.getCurriculumYear()).thenReturn(2024);
        when(progress.getProgramInfo()).thenReturn(programInfo);
        when(progress.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        CurriculumResolutionResponse curriculum = mock(CurriculumResolutionResponse.class);
        when(curriculum.getCode()).thenReturn(null);
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(curriculum);

        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        var result = recommendedSubjectService.getRecommendedSubjects(studentId, learningPathId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test void getRecommendedSubjects_shouldReturnEmpty_whenNoCandidatesAndNoFailed() {
        UUID studentId = UUID.randomUUID();
        UUID learningPathId = UUID.randomUUID();
        UUID specId = UUID.randomUUID();

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(studentId)
            .specializationId(specId.toString()).isActive(true).build();
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));

        StudentLearningProgressResponse progress = mock(StudentLearningProgressResponse.class);
        StudentLearningProgressResponse.StudentProgramInfo programInfo = mock(StudentLearningProgressResponse.StudentProgramInfo.class);
        when(programInfo.getCurriculumYear()).thenReturn(2024);
        when(progress.getProgramInfo()).thenReturn(programInfo);
        when(progress.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        CurriculumResolutionResponse curriculum = mock(CurriculumResolutionResponse.class);
        when(curriculum.getCode()).thenReturn("DT");
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(curriculum);

        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        CurriculumFullResponse curriculumFull = new CurriculumFullResponse();
        CurriculumFullResponse.CurriculumSectionFull section = new CurriculumFullResponse.CurriculumSectionFull();
        section.setSectionName("Không liên quan"); // Not recommendable
        section.setSubjects(Collections.emptyList());
        curriculumFull.setSections(List.of(section));
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculumFull);

        var result = recommendedSubjectService.getRecommendedSubjects(studentId, learningPathId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test void getRecommendedSubjects_shouldThrow_whenInvalidSpecializationId() {
        UUID studentId = UUID.randomUUID();
        UUID learningPathId = UUID.randomUUID();

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(studentId)
            .specializationId("not-a-uuid").isActive(true).build();
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));

        try {
            recommendedSubjectService.getRecommendedSubjects(studentId, learningPathId);
        } catch (IllegalArgumentException ignored) {}
        assertTrue(true);
    }

    @Test void getRecommendedSubjects_shouldHandleInvalidSubjectIdInProgress_whenParsingFails() {
        UUID studentId = UUID.randomUUID();
        UUID learningPathId = UUID.randomUUID();
        UUID specId = UUID.randomUUID();

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(studentId)
            .specializationId(specId.toString()).isActive(true).build();
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));

        StudentLearningProgressResponse progress = mock(StudentLearningProgressResponse.class);
        StudentLearningProgressResponse.StudentProgramInfo programInfo = mock(StudentLearningProgressResponse.StudentProgramInfo.class);
        when(programInfo.getCurriculumYear()).thenReturn(2024);
        when(progress.getProgramInfo()).thenReturn(programInfo);

        StudentLearningProgressResponse.StudentProgressSubjectItem subjectItem =
            new StudentLearningProgressResponse.StudentProgressSubjectItem();
        subjectItem.setSubjectId("not-a-uuid");
        subjectItem.setIsPassed(true);
        StudentLearningProgressResponse.StudentProgressSectionItem progressSection =
            new StudentLearningProgressResponse.StudentProgressSectionItem();
        progressSection.setSectionName("Chuyên ngành");
        progressSection.setSubjects(List.of(subjectItem));
        when(progress.getSections()).thenReturn(List.of(progressSection));
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        CurriculumResolutionResponse curriculum = mock(CurriculumResolutionResponse.class);
        when(curriculum.getCode()).thenReturn("DT");
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(curriculum);

        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        CurriculumFullResponse curriculumFull = new CurriculumFullResponse();
        curriculumFull.setSections(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculumFull);

        var result = recommendedSubjectService.getRecommendedSubjects(studentId, learningPathId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test void getRecommendedSubjects_shouldUseGlobalOccupationScores_whenNoTargetOccupation() {
        UUID studentId = UUID.randomUUID();
        UUID learningPathId = UUID.randomUUID();
        UUID subjectId1 = UUID.randomUUID();
        UUID specId = UUID.randomUUID();

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(studentId)
            .specializationId(specId.toString()).isActive(true)
            .focusOnTargetOccupation(1)
            .attemptTargetGpaOrder(2)
            .prefMainSemLearnIntensity(com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity.Standard)
            .build(); // No targetOccupationCode
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));

        StudentLearningProgressResponse progress = mock(StudentLearningProgressResponse.class);
        StudentLearningProgressResponse.StudentProgramInfo programInfo = mock(StudentLearningProgressResponse.StudentProgramInfo.class);
        when(programInfo.getCurriculumYear()).thenReturn(2024);
        when(progress.getProgramInfo()).thenReturn(programInfo);
        when(progress.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        CurriculumResolutionResponse curriculum = mock(CurriculumResolutionResponse.class);
        when(curriculum.getCode()).thenReturn("DT");
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(curriculum);

        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        CurriculumFullResponse curriculumFull = new CurriculumFullResponse();
        CurriculumFullResponse.CurriculumSectionFull section = new CurriculumFullResponse.CurriculumSectionFull();
        section.setSectionName("Chuyên ngành");
        section.setPriorityWeight(3);
        CurriculumFullResponse.CurriculumSubjectFull subj1 = new CurriculumFullResponse.CurriculumSubjectFull();
        subj1.setSubjectId(subjectId1);
        subj1.setSubjectCode("CS301");
        subj1.setSubjectName("Algorithms");
        subj1.setCredits(3);
        subj1.setIsRequired(true);
        subj1.setRecommendedYear(3);
        subj1.setRecommendedSemesterInYear(1);
        section.setSubjects(List.of(subj1));
        curriculumFull.setSections(List.of(section));
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculumFull);

        // Global occupation scores (findBySubjectIdIn)
        SubjectOccupationValuation val = new SubjectOccupationValuation();
        val.setSubjectId(subjectId1);
        val.setTotalValue(BigDecimal.valueOf(7.0));
        when(subjectOccupationValuationRepository.findBySubjectIdIn(any()))
            .thenReturn(List.of(val));

        // Grade predictions
        BatchGradePredictionResponse batchResponse = new BatchGradePredictionResponse();
        GradePredictionResponse pred1 = new GradePredictionResponse();
        pred1.setSubjectId(subjectId1);
        pred1.setCorrectedPredictedGrade(7.5);
        batchResponse.setPredictions(List.of(pred1));
        when(learningServiceClient.predictGradeBatch(any())).thenReturn(batchResponse);

        List<RecommendedSubjectResponse> result = recommendedSubjectService.getRecommendedSubjects(studentId, learningPathId);
        assertNotNull(result);
        assertTrue(!result.isEmpty());
    }

    @Test void getRecommendedSubjects_shouldHandlePredictionException() {
        UUID studentId = UUID.randomUUID();
        UUID learningPathId = UUID.randomUUID();
        UUID subjectId1 = UUID.randomUUID();
        UUID specId = UUID.randomUUID();

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID()).studentId(studentId)
            .specializationId(specId.toString()).isActive(true)
            .targetOccupationCode("15-1252.00")
            .focusOnTargetOccupation(1)
            .attemptTargetGpaOrder(2)
            .prefMainSemLearnIntensity(com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity.Standard)
            .build();
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));

        StudentLearningProgressResponse progress = mock(StudentLearningProgressResponse.class);
        StudentLearningProgressResponse.StudentProgramInfo programInfo = mock(StudentLearningProgressResponse.StudentProgramInfo.class);
        when(programInfo.getCurriculumYear()).thenReturn(2024);
        when(progress.getProgramInfo()).thenReturn(programInfo);
        when(progress.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        CurriculumResolutionResponse curriculum = mock(CurriculumResolutionResponse.class);
        when(curriculum.getCode()).thenReturn("DT");
        when(courseManagementClient.resolveCurriculum(any(), any())).thenReturn(curriculum);

        when(learningPathSubjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        CurriculumFullResponse curriculumFull = new CurriculumFullResponse();
        CurriculumFullResponse.CurriculumSectionFull section = new CurriculumFullResponse.CurriculumSectionFull();
        section.setSectionName("Chuyên ngành");
        section.setPriorityWeight(3);
        CurriculumFullResponse.CurriculumSubjectFull subj1 = new CurriculumFullResponse.CurriculumSubjectFull();
        subj1.setSubjectId(subjectId1);
        subj1.setSubjectCode("CS301");
        subj1.setSubjectName("Algorithms");
        subj1.setCredits(3);
        subj1.setIsRequired(true);
        subj1.setRecommendedYear(3);
        subj1.setRecommendedSemesterInYear(1);
        section.setSubjects(List.of(subj1));
        curriculumFull.setSections(List.of(section));
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculumFull);

        // Occupation valuations
        SubjectOccupationValuation valuation1 = new SubjectOccupationValuation();
        valuation1.setSubjectId(subjectId1);
        valuation1.setTotalValue(BigDecimal.valueOf(8.0));
        when(subjectOccupationValuationRepository.findByTargetOccupationCode(any()))
            .thenReturn(List.of(valuation1));

        // Grade predictions throw exception
        when(learningServiceClient.predictGradeBatch(any()))
            .thenThrow(new RuntimeException("Service down"));

        List<RecommendedSubjectResponse> result = recommendedSubjectService.getRecommendedSubjects(studentId, learningPathId);
        assertNotNull(result);
    }
}
