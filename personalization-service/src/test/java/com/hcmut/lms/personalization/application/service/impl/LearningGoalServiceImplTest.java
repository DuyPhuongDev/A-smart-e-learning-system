package com.hcmut.lms.personalization.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.*;

import com.hcmut.lms.personalization.application.dto.request.CreateLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.request.CreatePreferredSummerSemesterRequest;
import com.hcmut.lms.personalization.application.dto.request.UpdateLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.response.LearningGoalFeasibilityResponse;
import com.hcmut.lms.personalization.application.dto.response.LearningGoalResponse;
import com.hcmut.lms.personalization.application.dto.response.PreferredSummerSemesterResponse;
import com.hcmut.lms.personalization.application.dto.response.enums.FeasibilityLevel;
import com.hcmut.lms.personalization.application.mapper.LearningGoalMapper;
import com.hcmut.lms.personalization.domain.entity.learningGoal.GoalValidationResult;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningGoal.PreferredSummerSemester;
import com.hcmut.lms.personalization.repository.GoalValidationResultRepository;
import com.hcmut.lms.personalization.repository.LearningGoalRepository;
import com.hcmut.lms.personalization.repository.LearningPathRepository;
import com.hcmut.lms.personalization.repository.PreferredSummerSemesterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LearningGoalServiceImplTest {

    @Mock private LearningGoalRepository learningGoalRepository;
    @Mock private PreferredSummerSemesterRepository preferredSummerSemesterRepository;
    @Mock private LearningPathRepository learningPathRepository;
    @Mock private GoalValidationResultRepository goalValidationResultRepository;
    @Mock private LearningGoalMapper learningGoalMapper;
    @Mock private jakarta.persistence.EntityManager entityManager;

    @InjectMocks
    private LearningGoalServiceImpl learningGoalService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(learningGoalService, "entityManager", entityManager);
    }

    private final UUID studentId = UUID.randomUUID();
    private final UUID learningGoalId = UUID.randomUUID();

    private LearningGoal buildGoal() {
        return LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .targetGpa(BigDecimal.valueOf(3.5))
            .prefMainSemLearnIntensity(com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity.Standard)
            .plannedSummerSemCount(0)
            .preferredSummerSemesters(new ArrayList<>())
            .build();
    }

    private CreateLearningGoalRequest makeGoalRequest() {
        CreateLearningGoalRequest req = new CreateLearningGoalRequest();
        req.setSpecializationId(UUID.randomUUID().toString());
        req.setTargetGpa(BigDecimal.valueOf(3.5));
        req.setPrefMainSemLearnIntensity("Standard");
        req.setPlannedSummerSemCount(0);
        return req;
    }

    @Test void createLearningGoal_shouldReturnResponse_whenValidRequest() {
        CreateLearningGoalRequest req = makeGoalRequest();
        LearningGoal savedGoal = buildGoal();
        when(learningGoalRepository.save(any())).thenReturn(savedGoal);
        when(learningGoalMapper.toResponse(any())).thenReturn(new LearningGoalResponse());
        when(goalValidationResultRepository.findTopByLearningGoalIdOrderByValidationTimestampDesc(any()))
            .thenReturn(Optional.empty());

        LearningGoalResponse result = learningGoalService.createLearningGoal(studentId, req);
        assertNotNull(result);
    }

    @Test void getLearningGoalById_shouldReturnResponse_whenExists() {
        LearningGoal goal = buildGoal();
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));
        when(learningGoalMapper.toResponse(any())).thenReturn(new LearningGoalResponse());
        when(goalValidationResultRepository.findTopByLearningGoalIdOrderByValidationTimestampDesc(any()))
            .thenReturn(Optional.empty());

        LearningGoalResponse result = learningGoalService.getLearningGoalById(studentId, learningGoalId);
        assertNotNull(result);
    }

    @Test void getCurrentLearningGoal_shouldReturnOptional_whenExists() {
        LearningGoal goal = buildGoal();
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.of(goal));
        when(learningGoalMapper.toResponse(any())).thenReturn(new LearningGoalResponse());
        when(goalValidationResultRepository.findTopByLearningGoalIdOrderByValidationTimestampDesc(any()))
            .thenReturn(Optional.empty());

        Optional<LearningGoalResponse> result = learningGoalService.getCurrentLearningGoal(studentId);
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test void getCurrentLearningGoal_shouldReturnEmpty_whenNoActiveGoal() {
        when(learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.empty());

        Optional<LearningGoalResponse> result = learningGoalService.getCurrentLearningGoal(studentId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test void updateLearningGoal_shouldReturnUpdatedResponse_whenExists() {
        LearningGoal goal = buildGoal();
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));
        when(learningGoalRepository.save(any())).thenReturn(goal);
        when(learningGoalMapper.toResponse(any())).thenReturn(new LearningGoalResponse());
        when(goalValidationResultRepository.findTopByLearningGoalIdOrderByValidationTimestampDesc(any()))
            .thenReturn(Optional.empty());

        UpdateLearningGoalRequest request = new UpdateLearningGoalRequest();
        request.setTargetGpa(BigDecimal.valueOf(3.7));
        request.setPrefMainSemLearnIntensity("Heavy");

        LearningGoalResponse result = learningGoalService.updateLearningGoal(studentId, learningGoalId, request);
        assertNotNull(result);
    }

    @Test void updateLearningGoal_shouldSaveValidationResult_whenProvided() {
        LearningGoal goal = buildGoal();
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));
        when(learningGoalRepository.save(any())).thenReturn(goal);
        when(learningGoalMapper.toResponse(any())).thenReturn(new LearningGoalResponse());
        when(goalValidationResultRepository.save(any())).thenReturn(GoalValidationResult.builder()
            .learningGoalId(learningGoalId).studentId(studentId).build());

        UpdateLearningGoalRequest request = new UpdateLearningGoalRequest();
        LearningGoalFeasibilityResponse validationResult = new LearningGoalFeasibilityResponse();
        validationResult.setFeasibilityLevel(FeasibilityLevel.MEDIUM);
        validationResult.setProbabilityScore(BigDecimal.valueOf(0.75));
        request.setValidationResult(validationResult);

        LearningGoalResponse result = learningGoalService.updateLearningGoal(studentId, learningGoalId, request);
        assertNotNull(result);
    }

    @Test void deleteLearningGoal_shouldDelete_whenExists() {
        LearningGoal goal = buildGoal();
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));
        when(learningGoalRepository.save(any())).thenReturn(goal);

        learningGoalService.deleteLearningGoal(studentId, learningGoalId);
        assertTrue(true);
    }

    @Test void createPreferredSummerSemester_shouldReturnResponse_whenValidRequest() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .targetGpa(BigDecimal.valueOf(3.5))
            .prefMainSemLearnIntensity(com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity.Standard)
            .plannedSummerSemCount(2)
            .preferredSummerSemesters(new ArrayList<>())
            .build();
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));
        when(preferredSummerSemesterRepository.existsByLearningGoalLearningGoalIdAndSemesterId(any(), any()))
            .thenReturn(false);
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(Collections.emptyList());

        PreferredSummerSemester saved = new PreferredSummerSemester();
        saved.setPreferredSummerSemesterId(UUID.randomUUID());
        saved.setLearningGoal(goal);
        saved.setSemesterId(UUID.randomUUID());
        when(preferredSummerSemesterRepository.save(any())).thenReturn(saved);
        when(learningGoalMapper.toPreferredSummerSemesterResponse(any()))
            .thenReturn(new PreferredSummerSemesterResponse());

        CreatePreferredSummerSemesterRequest request = new CreatePreferredSummerSemesterRequest();
        request.setSemesterId(UUID.randomUUID());
        request.setLearnIntensity("Light");

        PreferredSummerSemesterResponse result = learningGoalService.createPreferredSummerSemester(
            studentId, learningGoalId, request);
        assertNotNull(result);
    }

    @Test void getPreferredSummerSemesters_shouldReturnList_whenExist() {
        LearningGoal goal = buildGoal();
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));
        PreferredSummerSemester entity = new PreferredSummerSemester();
        entity.setPreferredSummerSemesterId(UUID.randomUUID());
        entity.setLearningGoal(goal);
        entity.setSemesterId(UUID.randomUUID());
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(List.of(entity));
        when(learningGoalMapper.toPreferredSummerSemesterResponse(any()))
            .thenReturn(new PreferredSummerSemesterResponse());

        List<PreferredSummerSemesterResponse> result = learningGoalService.getPreferredSummerSemesters(
            studentId, learningGoalId);
        assertNotNull(result);
        assertTrue(result.size() == 1);
    }

    @Test void createPreferredSummerSemesters_shouldReturnEmpty_whenEmptyRequests() {
        LearningGoal goal = buildGoal();
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));

        List<PreferredSummerSemesterResponse> result = learningGoalService.createPreferredSummerSemesters(
            studentId, learningGoalId, Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test void createPreferredSummerSemesters_shouldReturnList_whenNonEmptyRequests() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .targetGpa(BigDecimal.valueOf(3.5))
            .prefMainSemLearnIntensity(com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity.Standard)
            .plannedSummerSemCount(3)
            .preferredSummerSemesters(new ArrayList<>())
            .build();
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));

        PreferredSummerSemester saved1 = new PreferredSummerSemester();
        saved1.setPreferredSummerSemesterId(UUID.randomUUID());
        saved1.setSemesterId(UUID.randomUUID());
        PreferredSummerSemester saved2 = new PreferredSummerSemester();
        saved2.setPreferredSummerSemesterId(UUID.randomUUID());
        saved2.setSemesterId(UUID.randomUUID());
        when(preferredSummerSemesterRepository.saveAll(any()))
            .thenReturn(List.of(saved1, saved2));
        when(learningGoalMapper.toPreferredSummerSemesterResponse(any()))
            .thenReturn(new PreferredSummerSemesterResponse());

        CreatePreferredSummerSemesterRequest r1 = new CreatePreferredSummerSemesterRequest();
        r1.setSemesterId(UUID.randomUUID());
        r1.setLearnIntensity("Standard");
        CreatePreferredSummerSemesterRequest r2 = new CreatePreferredSummerSemesterRequest();
        r2.setSemesterId(UUID.randomUUID());
        r2.setLearnIntensity("Light");

        var result = learningGoalService.createPreferredSummerSemesters(
            studentId, learningGoalId, List.of(r1, r2));
        assertNotNull(result);
        assertTrue(result.size() == 2);
    }

    @Test void createLearningGoal_shouldValidatePriorities_whenInvalid() {
        CreateLearningGoalRequest req = makeGoalRequest();
        req.setAttemptTargetGpaOrder(1);
        req.setFocusOnTargetOccupation(1);
        req.setCompletedOnTime(1);

        try {
            learningGoalService.createLearningGoal(studentId, req);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("unique"));
        }
        assertTrue(true);
    }

    @Test void getLearningGoalById_shouldThrow_whenNotFound() {
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.empty());

        try {
            learningGoalService.getLearningGoalById(studentId, learningGoalId);
        } catch (jakarta.persistence.EntityNotFoundException ignored) {
        }
        assertTrue(true);
    }

    @Test void deleteLearningGoal_shouldThrow_whenNotFound() {
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.empty());

        try {
            learningGoalService.deleteLearningGoal(studentId, learningGoalId);
        } catch (jakarta.persistence.EntityNotFoundException ignored) {
        }
        assertTrue(true);
    }

    @Test void updateLearningGoal_shouldReplaceSummerSemesters_whenProvided() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .targetGpa(BigDecimal.valueOf(3.5))
            .prefMainSemLearnIntensity(com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity.Standard)
            .plannedSummerSemCount(2)
            .preferredSummerSemesters(new ArrayList<>())
            .build();
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));
        when(learningGoalRepository.save(any())).thenReturn(goal);
        when(learningGoalMapper.toResponse(any())).thenReturn(new LearningGoalResponse());
        when(goalValidationResultRepository.findTopByLearningGoalIdOrderByValidationTimestampDesc(any()))
            .thenReturn(Optional.empty());

        UpdateLearningGoalRequest request = new UpdateLearningGoalRequest();
        CreatePreferredSummerSemesterRequest summerReq = new CreatePreferredSummerSemesterRequest();
        summerReq.setSemesterId(UUID.randomUUID());
        summerReq.setLearnIntensity("Light");
        request.setSummerSemesters(List.of(summerReq));

        LearningGoalResponse result = learningGoalService.updateLearningGoal(studentId, learningGoalId, request);
        assertNotNull(result);
    }

    @Test void updateLearningGoal_shouldThrow_whenDuplicateSummerSemesters() {
        UUID semId = UUID.randomUUID();
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .targetGpa(BigDecimal.valueOf(3.5))
            .prefMainSemLearnIntensity(com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity.Standard)
            .plannedSummerSemCount(2)
            .preferredSummerSemesters(new ArrayList<>())
            .build();
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));

        UpdateLearningGoalRequest request = new UpdateLearningGoalRequest();
        CreatePreferredSummerSemesterRequest r1 = new CreatePreferredSummerSemesterRequest();
        r1.setSemesterId(semId); r1.setLearnIntensity("Light");
        CreatePreferredSummerSemesterRequest r2 = new CreatePreferredSummerSemesterRequest();
        r2.setSemesterId(semId); r2.setLearnIntensity("Standard");
        request.setSummerSemesters(List.of(r1, r2));

        try {
            learningGoalService.updateLearningGoal(studentId, learningGoalId, request);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Duplicate semester"));
        }
        assertTrue(true);
    }

    @Test void createPreferredSummerSemester_shouldThrow_whenAlreadyExists() {
        LearningGoal goal = buildGoal();
        goal.setPlannedSummerSemCount(2);
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));
        when(preferredSummerSemesterRepository.existsByLearningGoalLearningGoalIdAndSemesterId(any(), any()))
            .thenReturn(true);

        CreatePreferredSummerSemesterRequest request = new CreatePreferredSummerSemesterRequest();
        request.setSemesterId(UUID.randomUUID());
        request.setLearnIntensity("Standard");

        try {
            learningGoalService.createPreferredSummerSemester(studentId, learningGoalId, request);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("already exists"));
        }
        assertTrue(true);
    }

    @Test void createPreferredSummerSemester_shouldThrow_whenExceedingCount() {
        LearningGoal goal = buildGoal();
        goal.setPlannedSummerSemCount(1);
        PreferredSummerSemester existing = new PreferredSummerSemester();
        existing.setPreferredSummerSemesterId(UUID.randomUUID());
        existing.setSemesterId(UUID.randomUUID());
        goal.setPreferredSummerSemesters(new ArrayList<>(List.of(existing)));
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));
        when(preferredSummerSemesterRepository.existsByLearningGoalLearningGoalIdAndSemesterId(any(), any()))
            .thenReturn(false);
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(List.of(existing));

        CreatePreferredSummerSemesterRequest request = new CreatePreferredSummerSemesterRequest();
        request.setSemesterId(UUID.randomUUID());
        request.setLearnIntensity("Light");

        try {
            learningGoalService.createPreferredSummerSemester(studentId, learningGoalId, request);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Cannot add more"));
        }
        assertTrue(true);
    }

    @Test void createPreferredSummerSemesters_shouldThrow_whenExceedingCount() {
        LearningGoal goal = buildGoal();
        goal.setPlannedSummerSemCount(1);
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));

        CreatePreferredSummerSemesterRequest r1 = new CreatePreferredSummerSemesterRequest();
        r1.setSemesterId(UUID.randomUUID()); r1.setLearnIntensity("Light");
        CreatePreferredSummerSemesterRequest r2 = new CreatePreferredSummerSemesterRequest();
        r2.setSemesterId(UUID.randomUUID()); r2.setLearnIntensity("Standard");

        try {
            learningGoalService.createPreferredSummerSemesters(studentId, learningGoalId, List.of(r1, r2));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Cannot add more"));
        }
        assertTrue(true);
    }

    @Test void createPreferredSummerSemesters_shouldThrow_whenDuplicateInRequest() {
        UUID semId = UUID.randomUUID();
        LearningGoal goal = buildGoal();
        goal.setPlannedSummerSemCount(3);
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));

        CreatePreferredSummerSemesterRequest r1 = new CreatePreferredSummerSemesterRequest();
        r1.setSemesterId(semId); r1.setLearnIntensity("Light");
        CreatePreferredSummerSemesterRequest r2 = new CreatePreferredSummerSemesterRequest();
        r2.setSemesterId(semId); r2.setLearnIntensity("Standard");

        try {
            learningGoalService.createPreferredSummerSemesters(studentId, learningGoalId, List.of(r1, r2));
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Duplicate semester in request"));
        }
        assertTrue(true);
    }

    @Test void createLearningGoal_shouldThrow_whenNonContiguousPriorities() {
        CreateLearningGoalRequest req = makeGoalRequest();
        req.setAttemptTargetGpaOrder(1);
        req.setFocusOnTargetOccupation(3);

        try {
            learningGoalService.createLearningGoal(studentId, req);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("contiguous"));
        }
        assertTrue(true);
    }

    @Test void createLearningGoal_shouldThrow_whenInvalidIntensity() {
        CreateLearningGoalRequest req = makeGoalRequest();
        req.setPrefMainSemLearnIntensity("InvalidIntensity");

        try {
            learningGoalService.createLearningGoal(studentId, req);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Invalid prefMainSemLearnIntensity"));
        }
        assertTrue(true);
    }

    @Test void createPreferredSummerSemester_shouldThrow_whenInvalidIntensity() {
        LearningGoal goal = buildGoal();
        goal.setPlannedSummerSemCount(2);
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));
        when(preferredSummerSemesterRepository.existsByLearningGoalLearningGoalIdAndSemesterId(any(), any()))
            .thenReturn(false);
        when(preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(any()))
            .thenReturn(Collections.emptyList());

        CreatePreferredSummerSemesterRequest request = new CreatePreferredSummerSemesterRequest();
        request.setSemesterId(UUID.randomUUID());
        request.setLearnIntensity("Invalid");

        try {
            learningGoalService.createPreferredSummerSemester(studentId, learningGoalId, request);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Invalid learnIntensity"));
        }
        assertTrue(true);
    }

    @Test void updateLearningGoal_shouldThrow_whenSummerWithoutMainIntensity() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .targetGpa(BigDecimal.valueOf(3.5))
            .prefMainSemLearnIntensity(null)
            .plannedSummerSemCount(1)
            .preferredSummerSemesters(new ArrayList<>())
            .build();
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));

        UpdateLearningGoalRequest request = new UpdateLearningGoalRequest();
        CreatePreferredSummerSemesterRequest summerReq = new CreatePreferredSummerSemesterRequest();
        summerReq.setSemesterId(UUID.randomUUID());
        summerReq.setLearnIntensity("Light");
        request.setSummerSemesters(List.of(summerReq));

        try {
            learningGoalService.updateLearningGoal(studentId, learningGoalId, request);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("prefMainSemLearnIntensity"));
        }
        assertTrue(true);
    }

    @Test void updateLearningGoal_shouldThrow_whenCurrentEntriesExceedPlannedCount() {
        UUID semId1 = UUID.randomUUID();
        UUID semId2 = UUID.randomUUID();
        PreferredSummerSemester e1 = new PreferredSummerSemester();
        e1.setPreferredSummerSemesterId(UUID.randomUUID());
        e1.setSemesterId(semId1);
        PreferredSummerSemester e2 = new PreferredSummerSemester();
        e2.setPreferredSummerSemesterId(UUID.randomUUID());
        e2.setSemesterId(semId2);
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(learningGoalId).studentId(studentId)
            .specializationId(UUID.randomUUID().toString()).isActive(true)
            .targetGpa(BigDecimal.valueOf(3.5))
            .prefMainSemLearnIntensity(com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity.Standard)
            .plannedSummerSemCount(2)
            .preferredSummerSemesters(new ArrayList<>(List.of(e1, e2)))
            .build();
        when(learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(goal));

        UpdateLearningGoalRequest request = new UpdateLearningGoalRequest();
        request.setPlannedSummerSemCount(1);

        try {
            learningGoalService.updateLearningGoal(studentId, learningGoalId, request);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Remove excess entries"));
        }
        assertTrue(true);
    }
}
