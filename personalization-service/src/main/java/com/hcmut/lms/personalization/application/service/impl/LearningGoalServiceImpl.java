package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.dto.request.CreateLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.request.CreatePreferredSummerSemesterRequest;
import com.hcmut.lms.personalization.application.dto.request.UpdateLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.response.LearningGoalFeasibilityResponse;
import com.hcmut.lms.personalization.application.dto.response.LearningGoalResponse;
import com.hcmut.lms.personalization.application.dto.response.PreferredSummerSemesterResponse;
import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import com.hcmut.lms.personalization.application.dto.response.enums.SummerLearningIntensity;
import com.hcmut.lms.personalization.application.mapper.LearningGoalMapper;
import com.hcmut.lms.personalization.application.service.LearningGoalService;
import com.hcmut.lms.personalization.application.service.impl.support.IntensityParsingSupport;
import com.hcmut.lms.personalization.domain.entity.learningGoal.GoalValidationResult;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningGoal.PreferredSummerSemester;
import com.hcmut.lms.personalization.repository.GoalValidationResultRepository;
import com.hcmut.lms.personalization.repository.LearningGoalRepository;
import com.hcmut.lms.personalization.repository.LearningPathRepository;
import com.hcmut.lms.personalization.repository.PreferredSummerSemesterRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LearningGoalServiceImpl implements LearningGoalService {

  private final LearningGoalRepository learningGoalRepository;
  private final PreferredSummerSemesterRepository preferredSummerSemesterRepository;
  private final LearningPathRepository learningPathRepository;
  private final GoalValidationResultRepository goalValidationResultRepository;
  private final LearningGoalMapper learningGoalMapper;

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  @Transactional(readOnly = true)
  public Optional<LearningGoalResponse> getCurrentLearningGoal(UUID studentId) {
    return learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(studentId)
        .map(this::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public LearningGoalResponse getLearningGoalById(UUID studentId, UUID learningGoalId) {
    return toResponse(getOwnedLearningGoal(studentId, learningGoalId));
  }

  @Override
  @Transactional
  public LearningGoalResponse createLearningGoal(UUID studentId, CreateLearningGoalRequest request) {
    log.info("Creating learning goal for studentId={}", studentId);
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
  @Transactional
  public LearningGoalResponse updateLearningGoal(
      UUID studentId, UUID learningGoalId,
      UpdateLearningGoalRequest request) {
    LearningGoal learningGoal = getOwnedLearningGoal(studentId, learningGoalId);

    // 1. Apply scalar field updates
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

    // 2. Replace summer semesters if provided
    if (request.getSummerSemesters() != null) {
//      LearningIntensity mainIntensity = learningGoal.getPrefMainSemLearnIntensity();
      Set<UUID> seenSemesterIds = new HashSet<>();
      List<PreferredSummerSemester> newEntries = new ArrayList<>();
      for (CreatePreferredSummerSemesterRequest dto : request.getSummerSemesters()) {
        if (seenSemesterIds.contains(dto.getSemesterId())) {
          throw new IllegalArgumentException("Duplicate semester in request: " + dto.getSemesterId());
        }
        seenSemesterIds.add(dto.getSemesterId());

        SummerLearningIntensity summerIntensity = parseSummerIntensity(dto.getLearnIntensity());
//        if (mainIntensity != null && intensityRank(summerIntensity.name()) > intensityRank(mainIntensity.name())) {
//          throw new IllegalArgumentException(
//              "Summer semester intensity cannot exceed prefMainSemLearnIntensity");
//        }
        PreferredSummerSemester entry = new PreferredSummerSemester();
        entry.setPreferredSummerSemesterId(UUID.randomUUID());
        entry.setLearningGoal(learningGoal);
        entry.setSemesterId(dto.getSemesterId());
        entry.setLearningIntensity(summerIntensity);
        newEntries.add(entry);
      }
      learningGoal.getPreferredSummerSemesters().clear();
      entityManager.flush();
      learningGoal.getPreferredSummerSemesters().addAll(newEntries);
//    } else {
//      // When summer semesters are not being replaced, validate intensity against existing DB entries
//      if (request.getPrefMainSemLearnIntensity() != null) {
//        enforceIntensityUpdateRule(learningGoal, request.getPrefMainSemLearnIntensity());
//      }
    }

    // 3. Validate consistency
    validatePriorityOrders(
        learningGoal.getAttemptTargetGpaOrder(), learningGoal.getFocusOnTargetOccupation(),
        learningGoal.getCompletedOnTime());
    validateSummerSemesterConsistency(learningGoal);

    // 4. Persist validation result if provided
    GoalValidationResult savedValidation = null;
    if (request.getValidationResult() != null) {
      LearningGoalFeasibilityResponse vr = request.getValidationResult();
      GoalValidationResult entity = GoalValidationResult.builder()
          .learningGoalId(learningGoal.getLearningGoalId())
          .studentId(learningGoal.getStudentId())
          .feasibilityLevel(vr.getFeasibilityLevel())
          .probabilityScore(vr.getProbabilityScore())
          .metrics(vr.getMetrics())
          .preliminaryChecks(vr.getPreliminaryChecks())
          .probabilityAnalysis(vr.getProbabilityAnalysis())
          .recommendations(vr.getRecommendations())
          .warnings(vr.getWarnings())
          .validationTimestamp(Instant.now())
          .build();
      savedValidation = goalValidationResultRepository.save(entity);
    }

    // 5. Save
    LearningGoal saved = learningGoalRepository.save(learningGoal);
    return savedValidation != null ? toResponse(saved, savedValidation) : toResponse(saved);
  }

  @Override
  @Transactional
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
  @Transactional
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
//    if (learningGoal.getPrefMainSemLearnIntensity() != null && intensityRank(summerIntensity.name()) > intensityRank(
//        learningGoal.getPrefMainSemLearnIntensity().name())) {
//      throw new IllegalArgumentException("Summer semester intensity cannot exceed prefMainSemLearnIntensity");
//    }

    PreferredSummerSemester saved = preferredSummerSemesterRepository.save(PreferredSummerSemester.builder()
        .preferredSummerSemesterId(UUID.randomUUID())
        .learningGoal(learningGoal)
        .semesterId(request.getSemesterId())
        .learningIntensity(summerIntensity)
        .build());

    return toPreferredSummerSemesterResponse(saved);
  }

  @Override
  @Transactional
  public List<PreferredSummerSemesterResponse> createPreferredSummerSemesters(
      UUID studentId, UUID learningGoalId,
      List<CreatePreferredSummerSemesterRequest> requests) {

    if (requests.isEmpty()) {
      return List.of();
    }

    LearningGoal learningGoal = getOwnedLearningGoal(studentId, learningGoalId);
    return createPreferredSummerSemesters(learningGoal, requests);
  }

  private List<PreferredSummerSemesterResponse> createPreferredSummerSemesters(
      LearningGoal learningGoal,
      List<CreatePreferredSummerSemesterRequest> requests) {

    List<PreferredSummerSemester> existing = learningGoal.getPreferredSummerSemesters();

    Set<UUID> existingSemesterIds = existing.stream()
        .map(PreferredSummerSemester::getSemesterId)
        .collect(Collectors.toSet());

    for (CreatePreferredSummerSemesterRequest request : requests) {
      if (existingSemesterIds.contains(request.getSemesterId())) {
        throw new IllegalArgumentException("Preferred summer semester already exists for semester: " + request.getSemesterId());
      }
    }

    Integer plannedSummerSemCount = learningGoal.getPlannedSummerSemCount();
    if (plannedSummerSemCount != null && existing.size() + requests.size() > plannedSummerSemCount) {
      throw new IllegalArgumentException("Cannot add more preferred summer semesters than plannedSummerSemCount");
    }

    LearningIntensity mainIntensity = learningGoal.getPrefMainSemLearnIntensity();

    List<PreferredSummerSemester> toSave = new ArrayList<>();
    Set<UUID> newSemesterIds = new HashSet<>();
    for (CreatePreferredSummerSemesterRequest request : requests) {
      if (newSemesterIds.contains(request.getSemesterId())) {
        throw new IllegalArgumentException("Duplicate semester in request: " + request.getSemesterId());
      }
      newSemesterIds.add(request.getSemesterId());

      SummerLearningIntensity summerIntensity = parseSummerIntensity(request.getLearnIntensity());
//      if (mainIntensity != null && intensityRank(summerIntensity.name()) > intensityRank(mainIntensity.name())) {
//        throw new IllegalArgumentException("Summer semester intensity cannot exceed prefMainSemLearnIntensity");
//      }

      toSave.add(PreferredSummerSemester.builder()
          .preferredSummerSemesterId(UUID.randomUUID())
          .learningGoal(learningGoal)
          .semesterId(request.getSemesterId())
          .learningIntensity(summerIntensity)
          .build());
    }

    List<PreferredSummerSemester> saved = preferredSummerSemesterRepository.saveAll(toSave);
    return saved.stream().map(this::toPreferredSummerSemesterResponse).toList();
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

  /**
   * Validates that the learning goal's summer semester configuration is consistent.
   * Uses the entity's in-memory collection to support both DB-loaded and just-replaced entries.
   */
  private void validateSummerSemesterConsistency(LearningGoal learningGoal) {
    Integer plannedCount = learningGoal.getPlannedSummerSemCount();
    if (plannedCount != null && plannedCount > 0 && learningGoal.getPrefMainSemLearnIntensity() == null) {
      throw new IllegalArgumentException(
          "prefMainSemLearnIntensity must be set when plannedSummerSemCount > 0");
    }

    if (plannedCount != null) {
      int currentCount = learningGoal.getPreferredSummerSemesters().size();
      if (currentCount > plannedCount) {
        throw new IllegalArgumentException(
            "Cannot set plannedSummerSemCount to " + plannedCount
                + " because " + currentCount
                + " preferred summer semesters already exist. Remove excess entries first.");
      }
    }
  }

//  private void enforceIntensityUpdateRule(LearningGoal learningGoal, String newIntensityRaw) {
//    LearningIntensity newIntensity = parseIntensity(newIntensityRaw);
//    List<PreferredSummerSemester> summerSemesters = learningGoal.getPreferredSummerSemesters();
//
//    boolean hasHigherSummerIntensity = summerSemesters.stream()
//        .map(PreferredSummerSemester::getLearningIntensity)
//        .filter(Objects::nonNull)
//        .anyMatch(value -> intensityRank(value.name()) > intensityRank(newIntensity.name()));
//
//    if (hasHigherSummerIntensity) {
//      throw new IllegalArgumentException(
//          "Cannot update prefMainSemLearnIntensity below existing preferred summer semester intensity");
//    }
//  }
//
//  private int intensityRank(String intensity) {
//    return switch (IntensityParsingSupport.normalizeTrimmedTitleCase(intensity)) {
//      case "Low" -> 1;
//      case "Light" -> 2;
//      case "Standard" -> 3;
//      case "Heavy" -> 4;
//      default -> throw new IllegalArgumentException("Invalid intensity value: " + intensity);
//    };
//  }

  private LearningGoalResponse toResponse(LearningGoal learningGoal) {
    LearningGoalResponse response = learningGoalMapper.toResponse(learningGoal);
    response.setGoalValidationResult(
        goalValidationResultRepository.findTopByLearningGoalIdOrderByValidationTimestampDesc(
            learningGoal.getLearningGoalId()).map(learningGoalMapper::toGoalValidationResultResponse).orElse(null));
    return response;
  }

  private LearningGoalResponse toResponse(LearningGoal learningGoal, GoalValidationResult validationResult) {
    LearningGoalResponse response = learningGoalMapper.toResponse(learningGoal);
    response.setGoalValidationResult(learningGoalMapper.toGoalValidationResultResponse(validationResult));
    return response;
  }

  private PreferredSummerSemesterResponse toPreferredSummerSemesterResponse(PreferredSummerSemester entity) {
    return learningGoalMapper.toPreferredSummerSemesterResponse(entity);
  }
}