package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.dto.request.CreateLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.request.CreatePreferredSummerSemesterRequest;
import com.hcmut.lms.personalization.application.dto.request.UpdateLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.response.LearningGoalResponse;
import com.hcmut.lms.personalization.application.dto.response.PreferredSummerSemesterResponse;
import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import com.hcmut.lms.personalization.application.dto.response.enums.SummerLearningIntensity;
import com.hcmut.lms.personalization.application.mapper.LearningGoalMapper;
import com.hcmut.lms.personalization.application.service.LearningGoalService;
import com.hcmut.lms.personalization.application.service.impl.support.IntensityParsingSupport;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningGoal.PreferredSummerSemester;
import com.hcmut.lms.personalization.repository.GoalValidationResultRepository;
import com.hcmut.lms.personalization.repository.LearningGoalRepository;
import com.hcmut.lms.personalization.repository.LearningPathRepository;
import com.hcmut.lms.personalization.repository.PreferredSummerSemesterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LearningGoalServiceImpl implements LearningGoalService {

  private final LearningGoalRepository learningGoalRepository;
  private final PreferredSummerSemesterRepository preferredSummerSemesterRepository;
  private final LearningPathRepository learningPathRepository;
  private final GoalValidationResultRepository goalValidationResultRepository;
  private final LearningGoalMapper learningGoalMapper;

  @Override
  @Transactional(readOnly = true)
  public LearningGoalResponse getCurrentLearningGoal(UUID studentId) {
    return learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(studentId)
        .map(this::toResponse)
        .orElse(null);
  }

  @Override
  @Transactional(readOnly = true)
  public LearningGoalResponse getLearningGoalById(UUID studentId, UUID learningGoalId) {
    return toResponse(getOwnedLearningGoal(studentId, learningGoalId));
  }

  @Override
  public LearningGoalResponse createLearningGoal(UUID studentId, CreateLearningGoalRequest request) {
    log.info("Creating learning goal for studentId={}", studentId);
    validateTargetGpa(request.getTargetGpa());
    validatePriorityOrders(
        request.getAttemptTargetGpaOrder(), request.getFocusOnTargetOccupation(),
        request.getCompletedOnTime());

    learningGoalRepository.deactivateActiveByStudentId(studentId);

    LearningGoal learningGoal = LearningGoal.builder()
        .learningGoalId(UUID.randomUUID())
        .studentId(studentId)
        .specializationId(request.getSpecializationId())
        .targetGpa(request.getTargetGpa())
        .expectedCompletedSemester(request.getExpectedCompletedSemesterId())
        .prefMainSemLearnIntensity(parseIntensity(request.getPrefMainSemLearnIntensity()))
        .plannedSummerSemCount(request.getPlannedSummerSemCount())
        .targetOccupationCode(request.getTargetOccupationCode())
        .attemptTargetGpaOrder(request.getAttemptTargetGpaOrder())
        .focusOnTargetOccupation(request.getFocusOnTargetOccupation())
        .completedOnTime(request.getCompletedOnTime())
        .isActive(true)
        .build();

    LearningGoal saved = learningGoalRepository.save(learningGoal);
    return toResponse(saved);
  }

  @Override
  public LearningGoalResponse updateLearningGoal(
      UUID studentId, UUID learningGoalId,
      UpdateLearningGoalRequest request) {
    LearningGoal learningGoal = getOwnedLearningGoal(studentId, learningGoalId);

    if (request.getPrefMainSemLearnIntensity() != null) {
      enforceIntensityUpdateRule(learningGoal, request.getPrefMainSemLearnIntensity());
    }

    if (request.getSpecializationId() != null) {
      learningGoal.setSpecializationId(request.getSpecializationId());
    }
    if (request.getTargetGpa() != null) {
      learningGoal.setTargetGpa(request.getTargetGpa());
    }
    if (request.getExpectedCompletedSemesterId() != null) {
      learningGoal.setExpectedCompletedSemester(request.getExpectedCompletedSemesterId());
    }
    if (request.getPrefMainSemLearnIntensity() != null) {
      learningGoal.setPrefMainSemLearnIntensity(parseIntensity(request.getPrefMainSemLearnIntensity()));
    }
    if (request.getPlannedSummerSemCount() != null) {
      learningGoal.setPlannedSummerSemCount(request.getPlannedSummerSemCount());
    }
    if (request.getTargetOccupationCode() != null) {
      learningGoal.setTargetOccupationCode(request.getTargetOccupationCode());
    }
    if (request.getAttemptTargetGpaOrder() != null) {
      learningGoal.setAttemptTargetGpaOrder(request.getAttemptTargetGpaOrder());
    }
    if (request.getFocusOnTargetOccupation() != null) {
      learningGoal.setFocusOnTargetOccupation(request.getFocusOnTargetOccupation());
    }
    if (request.getCompletedOnTime() != null) {
      learningGoal.setCompletedOnTime(request.getCompletedOnTime());
    }

    validateTargetGpa(learningGoal.getTargetGpa());
    validatePriorityOrders(
        learningGoal.getAttemptTargetGpaOrder(), learningGoal.getFocusOnTargetOccupation(),
        learningGoal.getCompletedOnTime());

    LearningGoal saved = learningGoalRepository.save(learningGoal);
    return toResponse(saved);
  }

  @Override
  public void deleteLearningGoal(UUID studentId, UUID learningGoalId) {
    LearningGoal learningGoal = getOwnedLearningGoal(studentId, learningGoalId);
    learningGoal.setIsActive(false);
    learningGoalRepository.save(learningGoal);
    learningPathRepository.deactivateActiveByStudentIdAndLearningGoalId(studentId, learningGoalId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PreferredSummerSemesterResponse> getPreferredSummerSemesters(UUID studentId, UUID learningGoalId) {
    getOwnedLearningGoal(studentId, learningGoalId);
    return preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(learningGoalId)
        .stream()
        .map(this::toPreferredSummerSemesterResponse)
        .toList();
  }

  @Override
  public PreferredSummerSemesterResponse createPreferredSummerSemester(
      UUID studentId, UUID learningGoalId,
      CreatePreferredSummerSemesterRequest request) {

    LearningGoal learningGoal = getOwnedLearningGoal(studentId, learningGoalId);

    if (preferredSummerSemesterRepository.existsByLearningGoalLearningGoalIdAndSemesterId(
        learningGoalId,
        request.getSemesterId())) {
      throw new IllegalArgumentException("Preferred summer semester already exists for this semester");
    }

    List<PreferredSummerSemester> existing =
        preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(
        learningGoalId);

    Integer plannedSummerSemCount = learningGoal.getPlannedSummerSemCount();
    if (plannedSummerSemCount != null && existing.size() >= plannedSummerSemCount) {
      throw new IllegalArgumentException("Cannot add more preferred summer semesters than plannedSummerSemCount");
    }

    SummerLearningIntensity summerIntensity = parseSummerIntensity(request.getLearnIntensity());
    if (learningGoal.getPrefMainSemLearnIntensity() != null && intensityRank(summerIntensity.name()) > intensityRank(
        learningGoal.getPrefMainSemLearnIntensity().name())) {
      throw new IllegalArgumentException("Summer semester intensity cannot exceed prefMainSemLearnIntensity");
    }

    PreferredSummerSemester saved = preferredSummerSemesterRepository.save(PreferredSummerSemester.builder()
        .preferredSummerSemesterId(UUID.randomUUID())
        .learningGoal(learningGoal)
        .semesterId(request.getSemesterId())
        .learningIntensity(summerIntensity)
        .build());

    return toPreferredSummerSemesterResponse(saved);
  }

  private LearningGoal getOwnedLearningGoal(UUID studentId, UUID learningGoalId) {
    return learningGoalRepository.findByLearningGoalIdAndStudentIdAndIsActiveTrue(learningGoalId, studentId)
        .orElseThrow(() -> new EntityNotFoundException("Learning goal not found with id: " + learningGoalId));
  }

  private LearningIntensity parseIntensity(String value) {
    try {
      return IntensityParsingSupport.parseRequiredTrimmedTitleCase(LearningIntensity.class, value);
    } catch (Exception ex) {
      throw new IllegalArgumentException("Invalid prefMainSemLearnIntensity: " + value);
    }
  }

  private SummerLearningIntensity parseSummerIntensity(String value) {
    try {
      return IntensityParsingSupport.parseRequiredTrimmedTitleCase(SummerLearningIntensity.class, value);
    } catch (Exception ex) {
      throw new IllegalArgumentException("Invalid learnIntensity: " + value);
    }
  }

  private void validateTargetGpa(BigDecimal targetGpa) {
    if (targetGpa == null) {
      return;
    }
    if (targetGpa.compareTo(BigDecimal.ZERO) < 0 || targetGpa.compareTo(BigDecimal.valueOf(4)) > 0) {
      throw new IllegalArgumentException("targetGpa must be between 0 and 4");
    }
  }

  private void validatePriorityOrders(
      Integer attemptTargetGpaOrder, Integer focusOnTargetOccupation, Integer completedOnTime) {
    List<Integer> priorities = Stream.of(attemptTargetGpaOrder, focusOnTargetOccupation, completedOnTime)
        .filter(Objects::nonNull)
        .toList();

    if (priorities.isEmpty()) {
      return;
    }

    if (priorities.stream().anyMatch(value -> value <= 0)) {
      throw new IllegalArgumentException("Priority fields must be greater than 0");
    }

    Set<Integer> unique = new HashSet<>(priorities);
    if (unique.size() != priorities.size()) {
      throw new IllegalArgumentException("Priority fields must be unique");
    }

    int expectedCount = priorities.size();
    for (int expected = 1; expected <= expectedCount; expected++) {
      if (!unique.contains(expected)) {
        throw new IllegalArgumentException("Priority fields must be contiguous from 1 to N");
      }
    }
  }

  private void enforceIntensityUpdateRule(LearningGoal learningGoal, String newIntensityRaw) {
    LearningIntensity newIntensity = parseIntensity(newIntensityRaw);
    List<PreferredSummerSemester> summerSemesters =
        preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(
        learningGoal.getLearningGoalId());

    boolean hasHigherSummerIntensity = summerSemesters.stream()
        .map(PreferredSummerSemester::getLearningIntensity)
        .filter(Objects::nonNull)
        .anyMatch(value -> intensityRank(value.name()) > intensityRank(newIntensity.name()));

    if (hasHigherSummerIntensity) {
      throw new IllegalArgumentException(
          "Cannot update prefMainSemLearnIntensity below existing preferred summer semester intensity");
    }
  }

  private int intensityRank(String intensity) {
    return switch (IntensityParsingSupport.normalizeTrimmedTitleCase(intensity)) {
      case "Light" -> 1;
      case "Moderate" -> 2;
      case "Standard" -> 3;
      case "Heavy" -> 4;
      default -> throw new IllegalArgumentException("Invalid intensity value: " + intensity);
    };
  }

  private LearningGoalResponse toResponse(LearningGoal learningGoal) {
    LearningGoalResponse response = learningGoalMapper.toResponse(learningGoal);
    response.setGoalValidationResult(
        goalValidationResultRepository.findTopByLearningGoalIdOrderByValidationTimestampDesc(
            learningGoal.getLearningGoalId()).map(learningGoalMapper::toGoalValidationResultResponse).orElse(null));
    return response;
  }

  private PreferredSummerSemesterResponse toPreferredSummerSemesterResponse(PreferredSummerSemester entity) {
    return learningGoalMapper.toPreferredSummerSemesterResponse(entity);
  }
}

