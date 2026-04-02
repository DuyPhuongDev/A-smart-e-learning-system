package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.dto.request.CompareLearningPathsRequest;
import com.hcmut.lms.personalization.application.dto.request.OptimizeLearningPathRequest;
import com.hcmut.lms.personalization.application.dto.request.UpdateLearningPathRequest;
import com.hcmut.lms.personalization.application.dto.response.*;
import com.hcmut.lms.personalization.application.mapper.LearningPathMapper;
import com.hcmut.lms.personalization.application.service.LearningPathService;
import com.hcmut.lms.personalization.application.service.impl.support.AcademicCalendarDisplaySupport;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.CurriculumResolutionResponse;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import com.hcmut.lms.personalization.client.dto.StudentLearningProgressResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPath;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSection;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSubject;
import com.hcmut.lms.personalization.domain.entity.learningPath.PrerequisiteNode;
import com.hcmut.lms.personalization.repository.LearningGoalRepository;
import com.hcmut.lms.personalization.repository.LearningPathRepository;
import com.hcmut.lms.personalization.repository.LearningPathSectionRepository;
import com.hcmut.lms.personalization.repository.LearningPathSubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LearningPathServiceImpl implements LearningPathService {

  private final LearningPathRepository learningPathRepository;
  private final LearningGoalRepository learningGoalRepository;
  private final CourseManagementClient courseManagementClient;
  private final LearningPathSectionRepository learningPathSectionRepository;
  private final LearningPathSubjectRepository learningPathSubjectRepository;
  private final LearningPathGenerationService learningPathGenerationService;
  private final LearningPathMapper learningPathMapper;

  @Override
  @Transactional(readOnly = true)
  public LearningPathResponse getActiveLearningPath(UUID studentId) {
    return learningPathRepository.findTopByStudentIdAndIsActiveTrueOrderByUpdatedAtDesc(studentId)
        .map(path -> toPathResponse(path, studentId))
        .orElse(null);
  }

  @Override
  @Transactional(readOnly = true)
  public LearningPathResponse getLearningPathById(UUID studentId, UUID learningPathId) {
    return toPathResponse(getOwnedPath(studentId, learningPathId), studentId);
  }

  @Override
  public LearningPathResponse createLearningPath(UUID studentId) {
    log.info("Creating learning path for studentId={}", studentId);

    LearningGoal learningGoal = learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(studentId)
        .orElseThrow(() -> new EntityNotFoundException("Active learning goal not found for studentId=" + studentId));

    UUID specializationId;
    try {
      specializationId = UUID.fromString(learningGoal.getSpecializationId());
    } catch (Exception ex) {
      throw new IllegalArgumentException("Invalid specializationId in active learning goal");
    }

    StudentLearningProgressResponse progress = courseManagementClient.getStudentProgress(studentId);
    Integer intakeYear = progress != null && progress.getProgramInfo() != null ? progress.getProgramInfo()
        .getCurriculumYear() : null;
    if (intakeYear == null) {
      throw new EntityNotFoundException("Cannot determine intake year for studentId=" + studentId);
    }

    CurriculumResolutionResponse curriculum = courseManagementClient.resolveCurriculum(specializationId, intakeYear);
    if (curriculum == null || curriculum.getCode() == null || curriculum.getCode().isBlank()) {
      throw new EntityNotFoundException(
          "Curriculum not found for specializationId=" + specializationId + " and intakeYear=" + intakeYear);
    }

    learningPathRepository.deactivateActiveByStudentId(studentId);

    // Generate learning path using the generation service
    LearningPath path = learningPathGenerationService.generateLearningPath(
        studentId, learningGoal.getLearningGoalId(), curriculum.getCode(), learningGoal);

    log.info("Learning path created successfully with id={}", path.getLearningPathId());
    return toPathResponse(path, studentId);
  }

  @Override
  public LearningPathResponse updateLearningPath(
      UUID studentId, UUID learningPathId,
      UpdateLearningPathRequest request) {
    LearningPath path = getOwnedPath(studentId, learningPathId);

    if (request.getTotalCredits() != null) {
      path.setTotalCredits(request.getTotalCredits());
    }
    if (request.getEstimatedDurationSemesters() != null) {
      path.setEstimatedDurationSemesters(request.getEstimatedDurationSemesters());
    }
    if (request.getPredictedGpa() != null) {
      path.setPredictedGpa(request.getPredictedGpa());
    }
    if (request.getCompletionRate() != null) {
      path.setCompletionRate(request.getCompletionRate());
    }

    LearningPath saved = learningPathRepository.save(path);
    return toPathResponse(saved, studentId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<LearningPathSectionResponse> getSections(UUID studentId, UUID learningPathId) {
    getOwnedPath(studentId, learningPathId);
    Map<UUID, SectionDisplayInfo> sectionDisplayInfo = buildSectionDisplayInfo(studentId);
    return learningPathSectionRepository.findByLearningPathIdOrderBySemesterOrderAsc(learningPathId)
        .stream()
        .map(section -> toSectionResponse(section, sectionDisplayInfo))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<LearningPathSubjectResponse> getSubjects(UUID studentId, UUID learningPathId) {
    getOwnedPath(studentId, learningPathId);
    return loadSubjectsBySemesterOrder(learningPathId).stream().map(this::toSubjectResponse).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<LearningPathOptimizationCandidateResponse> optimize(
      UUID studentId, UUID learningPathId,
      OptimizeLearningPathRequest request) {

    LearningPath path = getOwnedPath(studentId, learningPathId);

    LearningPathOptimizationCandidateResponse candidate = LearningPathOptimizationCandidateResponse.builder()
        .strategy(request.getStrategy())
        .title("Optimization preview")
        .description("Auto-generated candidate based on current learning path metrics")
        .projectedRiskLevel(path.getRiskLevel() != null ? path.getRiskLevel() : "medium")
        .projectedPredictedGpa(path.getPredictedGpa() != null ? path.getPredictedGpa().doubleValue() : null)
        .projectedTotalSemesters(path.getEstimatedDurationSemesters())
        .changes(Collections.emptyList())
        .build();

    return List.of(candidate);
  }

  @Override
  @Transactional(readOnly = true)
  public List<LearningPathValidationConflictResponse> validate(UUID studentId, UUID learningPathId) {
    getOwnedPath(studentId, learningPathId);

    List<LearningPathSection> sections = learningPathSectionRepository.findByLearningPathIdOrderBySemesterOrderAsc(
        learningPathId);

    Map<UUID, Integer> creditsBySection = new HashMap<>();
    for (LearningPathSubject subject : learningPathSubjectRepository.findByLearningPathId(learningPathId)) {
      creditsBySection.merge(subject.getLearningPathSectionId(), subject.getCredits(), Integer::sum);
    }

    List<LearningPathValidationConflictResponse> conflicts = new ArrayList<>();
    for (LearningPathSection section : sections) {
      int credits = creditsBySection.getOrDefault(section.getLearningPathSectionId(), 0);
      if (credits > 25) {
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

  @Override
  @Transactional(readOnly = true)
  public List<LearningPathChangeRecordResponse> getChanges(UUID studentId, UUID learningPathId) {
    getOwnedPath(studentId, learningPathId);
    return Collections.emptyList();
  }

  @Override
  @Transactional(readOnly = true)
  public LearningPathGraphResponse getGraph(UUID studentId, UUID learningPathId) {
    getOwnedPath(studentId, learningPathId);

    List<LearningPathSection> sections = learningPathSectionRepository.findByLearningPathIdOrderBySemesterOrderAsc(
        learningPathId);
    Map<UUID, Integer> semesterOrderBySection = sections.stream()
        .collect(
            Collectors.toMap(LearningPathSection::getLearningPathSectionId, LearningPathSection::getSemesterOrder));

    List<LearningPathSubject> subjects = learningPathSubjectRepository.findByLearningPathId(learningPathId);
    Map<UUID, LearningPathSubject> subjectById = subjects.stream()
        .collect(Collectors.toMap(LearningPathSubject::getSubjectId, s -> s, (left, right) -> left));

    List<PrerequisiteGraphNodeResponse> nodes = subjects.stream()
        .map(subject -> PrerequisiteGraphNodeResponse.builder()
            .id(subject.getSubjectId())
            .label(subject.getSubjectCode() + " - " + subject.getSubjectName())
            .semesterOrder(semesterOrderBySection.get(subject.getLearningPathSectionId()))
            .build())
        .toList();

    List<PrerequisiteGraphEdgeResponse> edges = new ArrayList<>();
    for (LearningPathSubject subject : subjects) {
      for (PrerequisiteNodeResponse prerequisite : toPrerequisiteResponses(subject.getPrerequisitesGraph())) {
        if (Boolean.TRUE.equals(
            prerequisite.getRequired()) && prerequisite.getSubjectId() != null && subjectById.containsKey(
            prerequisite.getSubjectId())) {
          edges.add(PrerequisiteGraphEdgeResponse.builder()
              .source(prerequisite.getSubjectId())
              .target(subject.getSubjectId())
              .build());
        }
      }
    }

    return LearningPathGraphResponse.builder().nodes(nodes).edges(edges).build();
  }

  @Override
  @Transactional(readOnly = true)
  public LearningPathComparisonResultResponse compare(UUID studentId, CompareLearningPathsRequest request) {
    LearningPath baselinePath = getOwnedPath(studentId, request.getBaselineLearningPathId());
    LearningPath targetPath = getOwnedPath(studentId, request.getTargetLearningPathId());

    LearningPathComparisonResultResponse.LearningPathComparisonMetrics baseline = toComparisonMetrics(baselinePath);
    LearningPathComparisonResultResponse.LearningPathComparisonMetrics candidate = toComparisonMetrics(targetPath);

    LearningPathComparisonResultResponse.LearningPathComparisonDeltas deltas =
        LearningPathComparisonResultResponse.LearningPathComparisonDeltas.builder()
        .totalSemesters(nullSafe(targetPath.getEstimatedDurationSemesters()) - nullSafe(
            baselinePath.getEstimatedDurationSemesters()))
        .avgCreditsPerSemester(round(candidate.getAvgCreditsPerSemester() - baseline.getAvgCreditsPerSemester()))
        .predictedGpa(round(candidate.getPredictedGpa() - baseline.getPredictedGpa()))
        .build();

    return LearningPathComparisonResultResponse.builder()
        .baselinePathId(baselinePath.getLearningPathId())
        .baseline(baseline)
        .candidate(candidate)
        .deltas(deltas)
        .build();
  }

  private LearningPath getOwnedPath(UUID studentId, UUID learningPathId) {
    return learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(learningPathId, studentId)
        .orElseThrow(() -> new EntityNotFoundException("Learning path not found with id: " + learningPathId));
  }

  private LearningPathResponse toPathResponse(LearningPath path, UUID studentId) {
    List<LearningPathSection> sections = learningPathSectionRepository.findByLearningPathIdOrderBySemesterOrderAsc(
        path.getLearningPathId());
    Map<UUID, SectionDisplayInfo> sectionDisplayInfo = buildSectionDisplayInfo(studentId);
    List<LearningPathSectionResponse> sectionResponses = sections.stream()
        .map(section -> toSectionResponse(section, sectionDisplayInfo))
        .toList();
    Map<UUID, Integer> semesterOrderBySection = sections.stream()
        .collect(
            Collectors.toMap(LearningPathSection::getLearningPathSectionId, LearningPathSection::getSemesterOrder));

    List<LearningPathSubject> subjects = loadSubjectsBySemesterOrder(path.getLearningPathId());
    List<LearningPathSubjectResponse> subjectResponses = subjects.stream().map(this::toSubjectResponse).toList();

    LearningPathGraphResponse graph = buildGraph(subjects, semesterOrderBySection);
    List<LearningPathValidationConflictResponse> validationConflicts = buildValidationConflicts(sections, subjects);

    LearningPathResponse response = learningPathMapper.toPathResponse(path);
    response.setSections(sectionResponses);
    response.setSubjects(subjectResponses);
    response.setGraph(graph);
    response.setValidationConflicts(validationConflicts);
    return response;
  }

  private LearningPathSectionResponse toSectionResponse(
      LearningPathSection section,
      Map<UUID, SectionDisplayInfo> sectionDisplayInfo) {
    LearningPathSectionResponse response = learningPathMapper.toSectionResponse(section);
    SectionDisplayInfo displayInfo = sectionDisplayInfo.get(section.getSemesterId());
    if (displayInfo != null) {
      response.setAcademicYear(displayInfo.academicYear());
      response.setSemester(displayInfo.semester());
    }
    return response;
  }

  private Map<UUID, SectionDisplayInfo> buildSectionDisplayInfo(UUID studentId) {
    Map<UUID, SectionDisplayInfo> displayBySemesterId = new HashMap<>();

    for (SemesterResponse semester : courseManagementClient.getRemainingSemesters(studentId)) {
      if (semester.getId() == null) {
        continue;
      }
      displayBySemesterId.put(semester.getId(),
          new SectionDisplayInfo(
              AcademicCalendarDisplaySupport.academicYearFromCode(semester.getAcademicYearCode()),
              AcademicCalendarDisplaySupport.semesterFromCode(semester.getSemesterCode())));
    }

    StudentLearningProgressResponse progress = courseManagementClient.getStudentProgress(studentId);
    if (progress != null && progress.getSections() != null) {
      for (StudentLearningProgressResponse.StudentProgressSectionItem sectionItem : progress.getSections()) {
        if (sectionItem.getSubjects() == null) {
          continue;
        }
        for (StudentLearningProgressResponse.StudentProgressSubjectItem subjectItem : sectionItem.getSubjects()) {
          UUID semesterId = parseUuid(subjectItem.getSemesterId());
          if (semesterId == null) {
            continue;
          }
          displayBySemesterId.putIfAbsent(
              semesterId,
              new SectionDisplayInfo(
                  AcademicCalendarDisplaySupport.academicYearFromCode(subjectItem.getAcademicYear()),
                  AcademicCalendarDisplaySupport.semesterFromCode(subjectItem.getSemesterCode())));
        }
      }
    }

    return displayBySemesterId;
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

  private record SectionDisplayInfo(String academicYear, String semester) {
  }

  private LearningPathSubjectResponse toSubjectResponse(LearningPathSubject subject) {
    LearningPathSubjectResponse response = learningPathMapper.toSubjectResponse(subject);
    response.setPrerequisitesGraph(toPrerequisiteResponses(subject.getPrerequisitesGraph()));
    return response;
  }

  private List<LearningPathSubject> loadSubjectsBySemesterOrder(UUID learningPathId) {
    List<LearningPathSection> sections = learningPathSectionRepository.findByLearningPathIdOrderBySemesterOrderAsc(
        learningPathId);
    Map<UUID, Integer> semesterOrderBySection = sections.stream()
        .collect(
            Collectors.toMap(LearningPathSection::getLearningPathSectionId, LearningPathSection::getSemesterOrder));

    return learningPathSubjectRepository.findByLearningPathId(learningPathId)
        .stream()
        .sorted(Comparator.comparingInt(
                (LearningPathSubject s) -> semesterOrderBySection.getOrDefault(
                    s.getLearningPathSectionId(),
                    Integer.MAX_VALUE))
            .thenComparingInt(s -> s.getStudyOrder() != null ? s.getStudyOrder() : Integer.MAX_VALUE)
            .thenComparing(LearningPathSubject::getSubjectCode))
        .toList();
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

  private LearningPathGraphResponse buildGraph(
      List<LearningPathSubject> subjects,
      Map<UUID, Integer> semesterOrderBySection) {
    Map<UUID, LearningPathSubject> subjectById = subjects.stream()
        .collect(Collectors.toMap(LearningPathSubject::getSubjectId, s -> s, (left, right) -> left));

    List<PrerequisiteGraphNodeResponse> nodes = subjects.stream()
        .map(subject -> PrerequisiteGraphNodeResponse.builder()
            .id(subject.getSubjectId())
            .label(subject.getSubjectCode() + " - " + subject.getSubjectName())
            .semesterOrder(semesterOrderBySection.get(subject.getLearningPathSectionId()))
            .build())
        .toList();

    List<PrerequisiteGraphEdgeResponse> edges = new ArrayList<>();
    for (LearningPathSubject subject : subjects) {
      for (PrerequisiteNodeResponse prerequisite : toPrerequisiteResponses(subject.getPrerequisitesGraph())) {
        if (Boolean.TRUE.equals(
            prerequisite.getRequired()) && prerequisite.getSubjectId() != null && subjectById.containsKey(
            prerequisite.getSubjectId())) {
          edges.add(PrerequisiteGraphEdgeResponse.builder()
              .source(prerequisite.getSubjectId())
              .target(subject.getSubjectId())
              .build());
        }
      }
    }

    return LearningPathGraphResponse.builder().nodes(nodes).edges(edges).build();
  }

  private List<LearningPathValidationConflictResponse> buildValidationConflicts(
      List<LearningPathSection> sections,
      List<LearningPathSubject> subjects) {
    Map<UUID, Integer> creditsBySection = new HashMap<>();
    for (LearningPathSubject subject : subjects) {
      creditsBySection.merge(subject.getLearningPathSectionId(), subject.getCredits(), Integer::sum);
    }

    List<LearningPathValidationConflictResponse> conflicts = new ArrayList<>();
    for (LearningPathSection section : sections) {
      int credits = creditsBySection.getOrDefault(section.getLearningPathSectionId(), 0);
      if (credits > 25) {
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

  private LearningPathComparisonResultResponse.LearningPathComparisonMetrics toComparisonMetrics(LearningPath path) {
    int totalSemesters = nullSafe(path.getEstimatedDurationSemesters());
    int totalCredits = nullSafe(path.getTotalCredits());
    double avgCredits = totalSemesters == 0 ? 0.0 : round((double) totalCredits / totalSemesters);

    return LearningPathComparisonResultResponse.LearningPathComparisonMetrics.builder()
        .totalSemesters(path.getEstimatedDurationSemesters())
        .avgCreditsPerSemester(avgCredits)
        .predictedGpa(path.getPredictedGpa() != null ? path.getPredictedGpa().doubleValue() : 0.0)
        .riskLevel(path.getRiskLevel())
        .build();
  }

  private int nullSafe(Integer value) {
    return value == null ? 0 : value;
  }

  private double round(double value) {
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
  }
}

