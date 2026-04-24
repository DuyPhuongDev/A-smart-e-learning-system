package com.hcmut.lms.personalization.application.service.impl.learning_path;

import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService.SemesterSlot;
import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService.SubjectCandidate;
import com.hcmut.lms.personalization.application.service.impl.support.SemesterClassifier;
import com.hcmut.lms.personalization.application.service.impl.support.SubjectCreditUtil;
import com.hcmut.lms.personalization.application.service.impl.validation.StudentProgressDataService;
import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.*;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPath;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSection;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSubject;
import com.hcmut.lms.personalization.domain.entity.learningPath.PrerequisiteNode;
import com.hcmut.lms.personalization.repository.LearningPathRepository;
import com.hcmut.lms.personalization.repository.LearningPathSectionRepository;
import com.hcmut.lms.personalization.repository.LearningPathSubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningPathPersistenceService {

  private final LearningServiceClient learningServiceClient;
  private final LearningPathRepository learningPathRepository;
  private final LearningPathSectionRepository learningPathSectionRepository;
  private final LearningPathSubjectRepository learningPathSubjectRepository;

  public LearningPath buildAndPersistLearningPath(
      UUID studentId,
      UUID learningGoalId,
      String curriculumCode,
      List<SemesterSlot> schedule,
      String riskLevel,
      StudentProgressDataService.StudentProgressData progressData,
      int effectiveCompletedCredits,
      List<SemesterResponse> pastSemesters) {

    int scheduledCredits = schedule.stream().mapToInt(SemesterSlot::getTotalCredits).sum();
    int completedCredits = Math.max(0, effectiveCompletedCredits);
    int learningPathTotalCredits = scheduledCredits + completedCredits;

    Map<UUID, Double> predictedGradesBySubjectId = buildPredictedGradesMap(schedule, studentId);
    BigDecimal predictedGpa = calculatePredictedGpaFromMap(schedule, predictedGradesBySubjectId, progressData);

    LearningPath path = LearningPath.builder()
        .studentId(studentId)
        .learningGoalId(learningGoalId)
        .curriculumCode(curriculumCode)
        .totalCredits(learningPathTotalCredits)
        .estimatedDurationSemesters(schedule.size())
        .predictedGpa(predictedGpa)
        .completionRate(BigDecimal.ZERO)
        .riskLevel(riskLevel)
        .isActive(true)
        .build();

    LearningPath savedPath = learningPathRepository.save(path);
    log.info("Saved learning path with id={}", savedPath.getLearningPathId());

    persistSectionsAndSubjects(savedPath, schedule, progressData, predictedGradesBySubjectId, pastSemesters);

    return savedPath;
  }

  private void persistSectionsAndSubjects(
      LearningPath path, List<SemesterSlot> schedule,
      StudentProgressDataService.StudentProgressData progressData,
      Map<UUID, Double> predictedGradesBySubjectId,
      List<SemesterResponse> pastSemesters) {
    log.info("Persisting sections and subjects for path={}", path.getLearningPathId());

    List<LearningPathSection> sections = new ArrayList<>();
    List<LearningPathSubject> allSubjects = new ArrayList<>();

    // Build a lookup of completed subjects by (academicYearId, semesterId)
    Map<CompletedSectionKey, List<StudentProgressDataService.CompletedSubjectDetail>> completedBySectionKey =
        progressData.completedSubjects()
            .stream()
            .collect(Collectors.groupingBy(this::toCompletedSectionKey, LinkedHashMap::new, Collectors.toList()));

    // Build the full past timeline: include all past main semesters (even empty),
    // skip empty past summer semesters.
    List<PastSectionData> pastTimeline = new ArrayList<>();
    if (pastSemesters != null && !pastSemesters.isEmpty()) {
      for (SemesterResponse semester : pastSemesters) {
        CompletedSectionKey key = new CompletedSectionKey(semester.getAcademicYearId(), semester.getId());
        List<StudentProgressDataService.CompletedSubjectDetail> completed =
            completedBySectionKey.getOrDefault(key, List.of());
        boolean isSummer = SemesterClassifier.isSummerSemester(semester);

        // Skip empty summer semesters; keep all main semesters (even empty)
        if (isSummer && completed.isEmpty()) {
          continue;
        }

        pastTimeline.add(new PastSectionData(semester, completed));
      }
    } else {
      // Fallback: if no past semester data, use the old behavior (only semesters with completed subjects)
      completedBySectionKey.entrySet().stream()
          .sorted(Comparator.comparingInt(e -> resolveCompletedSemesterOrder(e.getValue())))
          .forEachOrdered(entry ->
              pastTimeline.add(new PastSectionData(null, entry.getValue())));
    }

    // Build sections from the past timeline
    Map<UUID, Integer> academicYearOrderByAcademicYearId = new LinkedHashMap<>();
    int semesterOrderCounter = 1;
    int academicYearOrderCounter = 1;

    for (PastSectionData pastSection : pastTimeline) {
      SemesterResponse semester = pastSection.semester();
      List<StudentProgressDataService.CompletedSubjectDetail> completedSubjects = pastSection.completedSubjects();

      UUID academicYearId;
      UUID semesterId;
      int academicYearOrder;

      if (semester != null) {
        academicYearId = semester.getAcademicYearId();
        semesterId = semester.getId();
        if (!academicYearOrderByAcademicYearId.containsKey(academicYearId)) {
          academicYearOrderByAcademicYearId.put(academicYearId, academicYearOrderCounter++);
        }
        academicYearOrder = academicYearOrderByAcademicYearId.get(academicYearId);
      } else {
        // Fallback: derive from completed subjects
        academicYearId = completedSubjects.stream()
            .map(StudentProgressDataService.CompletedSubjectDetail::academicYearId)
            .filter(Objects::nonNull)
            .findFirst().orElse(null);
        semesterId = completedSubjects.stream()
            .map(StudentProgressDataService.CompletedSubjectDetail::semesterId)
            .filter(Objects::nonNull)
            .findFirst().orElse(null);
        academicYearOrder = resolveCompletedAcademicYearOrder(completedSubjects);
        if (academicYearId != null && !academicYearOrderByAcademicYearId.containsKey(academicYearId)) {
          academicYearOrderByAcademicYearId.put(academicYearId, academicYearOrder);
        }
      }

      int totalCredits = completedSubjects.stream()
          .mapToInt(SubjectCreditUtil::safeCompletedCredits).sum();

      LearningPathSection section = LearningPathSection.builder()
          .learningPathId(path.getLearningPathId())
          .academicYearId(academicYearId)
          .academicYearOrder(academicYearOrder)
          .semesterId(semesterId)
          .semesterOrder(semesterOrderCounter++)
          .totalCredits(totalCredits)
          .difficultyScore(BigDecimal.ZERO)
          .build();
      sections.add(section);
    }

    int completedSemesterOrderOffset = pastTimeline.size();

    // Extend academicYearOrder map for new academic years in the schedule
    int nextYearOrder = academicYearOrderByAcademicYearId.values().stream()
        .max(Integer::compareTo)
        .orElse(0) + 1;
    for (SemesterSlot slot : schedule) {
      SemesterResponse semesterInfo = slot.getSemesterInfo();
      if (semesterInfo != null && semesterInfo.getAcademicYearId() != null
          && !academicYearOrderByAcademicYearId.containsKey(semesterInfo.getAcademicYearId())) {
        academicYearOrderByAcademicYearId.put(semesterInfo.getAcademicYearId(), nextYearOrder++);
      }
    }

    for (SemesterSlot slot : schedule) {
      SemesterResponse semesterInfo = slot.getSemesterInfo();
      if (semesterInfo == null || semesterInfo.getId() == null || semesterInfo.getAcademicYearId() == null) {
        throw new IllegalStateException("Missing semesterId/academicYearId while generating learning path sections");
      }

      int semesterOrder = slot.getSemesterOrder() + completedSemesterOrderOffset;
      int academicYearOrder = academicYearOrderByAcademicYearId.getOrDefault(
          semesterInfo.getAcademicYearId(), nextYearOrder - 1);

      LearningPathSection section = LearningPathSection.builder()
          .learningPathId(path.getLearningPathId())
          .academicYearId(semesterInfo.getAcademicYearId())
          .academicYearOrder(academicYearOrder)
          .semesterId(semesterInfo.getId())
          .semesterOrder(semesterOrder)
          .totalCredits(slot.getTotalCredits())
          .difficultyScore(calculateDifficultyScore(slot))
          .build();

      sections.add(section);
    }

    List<LearningPathSection> savedSections = learningPathSectionRepository.saveAll(sections);
    log.info("Saved {} sections ({} past, {} scheduled)", savedSections.size(), pastTimeline.size(), schedule.size());

    // Persist completed subjects from the past timeline
    for (int i = 0; i < pastTimeline.size(); i++) {
      LearningPathSection savedSection = savedSections.get(i);
      List<StudentProgressDataService.CompletedSubjectDetail> completedSubjects = pastTimeline.get(i).completedSubjects();
      if (completedSubjects == null || completedSubjects.isEmpty()) {
        continue;  // Empty past section — no subjects to persist
      }

      List<StudentProgressDataService.CompletedSubjectDetail> sortedCompleted = completedSubjects.stream()
          .sorted(Comparator.comparingInt(s -> s.studyOrder() != null ? s.studyOrder() : Integer.MAX_VALUE))
          .toList();

      for (StudentProgressDataService.CompletedSubjectDetail completedSubject : sortedCompleted) {
        int completedCredits = SubjectCreditUtil.safeCompletedCredits(completedSubject);

        LearningPathSubject subject = LearningPathSubject.builder()
            .learningPathId(path.getLearningPathId())
            .learningPathSectionId(savedSection.getLearningPathSectionId())
            .subjectId(completedSubject.subjectId())
            .subjectCode(completedSubject.subjectCode())
            .subjectName(completedSubject.subjectName())
            .credits(completedCredits)
            .difficultyLevel("easy")
            .avgPassRate(null)
            .avgGrade(
                completedSubject.grade4() != null
                    ? BigDecimal.valueOf(completedSubject.grade4())
                    : null)
            .importanceScore(BigDecimal.ZERO)
            .prerequisitesGraph(Collections.emptyList())
            .isCompleted(Boolean.TRUE.equals(completedSubject.isPassed()))
            .studyOrder(completedSubject.studyOrder())
            .completionGrade(completedSubject.grade4() != null ? BigDecimal.valueOf(completedSubject.grade4()) : null)
            .attemptNo(completedSubject.attemptNo())
            .isHighestResult(completedSubject.isHighestResult())
            .build();

        allSubjects.add(subject);
      }
    }

    // Persist scheduled (future) subjects
    for (int i = 0; i < schedule.size(); i++) {
      SemesterSlot slot = schedule.get(i);
      LearningPathSection section = savedSections.get(i + pastTimeline.size());

      int studyOrderIndex = 1;
      for (SubjectCandidate candidate : slot.getSubjects()) {
        Double predictedGrade = predictedGradesBySubjectId.get(candidate.getSubjectId());

        LearningPathSubject subject = LearningPathSubject.builder()
            .learningPathId(path.getLearningPathId())
            .learningPathSectionId(section.getLearningPathSectionId())
            .subjectId(candidate.getSubjectId())
            .subjectCode(candidate.getSubjectCode())
            .subjectName(candidate.getSubjectName())
            .credits(SubjectCreditUtil.safeCredits(candidate))
            .difficultyLevel("medium")
            .avgPassRate(null)
            .avgGrade(null)
            .importanceScore(BigDecimal.valueOf(safePriority2(candidate)))
            .prerequisitesGraph(buildPrerequisiteGraph(candidate))
            .isCompleted(false)
            .isHighestResult(true)
            .studyOrder(studyOrderIndex)
            .predictedGrade(predictedGrade != null ? BigDecimal.valueOf(predictedGrade).setScale(2, RoundingMode.HALF_UP) : null)
            .build();

        allSubjects.add(subject);
        studyOrderIndex++;
      }
    }

    learningPathSubjectRepository.saveAll(allSubjects);
    log.info("Saved {} subjects (including {} completed)", allSubjects.size(), progressData.completedSubjects().size());
  }

  private CompletedSectionKey toCompletedSectionKey(StudentProgressDataService.CompletedSubjectDetail detail) {
    if (detail.semesterId() == null || detail.academicYearId() == null) {
      throw new IllegalStateException(
          "Missing semesterId/academicYearId for completed subjectId=" + detail.subjectId());
    }
    return new CompletedSectionKey(detail.academicYearId(), detail.semesterId());
  }

  private int resolveCompletedSemesterOrder(List<StudentProgressDataService.CompletedSubjectDetail> completedSubjects) {
    return completedSubjects.stream()
        .map(StudentProgressDataService.CompletedSubjectDetail::semesterOrder)
        .filter(Objects::nonNull)
        .min(Integer::compareTo)
        .orElseGet(() -> completedSubjects.stream()
            .map(StudentProgressDataService.CompletedSubjectDetail::studyOrder)
            .filter(Objects::nonNull)
            .min(Integer::compareTo)
            .orElse(0));
  }

  private int resolveCompletedAcademicYearOrder(
      List<StudentProgressDataService.CompletedSubjectDetail> completedSubjects) {
    return completedSubjects.stream()
        .map(StudentProgressDataService.CompletedSubjectDetail::academicYearOrder)
        .filter(Objects::nonNull)
        .min(Integer::compareTo)
        .orElse(1);
  }

  private Map<UUID, Double> buildPredictedGradesMap(List<SemesterSlot> schedule, UUID studentId) {
    // Build a map of subjectId -> planned semester credits using actual schedule data
    Map<UUID, Integer> subjectCreditsMap = new HashMap<>();
    for (SemesterSlot slot : schedule) {
      for (SubjectCandidate candidate : slot.getSubjects()) {
        subjectCreditsMap.putIfAbsent(candidate.getSubjectId(), slot.getTotalCredits());
      }
    }

    List<UUID> subjectIds = schedule.stream()
        .flatMap(slot -> slot.getSubjects().stream())
        .map(SubjectCandidate::getSubjectId)
        .distinct()
        .toList();

    if (subjectIds.isEmpty()) {
      return Collections.emptyMap();
    }

    List<BatchGradePredictionRequest.GradePredictionItem> items = subjectIds.stream()
        .map(subjectId -> BatchGradePredictionRequest.GradePredictionItem.builder()
            .studentId(studentId)
            .subjectId(subjectId)
            .plannedSemesterCredits(subjectCreditsMap.getOrDefault(subjectId, 17))
            .build())
        .toList();

    try {
      BatchGradePredictionRequest request = BatchGradePredictionRequest.builder().predictions(items).build();

      BatchGradePredictionResponse response = learningServiceClient.predictGradeBatch(request);

      if (response == null || response.getPredictions() == null) {
        return Collections.emptyMap();
      }

      return response.getPredictions()
          .stream()
          .filter(pred -> pred.getCorrectedPredictedGrade() != null)
          .collect(Collectors.toMap(
              GradePredictionResponse::getSubjectId,
              GradePredictionResponse::getCorrectedPredictedGrade,
              (a, b) -> b));
    } catch (Exception e) {
      log.warn("Failed to fetch predicted grades: {}", e.getMessage());
      return Collections.emptyMap();
    }
  }

  private BigDecimal calculateDifficultyScore(SemesterSlot slot) {
    double avgPriority = slot.getSubjects().stream().mapToInt(this::safePriority1).average().orElse(5.0);

    return BigDecimal.valueOf(avgPriority / 2.0).setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal calculatePredictedGpaFromMap(List<SemesterSlot> schedule, Map<UUID, Double> gradeMap,
      StudentProgressDataService.StudentProgressData progressData) {
    double totalWeightedGrade = progressData.currentGpa4() != null
        ? progressData.currentGpa4().doubleValue() * progressData.earnedCredits()
        : 0.0;
    int gpaDenominatorCredits = progressData.earnedCredits();

    for (SemesterSlot slot : schedule) {
      for (SubjectCandidate candidate : slot.getSubjects()) {
        Double grade = gradeMap.get(candidate.getSubjectId());
        if (grade != null) {
          int credits = SubjectCreditUtil.safeCredits(candidate);
          totalWeightedGrade += grade * credits;
          gpaDenominatorCredits += credits;
        }
      }
    }

    if (gpaDenominatorCredits == 0) {
      return null;
    }
    double gpa = totalWeightedGrade / gpaDenominatorCredits;
    return BigDecimal.valueOf(gpa).setScale(2, RoundingMode.HALF_UP);
  }

  private List<PrerequisiteNode> buildPrerequisiteGraph(SubjectCandidate candidate) {
    if (candidate.getPrerequisites() == null || candidate.getPrerequisites().isEmpty()) {
      return Collections.emptyList();
    }

    return candidate.getPrerequisites()
        .stream()
        .map(prereq -> PrerequisiteNode.builder()
            .subjectId(prereq.getSubjectId())
            .subjectCode(prereq.getSubjectCode())
            .subjectName(prereq.getSubjectName())
            .required(true)
            .build())
        .collect(Collectors.toList());
  }

  private int safePriority1(SubjectCandidate candidate) {
    return candidate.getPriority1() != null ? candidate.getPriority1() : 999;
  }

  private int safePriority2(SubjectCandidate candidate) {
    return candidate.getPriority2() != null ? candidate.getPriority2() : 99;
  }

  private record CompletedSectionKey(UUID academicYearId, UUID semesterId) {
  }

  private record PastSectionData(
      SemesterResponse semester,
      List<StudentProgressDataService.CompletedSubjectDetail> completedSubjects) {
  }
}

