package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.BatchClassDatasetLookupRequest;
import com.hcmut.lms.learning.client.dto.ClassSectionDatasetResponse;
import com.hcmut.lms.learning.client.dto.SemesterResponse;
import com.hcmut.lms.learning.client.dto.SubjectPrerequisiteMapResponse;
import com.hcmut.lms.learning.client.dto.SubjectWindowDatasetLookupRequest;
import com.hcmut.lms.learning.dto.internal.ExtractedFeatures;
import com.hcmut.lms.learning.entity.enrollment.Enrollment;
import com.hcmut.lms.learning.entity.semester.GradeSemesterMetrics;
import com.hcmut.lms.learning.entity.subject.SubjectSemesterMetrics;
import com.hcmut.lms.learning.mapper.FeatureExtractionMapper;
import com.hcmut.lms.learning.repository.EnrollmentRepository;
import com.hcmut.lms.learning.service.FeatureExtractionService;
import com.hcmut.lms.learning.service.GradeSemesterMetricsService;
import com.hcmut.lms.learning.service.SubjectSemesterMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.hcmut.lms.learning.util.GradeConversionUtil.convertTo4Point;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeatureExtractionServiceImpl implements FeatureExtractionService {

  private final EnrollmentRepository enrollmentRepository;
  private final CourseManagementClient courseManagementClient;
  private final FeatureExtractionMapper featureExtractionMapper;
  private final SubjectSemesterMetricsService subjectSemesterMetricsService;
  private final GradeSemesterMetricsService gradeSemesterMetricsService;

  /** Default credits when not provided — typical full-time load at HCMUT */
  private static final int DEFAULT_SEM_CREDITS = 17;

  private static final double SHRINKAGE_K = 10.0;
  /** 3-year window for course history (30 semKey units), matching GradePredictionDatasetServiceImpl */
  private static final int WINDOW_SPAN_COURSE = 30;

  /** Cache TTL for prerequisite mapping (10 minutes) */
  private static final long PREREQ_CACHE_TTL_MS = 10 * 60 * 1000L;

  private volatile Map<UUID, Set<UUID>> prereqCache;
  private volatile long prereqCacheTimestamp = 0;

  @Override
  public Map<String, Object> extractFeatures(UUID studentId,
                                              UUID subjectId,
                                              Integer plannedSemesterCredits) {
    log.info("Extracting features for studentId={}, subjectId={}", studentId, subjectId);

    // Get current semester (includes semKey directly — no second API call needed)
    SemesterResponse currentSemester = getCurrentSemester();
    if (currentSemester == null || currentSemester.getSemKey() == null) {
      log.warn("Cannot resolve current semester or semKey, using defaults");
      Map<String, Object> defaultFeatures = buildDefaultFeatures(plannedSemesterCredits);
      if (log.isDebugEnabled()) {
        log.debug(
            "Extracted default features for studentId={}, subjectId={}: {}", studentId, subjectId,
            formatFeaturesForLog(defaultFeatures));
      }
      return defaultFeatures;
    }

    int targetSemKey = currentSemester.getSemKey();
    UUID currentSemesterId = currentSemester.getId();
    int semCredits = plannedSemesterCredits != null ? plannedSemesterCredits : DEFAULT_SEM_CREDITS;

    // Get all student enrollments with grades
    List<Enrollment> studentEnrollments = enrollmentRepository.findByStudentId(studentId)
        .stream()
        .filter(e -> e.getFinalGrade() != null)
        .toList();

    // Get metadata for all student's classes
    Map<UUID, ClassSectionDatasetResponse> classMetadataMap = getClassMetadataMap(
        studentEnrollments.stream().map(Enrollment::getClassId).toList());
    if (log.isDebugEnabled()) {
      log.debug("Found {} graded enrollments for studentId={}", studentEnrollments.size(), studentId);
    }

    // Filter enrollments before target semester
    List<Enrollment> priorEnrollments = studentEnrollments.stream().filter(e -> {
      ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
      return meta != null && meta.getSemKey() < targetSemKey;
    }).toList();

    // Attempt count for this subject (0 first attempt)
    int retakeNo = studentEnrollments.stream().filter(e -> {
      ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
      return meta != null && subjectId.equals(meta.getSubjectId());
    }).mapToInt(e -> e.getAttemptNo() != null ? e.getAttemptNo() : 0).max().orElse(0);

    // Compute student history features (credit-weighted averages on 4-point scale)
    boolean hasStudentHistory = !priorEnrollments.isEmpty();
    int numSemestersPrior = hasStudentHistory ? (int) priorEnrollments.stream()
        .map(e -> classMetadataMap.get(e.getClassId()).getSemKey())
        .distinct()
        .count() : 0;

    double cumulativeGradeAvg = 0.0;
    if (hasStudentHistory) {
      double sumWeightedGrade = 0, sumCredits = 0;
      for (Enrollment e : priorEnrollments) {
        ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
        int credits = meta != null && meta.getCredits() != null ? meta.getCredits() : 0;
        double grade4pt = convertTo4Point(e.getFinalGrade());
        sumWeightedGrade += grade4pt * credits;
        sumCredits += credits;
      }
      cumulativeGradeAvg = sumCredits > 0 ? sumWeightedGrade / sumCredits : 0.0;
    }

    double previousSemGradeAvg = 0.0;
    if (hasStudentHistory) {
      // Use the latest prior semester that has grades (handles gaps/dropped semesters)
      Integer latestPriorSemKey = priorEnrollments.stream()
          .map(e -> classMetadataMap.get(e.getClassId()))
          .filter(Objects::nonNull)
          .map(ClassSectionDatasetResponse::getSemKey)
          .filter(Objects::nonNull)
          .filter(sk -> sk < targetSemKey)
          .max(Integer::compareTo)
          .orElse(null);

      if (latestPriorSemKey != null) {
        List<Enrollment> prevSemEnrollments = priorEnrollments.stream().filter(e -> {
          ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
          return meta != null && latestPriorSemKey.equals(meta.getSemKey());
        }).toList();

        if (!prevSemEnrollments.isEmpty()) {
          double sumWG = 0, sumC = 0;
          for (Enrollment e : prevSemEnrollments) {
            ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
            int credits = meta != null && meta.getCredits() != null ? meta.getCredits() : 0;
            double grade4pt = convertTo4Point(e.getFinalGrade());
            sumWG += grade4pt * credits;
            sumC += credits;
          }
          previousSemGradeAvg = sumC > 0 ? sumWG / sumC : 0.0;
        }
      }
    }

    // Subject baseline features — use pre-computed SubjectSemesterMetrics when available
    // (consistent with dataset pipeline), otherwise compute from course history with proper shrinkage
    double subjectHistMedianSmooth;
    int subjectHistCount;
    boolean subjectHistMissing;

    Optional<SubjectSemesterMetrics> metricsOpt = subjectSemesterMetricsService
        .findBySubjectIdAndSemesterId(subjectId, currentSemesterId);

    if (metricsOpt.isPresent()) {
      SubjectSemesterMetrics metrics = metricsOpt.get();
      subjectHistMedianSmooth = metrics.getSmoothedMedian4pt();
      subjectHistCount = metrics.getSampleCount() != null ? metrics.getSampleCount() : 0;
      subjectHistMissing = metrics.getIsFallback() || subjectHistCount == 0;
      if (log.isDebugEnabled()) {
        log.debug("Using pre-computed metrics for subjectId={} semesterId={}: " +
            "smoothedMedian4pt={}, sampleCount={}, isFallback={}",
            subjectId, currentSemesterId, subjectHistMedianSmooth, subjectHistCount, metrics.getIsFallback());
      }
    } else {
      // Fallback: compute from course history with shrinkage using semester global median
      List<Enrollment> subjectHistory = getCourseHistory(subjectId, targetSemKey);
      subjectHistCount = subjectHistory.size();
      subjectHistMissing = subjectHistCount == 0;

      if (subjectHistMissing) {
        subjectHistMedianSmooth = 0.0;
      } else {
        double rawMedian = computeMedian4pt(subjectHistory);
        double semMedian4pt = getSemesterGlobalMedian4pt(currentSemesterId);
        subjectHistMedianSmooth = (rawMedian * subjectHistCount + semMedian4pt * SHRINKAGE_K)
            / (subjectHistCount + SHRINKAGE_K);
      }
      if (log.isDebugEnabled()) {
        log.debug("Computed course history for subjectId={} semesterId={}: " +
            "histCount={}, histMissing={}", subjectId, currentSemesterId, subjectHistCount, subjectHistMissing);
      }
    }

    // Relative course features (prerequisites + recommendations)
    List<UUID> relatedSubjectIds = getRelatedSubjects(subjectId);
    boolean hasRelativeCourse = !relatedSubjectIds.isEmpty();

    double relativeAvgCourseGrade = 0.0;
    boolean hasRelativeEnrollments = false;
    if (hasRelativeCourse) {
      List<Enrollment> relativeEnrollments = priorEnrollments.stream().filter(e -> {
        ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
        return meta != null && relatedSubjectIds.contains(meta.getSubjectId());
      }).toList();

      if (!relativeEnrollments.isEmpty()) {
        hasRelativeEnrollments = true;
        double sumWeightedGrade = 0, sumCredits = 0;
        for (Enrollment e : relativeEnrollments) {
          ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
          int credits = meta != null && meta.getCredits() != null ? meta.getCredits() : 0;
          double grade4pt = convertTo4Point(e.getFinalGrade());
          sumWeightedGrade += grade4pt * credits;
          sumCredits += credits;
        }
        relativeAvgCourseGrade = sumCredits > 0 ? sumWeightedGrade / sumCredits : relativeEnrollments.stream()
            .mapToDouble(e -> convertTo4Point(e.getFinalGrade()))
            .average()
            .orElse(0.0);
      }
    }

    // Fallback: when no related courses with grades, use previous semester GPA if available
    if (!hasRelativeEnrollments) {
      relativeAvgCourseGrade = previousSemGradeAvg > 0.0 ? previousSemGradeAvg : 0.0;
    }

    Map<String, Object> features = featureExtractionMapper.toFeatureMap(ExtractedFeatures.builder()
        .semCredits(semCredits)
        .retakeNo(retakeNo)
        .numSemestersPrior(numSemestersPrior)
        .cumulativeGradeAvg(cumulativeGradeAvg)
        .previousSemGradeAvg(previousSemGradeAvg)
        .subjectHistMedianSmooth(subjectHistMedianSmooth)
        .relativeAvgCourseGrade(relativeAvgCourseGrade)
        .build());

    log.info("Extracted features for studentId={}, subjectId={}: {}",
        studentId, subjectId, formatFeaturesForLog(features));

    return features;
  }

  // ---- helpers ----

  /**
   * Get the semester global median on 4-point scale for shrinkage smoothing.
   * Looks up persisted grade_semester_metrics first, falls back to default.
   */
  private double getSemesterGlobalMedian4pt(UUID semesterId) {
    return gradeSemesterMetricsService.findBySemesterId(semesterId)
        .map(m -> convertTo4Point(m.getMedianGrade()))
        .orElse(convertTo4Point(SubjectSemesterMetrics.DEFAULT_MEAN_GRADE));
  }

  /**
   * Get the current semester from course-management-service.
   * semKey is included directly in the response — no additional API calls needed.
   */
  private SemesterResponse getCurrentSemester() {
    try {
      return courseManagementClient.getCurrentSemester();
    } catch (Exception e) {
      log.error("Failed to get current semester", e);
      return null;
    }
  }

  private Map<UUID, ClassSectionDatasetResponse> getClassMetadataMap(List<UUID> classIds) {
    if (classIds.isEmpty()) return Collections.emptyMap();
    try {
      List<ClassSectionDatasetResponse> responses = courseManagementClient.getClassSectionsForDataset(
          BatchClassDatasetLookupRequest.builder().classIds(classIds).build());
      return responses.stream().collect(Collectors.toMap(ClassSectionDatasetResponse::getClassId, r -> r));
    } catch (Exception e) {
      log.error("Failed to fetch class metadata for {} classes", classIds.size(), e);
      return Collections.emptyMap();
    }
  }

  /**
   * Get all graded enrollments for a subject within the 3-year (30 semKey units) window before targetSemKey.
   * Matches the WINDOW_SPAN_COURSE logic in GradePredictionDatasetServiceImpl.
   */
  private List<Enrollment> getCourseHistory(UUID subjectId, int targetSemKey) {
    int windowLow = targetSemKey - WINDOW_SPAN_COURSE;

    List<ClassSectionDatasetResponse> subjectClasses;
    try {
      subjectClasses = courseManagementClient.getClassSectionsBySubjectWindow(
          SubjectWindowDatasetLookupRequest.builder()
              .subjectId(subjectId)
              .targetSemKey(targetSemKey)
              .windowSpan(WINDOW_SPAN_COURSE)
              .build());
    } catch (Exception e) {
      log.error("Failed to fetch subject window classes for subjectId={}", subjectId, e);
      return Collections.emptyList();
    }

    Set<UUID> classIdsForSubject = subjectClasses.stream()
        .filter(meta -> meta.getSemKey() != null
            && meta.getSemKey() >= windowLow
            && meta.getSemKey() < targetSemKey)
        .map(ClassSectionDatasetResponse::getClassId)
        .collect(Collectors.toSet());

    if (classIdsForSubject.isEmpty()) return Collections.emptyList();

    return enrollmentRepository.findByClassIdInAndFinalGradeIsNotNull(new ArrayList<>(classIdsForSubject));
  }

  private double computeMedian4pt(List<Enrollment> courseHistory) {
    List<Double> grades4pt = courseHistory.stream().map(e -> convertTo4Point(e.getFinalGrade())).sorted().toList();

    int size = grades4pt.size();
    if (size == 0) return 0.0;
    if (size % 2 == 0) {
      return (grades4pt.get(size / 2 - 1) + grades4pt.get(size / 2)) / 2.0;
    } else {
      return grades4pt.get(size / 2);
    }
  }

  /**
   * Get related subjects (prerequisites + recommendations) for a subject.
   * Uses a TTL-based cache to avoid repeated API calls.
   */
  private List<UUID> getRelatedSubjects(UUID subjectId) {
    Map<UUID, Set<UUID>> cache = getPrerequisiteMappingCache();
    return new ArrayList<>(cache.getOrDefault(subjectId, Set.of()));
  }

  /**
   * Get or refresh the prerequisite mapping cache.
   * Cache expires after PREREQ_CACHE_TTL_MS (10 minutes).
   */
  private Map<UUID, Set<UUID>> getPrerequisiteMappingCache() {
    long now = System.currentTimeMillis();
    if (prereqCache == null || (now - prereqCacheTimestamp) > PREREQ_CACHE_TTL_MS) {
      synchronized (this) {
        if (prereqCache == null || (now - prereqCacheTimestamp) > PREREQ_CACHE_TTL_MS) {
          try {
            List<SubjectPrerequisiteMapResponse> responses = courseManagementClient.getPrerequisiteMapping();
            Map<UUID, Set<UUID>> map = new HashMap<>();
            for (SubjectPrerequisiteMapResponse r : responses) {
              if (r.getSubjectId() != null && r.getRelatedSubjectIds() != null) {
                map.put(r.getSubjectId(), new HashSet<>(r.getRelatedSubjectIds()));
              }
            }
            prereqCache = map;
            prereqCacheTimestamp = now;
          } catch (Exception e) {
            log.error("Failed to fetch prerequisite mapping", e);
            return prereqCache != null ? prereqCache : Map.of();
          }
        }
      }
    }
    return prereqCache;
  }

  /**
   * Fallback when current semester cannot be resolved.
   * All features use required defaults.
   */
  private Map<String, Object> buildDefaultFeatures(Integer plannedSemesterCredits) {
    int semCredits = plannedSemesterCredits != null ? plannedSemesterCredits : DEFAULT_SEM_CREDITS;
    Map<String, Object> features = featureExtractionMapper.toFeatureMap(ExtractedFeatures.builder()
        .semCredits(semCredits)
        .retakeNo(0)
        .numSemestersPrior(0)
        .cumulativeGradeAvg(0.0)
        .previousSemGradeAvg(0.0)
        .subjectHistMedianSmooth(0.0)
        .relativeAvgCourseGrade(0.0)
        .build());
    if (log.isDebugEnabled()) {
      log.debug("Default features used (plannedSemesterCredits={}): {}", plannedSemesterCredits,
          formatFeaturesForLog(features));
    }
    return features;
  }

  private String formatFeaturesForLog(Map<String, Object> features) {
    return features.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByKey())
        .map(e -> e.getKey() + "=" + e.getValue())
        .collect(Collectors.joining(", "));
  }
}