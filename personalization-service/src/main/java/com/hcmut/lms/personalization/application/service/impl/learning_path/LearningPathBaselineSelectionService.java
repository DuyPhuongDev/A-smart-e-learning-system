package com.hcmut.lms.personalization.application.service.impl.learning_path;

import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService.SubjectCandidate;
import com.hcmut.lms.personalization.application.service.impl.support.IntensityCreditCapSupport;
import com.hcmut.lms.personalization.application.service.impl.support.SubjectCreditUtil;
import com.hcmut.lms.personalization.application.service.impl.validation.StudentProgressDataService;
import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.*;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.occupationData.SubjectOccupationValuation;
import com.hcmut.lms.personalization.repository.SubjectOccupationValuationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningPathBaselineSelectionService {

  private static final String FREE_ELECTIVE_SECTION_KEY = normalizeSectionName("Tự chọn tự do");
  private static final String MAJOR_SPECIALIZATION_KEY = normalizeSectionName("Chuyên ngành");
  private static final String MAJOR_SPECIALIZATION_GROUP_C_KEY = normalizeSectionName("Chuyên ngành (Nhóm C)");

  private final LearningServiceClient learningServiceClient;
  private final SubjectOccupationValuationRepository subjectOccupationValuationRepository;

  public BaselineSelectionResult buildBaselinePath(
      CurriculumFullResponse curriculum, LearningGoal goal,
      StudentProgressDataService.StudentProgressData progressData) {

    List<SubjectCandidate> allUncompletedCandidates = new ArrayList<>();
    Set<UUID> completedIds = new HashSet<>(progressData.completedSubjectIds());

    List<CurriculumFullResponse.CurriculumSectionFull> sections = curriculum.getSections() != null ?
        curriculum.getSections() : Collections.emptyList();

    for (CurriculumFullResponse.CurriculumSectionFull section : sections) {
      int sectionWeight = section.getPriorityWeight() != null ? section.getPriorityWeight() : 5;
      List<CurriculumFullResponse.CurriculumSubjectFull> subjects = section.getSubjects() != null ?
          section.getSubjects() : Collections.emptyList();
      for (CurriculumFullResponse.CurriculumSubjectFull subject : subjects) {
        if (completedIds.contains(subject.getSubjectId())) {
          continue;
        }

        allUncompletedCandidates.add(SubjectCandidate.builder()
            .subjectId(subject.getSubjectId())
            .subjectCode(subject.getSubjectCode())
            .subjectName(subject.getSubjectName())
            .credits(SubjectCreditUtil.safeCredits(subject.getCredits()))
            .isRequired(subject.getIsRequired())
            .sectionId(section.getSectionId())
            .sectionName(section.getSectionName())
            .priority1(calculatePriority1(subject.getRecommendedYear(), subject.getRecommendedSemesterInYear()))
            .priority2(sectionWeight)
            .prerequisites(subject.getPrerequisites())
            .recommendations(subject.getRecommendations())
            .parallels(subject.getParallels())
            .build());
      }
    }

    return selectSubjectsForRequiredSectionCredits(
        allUncompletedCandidates, curriculum, goal, progressData, completedIds);
  }

  private BaselineSelectionResult selectSubjectsForRequiredSectionCredits(
      List<SubjectCandidate> candidates, CurriculumFullResponse curriculum, LearningGoal goal,
      StudentProgressDataService.StudentProgressData progressData, Set<UUID> completedSubjectIds) {

    List<CurriculumFullResponse.CurriculumSectionFull> curriculumSections = curriculum.getSections() != null ?
        curriculum.getSections() : Collections.emptyList();

    Map<UUID, Double> scoreBySubjectId = buildScoreMap(candidates, goal);

    Map<UUID, Double> completedGradeBySubjectId = progressData.completedSubjects()
        .stream()
        .filter(s -> Boolean.TRUE.equals(s.isHighestResult()))
        .collect(Collectors.toMap(
            StudentProgressDataService.CompletedSubjectDetail::subjectId,
            this::resolveCompletedGradeScore, Math::max));

    Map<UUID, List<SubjectCandidate>> uncompletedBySection = candidates.stream()
        .collect(Collectors.groupingBy(SubjectCandidate::getSectionId));

    Map<UUID, Integer> countedCompletedCreditsBySection = new HashMap<>();
    Map<UUID, Integer> overflowCompletedCreditsBySection = new HashMap<>();

    for (CurriculumFullResponse.CurriculumSectionFull section : curriculumSections) {
      UUID sectionId = section.getSectionId();
      int requiredCredits = section.getRequiredCredits() != null ? section.getRequiredCredits() : 0;

      List<CurriculumFullResponse.CurriculumSubjectFull> completedInSection = section.getSubjects() != null ?
          section.getSubjects()
          .stream()
          .filter(subject -> completedSubjectIds.contains(subject.getSubjectId()))
          .toList() : Collections.emptyList();

      Set<UUID> countedCompleted = selectBestCompletedSubset(
          completedInSection, requiredCredits, completedGradeBySubjectId);

      int countedCredits = completedInSection.stream()
          .filter(subject -> countedCompleted.contains(subject.getSubjectId()))
          .mapToInt(subject -> SubjectCreditUtil.safeCredits(subject.getCredits()))
          .sum();
      int overflowCredits = completedInSection.stream()
          .filter(subject -> !countedCompleted.contains(subject.getSubjectId()))
          .mapToInt(subject -> SubjectCreditUtil.safeCredits(subject.getCredits()))
          .sum();

      countedCompletedCreditsBySection.put(sectionId, countedCredits);
      overflowCompletedCreditsBySection.put(sectionId, overflowCredits);
    }

    Map<UUID, Integer> transferredCreditsBySection = transferSpecializedOverflowToFreeElective(
        curriculumSections,
        countedCompletedCreditsBySection, overflowCompletedCreditsBySection);

    Optional<CurriculumFullResponse.CurriculumSectionFull> freeElectiveOpt = curriculumSections.stream()
        .filter(section -> FREE_ELECTIVE_SECTION_KEY.equals(normalizeSectionName(section.getSectionName())))
        .findFirst();

    UUID freeElectiveSectionId = freeElectiveOpt.map(CurriculumFullResponse.CurriculumSectionFull::getSectionId)
        .orElse(null);

    Set<UUID> selectedSubjectIds = new HashSet<>();

    // 0-credit subjects are mandatory milestones in the generated plan.
    List<SubjectCandidate> mandatoryZeroCredit = candidates.stream()
        .filter(candidate -> SubjectCreditUtil.safeCredits(candidate) == 0)
        .sorted(Comparator.comparingInt(SubjectCreditUtil::safePriority1).thenComparingInt(SubjectCreditUtil::safePriority2))
        .toList();
    List<SubjectCandidate> selected = new ArrayList<>(mandatoryZeroCredit);
    mandatoryZeroCredit.stream().map(SubjectCandidate::getSubjectId).forEach(selectedSubjectIds::add);

    // Pick normal sections first. Free elective is processed last to avoid duplicate picks.
    for (CurriculumFullResponse.CurriculumSectionFull section : curriculumSections) {
      UUID sectionId = section.getSectionId();
      if (freeElectiveSectionId != null && freeElectiveSectionId.equals(sectionId)) {
        continue;
      }

      int neededCredits = resolveNeededCredits(section, countedCompletedCreditsBySection, transferredCreditsBySection);
      if (neededCredits <= 0) {
        continue;
      }

      List<SubjectCandidate> sectionCandidates = uncompletedBySection.getOrDefault(sectionId, Collections.emptyList())
          .stream()
          .filter(candidate -> !selectedSubjectIds.contains(candidate.getSubjectId()))
          .filter(candidate -> SubjectCreditUtil.safeCredits(candidate) > 0)
          .toList();

      List<SubjectCandidate> picked = selectCandidatesForCredits(sectionCandidates, neededCredits, scoreBySubjectId);
      selected.addAll(picked);
      picked.stream().map(SubjectCandidate::getSubjectId).forEach(selectedSubjectIds::add);
    }

    if (freeElectiveOpt.isPresent()) {
      CurriculumFullResponse.CurriculumSectionFull freeSection = freeElectiveOpt.get();
      int neededCredits = resolveNeededCredits(
          freeSection, countedCompletedCreditsBySection, transferredCreditsBySection);

      if (neededCredits > 0) {
        // Single pool: leftovers from "Chuyên ngành", "Chuyên ngành (Nhóm C)", and
        // subjects directly from "Tự chọn tự do". All three sources compete equally
        // on student-goal scores — neutralize section-weight so origin doesn't bias the knapsack.
        List<SubjectCandidate> freePool = new ArrayList<>();
        for (SubjectCandidate candidate : candidates) {
          if (selectedSubjectIds.contains(candidate.getSubjectId())) {
            continue;
          }
          if (SubjectCreditUtil.safeCredits(candidate) > 0
              && (MAJOR_SPECIALIZATION_KEY.equals(normalizeSectionName(candidate.getSectionName()))
                  || MAJOR_SPECIALIZATION_GROUP_C_KEY.equals(normalizeSectionName(candidate.getSectionName()))
                  || FREE_ELECTIVE_SECTION_KEY.equals(normalizeSectionName(candidate.getSectionName())))) {
            candidate.setPriority2(0);
            freePool.add(candidate);
          }
        }

        List<SubjectCandidate> pickedForFree = selectCandidatesForCredits(freePool, neededCredits, scoreBySubjectId);
        selected.addAll(pickedForFree);
        pickedForFree.stream().map(SubjectCandidate::getSubjectId).forEach(selectedSubjectIds::add);
      }
    }

    ensurePrerequisiteClosure(selected, candidates, selectedSubjectIds, completedSubjectIds);

    return new BaselineSelectionResult(selected);
  }

  /**
   * Adds any candidate subjects that are prerequisites or parallels of already-selected
   * subjects but were not included in the initial selection. Without this closure step the
   * scheduler cannot place subjects whose dependencies are missing from the candidate set.
   */
  private void ensurePrerequisiteClosure(
      List<SubjectCandidate> selected,
      List<SubjectCandidate> allUncompletedCandidates,
      Set<UUID> selectedSubjectIds,
      Set<UUID> completedIds) {

    Map<UUID, SubjectCandidate> candidateById = allUncompletedCandidates.stream()
        .collect(Collectors.toMap(SubjectCandidate::getSubjectId, c -> c));

    Set<UUID> toAdd = new HashSet<>();
    boolean changed = true;
    while (changed) {
      changed = false;
      for (SubjectCandidate subject : selected) {
        List<CurriculumFullResponse.SubjectRelation> prereqs =
            subject.getPrerequisites() != null ? subject.getPrerequisites() : List.of();
        for (CurriculumFullResponse.SubjectRelation prereq : prereqs) {
          UUID prereqId = prereq.getSubjectId();
          if (prereqId != null && !completedIds.contains(prereqId) && !selectedSubjectIds.contains(prereqId) && candidateById.containsKey(prereqId)) {
            toAdd.add(prereqId);
          }
        }
        List<CurriculumFullResponse.SubjectRelation> parallels =
            subject.getParallels() != null ? subject.getParallels() : List.of();
        for (CurriculumFullResponse.SubjectRelation parallel : parallels) {
          UUID parallelId = parallel.getSubjectId();
          if (parallelId != null && !completedIds.contains(parallelId) && !selectedSubjectIds.contains(parallelId) && candidateById.containsKey(parallelId)) {
            toAdd.add(parallelId);
          }
        }
      }
      if (!toAdd.isEmpty()) {
        for (UUID id : toAdd) {
          SubjectCandidate candidate = candidateById.get(id);
          if (candidate != null && !selectedSubjectIds.contains(id)) {
            selected.add(candidate);
            selectedSubjectIds.add(id);
          }
        }
        changed = true;
        toAdd.clear();
      }
    }
  }

  private int resolveNeededCredits(
      CurriculumFullResponse.CurriculumSectionFull section,
      Map<UUID, Integer> countedCompletedCreditsBySection, Map<UUID, Integer> transferredCreditsBySection) {
    int requiredCredits = section.getRequiredCredits() != null ? section.getRequiredCredits() : 0;
    int completedCredits = countedCompletedCreditsBySection.getOrDefault(section.getSectionId(), 0);
    int transferredCredits = transferredCreditsBySection.getOrDefault(section.getSectionId(), 0);
    return Math.max(0, requiredCredits - completedCredits - transferredCredits);
  }

  private Map<UUID, Integer> transferSpecializedOverflowToFreeElective(
      List<CurriculumFullResponse.CurriculumSectionFull> curriculumSections,
      Map<UUID, Integer> countedCompletedCreditsBySection, Map<UUID, Integer> overflowCompletedCreditsBySection) {

    Optional<CurriculumFullResponse.CurriculumSectionFull> freeElectiveSection = curriculumSections.stream()
        .filter(section -> FREE_ELECTIVE_SECTION_KEY.equals(normalizeSectionName(section.getSectionName())))
        .findFirst();

    if (freeElectiveSection.isEmpty()) {
      return Collections.emptyMap();
    }

    UUID freeElectiveSectionId = freeElectiveSection.get().getSectionId();
    int freeRequired = freeElectiveSection.get().getRequiredCredits() != null ? freeElectiveSection.get()
        .getRequiredCredits() : 0;
    int freeCompleted = countedCompletedCreditsBySection.getOrDefault(freeElectiveSectionId, 0);
    int freeDeficit = Math.max(0, freeRequired - freeCompleted);
    if (freeDeficit == 0) {
      return Collections.emptyMap();
    }

    // Overflow from ALL sections (except free elective itself) can transfer
    int transferable = curriculumSections.stream()
        .filter(section -> !FREE_ELECTIVE_SECTION_KEY.equals(normalizeSectionName(section.getSectionName())))
        .mapToInt(section -> overflowCompletedCreditsBySection.getOrDefault(section.getSectionId(), 0))
        .sum();

    int transferred = Math.min(freeDeficit, transferable);
    if (transferred <= 0) {
      return Collections.emptyMap();
    }

    return Map.of(freeElectiveSectionId, transferred);
  }

  private Set<UUID> selectBestCompletedSubset(
      List<CurriculumFullResponse.CurriculumSubjectFull> completedSubjects,
      int targetCredits, Map<UUID, Double> completedGradeBySubjectId) {

    if (completedSubjects.isEmpty() || targetCredits <= 0) {
      return Collections.emptySet();
    }

    Map<Integer, CreditSelectionState> states = new HashMap<>();
    states.put(0, CreditSelectionState.empty());

    for (CurriculumFullResponse.CurriculumSubjectFull subject : completedSubjects) {
      int credits = SubjectCreditUtil.safeCredits(subject.getCredits());
      if (credits <= 0) {
        continue;
      }
      double score = completedGradeBySubjectId.getOrDefault(subject.getSubjectId(), 0.0);

      Map<Integer, CreditSelectionState> nextStates = new HashMap<>(states);
      for (Map.Entry<Integer, CreditSelectionState> entry : states.entrySet()) {
        int nextCredit = entry.getKey() + credits;
        if (nextCredit > targetCredits) {
          continue;
        }
        CreditSelectionState nextState = entry.getValue().addSubject(subject.getSubjectId(), score);
        CreditSelectionState existing = nextStates.get(nextCredit);
        if (existing == null || nextState.hasBetterScoreThan(existing)) {
          nextStates.put(nextCredit, nextState);
        }
      }
      states = nextStates;
    }

    int bestCredits = states.keySet().stream().max(Integer::compareTo).orElse(0);
    return states.getOrDefault(bestCredits, CreditSelectionState.empty()).subjectIds();
  }

  private List<SubjectCandidate> selectCandidatesForCredits(
      List<SubjectCandidate> sectionCandidates, int targetCredits,
      Map<UUID, Double> scoreBySubjectId) {

    if (sectionCandidates.isEmpty() || targetCredits <= 0) {
      return Collections.emptyList();
    }

    Map<Integer, CreditSelectionState> states = new HashMap<>();
    states.put(0, CreditSelectionState.empty());

    for (SubjectCandidate candidate : sectionCandidates) {
      int credits = SubjectCreditUtil.safeCredits(candidate);
      if (credits <= 0) {
        continue;
      }
      double score = scoreBySubjectId.getOrDefault(candidate.getSubjectId(), 0.0);
      double priorityPenalty = (SubjectCreditUtil.safePriority1(candidate) * 0.001) + (SubjectCreditUtil.safePriority2(candidate) * 0.0001);
      double finalScore = score - priorityPenalty;

      Map<Integer, CreditSelectionState> nextStates = new HashMap<>(states);
      for (Map.Entry<Integer, CreditSelectionState> entry : states.entrySet()) {
        int nextCredit = entry.getKey() + credits;
        CreditSelectionState nextState = entry.getValue().addSubject(candidate.getSubjectId(), finalScore);
        CreditSelectionState existing = nextStates.get(nextCredit);
        if (existing == null || nextState.hasBetterScoreThan(existing)) {
          nextStates.put(nextCredit, nextState);
        }
      }
      states = nextStates;
    }

    Map<Integer, CreditSelectionState> finalStates = states;

    Optional<Integer> exactCredits = finalStates.keySet().stream().filter(c -> c == targetCredits).findFirst();
    int selectedCredits = exactCredits.orElseGet(() -> finalStates.keySet()
        .stream()
        .filter(c -> c >= targetCredits)
        .min(Integer::compareTo)
        .orElse(finalStates.keySet().stream().max(Integer::compareTo).orElse(0)));

    Set<UUID> selectedIds = finalStates.getOrDefault(selectedCredits, CreditSelectionState.empty()).subjectIds();

    return sectionCandidates.stream()
        .filter(candidate -> selectedIds.contains(candidate.getSubjectId()))
        .sorted(Comparator.comparingInt(SubjectCreditUtil::safePriority1).thenComparingInt(SubjectCreditUtil::safePriority2))
        .toList();
  }

  private Map<UUID, Double> buildScoreMap(List<SubjectCandidate> candidates, LearningGoal goal) {
    Integer gpaOrder = goal.getAttemptTargetGpaOrder();
    Integer occupationOrder = goal.getFocusOnTargetOccupation();
    Integer timeOrder = goal.getCompletedOnTime();

    boolean focusOccupation =
        occupationOrder != null && (gpaOrder == null || occupationOrder < gpaOrder) && (timeOrder == null || occupationOrder < timeOrder);

    if (focusOccupation) {
      return goal.getTargetOccupationCode() != null ? buildOccupationScoreMap(
          candidates, goal.getTargetOccupationCode()) : buildAverageOccupationScoreMap(candidates);
    }

    int mainCreditCap = IntensityCreditCapSupport.mainSemesterCapStrict(goal.getPrefMainSemLearnIntensity());
    return buildPredictedGradeScoreMap(candidates, goal.getStudentId(), mainCreditCap);
  }

  private int calculatePriority1(Integer recommendedYear, Integer recommendedSemester) {
    if (recommendedYear == null || recommendedSemester == null) {
      return 999;
    }
    return 2 * recommendedYear - 1 + recommendedSemester;
  }

  private double resolveCompletedGradeScore(StudentProgressDataService.CompletedSubjectDetail detail) {
    return detail.grade4() != null ? detail.grade4() : 0.0;
  }

  private Map<UUID, Double> buildOccupationScoreMap(List<SubjectCandidate> electives, String occupationCode) {
    List<SubjectOccupationValuation> valuations = subjectOccupationValuationRepository.findByTargetOccupationCode(
        occupationCode);

    Map<UUID, BigDecimal> valuationMap = valuations.stream()
        .collect(Collectors.toMap(
            SubjectOccupationValuation::getSubjectId, SubjectOccupationValuation::getTotalValue,
            (v1, v2) -> v1.compareTo(v2) > 0 ? v1 : v2));

    Map<UUID, Double> scores = new HashMap<>();
    for (SubjectCandidate elective : electives) {
      BigDecimal value = valuationMap.getOrDefault(elective.getSubjectId(), BigDecimal.ZERO);
      scores.put(elective.getSubjectId(), value.doubleValue());
    }
    return scores;
  }

  private Map<UUID, Double> buildAverageOccupationScoreMap(List<SubjectCandidate> electives) {
    List<UUID> subjectIds = electives.stream()
        .map(SubjectCandidate::getSubjectId)
        .filter(Objects::nonNull)
        .distinct()
        .toList();
    if (subjectIds.isEmpty()) {
      return Collections.emptyMap();
    }

    List<SubjectOccupationValuation> valuations = subjectOccupationValuationRepository.findBySubjectIdIn(subjectIds);

    Map<UUID, Double> avgScoreBySubjectId = valuations.stream()
        .collect(Collectors.groupingBy(
            SubjectOccupationValuation::getSubjectId,
            Collectors.averagingDouble(v -> v.getTotalValue().doubleValue())));

    Map<UUID, Double> scores = new HashMap<>();
    for (SubjectCandidate elective : electives) {
      scores.put(elective.getSubjectId(), avgScoreBySubjectId.getOrDefault(elective.getSubjectId(), 0.0));
    }

    return scores;
  }

  private Map<UUID, Double> buildPredictedGradeScoreMap(
      List<SubjectCandidate> electives, UUID studentId, int mainCreditCap) {
    List<BatchGradePredictionRequest.GradePredictionItem> items = electives.stream()
        .map(e -> BatchGradePredictionRequest.GradePredictionItem.builder()
            .studentId(studentId)
            .subjectId(e.getSubjectId())
            .plannedSemesterCredits(mainCreditCap)
            .build())
        .toList();

    BatchGradePredictionRequest request = BatchGradePredictionRequest.builder().predictions(items).build();

    try {
      BatchGradePredictionResponse response = learningServiceClient.predictGradeBatch(request);
      if (response == null || response.getPredictions() == null) {
        return Collections.emptyMap();
      }

      return response.getPredictions().stream().collect(Collectors.toMap(
          GradePredictionResponse::getSubjectId,
          pred -> pred.getCorrectedPredictedGrade() != null ? pred.getCorrectedPredictedGrade() : 0.0, Math::max));
    } catch (Exception e) {
      log.warn("Predicted grade scoring failed, fallback to zero scores: {}", e.getMessage());
      return Collections.emptyMap();
    }
  }

  private static String normalizeSectionName(String sectionName) {
    if (sectionName == null) {
      return "";
    }
    String normalized = Normalizer.normalize(sectionName, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    return normalized.trim().toLowerCase(Locale.ROOT);
  }

  public record BaselineSelectionResult(List<SubjectCandidate> candidates) {
  }

  private record CreditSelectionState(Set<UUID> subjectIds, double score) {
    private static CreditSelectionState empty() {
      return new CreditSelectionState(new LinkedHashSet<>(), 0.0);
    }

    private CreditSelectionState addSubject(UUID subjectId, double additionalScore) {
      LinkedHashSet<UUID> ids = new LinkedHashSet<>(subjectIds);
      ids.add(subjectId);
      return new CreditSelectionState(ids, score + additionalScore);
    }

    private boolean hasBetterScoreThan(CreditSelectionState other) {
      if (other == null) {
        return true;
      }
      if (Double.compare(score, other.score) != 0) {
        return score > other.score;
      }
      return subjectIds.size() < other.subjectIds.size();
    }
  }
}

