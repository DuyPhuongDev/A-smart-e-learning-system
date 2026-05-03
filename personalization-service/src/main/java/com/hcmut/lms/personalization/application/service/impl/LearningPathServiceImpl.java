package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.common.util.SubjectPassUtil;
import com.hcmut.lms.personalization.application.dto.record.CurriculumContext;
import com.hcmut.lms.personalization.application.dto.record.CurriculumEnrichmentData;
import com.hcmut.lms.personalization.application.dto.request.SubjectChangeDto;
import com.hcmut.lms.personalization.application.dto.request.UpdateLearningPathRequest;
import com.hcmut.lms.personalization.application.dto.response.*;
import com.hcmut.lms.personalization.application.service.LearningPathService;
import com.hcmut.lms.personalization.application.service.impl.learning_path.LearningPathGenerationService;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.*;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPath;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSection;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSubject;
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
import java.time.LocalDateTime;
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
  private final LearningPathResponseAssembler assembler;
  private final LearningServiceClient learningServiceClient;

  @Override
  @Transactional(readOnly = true)
  public LearningPathResponse getActiveLearningPath(UUID studentId) {
    LearningPath path = learningPathRepository.findTopByStudentIdAndIsActiveTrueOrderByUpdatedAtDesc(studentId)
        .orElseThrow(() -> new EntityNotFoundException("Active learning path not found for studentId=" + studentId));
    return assembler.toPathResponse(path, studentId);
  }

  @Override
  @Transactional(readOnly = true)
  public LearningPathResponse getLearningPathById(UUID studentId, UUID learningPathId) {
    return assembler.toPathResponse(getOwnedPath(studentId, learningPathId), studentId);
  }

  @Override
  public LearningPathResponse createLearningPath(UUID studentId) {
    log.info("Creating learning path for studentId={}", studentId);

    CurriculumContext ctx = resolveCurriculumContext(studentId);
    if (ctx.curriculum() == null || ctx.curriculum().getCode() == null || ctx.curriculum().getCode().isBlank()) {
      throw new EntityNotFoundException(
          "Curriculum not found for specializationId=" + ctx.specializationId());
    }

    learningPathRepository.deactivateActiveByStudentId(studentId);

    LearningPath path = learningPathGenerationService.generateLearningPath(
        studentId, ctx.goal().getLearningGoalId(), ctx.curriculum().getCode(), ctx.goal(), ctx.progress());

    log.info("Learning path created successfully with id={}", path.getLearningPathId());
    return assembler.toPathResponse(path, studentId);
  }

  @Override
  public LearningPathResponse updateLearningPath(
      UUID studentId, UUID learningPathId, UpdateLearningPathRequest request) {
    LearningPath path = getActiveOwnedPath(studentId, learningPathId);

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
    return assembler.toPathResponse(saved, studentId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<LearningPathSectionResponse> getSections(UUID studentId, UUID learningPathId) {
    getOwnedPath(studentId, learningPathId);
    LearningGoal goal = learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(studentId).orElse(null);
    StudentLearningProgressResponse progress = assembler.fetchProgressForStudent(studentId, goal);
    List<SemesterResponse> allSemesters = assembler.fetchAllSemestersSafely();
    Map<UUID, LearningPathResponseAssembler.SectionDisplayInfo> sectionDisplayInfo = assembler.buildSectionDisplayInfo(studentId, progress, allSemesters);
    return learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(learningPathId)
        .stream()
        .map(section -> assembler.toSectionResponse(section, sectionDisplayInfo))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<LearningPathSubjectResponse> getSubjects(UUID studentId, UUID learningPathId) {
    getOwnedPath(studentId, learningPathId);
    List<LearningPathSubject> subjects = assembler.loadSubjectsBySemesterOrder(learningPathId);
    LearningGoal goal = learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(studentId).orElse(null);
    StudentLearningProgressResponse progress = assembler.fetchProgressForStudent(studentId, goal);
    CurriculumEnrichmentData curriculumData = assembler.buildCurriculumEnrichmentData(progress, goal);
    Map<UUID, String> difficultyMap = assembler.fetchBatchDifficultyByIds(
        subjects.stream().map(LearningPathSubject::getSubjectId).distinct().toList());
    return subjects.stream().map(subject -> assembler.toSubjectResponse(subject, curriculumData.subjectMap(), difficultyMap)).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public LearningPathGraphResponse getGraph(UUID studentId, UUID learningPathId) {
    getOwnedPath(studentId, learningPathId);
    List<LearningPathSection> sections = learningPathSectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(
        learningPathId);
    Map<UUID, Integer> semesterOrderBySection = assembler.buildSemesterOrderBySection(sections);
    List<LearningPathSubject> subjects = learningPathSubjectRepository.findByLearningPathId(learningPathId);
    LearningGoal goal = learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(studentId).orElse(null);
    StudentLearningProgressResponse progress = assembler.fetchProgressForStudent(studentId, goal);
    CurriculumEnrichmentData curriculumData = assembler.buildCurriculumEnrichmentData(progress, goal);
    List<SemesterResponse> allSemesters = assembler.fetchAllSemestersSafely();
    var sectionDisplayInfo = assembler.buildSectionDisplayInfo(studentId, progress, allSemesters);
    return assembler.buildGraph(subjects, semesterOrderBySection, curriculumData.subjectMap(),
        sections, sectionDisplayInfo, assembler.findCurrentSemesterId(allSemesters));
  }

  @Override
  public LearningPathResponse updateLearningPathSubjects(UUID studentId, UUID learningPathId, List<SubjectChangeDto> changes) {
    LearningPath path = getActiveOwnedPath(studentId, learningPathId);
    List<LearningPathSubject> allSubjects = learningPathSubjectRepository.findByLearningPathId(learningPathId);
    Map<UUID, List<LearningPathSubject>> subjectsBySubjectId = allSubjects.stream()
        .collect(Collectors.groupingBy(LearningPathSubject::getSubjectId));
    Set<UUID> existingSubjectIds = allSubjects.stream()
        .filter(s -> !Boolean.TRUE.equals(s.getIsCompleted()))
        .map(LearningPathSubject::getSubjectId)
        .collect(Collectors.toSet());

    List<UUID> addSubjectIds = changes.stream()
        .filter(c -> "ADD".equalsIgnoreCase(c.getAction()))
        .map(SubjectChangeDto::getSubjectId)
        .toList();
    Map<UUID, String> addDifficultyMap = assembler.fetchBatchDifficultyByIds(addSubjectIds);

    List<LearningPathSubject> subjectsToSave = new ArrayList<>();
    List<LearningPathSubject> subjectsToRemove = new ArrayList<>();

    for (SubjectChangeDto change : changes) {
      switch (change.getAction().toUpperCase()) {
        case "ADD" -> {
          if (existingSubjectIds.contains(change.getSubjectId())) {
            throw new IllegalArgumentException("Subject already exists in learning path: " + change.getSubjectCode());
          }
          if (change.getTargetSectionId() == null) {
            throw new IllegalArgumentException("Target section must be specified when adding a subject");
          }
          String difficulty = addDifficultyMap.getOrDefault(change.getSubjectId(), "medium");
          LearningPathSubject newSubject = LearningPathSubject.builder()
              .learningPathId(learningPathId)
              .learningPathSectionId(change.getTargetSectionId())
              .curriculumSubjectId(change.getCurriculumSubjectId())
              .subjectId(change.getSubjectId())
              .subjectCode(change.getSubjectCode() != null ? change.getSubjectCode() : "")
              .subjectName(change.getSubjectName() != null ? change.getSubjectName() : "")
              .credits(change.getCredits() != null ? change.getCredits() : 0)
              .difficultyLevel(difficulty)
              .isCompleted(null)
              .prerequisitesGraph(List.of())
              .build();
          subjectsToSave.add(newSubject);
          existingSubjectIds.add(change.getSubjectId());
        }
        case "REMOVE" -> {
          List<LearningPathSubject> toRemove = subjectsBySubjectId.getOrDefault(change.getSubjectId(), List.of())
              .stream()
              .filter(s -> !Boolean.TRUE.equals(s.getIsCompleted()))
              .toList();
          if (toRemove.isEmpty()) {
            throw new IllegalArgumentException("Subject not found in learning path: " + change.getSubjectId());
          }
          subjectsToRemove.addAll(toRemove);
          existingSubjectIds.remove(change.getSubjectId());
        }
        case "MOVE" -> {
          if (change.getTargetSectionId() == null) {
            throw new IllegalArgumentException("Target section must be specified when moving a subject");
          }
          List<LearningPathSubject> toMove = subjectsBySubjectId.getOrDefault(change.getSubjectId(), List.of())
              .stream()
              .filter(s -> !Boolean.TRUE.equals(s.getIsCompleted()))
              .toList();
          if (toMove.isEmpty()) {
            throw new IllegalArgumentException("Subject not found in learning path: " + change.getSubjectId());
          }
          for (LearningPathSubject subject : toMove) {
            subject.setLearningPathSectionId(change.getTargetSectionId());
            subjectsToSave.add(subject);
          }
        }
        default -> throw new IllegalArgumentException("Invalid action: " + change.getAction());
      }
    }

    if (!subjectsToRemove.isEmpty()) {
      learningPathSubjectRepository.deleteAll(subjectsToRemove);
    }
    if (!subjectsToSave.isEmpty()) {
      learningPathSubjectRepository.saveAll(subjectsToSave);
    }

    recalculateSectionCredits(learningPathId);
    return assembler.toPathResponse(path, studentId);
  }

  private void recalculateSectionCredits(UUID learningPathId) {
    List<LearningPathSection> sections = learningPathSectionRepository.findByLearningPathId(learningPathId);
    List<LearningPathSubject> subjects = learningPathSubjectRepository.findByLearningPathId(learningPathId);

    Map<UUID, Integer> creditsBySection = new HashMap<>();
    for (LearningPathSubject subject : subjects) {
      creditsBySection.merge(subject.getLearningPathSectionId(), subject.getCredits(), Integer::sum);
    }

    for (LearningPathSection section : sections) {
      section.setTotalCredits(creditsBySection.getOrDefault(section.getLearningPathSectionId(), 0));
    }
    learningPathSectionRepository.saveAll(sections);
  }



  private CurriculumContext resolveCurriculumContext(UUID studentId) {
    LearningGoal goal = learningGoalRepository.findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(studentId)
        .orElseThrow(() -> new EntityNotFoundException("Active learning goal not found for studentId=" + studentId));
    UUID specializationId;
    try {
      specializationId = UUID.fromString(goal.getSpecializationId());
    } catch (Exception ex) {
      throw new IllegalArgumentException("Invalid specializationId in active learning goal");
    }
    StudentLearningProgressResponse progress = courseManagementClient.getStudentProgress(studentId, specializationId);
    Integer intakeYear = progress != null && progress.getProgramInfo() != null
        ? progress.getProgramInfo().getCurriculumYear() : null;
    CurriculumResolutionResponse curriculum = courseManagementClient.resolveCurriculum(specializationId, intakeYear);
    return new CurriculumContext(goal, specializationId, progress, curriculum);
  }

  @Override
  @Transactional(readOnly = true)
  public List<LearningPathSubjectResponse> searchSubjects(UUID studentId, UUID learningPathId, String keyword) {
    getActiveOwnedPath(studentId, learningPathId);

    List<LearningPathSubject> pathSubjects = learningPathSubjectRepository.findByLearningPathId(learningPathId);
    Set<UUID> pathSubjectIds = pathSubjects.stream().map(LearningPathSubject::getSubjectId).collect(Collectors.toSet());

    var searchResults = courseManagementClient.searchSubjects(keyword != null ? keyword : "");

    var filteredResults = searchResults.stream()
        .filter(s -> !pathSubjectIds.contains(s.getId()))
        .limit(10)
        .toList();

    Map<UUID, String> difficultyMap = assembler.fetchBatchDifficultyByIds(filteredResults.stream().map(
        SubjectResponse::getId).toList());

    return filteredResults.stream()
        .map(s -> LearningPathSubjectResponse.builder()
            .learningPathSubjectId(null)
            .learningPathId(learningPathId)
            .learningPathSectionId(null)
            .subjectId(s.getId())
            .subjectCode(s.getCode() != null ? s.getCode() : "")
            .subjectName(s.getName() != null ? s.getName() : "")
            .credits(s.getCredits() != null ? s.getCredits() : 0)
            .difficultyLevel(difficultyMap.getOrDefault(s.getId(), "medium"))
            .isCompleted(null)
            .prerequisitesGraph(List.of())
            .build())
        .toList();
  }

  @Override
  public LearningPathSyncProgressResponse syncLearningPathProgress(UUID studentId, UUID learningPathId) {
    log.info("Syncing learning path progress for studentId={}, learningPathId={}", studentId, learningPathId);

    LearningPath path = getActiveOwnedPath(studentId, learningPathId);

    // 1. Batch read all learning path subjects
    List<LearningPathSubject> allSubjects = learningPathSubjectRepository.findByLearningPathId(learningPathId);

    // 2. Batch read all enrollments with subject IDs (single Feign call)
    List<StudentEnrollmentWithSubjectResponse> enrollments;
    try {
      enrollments = learningServiceClient.getStudentEnrollmentsWithSubjects(studentId);
    } catch (Exception e) {
      log.warn("Failed to fetch enrollments from learning-service: {}", e.getMessage());
      enrollments = List.of();
    }

    if (enrollments.isEmpty()) {
      log.info("No enrollments found for student, returning path as-is");
      return LearningPathSyncProgressResponse.builder()
          .path(assembler.toPathResponse(path, studentId))
          .failedSubjects(List.of())
          .build();
    }

    // 3. Group enrollments by subjectId, sorted by attemptNo
    Map<UUID, List<StudentEnrollmentWithSubjectResponse>> enrollmentsBySubject = enrollments.stream()
        .filter(e -> e.getSubjectId() != null)
        .collect(Collectors.groupingBy(
            StudentEnrollmentWithSubjectResponse::getSubjectId,
            Collectors.collectingAndThen(
                Collectors.toList(),
                list -> list.stream()
                    .sorted(Comparator.comparing(
                        StudentEnrollmentWithSubjectResponse::getAttemptNo,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList())));

    // 4. Group LP subjects by subjectId
    Map<UUID, List<LearningPathSubject>> lpSubjectsBySubjectId = allSubjects.stream()
        .collect(Collectors.groupingBy(LearningPathSubject::getSubjectId));

    // Pre-fetch all sections to avoid N+1 inside the matching loop
    Map<UUID, LearningPathSection> sectionMap = learningPathSectionRepository.findByLearningPathId(learningPathId)
        .stream()
        .collect(Collectors.toMap(LearningPathSection::getLearningPathSectionId, s -> s));

    List<LearningPathSubject> subjectsToSave = new ArrayList<>();
    List<FailedSubjectInfo> failedSubjects = new ArrayList<>();

    // 5. Match by exact attemptNo
    for (Map.Entry<UUID, List<LearningPathSubject>> entry : lpSubjectsBySubjectId.entrySet()) {
      UUID subjectId = entry.getKey();
      List<LearningPathSubject> lpSubjects = entry.getValue();
      List<StudentEnrollmentWithSubjectResponse> subjectEnrollments = enrollmentsBySubject.getOrDefault(subjectId, List.of());

      // Separate LP subjects with known attemptNo from planned (null) ones
      Map<Integer, LearningPathSubject> lpByAttemptNo = new HashMap<>();
      List<LearningPathSubject> plannedLpSubjects = new ArrayList<>();
      for (LearningPathSubject lp : lpSubjects) {
        if (lp.getAttemptNo() != null) {
          lpByAttemptNo.put(lp.getAttemptNo(), lp);
        } else {
          plannedLpSubjects.add(lp);
        }
      }

      // Match each enrollment by exact attemptNo to an LP subject
      for (StudentEnrollmentWithSubjectResponse enrollment : subjectEnrollments) {
        Integer enrollmentAttemptNo = enrollment.getAttemptNo();
        LearningPathSubject lpSubject = lpByAttemptNo.get(enrollmentAttemptNo);

        if (lpSubject != null) {
          updateSubjectFromEnrollment(lpSubject, enrollment);
          subjectsToSave.add(lpSubject);
        } else if (!plannedLpSubjects.isEmpty()) {
          // No exact match — only consume a planned slot if the enrollment passed
          Boolean isPassed = evaluateIsCompleted(enrollment);
          if (Boolean.TRUE.equals(isPassed)) {
            LearningPathSubject planned = plannedLpSubjects.removeFirst();
            updateSubjectFromEnrollment(planned, enrollment);
            subjectsToSave.add(planned);
          }
          // If failed, leave planned slot as-is (it's the retake)
        }
        // else: enrollment has no matching LP subject and no planned slot — ignored
      }

      // Build failed subjects: only if no passed enrollment AND no planned retake remains
      boolean anyPassed = subjectEnrollments.stream()
          .anyMatch(e -> Boolean.TRUE.equals(evaluateIsCompleted(e)));

      if (!anyPassed && plannedLpSubjects.isEmpty()) {
        for (StudentEnrollmentWithSubjectResponse enrollment : subjectEnrollments) {
          Boolean isPassed = evaluateIsCompleted(enrollment);
          if (Boolean.FALSE.equals(isPassed) && enrollment.getFinalGrade() != null) {
            LearningPathSubject matchedLp = lpByAttemptNo.get(enrollment.getAttemptNo());
            LearningPathSection section = matchedLp != null
                ? sectionMap.get(matchedLp.getLearningPathSectionId())
                : null;
            failedSubjects.add(FailedSubjectInfo.builder()
                .subjectId(subjectId)
                .subjectCode(matchedLp != null ? matchedLp.getSubjectCode() : "")
                .subjectName(matchedLp != null ? matchedLp.getSubjectName() : "")
                .semesterOrder(section != null ? section.getSemesterOrder() : null)
                .completionGrade(enrollment.getFinalGrade())
                .attemptNo(enrollment.getAttemptNo())
                .build());
          }
        }
      }
    }

    // 6. Batch save
    if (!subjectsToSave.isEmpty()) {
      learningPathSubjectRepository.saveAll(subjectsToSave);
    }

    // 7. Recalculate completion rate in-memory
    int totalCredits = allSubjects.stream().mapToInt(LearningPathSubject::getCredits).sum();
    int completedCredits = allSubjects.stream()
        .filter(s -> Boolean.TRUE.equals(s.getIsCompleted()))
        .mapToInt(LearningPathSubject::getCredits)
        .sum();
    if (totalCredits > 0) {
      path.setCompletionRate(BigDecimal.valueOf(completedCredits * 100.0 / totalCredits));
    }
    learningPathRepository.save(path);

    log.info("Synced {} subjects, {} failed", subjectsToSave.size(), failedSubjects.size());
    return LearningPathSyncProgressResponse.builder()
        .path(assembler.toPathResponse(path, studentId))
        .failedSubjects(failedSubjects)
        .build();
  }

  private Boolean evaluateIsCompleted(StudentEnrollmentWithSubjectResponse enrollment) {
    return SubjectPassUtil.evaluateIsPassed(
        enrollment.getIsPassed(), enrollment.getFinalGrade(), enrollment.getGradingType());
  }

  private void updateSubjectFromEnrollment(LearningPathSubject lpSubject, StudentEnrollmentWithSubjectResponse enrollment) {
    Boolean isCompleted = evaluateIsCompleted(enrollment);
    if (isCompleted != null) {
      lpSubject.setIsCompleted(isCompleted);
    }
    lpSubject.setCompletionGrade(enrollment.getFinalGrade() != null ? BigDecimal.valueOf(enrollment.getFinalGrade()) : null);
    lpSubject.setAttemptNo(enrollment.getAttemptNo());
    if (enrollment.getCompletionTime() != null && !enrollment.getCompletionTime().isBlank()) {
      try {
        lpSubject.setCompletionDate(LocalDateTime.parse(enrollment.getCompletionTime()));
      } catch (Exception ex) {
        log.debug("Unable to parse completionTime: {}", enrollment.getCompletionTime());
      }
    }
  }

  private LearningPath getActiveOwnedPath(UUID studentId, UUID learningPathId) {
    return learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(learningPathId, studentId)
        .orElseThrow(() -> new EntityNotFoundException("Active learning path not found with id: " + learningPathId));
  }

  private LearningPath getOwnedPath(UUID studentId, UUID learningPathId) {
    return learningPathRepository.findByLearningPathIdAndStudentId(learningPathId, studentId)
        .orElseThrow(() -> new EntityNotFoundException("Learning path not found with id: " + learningPathId));
  }
}