package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.service.impl.support.SemesterClassifier;
import com.hcmut.lms.personalization.application.service.impl.support.SubjectCreditUtil;
import com.hcmut.lms.personalization.client.dto.CurriculumFullResponse;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import com.hcmut.lms.personalization.exception.CyclicDependencyException;
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

  private static final int MAX_BACKTRACK_NODES = 50_000_000;

  /**
   * Hard constraints on the type of semester where certain subjects must be placed.
   * Keys are subject codes; values are the required semester type
   * (1=HK1, 2=HK2, 3=HK3/summer), derived from the last digit of semKey.
   * <p>
   * A subject listed here will ONLY be placed in a semester whose type matches
   * the required value. If no matching semester is available, the scheduling
   * will fail for that subject.
   */
  private static final Map<String, Integer> SUBJECT_SEMESTER_TYPE_CONSTRAINTS = Map.of(
      "MI1003", 2  // MI1003 must be in HK2 (2nd semester of academic year)
                                                                                      );

  public List<SemesterSlot> scheduleSubjects(
      List<SubjectCandidate> candidates, List<SemesterResponse> remainingSemesters, int mainCreditCap,
      Map<UUID, Integer> preferredSummerCapBySemesterId, int plannedSummerSemCount, Set<UUID> completedSubjectIds) {

    log.info("Scheduling subjects into semesters");

    Map<UUID, SubjectCandidate> candidateById = candidates.stream()
        .collect(Collectors.toMap(SubjectCandidate::getSubjectId, candidate -> candidate));

    Map<UUID, Set<UUID>> dependentGraph = buildDependentGraph(candidates);
    validateAcyclicGraph(dependentGraph);

    Map<UUID, Integer> staticMaxDepth = computeStaticMaxDepth(
        dependentGraph,
        candidates.stream().map(SubjectCandidate::getSubjectId).collect(Collectors.toSet()));

    // Sort candidates: priority1 (curriculum chronology), then downstream depth
    // (subjects that are prerequisites for longer chains are scheduled first),
    // then section weight as final tiebreaker.
    List<SubjectCandidate> sorted = candidates.stream()
        .sorted(Comparator.comparingInt(SubjectCreditUtil::safePriority1)
            .thenComparingInt(SubjectCreditUtil::safePriority2)
            .thenComparingInt(c -> -staticMaxDepth.getOrDefault(c.getSubjectId(), 1)))
        .toList();

    for (SubjectCandidate candidate : sorted) {
      log.debug(
          "Candidate: subjectId={}, subjectCode={}, priority1={}, priority2={}, staticMaxDepth={}",
          candidate.getSubjectId(), candidate.getSubjectCode(), SubjectCreditUtil.safePriority1(candidate), SubjectCreditUtil.safePriority2(candidate),
          staticMaxDepth.getOrDefault(candidate.getSubjectId(), 1));
    }

    int totalRemainingCredits = candidates.stream().mapToInt(SubjectCreditUtil::safeCredits).sum();
    int longestChainDepth = calculateLongestPrerequisiteChain(sorted, candidateById);

    int summerCapacity = preferredSummerCapBySemesterId.values().stream().mapToInt(Integer::intValue).sum();
    int effectiveMainCap = mainCreditCap - 1;
    int effectiveSummerCapacity = preferredSummerCapBySemesterId.values().stream().mapToInt(cap -> cap - 1).sum();
    int mainCreditsNeeded = Math.max(0, totalRemainingCredits - effectiveSummerCapacity);
    int maxMainByCredits = mainCreditsNeeded > 0 ? (int) Math.ceil((double) mainCreditsNeeded / effectiveMainCap) : 0;
    int chainDepthBeyondSummer = Math.max(0, longestChainDepth - plannedSummerSemCount);
    int boundedMainSemesters = Math.max(Math.max(maxMainByCredits, chainDepthBeyondSummer), 1);

    log.info(
        "Semester boundary: totalRemainingCredits={}, summerCapacity={}, mainCreditsNeeded={}, " + "maxMainByCredits" + "={}, longestChain={}, boundedMainSemesters={}",
        totalRemainingCredits, summerCapacity, mainCreditsNeeded, maxMainByCredits, longestChainDepth,
        boundedMainSemesters);

    List<SemesterSlot> semesters = buildSemesterSlots(
        remainingSemesters, mainCreditCap, preferredSummerCapBySemesterId,
        plannedSummerSemCount, boundedMainSemesters);

    Set<UUID> completed = new HashSet<>(completedSubjectIds);
    Map<UUID, Integer> scheduledSemesterOrders = new HashMap<>();
    Map<Integer, Set<UUID>> scheduledBySemester = new HashMap<>();
    int[] exploredNodes = new int[]{0};

    boolean feasible = backtrackAssign(
        0, sorted, semesters, staticMaxDepth,
        completed, scheduledSemesterOrders, scheduledBySemester, exploredNodes);

    log.info("Backtracking explored {} nodes", exploredNodes[0]);
    if (!feasible) {
      log.warn("Unable to schedule all subjects with current constraints after exploring {} nodes", exploredNodes[0]);
    }

    log.info("Scheduled {} subjects across {} semesters", scheduledSemesterOrders.size(), semesters.size());
    return semesters;
  }

  /**
   * Builds semester slots from the remaining semesters list.
   *
   * <p>Main semesters are limited to {@code maxMainSemesters} — excess main
   * semesters beyond the credit-based boundary are skipped.
   *
   * <p>Summer semesters are only included when they have an explicit intensity
   * configuration in the preferredSummerCapBySemesterId map.
   */
  private List<SemesterSlot> buildSemesterSlots(
      List<SemesterResponse> remainingSemesters, int mainCreditCap, Map<UUID, Integer> preferredSummerCapBySemesterId,
      int plannedSummerSemCount, int maxMainSemesters) {

    if (plannedSummerSemCount > 0 && preferredSummerCapBySemesterId.size() != plannedSummerSemCount) {
      throw new IllegalArgumentException(
          "plannedSummerSemCount is " + plannedSummerSemCount + " but " + preferredSummerCapBySemesterId.size() + " " + "preferred summer semester cap entries provided");
    }

    List<SemesterSlot> semesters = new ArrayList<>();
    int order = 0;
    int mainSlotsCreated = 0;

    for (SemesterResponse semesterInfo : remainingSemesters) {
      boolean isSummer = SemesterClassifier.isSummerSemester(semesterInfo);

      if (isSummer) {
        if (!preferredSummerCapBySemesterId.containsKey(semesterInfo.getId())) {
          continue;
        }
        Integer summerCap = preferredSummerCapBySemesterId.get(semesterInfo.getId());
        if (summerCap == null) {
          throw new IllegalArgumentException(
              "No credit cap found for preferred summer semester " + semesterInfo.getId() + ". Every included summer "
                  + "semester must have a corresponding PreferredSummerSemester entry.");
        }
        order++;
        semesters.add(SemesterSlot.builder()
            .semesterOrder(order)
            .subjects(new ArrayList<>())
            .totalCredits(0)
            .creditCap(summerCap)
            .semesterInfo(semesterInfo)
            .isSummer(true)
            .build());
      } else {
        if (mainSlotsCreated >= maxMainSemesters) {
          continue;
        }
        mainSlotsCreated++;
        order++;
        semesters.add(SemesterSlot.builder()
            .semesterOrder(order)
            .subjects(new ArrayList<>())
            .totalCredits(0)
            .creditCap(mainCreditCap)
            .semesterInfo(semesterInfo)
            .isSummer(false)
            .build());
      }
    }

    log.info(
        "Built {} semester slots ({} main, {} summer) from {} remaining semesters, maxMainSemesters={}",
        semesters.size(), semesters.stream().filter(s -> !Boolean.TRUE.equals(s.getIsSummer())).count(),
        semesters.stream().filter(s -> Boolean.TRUE.equals(s.getIsSummer())).count(), remainingSemesters.size(),
        maxMainSemesters);

    return semesters;
  }

  private boolean backtrackAssign(
      int index, List<SubjectCandidate> ordered, List<SemesterSlot> semesters,
      Map<UUID, Integer> staticMaxDepth, Set<UUID> completed, Map<UUID, Integer> scheduledSemesterOrders,
      Map<Integer, Set<UUID>> scheduledBySemester, int[] exploredNodes) {

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

    int minSemesterOrder = calculateMinSemesterOrder(
        candidate, completed, scheduledSemesterOrders);

    // Preferred start: treat parallels like prerequisites (prefer different semester)
    int preferredStart = minSemesterOrder;
    if (candidate.getParallels() != null) {
      for (CurriculumFullResponse.SubjectRelation parallel : candidate.getParallels()) {
        UUID parallelId = parallel.getSubjectId();
        if (parallelId == null || completed.contains(parallelId)) {
          continue;
        }
        Integer parallelOrder = scheduledSemesterOrders.get(parallelId);
        if (parallelOrder != null) {
          preferredStart = Math.max(preferredStart, parallelOrder + 1);
        }
      }
    }

    // Pass 1: try preferred semesters (different from parallel) first
    for (int semesterOrder = preferredStart; semesterOrder <= semesters.size(); semesterOrder++) {
      SemesterSlot semester = semesters.get(semesterOrder - 1);
      if (canNotPlaceSubject(candidate, semester, completed, scheduledSemesterOrders, staticMaxDepth, semesters.size())) {
        continue;
      }

      placeSubject(candidate, semester, scheduledSemesterOrders, scheduledBySemester);

      if (backtrackAssign(
          index + 1, ordered, semesters, staticMaxDepth, completed,
          scheduledSemesterOrders, scheduledBySemester, exploredNodes)) {
        return true;
      }

      unplaceSubject(candidate, semester, scheduledSemesterOrders, scheduledBySemester);
    }

    // Pass 2: fall back to same-semester-as-parallel
    if (preferredStart > minSemesterOrder) {
      for (int semesterOrder = minSemesterOrder; semesterOrder < preferredStart; semesterOrder++) {
        SemesterSlot semester = semesters.get(semesterOrder - 1);
        if (canNotPlaceSubject(
            candidate, semester, completed, scheduledSemesterOrders, staticMaxDepth,
            semesters.size())) {
          continue;
        }

        placeSubject(candidate, semester, scheduledSemesterOrders, scheduledBySemester);

        if (backtrackAssign(
            index + 1, ordered, semesters, staticMaxDepth, completed,
            scheduledSemesterOrders, scheduledBySemester, exploredNodes)) {
          return true;
        }

        unplaceSubject(candidate, semester, scheduledSemesterOrders, scheduledBySemester);
      }
    }

    return false;
  }

  private int calculateMinSemesterOrder(
      SubjectCandidate candidate, Set<UUID> completed, Map<UUID, Integer> scheduledSemesterOrders) {

    int minOrder = 1;

    if (candidate.getPrerequisites() != null) {
      for (CurriculumFullResponse.SubjectRelation prereq : candidate.getPrerequisites()) {
        UUID prereqId = prereq.getSubjectId();
        if (prereqId == null || completed.contains(prereqId)) {
          continue;
        }

        Integer prereqOrder = scheduledSemesterOrders.get(prereqId);
        if (prereqOrder != null) {
          minOrder = Math.max(minOrder, prereqOrder + 1);
        }
      }
    }

    // Parallels can be in the same or earlier semester, so the subject
    // must be at or after its parallel's semester.
    if (candidate.getParallels() != null) {
      for (CurriculumFullResponse.SubjectRelation parallel : candidate.getParallels()) {
        UUID parallelId = parallel.getSubjectId();
        if (parallelId == null || completed.contains(parallelId)) {
          continue;
        }

        Integer parallelOrder = scheduledSemesterOrders.get(parallelId);
        if (parallelOrder != null) {
          minOrder = Math.max(minOrder, parallelOrder);
        }
      }
    }
    return minOrder;
  }

  private boolean canNotPlaceSubject(
      SubjectCandidate candidate, SemesterSlot semester, Set<UUID> completed,
      Map<UUID, Integer> scheduledSemesterOrders,
      Map<UUID, Integer> staticMaxDepth, int totalSemesters) {

    int credits = SubjectCreditUtil.safeCredits(candidate);

    if (semester.getTotalCredits() + credits > semester.getCreditCap()) {
      return true;
    }

    // Stop scheduling into this semester when it already has cap-1 credits.
    // This prevents over-stuffing while still allowing a subject that pushes
    // from below cap-1 to the full cap (e.g., a 2-credit subject when at 16/18).
    if (semester.getTotalCredits() >= semester.getCreditCap() - 1) {
      return true;
    }

    // Check semester-type constraints (e.g., MI1003 must be in HK2)
    Integer requiredSemType = SUBJECT_SEMESTER_TYPE_CONSTRAINTS.get(candidate.getSubjectCode());
    if (requiredSemType != null) {
      if (SemesterClassifier.semesterType(semester.getSemesterInfo()) != requiredSemType) {
        return true;
      }
    }

    if (!prerequisitesSatisfied(candidate, semester.getSemesterOrder(), completed, scheduledSemesterOrders)) {
      return true;
    }

    if (!parallelsSatisfied(candidate, semester.getSemesterOrder(), completed, scheduledSemesterOrders)) {
      return true;
    }

    return !hasChainCapacity(candidate.getSubjectId(), semester.getSemesterOrder(), totalSemesters, staticMaxDepth);
  }

  private boolean prerequisitesSatisfied(
      SubjectCandidate candidate, int semesterOrder,
      Set<UUID> completed, Map<UUID, Integer> scheduledSemesterOrders) {

    if (candidate.getPrerequisites() == null) {
      return true;
    }

    for (CurriculumFullResponse.SubjectRelation prereq : candidate.getPrerequisites()) {
      UUID prereqId = prereq.getSubjectId();
      if (prereqId == null || completed.contains(prereqId)) {
        continue;
      }

      Integer prereqOrder = scheduledSemesterOrders.get(prereqId);
      if (prereqOrder == null || prereqOrder >= semesterOrder) {
        return false;
      }
    }

    return true;
  }

  private boolean parallelsSatisfied(
      SubjectCandidate candidate, int semesterOrder,
      Set<UUID> completed, Map<UUID, Integer> scheduledSemesterOrders) {

    if (candidate.getParallels() == null) {
      return true;
    }

    for (CurriculumFullResponse.SubjectRelation parallel : candidate.getParallels()) {
      UUID parallelId = parallel.getSubjectId();
      if (parallelId == null || completed.contains(parallelId)) {
        continue;
      }

      Integer scheduledOrder = scheduledSemesterOrders.get(parallelId);
      if (scheduledOrder != null) {
        // Same or earlier semester is allowed; later semester is not
        if (scheduledOrder > semesterOrder) {
          return false;
        }
        continue;
      }

      // Not completed, not scheduled — unsatisfied
      return false;
    }
    return true;
  }

  private boolean hasChainCapacity(
      UUID subjectId, int startSemesterOrder, int totalSemesters,
      Map<UUID, Integer> staticMaxDepth) {

    Integer depth = staticMaxDepth.get(subjectId);
    if (depth == null) {
      return true;
    }
    int remainingSemesters = totalSemesters - startSemesterOrder + 1;
    return depth <= remainingSemesters;
  }

  private void placeSubject(
      SubjectCandidate candidate, SemesterSlot semester,
      Map<UUID, Integer> scheduledSemesterOrders, Map<Integer, Set<UUID>> scheduledBySemester) {

    if (scheduledSemesterOrders.containsKey(candidate.getSubjectId())) {
      return;
    }
    Set<UUID> semesterSet = scheduledBySemester.computeIfAbsent(
        semester.getSemesterOrder(), ignored -> new HashSet<>());
    semester.getSubjects().add(candidate);
    semester.setTotalCredits(semester.getTotalCredits() + SubjectCreditUtil.safeCredits(candidate));
    scheduledSemesterOrders.put(candidate.getSubjectId(), semester.getSemesterOrder());
    semesterSet.add(candidate.getSubjectId());
  }

  private void unplaceSubject(
      SubjectCandidate candidate, SemesterSlot semester,
      Map<UUID, Integer> scheduledSemesterOrders, Map<Integer, Set<UUID>> scheduledBySemester) {

    Set<UUID> semesterSet = scheduledBySemester.getOrDefault(semester.getSemesterOrder(), new HashSet<>());
    Iterator<SubjectCandidate> iterator = semester.getSubjects().iterator();
    while (iterator.hasNext()) {
      SubjectCandidate subject = iterator.next();
      if (!subject.getSubjectId().equals(candidate.getSubjectId())) {
        continue;
      }
      iterator.remove();
      semester.setTotalCredits(semester.getTotalCredits() - SubjectCreditUtil.safeCredits(subject));
      scheduledSemesterOrders.remove(subject.getSubjectId());
      semesterSet.remove(subject.getSubjectId());
      break;
    }

    if (semesterSet.isEmpty()) {
      scheduledBySemester.remove(semester.getSemesterOrder());
    }
  }

  /**
   * Calculates the longest prerequisite chain depth among the candidates.
   * Returns the minimum number of semesters needed due to prerequisite ordering.
   */
  private int calculateLongestPrerequisiteChain(
      List<SubjectCandidate> candidates,
      Map<UUID, SubjectCandidate> candidateById) {

    Map<UUID, Integer> memo = new HashMap<>();
    int maxDepth = 0;
    for (SubjectCandidate candidate : candidates) {
      maxDepth = Math.max(
          maxDepth,
          prerequisiteChainDepth(candidate.getSubjectId(), candidateById, memo, new HashSet<>()));
    }
    return maxDepth;
  }

  private int prerequisiteChainDepth(
      UUID subjectId, Map<UUID, SubjectCandidate> candidateById, Map<UUID, Integer> memo,
      Set<UUID> visiting) {

    if (memo.containsKey(subjectId)) {
      return memo.get(subjectId);
    }
    if (!visiting.add(subjectId)) {
      return 1;
    }

    SubjectCandidate candidate = candidateById.get(subjectId);
    if (candidate == null || candidate.getPrerequisites() == null || candidate.getPrerequisites().isEmpty()) {
      visiting.remove(subjectId);
      memo.put(subjectId, 1);
      return 1;
    }

    int maxPrereqDepth = 0;
    for (CurriculumFullResponse.SubjectRelation prereq : candidate.getPrerequisites()) {
      UUID prereqId = prereq.getSubjectId();
      if (prereqId == null || !candidateById.containsKey(prereqId)) {
        continue;
      }
      maxPrereqDepth = Math.max(maxPrereqDepth, prerequisiteChainDepth(prereqId, candidateById, memo, visiting));
    }

    visiting.remove(subjectId);
    int depth = maxPrereqDepth + 1;
    memo.put(subjectId, depth);
    return depth;
  }

  /**
   * Pre-computes the longest path from each subject to a sink node in the
   * dependent graph. Used for O(1) chain-capacity checks during backtracking.
   */
  private Map<UUID, Integer> computeStaticMaxDepth(Map<UUID, Set<UUID>> dependentGraph, Set<UUID> allCandidateIds) {

    Map<UUID, Integer> maxDepth = new HashMap<>();
    for (UUID id : allCandidateIds) {
      computeStaticMaxDepthDFS(id, dependentGraph, maxDepth, new HashSet<>());
    }
    return maxDepth;
  }

  private int computeStaticMaxDepthDFS(
      UUID subjectId, Map<UUID, Set<UUID>> dependentGraph, Map<UUID, Integer> maxDepth,
      Set<UUID> visiting) {

    if (maxDepth.containsKey(subjectId)) {
      return maxDepth.get(subjectId);
    }
    if (!visiting.add(subjectId)) {
      return 0;
    }

    int depth = 1;
    for (UUID dependentId : dependentGraph.getOrDefault(subjectId, Collections.emptySet())) {
      depth = Math.max(depth, 1 + computeStaticMaxDepthDFS(dependentId, dependentGraph, maxDepth, visiting));
    }

    visiting.remove(subjectId);
    maxDepth.put(subjectId, depth);
    return depth;
  }

  /**
   * Builds a dependency graph mapping each subject to the set of subjects that depend on it.
   * Only hard prerequisites are included.
   */
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
    }

    return graph;
  }

  /**
   * Validates that the dependency graph has no cycles.
   * Throws {@link CyclicDependencyException} if a cycle is detected.
   */
  private void validateAcyclicGraph(Map<UUID, Set<UUID>> graph) {
    Set<UUID> visited = new HashSet<>();
    Set<UUID> visiting = new HashSet<>();
    for (UUID node : graph.keySet()) {
      if (hasCycle(node, graph, visited, visiting)) {
        throw new CyclicDependencyException(
            node.toString(),
            "Curriculum contains a cyclic prerequisite dependency involving subject " + node);
      }
    }
  }

  private boolean hasCycle(UUID node, Map<UUID, Set<UUID>> graph, Set<UUID> visited, Set<UUID> visiting) {
    if (visited.contains(node)) {
      return false;
    }
    if (!visiting.add(node)) {
      return true;
    }
    for (UUID neighbor : graph.getOrDefault(node, Collections.emptySet())) {
      if (hasCycle(neighbor, graph, visited, visiting)) {
        return true;
      }
    }
    visiting.remove(node);
    visited.add(node);
    return false;
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