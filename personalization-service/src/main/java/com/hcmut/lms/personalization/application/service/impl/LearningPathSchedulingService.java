package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.service.impl.support.IntensityCreditCapSupport;
import com.hcmut.lms.personalization.application.service.impl.support.SemesterClassifier;
import com.hcmut.lms.personalization.application.service.impl.validation.SemesterCalculationService;
import com.hcmut.lms.personalization.client.dto.CurriculumFullResponse;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Phase-2 scheduler that assigns baseline subjects into concrete semesters.
 */
@Service
@Slf4j
public class LearningPathSchedulingService {

  private static final int MAX_BACKTRACK_NODES = 50_000;

  public List<SemesterSlot> scheduleSubjects(
      List<SubjectCandidate> candidates,
      SemesterCalculationService.SemesterAvailability availability,
      List<SemesterResponse> remainingSemesters,
      int mainCreditCap,
      Map<UUID, Integer> preferredSummerCapBySemesterId,
      Set<UUID> completedSubjectIds) {

    log.info("Scheduling subjects into semesters");

    List<SubjectCandidate> sorted = candidates.stream()
        .sorted(Comparator.comparingInt(this::safePriority1)
            .thenComparingInt(this::safePriority2))
        .toList();

    Map<UUID, SubjectCandidate> candidateById = sorted.stream()
        .collect(Collectors.toMap(SubjectCandidate::getSubjectId, candidate -> candidate));

    Map<UUID, Set<UUID>> dependentGraph = buildDependentGraph(sorted);

    List<SemesterSlot> semesters = buildSemesterSlots(
        availability,
        remainingSemesters,
        mainCreditCap,
        preferredSummerCapBySemesterId);

    Set<UUID> completed = new HashSet<>(completedSubjectIds);
    Map<UUID, Integer> scheduledSemesterOrders = new HashMap<>();
    Map<Integer, Set<UUID>> scheduledBySemester = new HashMap<>();
    int[] exploredNodes = new int[]{0};

    boolean feasible = backtrackAssign(
        0, sorted, semesters, candidateById, dependentGraph, completed, scheduledSemesterOrders, scheduledBySemester,
        exploredNodes);

    log.info("Backtracking explored {} nodes", exploredNodes[0]);
    if (!feasible) {
      log.warn("Unable to schedule all subjects with current constraints after exploring {} nodes", exploredNodes[0]);
    }

    log.info("Scheduled {} subjects across {} semesters", scheduledSemesterOrders.size(), semesters.size());
    return semesters;
  }

  private List<SemesterSlot> buildSemesterSlots(
      SemesterCalculationService.SemesterAvailability availability,
      List<SemesterResponse> remainingSemesters,
      int mainCreditCap,
      Map<UUID, Integer> preferredSummerCapBySemesterId) {

    List<SemesterSlot> semesters = new ArrayList<>();
    int totalSemesters = availability.totalSemesters();

    if (totalSemesters > remainingSemesters.size()) {
      log.warn(
          "Available semester window ({}) is larger than fetched remaining semesters ({}). Scheduling uses fetched list size.",
          totalSemesters,
          remainingSemesters.size());
    }

    for (int i = 0; i < totalSemesters && i < remainingSemesters.size(); i++) {
      SemesterResponse semesterInfo = remainingSemesters.get(i);
      boolean isSummer = SemesterClassifier.isSummerSemester(semesterInfo);
      int semesterCap = isSummer
          ? preferredSummerCapBySemesterId.getOrDefault(
              semesterInfo.getId(), IntensityCreditCapSupport.defaultSummerSemesterCap())
          : mainCreditCap;

      semesters.add(SemesterSlot.builder()
          .semesterOrder(i + 1)
          .subjects(new ArrayList<>())
          .totalCredits(0)
          .creditCap(semesterCap)
          .semesterInfo(semesterInfo)
          .isSummer(isSummer)
          .build());
    }

    return semesters;
  }

  private boolean backtrackAssign(
      int index,
      List<SubjectCandidate> ordered,
      List<SemesterSlot> semesters,
      Map<UUID, SubjectCandidate> candidateById,
      Map<UUID, Set<UUID>> dependentGraph,
      Set<UUID> completed,
      Map<UUID, Integer> scheduledSemesterOrders,
      Map<Integer, Set<UUID>> scheduledBySemester,
      int[] exploredNodes) {

    if (exploredNodes[0]++ > MAX_BACKTRACK_NODES) {
      return false;
    }

    while (index < ordered.size() && scheduledSemesterOrders.containsKey(ordered.get(index).getSubjectId())) {
      index++;
    }
    if (index >= ordered.size()) {
      return true;
    }

    SubjectCandidate candidate = ordered.get(index);
    Set<UUID> placementBundle = resolvePlacementBundle(candidate, candidateById, completed, scheduledSemesterOrders);
    if (placementBundle.isEmpty()) {
      return false;
    }

    int minSemesterOrder = calculateMinSemesterOrder(
        placementBundle,
        candidateById,
        completed,
        scheduledSemesterOrders);

    for (int semesterOrder = minSemesterOrder; semesterOrder <= semesters.size(); semesterOrder++) {
      SemesterSlot semester = semesters.get(semesterOrder - 1);
      if (!canPlaceBundle(
          placementBundle, semester, candidateById, completed, scheduledSemesterOrders, scheduledBySemester,
          dependentGraph, semesters.size())) {
        continue;
      }

      placeBundle(placementBundle, semester, candidateById, scheduledSemesterOrders, scheduledBySemester);

      if (backtrackAssign(
          index + 1, ordered, semesters, candidateById, dependentGraph, completed, scheduledSemesterOrders,
          scheduledBySemester, exploredNodes)) {
        return true;
      }

      unplaceBundle(placementBundle, semester, scheduledSemesterOrders, scheduledBySemester);
    }

    return false;
  }

  private Set<UUID> resolvePlacementBundle(
      SubjectCandidate candidate,
      Map<UUID, SubjectCandidate> candidateById,
      Set<UUID> completed,
      Map<UUID, Integer> scheduledSemesterOrders) {

    LinkedHashSet<UUID> bundle = new LinkedHashSet<>();
    bundle.add(candidate.getSubjectId());

    if (candidate.getParallels() == null) {
      return bundle;
    }

    for (CurriculumFullResponse.SubjectRelation parallel : candidate.getParallels()) {
      UUID parallelId = parallel.getSubjectId();
      if (parallelId == null || completed.contains(parallelId) || scheduledSemesterOrders.containsKey(parallelId)) {
        continue;
      }

      SubjectCandidate parallelCandidate = candidateById.get(parallelId);
      if (parallelCandidate == null) {
        return Collections.emptySet();
      }
      bundle.add(parallelId);
    }

    return bundle;
  }

  private int calculateMinSemesterOrder(
      Set<UUID> placementBundle,
      Map<UUID, SubjectCandidate> candidateById,
      Set<UUID> completed,
      Map<UUID, Integer> scheduledSemesterOrders) {

    int minOrder = 1;
    for (UUID subjectId : placementBundle) {
      SubjectCandidate candidate = candidateById.get(subjectId);
      if (candidate == null || candidate.getPrerequisites() == null) {
        continue;
      }

      for (CurriculumFullResponse.SubjectRelation prereq : candidate.getPrerequisites()) {
        UUID prereqId = prereq.getSubjectId();
        if (prereqId == null || completed.contains(prereqId) || placementBundle.contains(prereqId)) {
          continue;
        }

        Integer prereqOrder = scheduledSemesterOrders.get(prereqId);
        if (prereqOrder != null) {
          minOrder = Math.max(minOrder, prereqOrder + 1);
        }
      }
    }
    return minOrder;
  }

  private boolean canPlaceBundle(
      Set<UUID> placementBundle,
      SemesterSlot semester,
      Map<UUID, SubjectCandidate> candidateById,
      Set<UUID> completed,
      Map<UUID, Integer> scheduledSemesterOrders,
      Map<Integer, Set<UUID>> scheduledBySemester,
      Map<UUID, Set<UUID>> dependentGraph,
      int totalSemesters) {

    Map<UUID, Integer> chainDepthMemo = new HashMap<>();

    int bundleCredits = placementBundle.stream()
        .map(candidateById::get)
        .filter(Objects::nonNull)
        .mapToInt(this::safeCredits)
        .sum();

    if (semester.getTotalCredits() + bundleCredits > semester.getCreditCap()) {
      return false;
    }

    for (UUID subjectId : placementBundle) {
      SubjectCandidate candidate = candidateById.get(subjectId);
      if (candidate == null) {
        return false;
      }

      if (!prerequisitesSatisfied(
          candidate, semester.getSemesterOrder(), placementBundle, completed,
          scheduledSemesterOrders)) {
        return false;
      }

      if (!parallelsSatisfied(
          candidate, semester.getSemesterOrder(), placementBundle, completed, scheduledSemesterOrders,
          scheduledBySemester)) {
        return false;
      }

      if (!hasChainCapacity(
          subjectId, semester.getSemesterOrder(), totalSemesters, dependentGraph, scheduledSemesterOrders, completed,
          placementBundle, chainDepthMemo)) {
        return false;
      }
    }

    return true;
  }

  private boolean prerequisitesSatisfied(
      SubjectCandidate candidate,
      int semesterOrder,
      Set<UUID> placementBundle,
      Set<UUID> completed,
      Map<UUID, Integer> scheduledSemesterOrders) {

    if (candidate.getPrerequisites() == null) {
      return true;
    }

    for (CurriculumFullResponse.SubjectRelation prereq : candidate.getPrerequisites()) {
      UUID prereqId = prereq.getSubjectId();
      if (prereqId == null || completed.contains(prereqId)) {
        continue;
      }

      if (placementBundle.contains(prereqId)) {
        return false;
      }

      Integer prereqOrder = scheduledSemesterOrders.get(prereqId);
      if (prereqOrder == null || prereqOrder >= semesterOrder) {
        return false;
      }
    }

    return true;
  }

  private boolean parallelsSatisfied(
      SubjectCandidate candidate,
      int semesterOrder,
      Set<UUID> placementBundle,
      Set<UUID> completed,
      Map<UUID, Integer> scheduledSemesterOrders,
      Map<Integer, Set<UUID>> scheduledBySemester) {

    if (candidate.getParallels() == null) {
      return true;
    }

    Set<UUID> alreadyInSemester = scheduledBySemester.getOrDefault(semesterOrder, Collections.emptySet());
    for (CurriculumFullResponse.SubjectRelation parallel : candidate.getParallels()) {
      UUID parallelId = parallel.getSubjectId();
      if (parallelId == null || completed.contains(parallelId)) {
        continue;
      }

      Integer scheduledOrder = scheduledSemesterOrders.get(parallelId);
      if (scheduledOrder != null) {
        if (scheduledOrder != semesterOrder) {
          return false;
        }
        continue;
      }

      if (!placementBundle.contains(parallelId) && !alreadyInSemester.contains(parallelId)) {
        return false;
      }
    }
    return true;
  }

  private boolean hasChainCapacity(
      UUID subjectId,
      int startSemesterOrder,
      int totalSemesters,
      Map<UUID, Set<UUID>> dependentGraph,
      Map<UUID, Integer> scheduledSemesterOrders,
      Set<UUID> completed,
      Set<UUID> placementBundle,
      Map<UUID, Integer> memo) {

    int remainingSemesters = totalSemesters - startSemesterOrder + 1;
    int chainDepth = calculateRemainingChainDepth(
        subjectId,
        dependentGraph,
        scheduledSemesterOrders,
        completed,
        placementBundle,
        memo,
        new HashSet<>());
    return chainDepth <= remainingSemesters;
  }

  private int calculateRemainingChainDepth(
      UUID subjectId,
      Map<UUID, Set<UUID>> dependentGraph,
      Map<UUID, Integer> scheduledSemesterOrders,
      Set<UUID> completed,
      Set<UUID> placementBundle,
      Map<UUID, Integer> memo,
      Set<UUID> visiting) {

    if (memo.containsKey(subjectId)) {
      return memo.get(subjectId);
    }
    if (!visiting.add(subjectId)) {
      return 1;
    }

    int maxDepth = 1;
    for (UUID dependentId : dependentGraph.getOrDefault(subjectId, Collections.emptySet())) {
      if (completed.contains(dependentId) || scheduledSemesterOrders.containsKey(dependentId)
          || placementBundle.contains(dependentId)) {
        continue;
      }
      int depth = 1 + calculateRemainingChainDepth(
          dependentId,
          dependentGraph,
          scheduledSemesterOrders,
          completed,
          placementBundle,
          memo,
          visiting);
      maxDepth = Math.max(maxDepth, depth);
    }

    visiting.remove(subjectId);
    memo.put(subjectId, maxDepth);
    return maxDepth;
  }

  private void placeBundle(
      Set<UUID> placementBundle,
      SemesterSlot semester,
      Map<UUID, SubjectCandidate> candidateById,
      Map<UUID, Integer> scheduledSemesterOrders,
      Map<Integer, Set<UUID>> scheduledBySemester) {

    Set<UUID> semesterSet = scheduledBySemester.computeIfAbsent(
        semester.getSemesterOrder(), ignored -> new HashSet<>());
    for (UUID subjectId : placementBundle) {
      SubjectCandidate candidate = candidateById.get(subjectId);
      if (candidate == null || scheduledSemesterOrders.containsKey(subjectId)) {
        continue;
      }
      semester.getSubjects().add(candidate);
      semester.setTotalCredits(
          semester.getTotalCredits() + safeCredits(candidate));
      scheduledSemesterOrders.put(subjectId, semester.getSemesterOrder());
      semesterSet.add(subjectId);
    }
  }

  private void unplaceBundle(
      Set<UUID> placementBundle,
      SemesterSlot semester,
      Map<UUID, Integer> scheduledSemesterOrders,
      Map<Integer, Set<UUID>> scheduledBySemester) {

    Set<UUID> semesterSet = scheduledBySemester.getOrDefault(semester.getSemesterOrder(), Collections.emptySet());
    Iterator<SubjectCandidate> iterator = semester.getSubjects().iterator();
    while (iterator.hasNext()) {
      SubjectCandidate subject = iterator.next();
      if (!placementBundle.contains(subject.getSubjectId())) {
        continue;
      }
      iterator.remove();
      semester.setTotalCredits(semester.getTotalCredits() - safeCredits(subject));
      scheduledSemesterOrders.remove(subject.getSubjectId());
      semesterSet.remove(subject.getSubjectId());
    }

    if (semesterSet.isEmpty()) {
      scheduledBySemester.remove(semester.getSemesterOrder());
    }
  }

  private Map<UUID, Set<UUID>> buildDependentGraph(List<SubjectCandidate> candidates) {
    Map<UUID, Set<UUID>> graph = new HashMap<>();
    Set<UUID> candidateIds = candidates.stream().map(SubjectCandidate::getSubjectId).collect(Collectors.toSet());

    for (SubjectCandidate candidate : candidates) {
      UUID targetId = candidate.getSubjectId();

      if (candidate.getPrerequisites() != null) {
        for (CurriculumFullResponse.SubjectRelation prereq : candidate.getPrerequisites()) {
          UUID prereqId = prereq.getSubjectId();
          if (prereqId != null && candidateIds.contains(prereqId)) {
            graph.computeIfAbsent(prereqId, ignored -> new HashSet<>()).add(targetId);
          }
        }
      }

      if (candidate.getRecommendations() != null) {
        for (CurriculumFullResponse.SubjectRelation recommendation : candidate.getRecommendations()) {
          UUID recommendationId = recommendation.getSubjectId();
          if (recommendationId != null && candidateIds.contains(recommendationId)) {
            graph.computeIfAbsent(recommendationId, ignored -> new HashSet<>()).add(targetId);
          }
        }
      }
    }

    return graph;
  }

  private int safePriority1(SubjectCandidate candidate) {
    return candidate.getPriority1() != null ? candidate.getPriority1() : Integer.MAX_VALUE;
  }

  private int safePriority2(SubjectCandidate candidate) {
    return candidate.getPriority2() != null ? candidate.getPriority2() : Integer.MAX_VALUE;
  }

  private int safeCredits(SubjectCandidate candidate) {
    Integer credits = candidate.getCredits();
    if (credits == null) {
      return 0;
    }
    if (credits < 0) {
      throw new IllegalStateException(
          "Invalid subject credits while scheduling: subjectId="
              + candidate.getSubjectId()
              + ", subjectCode="
              + candidate.getSubjectCode()
              + ", credits="
              + credits);
    }
    return credits;
  }

  @Data
  @Builder
  public static class SubjectCandidate {
    private UUID subjectId;
    private String subjectCode;
    private String subjectName;
    private Integer credits;
    private Boolean isRequired;
    private UUID sectionId;
    private String sectionName;
    private Integer priority1;
    private Integer priority2;
    private List<CurriculumFullResponse.SubjectRelation> prerequisites;
    private List<CurriculumFullResponse.SubjectRelation> recommendations;
    private List<CurriculumFullResponse.SubjectRelation> parallels;
  }

  @Data
  @Builder
  public static class SemesterSlot {
    private Integer semesterOrder;
    private List<SubjectCandidate> subjects;
    private Integer totalCredits;
    private Integer creditCap;
    private SemesterResponse semesterInfo;
    private Boolean isSummer;
  }
}

