package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.dto.record.CurriculumEnrichmentData;
import com.hcmut.lms.personalization.application.dto.response.*;
import com.hcmut.lms.personalization.application.mapper.LearningPathMapper;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.CurriculumFullResponse;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import com.hcmut.lms.personalization.client.dto.StudentLearningProgressResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPath;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSection;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSubject;
import com.hcmut.lms.personalization.domain.entity.learningPath.PrerequisiteNode;
import com.hcmut.lms.personalization.repository.LearningGoalRepository;
import com.hcmut.lms.personalization.repository.LearningPathSectionRepository;
import com.hcmut.lms.personalization.repository.LearningPathSubjectRepository;
import com.hcmut.lms.personalization.application.service.impl.support.AcademicCalendarDisplaySupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class LearningPathResponseAssembler {

  private static final int MAX_RECOMMENDED_CREDITS_PER_SEMESTER = 25;

  private final LearningPathMapper learningPathMapper;
  private final CourseManagementClient courseManagementClient;
  private final LearningServiceClient learningClient;
  private final LearningGoalRepository learningGoalRepository;
  private final LearningPathSectionRepository learningPathSectionRepository;
  private final LearningPathSubjectRepository learningPathSubjectRepository;

  record SectionDisplayInfo(String academicYear, String semester) {}

  public LearningPathResponse toPathResponse(LearningPath path, UUID studentId) {
    List<LearningPathSection> sections = learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(
        path.getLearningPathId());

    LearningGoal goal = learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(studentId)
        .orElse(null);
    StudentLearningProgressResponse progress = fetchProgressForStudent(studentId, goal);
    List<SemesterResponse> allSemesters = fetchAllSemestersSafely();
    Map<UUID, SectionDisplayInfo> sectionDisplayInfo = buildSectionDisplayInfo(studentId, progress, allSemesters);
    List<LearningPathSectionResponse> sectionResponses = sections.stream()
        .map(section -> toSectionResponse(section, sectionDisplayInfo))
        .toList();
    Map<UUID, Integer> semesterOrderBySection = buildSemesterOrderBySection(sections);

    List<LearningPathSubject> subjects = loadSubjectsBySemesterOrder(path.getLearningPathId(), sections);

    CurriculumEnrichmentData curriculumData = buildCurriculumEnrichmentData(progress, goal);
    Map<UUID, String> difficultyMap = fetchBatchDifficultyByIds(
        subjects.stream().map(LearningPathSubject::getSubjectId).distinct().toList());

    List<LearningPathSubjectResponse> subjectResponses = subjects.stream()
        .map(subject -> toSubjectResponse(subject, curriculumData.subjectMap(), difficultyMap))
        .toList();

    LearningPathGraphResponse graph = buildGraph(subjects, semesterOrderBySection, curriculumData.subjectMap(),
        sections, sectionDisplayInfo, findCurrentSemesterId(allSemesters));
    List<LearningPathValidationConflictResponse> validationConflicts = buildValidationConflicts(sections, subjects);

    LearningPathResponse response = learningPathMapper.toPathResponse(path);
    response.setSections(sectionResponses);
    response.setSubjects(subjectResponses);
    response.setGraph(graph);
    response.setValidationConflicts(validationConflicts);
    response.setEarnedCredits(curriculumData.earnedCredits());
    response.setRequiredCredits(curriculumData.requiredCredits());
    return response;
  }

  public StudentLearningProgressResponse fetchProgressForStudent(UUID studentId, LearningGoal goal) {
    try {
      UUID specializationId = goal != null && goal.getSpecializationId() != null
          ? UUID.fromString(goal.getSpecializationId()) : null;
      return courseManagementClient.getStudentProgress(studentId, specializationId);
    } catch (Exception e) {
      log.warn("Failed to fetch student progress: {}", e.getMessage());
      return null;
    }
  }

  public CurriculumEnrichmentData buildCurriculumEnrichmentData(
      StudentLearningProgressResponse progress, LearningGoal goal) {
    try {
      if (goal == null || goal.getSpecializationId() == null) {
        return CurriculumEnrichmentData.empty();
      }
      UUID goalSpecializationId = UUID.fromString(goal.getSpecializationId());
      Integer intakeYear = progress != null && progress.getProgramInfo() != null ? progress.getProgramInfo().getCurriculumYear() : null;
      var curriculum = courseManagementClient.resolveCurriculum(goalSpecializationId, intakeYear);
      if (curriculum == null || curriculum.getCode() == null) {
        return CurriculumEnrichmentData.empty();
      }
      CurriculumFullResponse curriculumFull = courseManagementClient.getCurriculumFull(curriculum.getCode());
      if (curriculumFull == null || curriculumFull.getSections() == null) {
        return CurriculumEnrichmentData.empty();
      }
      Map<UUID, CurriculumFullResponse.CurriculumSubjectFull> result = new HashMap<>();
      for (CurriculumFullResponse.CurriculumSectionFull section : curriculumFull.getSections()) {
        if (section.getSubjects() == null) continue;
        for (CurriculumFullResponse.CurriculumSubjectFull subject : section.getSubjects()) {
          if (subject.getSubjectId() != null) {
            result.put(subject.getSubjectId(), subject);
          }
        }
      }
      Integer earnedCredits = progress != null && progress.getSummary() != null ? progress.getSummary().getEarnedCredits() : null;
      Integer requiredCredits = progress != null && progress.getSummary() != null ? progress.getSummary().getRequiredCredits() : null;
      return new CurriculumEnrichmentData(result, earnedCredits, requiredCredits);
    } catch (Exception e) {
      log.error("Failed to load curriculum data for enrichment, graph will be incomplete: {}", e.getMessage());
      return CurriculumEnrichmentData.empty();
    }
  }

  Map<UUID, SectionDisplayInfo> buildSectionDisplayInfo(UUID studentId, StudentLearningProgressResponse progress,
      List<SemesterResponse> allSemesters) {
    Map<UUID, SectionDisplayInfo> displayBySemesterId = new HashMap<>();

    List<SemesterResponse> remainingSemesters = courseManagementClient.getRemainingSemesters(studentId);
    if (remainingSemesters != null) {
      for (SemesterResponse semester : remainingSemesters) {
        if (semester.getId() == null) continue;
        displayBySemesterId.put(semester.getId(),
            new SectionDisplayInfo(
                AcademicCalendarDisplaySupport.academicYearFromCode(semester.getAcademicYearCode()),
                AcademicCalendarDisplaySupport.semesterFromCode(semester.getSemesterCode())));
      }
    }

    if (progress != null && progress.getSections() != null) {
      for (StudentLearningProgressResponse.StudentProgressSectionItem sectionItem : progress.getSections()) {
        if (sectionItem.getSubjects() == null) continue;
        for (StudentLearningProgressResponse.StudentProgressSubjectItem subjectItem : sectionItem.getSubjects()) {
          UUID semesterId = parseUuid(subjectItem.getSemesterId());
          if (semesterId == null) continue;
          displayBySemesterId.putIfAbsent(semesterId,
              new SectionDisplayInfo(
                  AcademicCalendarDisplaySupport.academicYearFromCode(subjectItem.getAcademicYear()),
                  AcademicCalendarDisplaySupport.semesterFromCode(subjectItem.getSemesterCode())));
        }
      }
    }

    if (allSemesters != null) {
      for (SemesterResponse semester : allSemesters) {
        if (semester.getId() == null) continue;
        displayBySemesterId.putIfAbsent(semester.getId(),
            new SectionDisplayInfo(
                AcademicCalendarDisplaySupport.academicYearFromCode(semester.getAcademicYearCode()),
                AcademicCalendarDisplaySupport.semesterFromCode(semester.getSemesterCode())));
      }
    }

    return displayBySemesterId;
  }

  LearningPathSectionResponse toSectionResponse(LearningPathSection section, Map<UUID, SectionDisplayInfo> sectionDisplayInfo) {
    LearningPathSectionResponse response = learningPathMapper.toSectionResponse(section);
    SectionDisplayInfo displayInfo = sectionDisplayInfo.get(section.getSemesterId());
    if (displayInfo != null) {
      response.setAcademicYear(displayInfo.academicYear());
      response.setSemester(displayInfo.semester());
    }
    return response;
  }

  LearningPathSubjectResponse toSubjectResponse(LearningPathSubject subject,
      Map<UUID, CurriculumFullResponse.CurriculumSubjectFull> curriculumSubjectMap,
      Map<UUID, String> difficultyMap) {
    LearningPathSubjectResponse response = learningPathMapper.toSubjectResponse(subject);
    response.setPrerequisitesGraph(toPrerequisiteResponses(subject.getPrerequisitesGraph()));

    CurriculumFullResponse.CurriculumSubjectFull curriculumSubject = curriculumSubjectMap.get(subject.getSubjectId());
    if (curriculumSubject != null) {
      if (curriculumSubject.getParallels() != null && !curriculumSubject.getParallels().isEmpty()) {
        response.setParallelsGraph(curriculumSubject.getParallels().stream()
            .map(p -> PrerequisiteNodeResponse.builder()
                .subjectId(p.getSubjectId())
                .subjectCode(p.getSubjectCode())
                .subjectName(p.getSubjectName())
                .required(false)
                .build())
            .toList());
      }
      if (curriculumSubject.getRecommendations() != null && !curriculumSubject.getRecommendations().isEmpty()) {
        response.setRecommendationsGraph(curriculumSubject.getRecommendations().stream()
            .map(r -> PrerequisiteNodeResponse.builder()
                .subjectId(r.getSubjectId())
                .subjectCode(r.getSubjectCode())
                .subjectName(r.getSubjectName())
                .required(false)
                .build())
            .toList());
      }
    }

    String difficulty = difficultyMap.get(subject.getSubjectId());
    if (difficulty != null) {
      response.setDifficultyLevel(difficulty);
    }

    return response;
  }

  List<LearningPathSubject> loadSubjectsBySemesterOrder(UUID learningPathId) {
    List<LearningPathSection> sections = learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(
        learningPathId);
    return loadSubjectsBySemesterOrder(learningPathId, sections);
  }

  List<LearningPathSubject> loadSubjectsBySemesterOrder(UUID learningPathId, List<LearningPathSection> sections) {
    Map<UUID, Integer> semesterOrderBySection = buildSemesterOrderBySection(sections);

    return learningPathSubjectRepository.findByLearningPathId(learningPathId)
        .stream()
        .sorted(Comparator.comparingInt(
                (LearningPathSubject s) -> semesterOrderBySection.getOrDefault(s.getLearningPathSectionId(), Integer.MAX_VALUE))
            .thenComparingInt(s -> s.getStudyOrder() != null ? s.getStudyOrder() : Integer.MAX_VALUE)
            .thenComparing(LearningPathSubject::getSubjectCode))
        .toList();
  }

  LearningPathGraphResponse buildGraph(List<LearningPathSubject> subjects,
      Map<UUID, Integer> semesterOrderBySection,
      Map<UUID, CurriculumFullResponse.CurriculumSubjectFull> curriculumSubjectMap,
      List<LearningPathSection> sections,
      Map<UUID, SectionDisplayInfo> sectionDisplayInfo,
      UUID currentSemesterId) {

    Map<UUID, LearningPathSubject> lpSubjectBySubjectId = subjects.stream()
        .filter(s -> Boolean.TRUE.equals(s.getIsHighestResult()))
        .collect(Collectors.toMap(LearningPathSubject::getSubjectId, s -> s, (a, b) -> a));

    // Build nodes from learning path subjects only
    Map<UUID, PrerequisiteGraphNodeResponse> nodeBySubject = new HashMap<>();
    for (var entry : lpSubjectBySubjectId.entrySet()) {
      UUID subjectId = entry.getKey();
      LearningPathSubject lpSubject = entry.getValue();
      boolean isCompleted = Boolean.TRUE.equals(lpSubject.getIsCompleted());
      Integer semesterOrder = semesterOrderBySection.get(lpSubject.getLearningPathSectionId());

      nodeBySubject.put(subjectId, PrerequisiteGraphNodeResponse.builder()
          .id(subjectId)
          .label(lpSubject.getSubjectCode() + " - " + lpSubject.getSubjectName())
          .semesterOrder(semesterOrder)
          .isCompleted(isCompleted)
          .build());
    }

    // Build prerequisite edges — filter by LP subject membership
    List<PrerequisiteGraphEdgeResponse> edges = new ArrayList<>();
    Set<String> existingEdgeKeys = new HashSet<>();

    // First: edges from learning path subjects' stored prerequisitesGraph
    for (var entry : lpSubjectBySubjectId.entrySet()) {
      for (PrerequisiteNodeResponse prerequisite : toPrerequisiteResponses(entry.getValue().getPrerequisitesGraph())) {
        if (Boolean.TRUE.equals(prerequisite.getRequired()) && prerequisite.getSubjectId() != null
            && lpSubjectBySubjectId.containsKey(prerequisite.getSubjectId())) {
          String edgeKey = prerequisite.getSubjectId() + "->" + entry.getKey();
          if (existingEdgeKeys.add(edgeKey)) {
            edges.add(PrerequisiteGraphEdgeResponse.builder()
                .source(prerequisite.getSubjectId())
                .target(entry.getKey())
                .build());
          }
        }
      }
    }

    // Then: supplement from curriculum prerequisite relationships
    for (var entry : curriculumSubjectMap.entrySet()) {
      if (!lpSubjectBySubjectId.containsKey(entry.getKey())) continue;
      CurriculumFullResponse.CurriculumSubjectFull cs = entry.getValue();
      if (cs.getPrerequisites() != null) {
        for (CurriculumFullResponse.SubjectRelation prereq : cs.getPrerequisites()) {
          if (prereq.getSubjectId() != null && lpSubjectBySubjectId.containsKey(prereq.getSubjectId())) {
            String edgeKey = prereq.getSubjectId() + "->" + entry.getKey();
            if (existingEdgeKeys.add(edgeKey)) {
              edges.add(PrerequisiteGraphEdgeResponse.builder()
                  .source(prereq.getSubjectId())
                  .target(entry.getKey())
                  .build());
            }
          }
        }
      }
    }

    // Build parallel and recommendation edges — filter by LP subject membership
    List<PrerequisiteGraphEdgeResponse> parallelEdges = new ArrayList<>();
    List<PrerequisiteGraphEdgeResponse> recommendationEdges = new ArrayList<>();
    for (var entry : curriculumSubjectMap.entrySet()) {
      if (!lpSubjectBySubjectId.containsKey(entry.getKey())) continue;
      CurriculumFullResponse.CurriculumSubjectFull cs = entry.getValue();

      if (cs.getParallels() != null) {
        for (CurriculumFullResponse.SubjectRelation parallel : cs.getParallels()) {
          if (parallel.getSubjectId() != null && lpSubjectBySubjectId.containsKey(parallel.getSubjectId())) {
            parallelEdges.add(PrerequisiteGraphEdgeResponse.builder()
                .source(parallel.getSubjectId())
                .target(entry.getKey())
                .build());
          }
        }
      }

      if (cs.getRecommendations() != null) {
        for (CurriculumFullResponse.SubjectRelation recommendation : cs.getRecommendations()) {
          if (recommendation.getSubjectId() != null && lpSubjectBySubjectId.containsKey(recommendation.getSubjectId())) {
            recommendationEdges.add(PrerequisiteGraphEdgeResponse.builder()
                .source(recommendation.getSubjectId())
                .target(entry.getKey())
                .build());
          }
        }
      }
    }

    Set<UUID> activeNodeIds = new HashSet<>();
    for (var edge : edges) {
      activeNodeIds.add(edge.getSource());
      activeNodeIds.add(edge.getTarget());
    }
    for (var edge : parallelEdges) {
      activeNodeIds.add(edge.getSource());
      activeNodeIds.add(edge.getTarget());
    }
    for (var edge : recommendationEdges) {
      activeNodeIds.add(edge.getSource());
      activeNodeIds.add(edge.getTarget());
    }

    List<PrerequisiteGraphNodeResponse> nodes = activeNodeIds.stream()
        .map(nodeBySubject::get)
        .filter(Objects::nonNull)
        .toList();

    Map<UUID, LearningPathSection> sectionById = sections.stream()
        .collect(Collectors.toMap(LearningPathSection::getLearningPathSectionId, s -> s, (a, b) -> a));

    List<List<PrerequisiteChainNodeResponse>> chains = buildChains(nodes, edges,
        lpSubjectBySubjectId, semesterOrderBySection, sectionById, sectionDisplayInfo, currentSemesterId);
    List<List<PrerequisiteChainNodeResponse>> parallelGroups = buildParallelGroups(nodes, parallelEdges,
        lpSubjectBySubjectId, semesterOrderBySection, sectionById, sectionDisplayInfo, currentSemesterId);
    List<List<PrerequisiteChainNodeResponse>> recommendationChains = buildRecommendationChains(nodes, recommendationEdges,
        lpSubjectBySubjectId, semesterOrderBySection, sectionById, sectionDisplayInfo, currentSemesterId);

    return LearningPathGraphResponse.builder()
        .chains(chains.isEmpty() ? null : chains)
        .parallelGroups(parallelGroups.isEmpty() ? null : parallelGroups)
        .recommendationChains(recommendationChains.isEmpty() ? null : recommendationChains)
        .build();
  }

  private List<List<PrerequisiteChainNodeResponse>> buildChains(
      List<PrerequisiteGraphNodeResponse> nodes,
      List<PrerequisiteGraphEdgeResponse> edges,
      Map<UUID, LearningPathSubject> lpSubjectBySubjectId,
      Map<UUID, Integer> semesterOrderBySection,
      Map<UUID, LearningPathSection> sectionById,
      Map<UUID, SectionDisplayInfo> sectionDisplayInfo,
      UUID currentSemesterId) {

    Set<UUID> nodeIds = nodes.stream().map(PrerequisiteGraphNodeResponse::getId).collect(Collectors.toSet());
    Map<UUID, List<UUID>> outgoing = new HashMap<>();
    Map<UUID, List<UUID>> incoming = new HashMap<>();
    for (var edge : edges) {
      if (nodeIds.contains(edge.getSource()) && nodeIds.contains(edge.getTarget())) {
        outgoing.computeIfAbsent(edge.getSource(), k -> new ArrayList<>()).add(edge.getTarget());
        incoming.computeIfAbsent(edge.getTarget(), k -> new ArrayList<>()).add(edge.getSource());
      }
    }

    List<UUID> rootIds = nodes.stream()
        .filter(n -> !incoming.containsKey(n.getId()) && outgoing.containsKey(n.getId()))
        .map(PrerequisiteGraphNodeResponse::getId)
        .toList();

    List<List<PrerequisiteChainNodeResponse>> chains = new ArrayList<>();
    for (UUID rootId : rootIds) {
      traceChain(rootId, new ArrayList<>(), new HashSet<>(), outgoing,
          lpSubjectBySubjectId, semesterOrderBySection, sectionById, sectionDisplayInfo, currentSemesterId, chains);
    }

    Set<String> seen = new HashSet<>();
    return chains.stream().filter(chain -> {
      String key = chain.stream().map(n -> n.getId().toString()).collect(Collectors.joining("|"));
      return seen.add(key);
    }).toList();
  }

  private void traceChain(UUID nodeId, List<PrerequisiteChainNodeResponse> prefix, Set<UUID> visited,
      Map<UUID, List<UUID>> outgoing,
      Map<UUID, LearningPathSubject> lpSubjectBySubjectId,
      Map<UUID, Integer> semesterOrderBySection,
      Map<UUID, LearningPathSection> sectionById,
      Map<UUID, SectionDisplayInfo> sectionDisplayInfo,
      UUID currentSemesterId,
      List<List<PrerequisiteChainNodeResponse>> result) {
    if (visited.contains(nodeId)) return;
    List<PrerequisiteChainNodeResponse> chain = new ArrayList<>(prefix);
    chain.add(enrichChainNode(nodeId, lpSubjectBySubjectId, semesterOrderBySection, sectionById, sectionDisplayInfo, currentSemesterId));
    Set<UUID> pathVisited = new HashSet<>(visited);
    pathVisited.add(nodeId);

    List<UUID> children = outgoing.getOrDefault(nodeId, List.of());
    if (children.isEmpty()) {
      result.add(chain);
      return;
    }
    for (UUID childId : children) {
      traceChain(childId, chain, pathVisited, outgoing,
          lpSubjectBySubjectId, semesterOrderBySection, sectionById, sectionDisplayInfo, currentSemesterId, result);
    }
  }

  private List<List<PrerequisiteChainNodeResponse>> buildParallelGroups(
      List<PrerequisiteGraphNodeResponse> nodes,
      List<PrerequisiteGraphEdgeResponse> parallelEdges,
      Map<UUID, LearningPathSubject> lpSubjectBySubjectId,
      Map<UUID, Integer> semesterOrderBySection,
      Map<UUID, LearningPathSection> sectionById,
      Map<UUID, SectionDisplayInfo> sectionDisplayInfo,
      UUID currentSemesterId) {

    if (parallelEdges == null || parallelEdges.isEmpty()) return List.of();

    Set<UUID> nodeIds = nodes.stream().map(PrerequisiteGraphNodeResponse::getId).collect(Collectors.toSet());

    Set<UUID> visited = new HashSet<>();
    List<List<PrerequisiteChainNodeResponse>> groups = new ArrayList<>();

    for (var edge : parallelEdges) {
      if (!nodeIds.contains(edge.getSource()) || !nodeIds.contains(edge.getTarget())) continue;
      if (visited.contains(edge.getSource()) && visited.contains(edge.getTarget())) continue;

      List<PrerequisiteChainNodeResponse> component = new ArrayList<>();
      Queue<UUID> queue = new LinkedList<>(List.of(edge.getSource(), edge.getTarget()));
      Set<UUID> componentVisited = new HashSet<>();
      while (!queue.isEmpty()) {
        UUID nodeId = queue.poll();
        if (componentVisited.contains(nodeId) || visited.contains(nodeId)) continue;
        componentVisited.add(nodeId);
        component.add(enrichChainNode(nodeId, lpSubjectBySubjectId, semesterOrderBySection, sectionById, sectionDisplayInfo, currentSemesterId));
        for (var e : parallelEdges) {
          if (e.getSource().equals(nodeId) && !componentVisited.contains(e.getTarget()) && !visited.contains(e.getTarget())) {
            queue.add(e.getTarget());
          }
          if (e.getTarget().equals(nodeId) && !componentVisited.contains(e.getSource()) && !visited.contains(e.getSource())) {
            queue.add(e.getSource());
          }
        }
      }
      if (component.size() > 1) {
        componentVisited.forEach(visited::add);
        component.sort(Comparator.comparingInt(n -> n.getSemesterOrder() != null ? n.getSemesterOrder() : 0));
        groups.add(component);
      }
    }
    return groups;
  }

  private List<List<PrerequisiteChainNodeResponse>> buildRecommendationChains(
      List<PrerequisiteGraphNodeResponse> nodes,
      List<PrerequisiteGraphEdgeResponse> recommendationEdges,
      Map<UUID, LearningPathSubject> lpSubjectBySubjectId,
      Map<UUID, Integer> semesterOrderBySection,
      Map<UUID, LearningPathSection> sectionById,
      Map<UUID, SectionDisplayInfo> sectionDisplayInfo,
      UUID currentSemesterId) {

    if (recommendationEdges == null || recommendationEdges.isEmpty()) return List.of();

    Set<UUID> nodeIds = nodes.stream().map(PrerequisiteGraphNodeResponse::getId).collect(Collectors.toSet());
    Map<UUID, List<UUID>> recOutgoing = new HashMap<>();
    Map<UUID, List<UUID>> recIncoming = new HashMap<>();
    for (var edge : recommendationEdges) {
      if (nodeIds.contains(edge.getSource()) && nodeIds.contains(edge.getTarget())) {
        recOutgoing.computeIfAbsent(edge.getSource(), k -> new ArrayList<>()).add(edge.getTarget());
        recIncoming.computeIfAbsent(edge.getTarget(), k -> new ArrayList<>()).add(edge.getSource());
      }
    }

    List<UUID> recRoots = nodes.stream()
        .filter(n -> !recIncoming.containsKey(n.getId()) && recOutgoing.containsKey(n.getId()))
        .map(PrerequisiteGraphNodeResponse::getId)
        .toList();

    List<List<PrerequisiteChainNodeResponse>> recChains = new ArrayList<>();
    for (UUID rootId : recRoots) {
      traceChain(rootId, new ArrayList<>(), new HashSet<>(), recOutgoing,
          lpSubjectBySubjectId, semesterOrderBySection, sectionById, sectionDisplayInfo, currentSemesterId, recChains);
    }
    return recChains;
  }

  private PrerequisiteChainNodeResponse enrichChainNode(UUID nodeId,
      Map<UUID, LearningPathSubject> lpSubjectBySubjectId,
      Map<UUID, Integer> semesterOrderBySection,
      Map<UUID, LearningPathSection> sectionById,
      Map<UUID, SectionDisplayInfo> sectionDisplayInfo,
      UUID currentSemesterId) {

    LearningPathSubject lpSubject = lpSubjectBySubjectId.get(nodeId);

    String subjectCode = lpSubject.getSubjectCode();
    String subjectName = lpSubject.getSubjectName();
    Integer credits = lpSubject.getCredits();
    boolean isCompleted = Boolean.TRUE.equals(lpSubject.getIsCompleted());

    Integer semesterOrder = semesterOrderBySection.getOrDefault(lpSubject.getLearningPathSectionId(), null);
    if (semesterOrder == null) {
      LearningPathSection section = sectionById.get(lpSubject.getLearningPathSectionId());
      semesterOrder = section != null ? section.getSemesterOrder() : null;
    }

    String semesterLabel = null;
    boolean isCurrentSemester = false;
    LearningPathSection section = sectionById.get(lpSubject.getLearningPathSectionId());
    if (section != null) {
      SectionDisplayInfo displayInfo = sectionDisplayInfo.get(section.getSemesterId());
      if (displayInfo != null) {
        semesterLabel = displayInfo.academicYear() + " - " + displayInfo.semester();
      } else {
        semesterLabel = "Kỳ " + section.getSemesterOrder();
      }
      isCurrentSemester = currentSemesterId != null && currentSemesterId.equals(section.getSemesterId());
    }

    return PrerequisiteChainNodeResponse.builder()
        .id(nodeId)
        .subjectCode(subjectCode)
        .subjectName(subjectName)
        .credits(credits)
        .semesterOrder(semesterOrder)
        .semesterLabel(semesterLabel)
        .isCompleted(isCompleted)
        .isCurrentSemester(isCurrentSemester)
        .build();
  }

  List<LearningPathValidationConflictResponse> buildValidationConflicts(
      List<LearningPathSection> sections, List<LearningPathSubject> subjects) {
    Map<UUID, Integer> creditsBySection = new HashMap<>();
    for (LearningPathSubject subject : subjects) {
      creditsBySection.merge(subject.getLearningPathSectionId(), subject.getCredits(), Integer::sum);
    }

    List<LearningPathValidationConflictResponse> conflicts = new ArrayList<>();
    for (LearningPathSection section : sections) {
      int credits = creditsBySection.getOrDefault(section.getLearningPathSectionId(), 0);
      if (credits > MAX_RECOMMENDED_CREDITS_PER_SEMESTER) {
        conflicts.add(LearningPathValidationConflictResponse.builder()
            .conflictType("credit-limit")
            .semesterOrder(section.getSemesterOrder())
            .description("Total credits exceed recommended limit")
            .suggestedResolution("Move one elective to another semester")
            .relatedSubjectIds(Collections.emptyList())
            .build());
      }
    }

    return conflicts;
  }

  Map<UUID, String> fetchBatchDifficultyByIds(List<UUID> subjectIds) {
    if (subjectIds == null || subjectIds.isEmpty()) {
      return Map.of();
    }
    try {
      Map<UUID, String> result = learningClient.getBatchDifficulty(subjectIds);
      return result != null ? result : Map.of();
    } catch (Exception e) {
      log.warn("Failed to fetch batch difficulty: {}", e.getMessage());
      return Map.of();
    }
  }

  private List<PrerequisiteNodeResponse> toPrerequisiteResponses(List<PrerequisiteNode> prerequisitesGraph) {
    if (prerequisitesGraph == null || prerequisitesGraph.isEmpty()) {
      return Collections.emptyList();
    }
    return prerequisitesGraph.stream()
        .map(node -> PrerequisiteNodeResponse.builder()
            .subjectId(node.getSubjectId())
            .subjectCode(node.getSubjectCode())
            .subjectName(node.getSubjectName())
            .required(node.getRequired())
            .build())
        .toList();
  }

  private UUID parseUuid(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return UUID.fromString(value);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  Map<UUID, Integer> buildSemesterOrderBySection(List<LearningPathSection> sections) {
    return sections.stream()
        .collect(Collectors.toMap(LearningPathSection::getLearningPathSectionId, LearningPathSection::getSemesterOrder));
  }

  UUID findCurrentSemesterId(List<SemesterResponse> allSemesters) {
    if (allSemesters == null) return null;
    java.time.LocalDate today = java.time.LocalDate.now();
    for (SemesterResponse semester : allSemesters) {
      if (semester.getStartDate() != null && semester.getEndDate() != null) {
        java.time.LocalDate start = java.time.LocalDate.parse(semester.getStartDate());
        java.time.LocalDate end = java.time.LocalDate.parse(semester.getEndDate());
        if (!today.isBefore(start) && !today.isAfter(end)) {
          return semester.getId();
        }
      }
    }
    return null;
  }

  List<SemesterResponse> fetchAllSemestersSafely() {
    try {
      return courseManagementClient.getAllSemesters();
    } catch (Exception e) {
      log.warn("Failed to fetch all semesters: {}", e.getMessage());
      return null;
    }
  }
}