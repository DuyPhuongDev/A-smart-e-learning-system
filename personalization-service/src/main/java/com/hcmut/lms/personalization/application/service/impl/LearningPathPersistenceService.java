package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService.SemesterSlot;
import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService.SubjectCandidate;
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
      int effectiveCompletedCredits) {

    int scheduledCredits = schedule.stream().mapToInt(SemesterSlot::getTotalCredits).sum();
    int completedCredits = Math.max(0, effectiveCompletedCredits);
    int totalCredits = scheduledCredits + completedCredits;
    BigDecimal predictedGpa = calculatePredictedGpa(schedule, studentId);

    LearningPath path = LearningPath.builder()
        .studentId(studentId)
        .learningGoalId(learningGoalId)
        .curriculumCode(curriculumCode)
        .totalCredits(totalCredits)
        .estimatedDurationSemesters(schedule.size())
        .predictedGpa(predictedGpa)
        .completionRate(BigDecimal.ZERO)
        .riskLevel(riskLevel)
        .isActive(true)
        .build();

    LearningPath savedPath = learningPathRepository.save(path);
    log.info("Saved learning path with id={}", savedPath.getLearningPathId());

    persistSectionsAndSubjects(savedPath, schedule, progressData, studentId);

    return savedPath;
  }

  private void persistSectionsAndSubjects(
      LearningPath path, List<SemesterSlot> schedule,
      StudentProgressDataService.StudentProgressData progressData, UUID studentId) {
    log.info("Persisting sections and subjects for path={}", path.getLearningPathId());

    List<LearningPathSection> sections = new ArrayList<>();
    List<LearningPathSubject> allSubjects = new ArrayList<>();

    List<Map.Entry<CompletedSectionKey, List<StudentProgressDataService.CompletedSubjectDetail>>> completedSections =
        progressData.completedSubjects()
            .stream()
            .collect(Collectors.groupingBy(this::toCompletedSectionKey, LinkedHashMap::new, Collectors.toList()))
            .entrySet()
            .stream()
            .sorted(Comparator.comparingInt(entry -> resolveCompletedSemesterOrder(entry.getValue())))
            .toList();

    for (Map.Entry<CompletedSectionKey, List<StudentProgressDataService.CompletedSubjectDetail>> completedSection :
        completedSections) {
      List<StudentProgressDataService.CompletedSubjectDetail> completedSubjects = completedSection.getValue();
      LearningPathSection section = LearningPathSection.builder()
          .learningPathId(path.getLearningPathId())
          .academicYearId(completedSection.getKey().academicYearId())
          .academicYearOrder(resolveCompletedAcademicYearOrder(completedSubjects))
          .semesterId(completedSection.getKey().semesterId())
          .semesterOrder(resolveCompletedSemesterOrder(completedSubjects))
          .totalCredits(completedSubjects.stream().mapToInt(this::safeCompletedCredits).sum())
          .difficultyScore(BigDecimal.ZERO)
          .build();
      sections.add(section);
    }

    int completedSectionCount = completedSections.size();
    int completedSemesterOrderOffset = completedSections.stream()
        .map(Map.Entry::getValue)
        .mapToInt(this::resolveCompletedSemesterOrder)
        .max()
        .orElse(0);

    for (SemesterSlot slot : schedule) {
      SemesterResponse semesterInfo = slot.getSemesterInfo();
      if (semesterInfo == null || semesterInfo.getId() == null || semesterInfo.getAcademicYearId() == null) {
        throw new IllegalStateException("Missing semesterId/academicYearId while generating learning path sections");
      }

      int semesterOrder = slot.getSemesterOrder() + completedSemesterOrderOffset;

      LearningPathSection section = LearningPathSection.builder()
          .learningPathId(path.getLearningPathId())
          .academicYearId(semesterInfo.getAcademicYearId())
          .academicYearOrder(((semesterOrder - 1) / 2) + 1)
          .semesterId(semesterInfo.getId())
          .semesterOrder(semesterOrder)
          .totalCredits(slot.getTotalCredits())
          .difficultyScore(calculateDifficultyScore(slot))
          .build();

      sections.add(section);
    }

    List<LearningPathSection> savedSections = learningPathSectionRepository.saveAll(sections);
    log.info("Saved {} sections (including {} completed)", savedSections.size(), completedSectionCount);

    for (int sectionIndex = 0; sectionIndex < completedSections.size(); sectionIndex++) {
      LearningPathSection savedCompletedSection = savedSections.get(sectionIndex);
      List<StudentProgressDataService.CompletedSubjectDetail> sortedCompleted = completedSections.get(sectionIndex)
          .getValue()
          .stream()
          .sorted(Comparator.comparingInt(s -> s.studyOrder() != null ? s.studyOrder() : Integer.MAX_VALUE))
          .toList();

      for (StudentProgressDataService.CompletedSubjectDetail completedSubject : sortedCompleted) {
        int completedCredits = safeCompletedCredits(completedSubject);

        LearningPathSubject subject = LearningPathSubject.builder()
            .learningPathId(path.getLearningPathId())
            .learningPathSectionId(savedCompletedSection.getLearningPathSectionId())
            .subjectId(completedSubject.subjectId())
            .subjectCode(completedSubject.subjectCode())
            .subjectName(completedSubject.subjectName())
            .credits(completedCredits)
            .difficultyLevel("easy")
            .avgPassRate(BigDecimal.ONE)
            .avgGrade(
                completedSubject.grade4() != null
                    ? BigDecimal.valueOf(completedSubject.grade4())
                    : BigDecimal.valueOf(7.0))
            .importanceScore(BigDecimal.ZERO)
            .prerequisitesGraph(Collections.emptyList())
            .isCompleted(true)
            .studyOrder(completedSubject.studyOrder())
            .completionGrade(completedSubject.grade4() != null ? BigDecimal.valueOf(completedSubject.grade4()) : null)
            .build();

        allSubjects.add(subject);
      }
    }

    Map<UUID, Double> predictedGradesBySubjectId = buildPredictedGradesMap(schedule, studentId);

    for (int i = 0; i < schedule.size(); i++) {
      SemesterSlot slot = schedule.get(i);
      LearningPathSection section = savedSections.get(i + completedSectionCount);

      for (SubjectCandidate candidate : slot.getSubjects()) {
        Double predictedGrade = predictedGradesBySubjectId.getOrDefault(candidate.getSubjectId(), 3.0);

        LearningPathSubject subject = LearningPathSubject.builder()
            .learningPathId(path.getLearningPathId())
            .learningPathSectionId(section.getLearningPathSectionId())
            .subjectId(candidate.getSubjectId())
            .subjectCode(candidate.getSubjectCode())
            .subjectName(candidate.getSubjectName())
            .credits(safeCredits(candidate))
            .difficultyLevel("medium")
            .avgPassRate(BigDecimal.valueOf(0.85))
            .avgGrade(BigDecimal.valueOf(7.0))
            .importanceScore(BigDecimal.valueOf(safePriority2(candidate)))
            .prerequisitesGraph(buildPrerequisiteGraph(candidate))
            .isCompleted(false)
            .predictedGrade(BigDecimal.valueOf(predictedGrade).setScale(2, RoundingMode.HALF_UP))
            .build();

        allSubjects.add(subject);
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
            .plannedSemesterCredits(17)
            .build())
        .toList();

    try {
      BatchGradePredictionRequest request = BatchGradePredictionRequest.builder().predictions(items).build();

      BatchGradePredictionResponse response = learningServiceClient.predictGradeBatch(request);

      return response.getPredictions()
          .stream()
          .collect(Collectors.toMap(
              GradePredictionResponse::getSubjectId,
              pred -> pred.getCorrectedPredictedGrade() != null ? pred.getCorrectedPredictedGrade() : 3.0));
    } catch (Exception e) {
      log.warn("Failed to fetch predicted grades: {}", e.getMessage());
      return Collections.emptyMap();
    }
  }

  private BigDecimal calculateDifficultyScore(SemesterSlot slot) {
    double avgPriority = slot.getSubjects().stream().mapToInt(this::safePriority1).average().orElse(5.0);

    return BigDecimal.valueOf(avgPriority / 2.0).setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal calculatePredictedGpa(List<SemesterSlot> schedule, UUID studentId) {
    List<UUID> allSubjectIds = schedule.stream()
        .flatMap(slot -> slot.getSubjects().stream())
        .map(SubjectCandidate::getSubjectId)
        .toList();

    if (allSubjectIds.isEmpty()) {
      return BigDecimal.valueOf(3.0).setScale(2, RoundingMode.HALF_UP);
    }

    List<BatchGradePredictionRequest.GradePredictionItem> items = allSubjectIds.stream()
        .map(subjectId -> BatchGradePredictionRequest.GradePredictionItem.builder()
            .studentId(studentId)
            .subjectId(subjectId)
            .plannedSemesterCredits(17)
            .build())
        .toList();

    BatchGradePredictionRequest request = BatchGradePredictionRequest.builder().predictions(items).build();

    try {
      BatchGradePredictionResponse response = learningServiceClient.predictGradeBatch(request);

      double totalWeightedGrade = 0.0;
      int totalCredits = 0;

      Map<UUID, Double> gradeMap = response.getPredictions()
          .stream()
          .collect(Collectors.toMap(
              GradePredictionResponse::getSubjectId,
              pred -> pred.getCorrectedPredictedGrade() != null ? pred.getCorrectedPredictedGrade() : 7.0));

      for (SemesterSlot slot : schedule) {
        for (SubjectCandidate candidate : slot.getSubjects()) {
          double grade = gradeMap.getOrDefault(candidate.getSubjectId(), 7.0);
          int credits = safeCredits(candidate);
          totalWeightedGrade += grade * credits;
          totalCredits += credits;
        }
      }

      double gpa = totalCredits > 0 ? totalWeightedGrade / totalCredits : 3.0;
      return BigDecimal.valueOf(gpa).setScale(2, RoundingMode.HALF_UP);

    } catch (Exception e) {
      log.warn("Failed to calculate predicted GPA, using default: {}", e.getMessage());
      return BigDecimal.valueOf(3.0).setScale(2, RoundingMode.HALF_UP);
    }
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

  private int safeCredits(SubjectCandidate candidate) {
    Integer credits = candidate.getCredits();
    if (credits == null) {
      return 0;
    }
    if (credits < 0) {
      throw new IllegalStateException(
          "Invalid subject credits while persisting path: subjectId="
              + candidate.getSubjectId()
              + ", subjectCode="
              + candidate.getSubjectCode()
              + ", credits="
              + credits);
    }
    return credits;
  }

  private int safeCompletedCredits(StudentProgressDataService.CompletedSubjectDetail subject) {
    Integer credits = subject.credits();
    if (credits == null) {
      return 0;
    }
    if (credits < 0) {
      throw new IllegalStateException(
          "Invalid completed subject credits while persisting path: subjectId="
              + subject.subjectId()
              + ", subjectCode="
              + subject.subjectCode()
              + ", credits="
              + credits);
    }
    return credits;
  }

  private int safePriority1(SubjectCandidate candidate) {
    return candidate.getPriority1() != null ? candidate.getPriority1() : 999;
  }

  private int safePriority2(SubjectCandidate candidate) {
    return candidate.getPriority2() != null ? candidate.getPriority2() : 99;
  }

  private record CompletedSectionKey(UUID academicYearId, UUID semesterId) {
  }
}

