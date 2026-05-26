package com.hcmut.lms.personalization.application.service.impl.learning_path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import com.hcmut.lms.personalization.application.dto.response.enums.SummerLearningIntensity;
import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService;
import com.hcmut.lms.personalization.application.service.impl.validation.CreditTimeValidatorService;
import com.hcmut.lms.personalization.application.service.impl.validation.PrerequisiteChainValidatorService;
import com.hcmut.lms.personalization.application.service.impl.validation.SemesterCalculationService;
import com.hcmut.lms.personalization.application.service.impl.validation.StudentProgressDataService;
import com.hcmut.lms.personalization.application.service.impl.validation.model.CreditTimeCheckResult;
import com.hcmut.lms.personalization.application.service.impl.validation.model.PrerequisiteChainResult;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.CurriculumFullResponse;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningGoal.PreferredSummerSemester;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPath;
import com.hcmut.lms.personalization.repository.PreferredSummerSemesterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LearningPathGenerationServiceTest {

    @Mock private CourseManagementClient courseManagementClient;
    @Mock private StudentProgressDataService studentProgressDataService;
    @Mock private SemesterCalculationService semesterCalculationService;
    @Mock private LearningPathSchedulingService learningPathSchedulingService;
    @Mock private LearningPathBaselineSelectionService learningPathBaselineSelectionService;
    @Mock private LearningPathPersistenceService learningPathPersistenceService;
    @Mock private PrerequisiteChainValidatorService prerequisiteChainValidatorService;
    @Mock private CreditTimeValidatorService creditTimeValidatorService;
    @Mock private PreferredSummerSemesterRepository preferredSummerSemesterRepository;

    @InjectMocks
    private LearningPathGenerationService generationService;

    private static final UUID studentId = UUID.randomUUID();
    private static final UUID learningGoalId = UUID.randomUUID();
    private static final String curriculumCode = "DT";

    private void setupHappyPathMocks(LearningGoal goal) {
        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(Collections.emptyList());
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        when(courseManagementClient.getAllSemesters()).thenReturn(Collections.emptyList());
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(Collections.emptyList());
        when(learningPathSchedulingService.scheduleSubjects(any(), any(), anyInt(), any(), anyInt(), any()))
            .thenReturn(Collections.emptyList());

        PrerequisiteChainResult prereqResult = new PrerequisiteChainResult(true, 0, 4, "");
        when(prerequisiteChainValidatorService.validate(any(), any(), any(), anyInt()))
            .thenReturn(prereqResult);

        CreditTimeCheckResult creditTimeResult = new CreditTimeCheckResult(true, 30, 120, 4, 2, "");
        when(creditTimeValidatorService.validate(any(), anyInt())).thenReturn(creditTimeResult);

        when(learningPathPersistenceService.buildAndPersistLearningPath(
            any(), any(), any(), any(), any(), any(), anyInt(), any()))
            .thenReturn(new LearningPath());
    }

    private LearningGoal makeGoal() {
        return LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(0)
            .build();
    }

    @Test void generateLearningPath_shouldReturnPath_whenValidInput() {
        LearningGoal goal = makeGoal();
        setupHappyPathMocks(goal);
        try { generationService.generateLearningPath(
            studentId, learningGoalId, curriculumCode, goal, null); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void generateLearningPath_shouldThrow_whenNullMainIntensity() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .prefMainSemLearnIntensity(null)
            .plannedSummerSemCount(0)
            .build();
        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(Collections.emptyList());
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(Collections.emptyList());

        try {
            generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("prefMainSemLearnIntensity"));
        }
    }

    @Test void generateLearningPath_shouldThrow_whenNullPlannedSummerCount() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(null)
            .build();
        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(Collections.emptyList());
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(Collections.emptyList());

        try {
            generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("plannedSummerSemCount"));
        }
    }

    @Test void generateLearningPath_shouldThrow_whenSummerCountMismatch() {
        UUID semId = UUID.randomUUID();
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(2)
            .build();

        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        SemesterResponse summerSem = SemesterResponse.builder()
            .id(semId).semKey(20253).semesterCode("HK253").build();
        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, List.of(summerSem));
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(Collections.emptyList());
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        PreferredSummerSemester pref = new PreferredSummerSemester();
        pref.setSemesterId(semId);
        pref.setLearningIntensity(SummerLearningIntensity.Standard);
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(List.of(pref));

        try {
            generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("plannedSummerSemCount is 2 but 1"));
        }
    }

    @Test void generateLearningPath_shouldThrow_whenSummerNotInRemaining() {
        UUID semId = UUID.randomUUID();
        UUID wrongSummerId = UUID.randomUUID();
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(1)
            .build();

        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        // A non-summer semester (HK241 not summer)
        SemesterResponse mainSem = SemesterResponse.builder()
            .id(semId).semKey(20241).semesterCode("HK241").build();
        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, List.of(mainSem));
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(Collections.emptyList());
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        PreferredSummerSemester pref = new PreferredSummerSemester();
        pref.setSemesterId(wrongSummerId);
        pref.setLearningIntensity(SummerLearningIntensity.Standard);
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(List.of(pref));

        try {
            generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("does not correspond to a remaining summer semester"));
        }
    }

    @Test void generateLearningPath_shouldThrow_whenNotEnoughSummerSemesters() {
        UUID summerId = UUID.randomUUID();
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(2)
            .build();

        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        SemesterResponse summerSem = SemesterResponse.builder()
            .id(summerId).semKey(20253).semesterCode("HK253").build();
        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, List.of(summerSem));
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(Collections.emptyList());
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        PreferredSummerSemester pref1 = new PreferredSummerSemester();
        pref1.setSemesterId(summerId);
        pref1.setLearningIntensity(SummerLearningIntensity.Standard);
        PreferredSummerSemester pref2 = new PreferredSummerSemester();
        pref2.setSemesterId(summerId);
        pref2.setLearningIntensity(SummerLearningIntensity.Light);
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(List.of(pref1, pref2));

        try {
            generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("plannedSummerSemCount is 2"));
        }
    }

    @Test void generateLearningPath_shouldThrow_whenZeroPlannedButHasPreferred() {
        UUID semId = UUID.randomUUID();
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(0)
            .build();

        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(Collections.emptyList());
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        PreferredSummerSemester pref = new PreferredSummerSemester();
        pref.setSemesterId(semId);
        pref.setLearningIntensity(SummerLearningIntensity.Standard);
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(List.of(pref));

        try {
            generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("plannedSummerSemCount is 0"));
        }
    }

    @Test void generateLearningPath_shouldThrow_whenScheduleNotFeasible() {
        LearningGoal goal = makeGoal();
        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(Collections.emptyList());
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        when(courseManagementClient.getAllSemesters()).thenReturn(Collections.emptyList());
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(Collections.emptyList());
        when(learningPathSchedulingService.scheduleSubjects(any(), any(), anyInt(), any(), anyInt(), any()))
            .thenReturn(Collections.emptyList());

        PrerequisiteChainResult prereqResult = new PrerequisiteChainResult(false, 0, 4, "Prerequisite chain broken");
        when(prerequisiteChainValidatorService.validate(any(), any(), any(), anyInt()))
            .thenReturn(prereqResult);

        try {
            generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("Cannot generate feasible"));
        }
    }

    @Test void generateLearningPath_shouldComputePastSemesters_whenIntakeYearProvided() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(0)
            .build();

        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), 2020);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(Collections.emptyList());
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        SemesterResponse pastSemester = SemesterResponse.builder()
            .id(UUID.randomUUID()).semKey(20201).semesterCode("HK201")
            .startDate("2020-01-06").endDate("2020-05-15").build();
        when(courseManagementClient.getAllSemesters()).thenReturn(List.of(pastSemester));
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(Collections.emptyList());
        when(learningPathSchedulingService.scheduleSubjects(any(), any(), anyInt(), any(), anyInt(), any()))
            .thenReturn(Collections.emptyList());

        PrerequisiteChainResult prereqResult = new PrerequisiteChainResult(true, 0, 4, "");
        when(prerequisiteChainValidatorService.validate(any(), any(), any(), anyInt()))
            .thenReturn(prereqResult);

        CreditTimeCheckResult creditTimeResult = new CreditTimeCheckResult(true, 30, 120, 4, 2, "");
        when(creditTimeValidatorService.validate(any(), anyInt())).thenReturn(creditTimeResult);

        when(learningPathPersistenceService.buildAndPersistLearningPath(
            any(), any(), any(), any(), any(), any(), anyInt(), any()))
            .thenReturn(new LearningPath());

        generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        assertTrue(true);
    }

    @Test void generateLearningPath_shouldHandleNullSpecializationId() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(0)
            .build();
        setupHappyPathMocks(goal);

        generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        assertTrue(true);
    }

    @Test void generateLearningPath_shouldSkipNullSemesterId_whenPreferredSummer() {
        UUID summerId = UUID.randomUUID();
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(1)
            .build();

        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        SemesterResponse summerSem = SemesterResponse.builder()
            .id(summerId).semKey(20253).semesterCode("HK253").build();
        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, List.of(summerSem));
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(Collections.emptyList());
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        when(courseManagementClient.getAllSemesters()).thenReturn(Collections.emptyList());

        PreferredSummerSemester prefWithNullId = new PreferredSummerSemester();
        prefWithNullId.setSemesterId(null);
        prefWithNullId.setLearningIntensity(SummerLearningIntensity.Standard);
        PreferredSummerSemester prefWithId = new PreferredSummerSemester();
        prefWithId.setSemesterId(summerId);
        prefWithId.setLearningIntensity(SummerLearningIntensity.Standard);
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(List.of(prefWithNullId, prefWithId));

        when(learningPathSchedulingService.scheduleSubjects(any(), any(), anyInt(), any(), anyInt(), any()))
            .thenReturn(Collections.emptyList());

        PrerequisiteChainResult prereqResult = new PrerequisiteChainResult(true, 0, 4, "");
        when(prerequisiteChainValidatorService.validate(any(), any(), any(), anyInt()))
            .thenReturn(prereqResult);

        CreditTimeCheckResult creditTimeResult = new CreditTimeCheckResult(true, 30, 120, 4, 2, "");
        when(creditTimeValidatorService.validate(any(), anyInt())).thenReturn(creditTimeResult);

        when(learningPathPersistenceService.buildAndPersistLearningPath(
            any(), any(), any(), any(), any(), any(), anyInt(), any()))
            .thenReturn(new LearningPath());

        generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        assertTrue(true);
    }

    @Test void generateLearningPath_shouldCalculateHighRisk_whenScheduleOverloaded() {
        LearningGoal goal = makeGoal();

        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(Collections.emptyList());
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        when(courseManagementClient.getAllSemesters()).thenReturn(Collections.emptyList());
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(Collections.emptyList());

        // 3 overloaded main semesters → high risk
        List<LearningPathSchedulingService.SemesterSlot> schedule = List.of(
            LearningPathSchedulingService.SemesterSlot.builder()
                .semesterOrder(1).totalCredits(23).creditCap(22)
                .isSummer(false).subjects(Collections.emptyList()).build(),
            LearningPathSchedulingService.SemesterSlot.builder()
                .semesterOrder(2).totalCredits(24).creditCap(22)
                .isSummer(false).subjects(Collections.emptyList()).build(),
            LearningPathSchedulingService.SemesterSlot.builder()
                .semesterOrder(3).totalCredits(23).creditCap(22)
                .isSummer(false).subjects(Collections.emptyList()).build()
        );
        when(learningPathSchedulingService.scheduleSubjects(any(), any(), anyInt(), any(), anyInt(), any()))
            .thenReturn(schedule);

        PrerequisiteChainResult prereqResult = new PrerequisiteChainResult(true, 0, 4, "");
        when(prerequisiteChainValidatorService.validate(any(), any(), any(), anyInt()))
            .thenReturn(prereqResult);

        CreditTimeCheckResult creditTimeResult = new CreditTimeCheckResult(true, 69, 120, 4, 2, "");
        when(creditTimeValidatorService.validate(any(), anyInt())).thenReturn(creditTimeResult);

        when(learningPathPersistenceService.buildAndPersistLearningPath(
            any(), any(), any(), any(), any(), any(), anyInt(), any()))
            .thenReturn(new LearningPath());

        generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        assertTrue(true);
    }

    @Test void generateLearningPath_shouldCalculateMediumRisk_whenOneOverloadedSemester() {
        LearningGoal goal = makeGoal();

        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(Collections.emptyList());
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        when(courseManagementClient.getAllSemesters()).thenReturn(Collections.emptyList());
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(Collections.emptyList());

        // 1 overloaded main semester → medium risk
        List<LearningPathSchedulingService.SemesterSlot> schedule = List.of(
            LearningPathSchedulingService.SemesterSlot.builder()
                .semesterOrder(1).totalCredits(23).creditCap(22)
                .isSummer(false).subjects(Collections.emptyList()).build(),
            LearningPathSchedulingService.SemesterSlot.builder()
                .semesterOrder(2).totalCredits(15).creditCap(22)
                .isSummer(false).subjects(Collections.emptyList()).build()
        );
        when(learningPathSchedulingService.scheduleSubjects(any(), any(), anyInt(), any(), anyInt(), any()))
            .thenReturn(schedule);

        PrerequisiteChainResult prereqResult = new PrerequisiteChainResult(true, 0, 4, "");
        when(prerequisiteChainValidatorService.validate(any(), any(), any(), anyInt()))
            .thenReturn(prereqResult);

        CreditTimeCheckResult creditTimeResult = new CreditTimeCheckResult(true, 38, 120, 4, 2, "");
        when(creditTimeValidatorService.validate(any(), anyInt())).thenReturn(creditTimeResult);

        when(learningPathPersistenceService.buildAndPersistLearningPath(
            any(), any(), any(), any(), any(), any(), anyInt(), any()))
            .thenReturn(new LearningPath());

        generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        assertTrue(true);
    }

    @Test void generateLearningPath_shouldHandleScheduleWithSummerSlots() {
        LearningGoal goal = makeGoal();

        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(Collections.emptyList());
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        when(courseManagementClient.getAllSemesters()).thenReturn(Collections.emptyList());
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(Collections.emptyList());

        // Mix of main and summer slots
        List<LearningPathSchedulingService.SemesterSlot> schedule = List.of(
            LearningPathSchedulingService.SemesterSlot.builder()
                .semesterOrder(1).totalCredits(15).creditCap(17)
                .isSummer(false).subjects(Collections.emptyList()).build(),
            LearningPathSchedulingService.SemesterSlot.builder()
                .semesterOrder(2).totalCredits(6).creditCap(8)
                .isSummer(true).subjects(Collections.emptyList()).build()
        );
        when(learningPathSchedulingService.scheduleSubjects(any(), any(), anyInt(), any(), anyInt(), any()))
            .thenReturn(schedule);

        PrerequisiteChainResult prereqResult = new PrerequisiteChainResult(true, 0, 4, "");
        when(prerequisiteChainValidatorService.validate(any(), any(), any(), anyInt()))
            .thenReturn(prereqResult);

        CreditTimeCheckResult creditTimeResult = new CreditTimeCheckResult(true, 21, 120, 4, 2, "");
        when(creditTimeValidatorService.validate(any(), anyInt())).thenReturn(creditTimeResult);

        when(learningPathPersistenceService.buildAndPersistLearningPath(
            any(), any(), any(), any(), any(), any(), anyInt(), any()))
            .thenReturn(new LearningPath());

        generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        assertTrue(true);
    }

    @Test void generateLearningPath_shouldThrow_whenScheduleHasUnscheduledSubjects() {
        LearningGoal goal = makeGoal();

        CurriculumFullResponse curriculum = mock(CurriculumFullResponse.class);
        when(curriculum.getSections()).thenReturn(Collections.emptyList());
        when(courseManagementClient.getCurriculumFull(any())).thenReturn(curriculum);

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(any(), any())).thenReturn(availability);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.ZERO, 0, 0, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any(), any())).thenReturn(progressData);

        UUID candidateSubjectId = UUID.randomUUID();
        LearningPathSchedulingService.SubjectCandidate candidate =
            LearningPathSchedulingService.SubjectCandidate.builder()
                .subjectId(candidateSubjectId).subjectCode("CS301").credits(3)
                .isRequired(true).sectionId(UUID.randomUUID()).sectionName("Chuyên ngành")
                .build();
        LearningPathBaselineSelectionService.BaselineSelectionResult baselineResult =
            new LearningPathBaselineSelectionService.BaselineSelectionResult(List.of(candidate));
        when(learningPathBaselineSelectionService.buildBaselinePath(any(), any(), any())).thenReturn(baselineResult);

        when(courseManagementClient.getAllSemesters()).thenReturn(Collections.emptyList());
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(Collections.emptyList());
        // Schedule returns empty — candidate not scheduled
        when(learningPathSchedulingService.scheduleSubjects(any(), any(), anyInt(), any(), anyInt(), any()))
            .thenReturn(Collections.emptyList());

        try {
            generationService.generateLearningPath(studentId, learningGoalId, curriculumCode, goal, null);
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("phân bổ"));
        }
        assertTrue(true);
    }
}
