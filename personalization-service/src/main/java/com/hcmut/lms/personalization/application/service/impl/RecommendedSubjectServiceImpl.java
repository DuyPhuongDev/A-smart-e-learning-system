package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.dto.record.CandidateInfo;
import com.hcmut.lms.personalization.application.dto.record.CurriculumContext;
import com.hcmut.lms.personalization.application.dto.record.PreferenceCategory;
import com.hcmut.lms.personalization.application.dto.response.RecommendedSubjectResponse;
import com.hcmut.lms.personalization.application.service.RecommendedSubjectService;
import com.hcmut.lms.personalization.application.service.impl.support.IntensityCreditCapSupport;
import com.hcmut.lms.personalization.application.service.impl.support.SubjectCreditUtil;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.*;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSubject;
import com.hcmut.lms.personalization.domain.entity.occupationData.SubjectOccupationValuation;
import com.hcmut.lms.personalization.repository.LearningGoalRepository;
import com.hcmut.lms.personalization.repository.LearningPathSubjectRepository;
import com.hcmut.lms.personalization.repository.SubjectOccupationValuationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecommendedSubjectServiceImpl implements RecommendedSubjectService {
  private final LearningServiceClient learningServiceClient;
  private final SubjectOccupationValuationRepository subjectOccupationValuationRepository;
  private final CourseManagementClient courseManagementClient;
  private final LearningGoalRepository learningGoalRepository;
  private final LearningPathSubjectRepository learningPathSubjectRepository;

  private static final String MAJOR_SECTION_KEY = normalizeSectionName("Chuyên ngành");
  private static final String FREE_ELECTIVE_SECTION_KEY = normalizeSectionName("Tự chọn tự do");
  private static final String MANAGEMENT_SECTION_KEY = normalizeSectionName("Quản lý");

  private static String normalizeSectionName(String sectionName) {
    if (sectionName == null) return "";
    String normalized = Normalizer.normalize(sectionName, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    return normalized.trim().toLowerCase(Locale.ROOT);
  }

  private static boolean isRecommendableSection(String normalizedSectionName) {
    return normalizedSectionName.contains(MAJOR_SECTION_KEY)
        || normalizedSectionName.equals(FREE_ELECTIVE_SECTION_KEY)
        || normalizedSectionName.contains(MANAGEMENT_SECTION_KEY);
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
  public List<RecommendedSubjectResponse> getRecommendedSubjects(UUID studentId, UUID learningPathId) {
    CurriculumContext ctx = resolveCurriculumContext(studentId);

    List<LearningPathSubject> pathSubjects = learningPathSubjectRepository.findByLearningPathId(learningPathId);

    // Completed subjects: passed anywhere (in path or in student progress)
    Set<UUID> completedSubjectIds = new HashSet<>();
    pathSubjects.stream()
        .filter(s -> Boolean.TRUE.equals(s.getIsCompleted()))
        .map(LearningPathSubject::getSubjectId)
        .forEach(completedSubjectIds::add);

    if (ctx.progress() != null && ctx.progress().getSections() != null) {
      for (var section : ctx.progress().getSections()) {
        if (section.getSubjects() != null) {
          for (var subj : section.getSubjects()) {
            if (Boolean.TRUE.equals(subj.getIsPassed()) && subj.getSubjectId() != null) {
              try { completedSubjectIds.add(UUID.fromString(subj.getSubjectId())); }
              catch (IllegalArgumentException ignored) {}
            }
          }
        }
      }
    }

    // Failed subjects in path: isHighestResult=true AND isCompleted=false AND attempted (not future)
    Set<UUID> failedSubjectIds = pathSubjects.stream()
        .filter(s -> Boolean.TRUE.equals(s.getIsHighestResult()) && !Boolean.TRUE.equals(s.getIsCompleted()) && s.getAttemptNo() != null)
        .map(LearningPathSubject::getSubjectId)
        .collect(Collectors.toSet());
    Map<UUID, LearningPathSubject> failedSubjectMap = pathSubjects.stream()
        .filter(s -> Boolean.TRUE.equals(s.getIsHighestResult()) && !Boolean.TRUE.equals(s.getIsCompleted()) && s.getAttemptNo() != null)
        .collect(Collectors.toMap(LearningPathSubject::getSubjectId, s -> s, (a, b) -> a));

    // Exclude both completed and failed subjects from normal candidate flow
    Set<UUID> excludedFromCandidates = new HashSet<>(completedSubjectIds);
    excludedFromCandidates.addAll(failedSubjectIds);

    if (ctx.curriculum() == null || ctx.curriculum().getCode() == null) {
      return List.of();
    }

    CurriculumFullResponse curriculumFull = courseManagementClient.getCurriculumFull(ctx.curriculum().getCode());
    if (curriculumFull == null || curriculumFull.getSections() == null) {
      return List.of();
    }

    // Build per-section credit threshold from progress data
    Map<String, int[]> sectionCreditThreshold = new HashMap<>();
    if (ctx.progress() != null && ctx.progress().getSections() != null) {
      for (var section : ctx.progress().getSections()) {
        String normalized = normalizeSectionName(section.getSectionName());
        sectionCreditThreshold.put(normalized, new int[]{
            section.getCompletedCredits() != null ? section.getCompletedCredits() : 0,
            section.getRequiredCredits() != null ? section.getRequiredCredits() : 0
        });
      }
    }

    // Build candidate list: only from recommendable sections (Quản lý, Chuyên ngành, Tự chọn tự do)
    // and subjects already in the learning path that haven't been passed
    List<CandidateInfo> candidates = new ArrayList<>();
    for (CurriculumFullResponse.CurriculumSectionFull section : curriculumFull.getSections()) {
      if (section.getSubjects() == null) continue;
      String normalizedSection = normalizeSectionName(section.getSectionName());

      // Only recommend subjects from specific sections
      if (!isRecommendableSection(normalizedSection)) continue;

      // Skip sections that have already met their credit threshold (unless always-allowed)
      if (!normalizedSection.equals(FREE_ELECTIVE_SECTION_KEY)) {
        int[] credits = sectionCreditThreshold.get(normalizedSection);
        if (credits != null && credits[0] >= credits[1]) continue;
      }

      int sectionWeight = section.getPriorityWeight() != null ? section.getPriorityWeight() : 5;
      for (CurriculumFullResponse.CurriculumSubjectFull subject : section.getSubjects()) {
        if (excludedFromCandidates.contains(subject.getSubjectId())) continue;
        int priority1 = calculatePriority1(subject.getRecommendedYear(), subject.getRecommendedSemesterInYear());
        candidates.add(new CandidateInfo(
            subject.getSubjectId(), subject.getSubjectCode(), subject.getSubjectName(),
            SubjectCreditUtil.safeCredits(subject.getCredits()),
            subject.getIsRequired(), priority1, sectionWeight));
      }
    }

    // Also include subjects already in the learning path that haven't been passed
    Set<UUID> candidateSubjectIds = candidates.stream()
        .map(CandidateInfo::subjectId).collect(Collectors.toSet());
    for (LearningPathSubject ps : pathSubjects) {
      if (completedSubjectIds.contains(ps.getSubjectId())) continue;
      if (failedSubjectIds.contains(ps.getSubjectId())) continue;
      if (candidateSubjectIds.contains(ps.getSubjectId())) continue;
      candidates.add(new CandidateInfo(
          ps.getSubjectId(), ps.getSubjectCode(), ps.getSubjectName(),
          ps.getCredits() != null ? ps.getCredits() : 0,
          true, 999, 5));
    }

    if (candidates.isEmpty() && failedSubjectIds.isEmpty()) return List.of();

    // Build score maps for occupation and GPA categories
    Map<UUID, Double> occupationScores = buildOccupationScoreMap(candidates, ctx.goal());
    Map<UUID, Double> rawPredictedGrades = new HashMap<>();
    Map<UUID, Double> gpaScores = buildPredictedGradeScoreMap(candidates, studentId, ctx.goal(), rawPredictedGrades);

    // Determine preference order (lower value = higher priority)
    int occupationOrder = ctx.goal().getFocusOnTargetOccupation() != null ? ctx.goal().getFocusOnTargetOccupation() : Integer.MAX_VALUE;
    int gpaOrder = ctx.goal().getAttemptTargetGpaOrder() != null ? ctx.goal().getAttemptTargetGpaOrder() : Integer.MAX_VALUE;

    List<PreferenceCategory> categories = List.of(
        new PreferenceCategory("occupation", occupationOrder, occupationScores),
        new PreferenceCategory("gpa", gpaOrder, gpaScores)
    );
    categories = categories.stream()
        .sorted(Comparator.comparingInt(PreferenceCategory::order))
        .toList();

    // Select top 5 per category, excluding already-selected subjects
    List<RecommendedSubjectResponse> recommendations = new ArrayList<>();
    Set<UUID> selectedIds = new HashSet<>();

    for (PreferenceCategory category : categories) {
      List<CandidateInfo> ranked = candidates.stream()
          .filter(c -> !selectedIds.contains(c.subjectId()))
          .sorted((a, b) -> {
            double scoreA = category.scores().getOrDefault(a.subjectId(), 0.0)
                - (a.priority1() * 0.001 + a.sectionWeight() * 0.0001);
            double scoreB = category.scores().getOrDefault(b.subjectId(), 0.0)
                - (b.priority1() * 0.001 + b.sectionWeight() * 0.0001);
            return Double.compare(scoreB, scoreA);
          })
          .limit(5)
          .toList();

      String reason = switch (category.key()) {
        case "occupation" -> "Phù hợp với định hướng kiến thức chuyên sâu & nghề nghiệp";
        case "gpa" -> "Phù hợp để đạt GPA mong muốn";
        default -> "Phù hợp với mục tiêu học tập";
      };

      for (CandidateInfo c : ranked) {
        double finalScore = category.scores().getOrDefault(c.subjectId(), 0.0)
            - (c.priority1() * 0.001 + c.sectionWeight() * 0.0001);
        Double predictedGrade = rawPredictedGrades.get(c.subjectId());
        recommendations.add(RecommendedSubjectResponse.builder()
            .subjectId(c.subjectId())
            .subjectCode(c.subjectCode())
            .subjectName(c.subjectName())
            .credits(c.credits())
            .recommendationReason(reason)
            .importanceScore(BigDecimal.valueOf(Math.max(finalScore, 0)).setScale(2, RoundingMode.HALF_UP))
            .preferenceCategory(category.key())
            .predictedGrade(predictedGrade)
            .build());
        selectedIds.add(c.subjectId());
      }
    }

    // Add retake recommendations for failed subjects (highest priority)
    for (UUID failedId : failedSubjectIds) {
      if (completedSubjectIds.contains(failedId)) continue;
      LearningPathSubject fs = failedSubjectMap.get(failedId);
      recommendations.addFirst(RecommendedSubjectResponse.builder()
          .subjectId(fs.getSubjectId())
          .subjectCode(fs.getSubjectCode())
          .subjectName(fs.getSubjectName())
          .credits(fs.getCredits())
          .recommendationReason("Học lại môn đã rớt")
          .importanceScore(BigDecimal.valueOf(99.99).setScale(2, RoundingMode.HALF_UP))
          .preferenceCategory("retake")
          .build());
      selectedIds.add(failedId);
    }

    return recommendations;
  }

  private int calculatePriority1(Integer recommendedYear, Integer recommendedSemester) {
    if (recommendedYear == null || recommendedSemester == null) return Integer.MAX_VALUE;
    return 2 * recommendedYear - 1 + recommendedSemester;
  }

  private Map<UUID, Double> normalizeScores(Map<UUID, Double> rawScores) {
    if (rawScores.isEmpty()) return rawScores;
    double maxScore = rawScores.values().stream()
        .mapToDouble(Double::doubleValue)
        .max()
        .orElse(1.0);
    if (maxScore <= 0) maxScore = 1.0;
    Map<UUID, Double> normalized = new HashMap<>();
    for (var entry : rawScores.entrySet()) {
      double score = (entry.getValue() / maxScore) * 100.0;
      normalized.put(entry.getKey(), score);
    }
    return normalized;
  }

  private Map<UUID, Double> buildOccupationScoreMap(List<CandidateInfo> candidates, LearningGoal goal) {
    List<UUID> subjectIds = candidates.stream().map(CandidateInfo::subjectId).toList();
    if (subjectIds.isEmpty()) return Map.of();

    Map<UUID, BigDecimal> valuationMap;
    if (goal.getTargetOccupationCode() != null && !goal.getTargetOccupationCode().isBlank()) {
      valuationMap = subjectOccupationValuationRepository.findByTargetOccupationCode(goal.getTargetOccupationCode())
          .stream()
          .collect(Collectors.toMap(
              SubjectOccupationValuation::getSubjectId,
              SubjectOccupationValuation::getTotalValue, (v1, v2) -> v1.compareTo(v2) > 0 ? v1 : v2));
    } else {
      valuationMap = subjectOccupationValuationRepository.findBySubjectIdIn(subjectIds)
          .stream()
          .collect(Collectors.groupingBy(SubjectOccupationValuation::getSubjectId,
              Collectors.collectingAndThen(
                  Collectors.averagingDouble(v -> v.getTotalValue().doubleValue()),
                  BigDecimal::new)));
    }

    Map<UUID, Double> rawScores = new HashMap<>();
    for (CandidateInfo c : candidates) {
      double rawScore = valuationMap.getOrDefault(c.subjectId(), BigDecimal.ZERO).doubleValue();
      rawScores.put(c.subjectId(), rawScore);
    }
    return normalizeScores(rawScores);
  }

  private Map<UUID, Double> buildPredictedGradeScoreMap(List<CandidateInfo> candidates, UUID studentId, LearningGoal goal, Map<UUID, Double> rawPredictedGrades) {
    List<UUID> subjectIds = candidates.stream().map(CandidateInfo::subjectId).toList();
    if (subjectIds.isEmpty()) return Map.of();

    int mainCreditCap = IntensityCreditCapSupport.mainSemesterCapStrict(goal.getPrefMainSemLearnIntensity());

    List<BatchGradePredictionRequest.GradePredictionItem> items = subjectIds.stream()
        .map(subjectId -> BatchGradePredictionRequest.GradePredictionItem.builder()
            .studentId(studentId)
            .subjectId(subjectId)
            .plannedSemesterCredits(mainCreditCap)
            .build())
        .toList();

    try {
      BatchGradePredictionResponse response = learningServiceClient.predictGradeBatch(
          BatchGradePredictionRequest.builder().predictions(items).build());
      if (response == null || response.getPredictions() == null) return Map.of();
      Map<UUID, Double> rawScores = response.getPredictions().stream()
          .filter(p -> p.getCorrectedPredictedGrade() != null)
          .collect(Collectors.toMap(
              GradePredictionResponse::getSubjectId,
              GradePredictionResponse::getCorrectedPredictedGrade, (a, b) -> b));
      rawPredictedGrades.putAll(rawScores);
      return normalizeScores(rawScores);
    } catch (Exception e) {
      log.warn("Failed to fetch predicted grades for recommendations: {}", e.getMessage());
      return Map.of();
    }
  }

}
