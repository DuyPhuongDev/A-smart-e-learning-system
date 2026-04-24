package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.ClassSectionDatasetResponse;
import com.hcmut.lms.learning.client.dto.SemesterResponse;
import com.hcmut.lms.learning.entity.enrollment.Enrollment;
import com.hcmut.lms.learning.entity.semester.GradeSemesterMetrics;
import com.hcmut.lms.learning.entity.subject.SubjectSemesterMetrics;
import com.hcmut.lms.learning.repository.EnrollmentRepository;
import com.hcmut.lms.learning.repository.SubjectSemesterMetricsRepository;
import com.hcmut.lms.learning.service.GradeSemesterMetricsService;
import com.hcmut.lms.learning.service.SubjectSemesterMetricsService;
import com.hcmut.lms.learning.util.GradeConversionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectSemesterMetricsServiceImpl implements SubjectSemesterMetricsService {
  private final EnrollmentRepository enrollmentRepository;
  private final SubjectSemesterMetricsRepository metricsRepository;
  private final CourseManagementClient courseManagementClient;
  private final GradeSemesterMetricsService gradeSemesterMetricsService;

  private static final int BATCH_SIZE = 500;
  private static final int WINDOW_SPAN_COURSE = 30;
  private static final double SHRINKAGE_K = 10.0;

  // Default values for semester global stats (10-point scale)
  private static final double DEFAULT_SEMESTER_MEDIAN = SubjectSemesterMetrics.DEFAULT_MEAN_GRADE; // 6.0
  private static final double DEFAULT_SEMESTER_MEAN = SubjectSemesterMetrics.DEFAULT_MEAN_GRADE;   // 6.0
  private static final int DEFAULT_SEMESTER_SAMPLE_COUNT = 0;

  @Override
  @Transactional
  public int computeAndSaveAllMetrics() {
    log.info("Starting subject semester metrics computation");

    // Step 1: Load enrollment data (may be empty — still compute Level 2/3 defaults)
    List<Enrollment> gradedEnrollments = enrollmentRepository.findByFinalGradeIsNotNull();
    log.info("Loaded {} graded enrollments", gradedEnrollments.size());

    Map<UUID, ClassSectionDatasetResponse> classMetaMap = fetchClassMetadata(gradedEnrollments);
    List<EnrichedEnrollment> enriched = buildEnrichedEnrollments(gradedEnrollments, classMetaMap);
    log.info("Enriched {} enrollments successfully", enriched.size());

    // Step 2: Build subject grade index for O(1) window filtering
    Map<UUID, List<SubjectGrade>> gradesBySubject = buildSubjectGradeIndex(enriched);
    log.info("Built grade index for {} subjects", gradesBySubject.size());

    // Step 3: Compute per-semester global statistics from enrollment data
    Map<UUID, SemesterGlobalStats> semesterGlobalStatsFromEnrollment = computeSemesterGlobalStats(enriched);
    log.info("Computed global statistics for {} semesters from enrollment data", semesterGlobalStatsFromEnrollment.size());

    // Step 4: Get ALL subjects and ALL semesters from course-management-service
    List<UUID> allSubjectIds;
    try {
      allSubjectIds = courseManagementClient.getAllSubjectIds();
      log.info("Fetched {} subject IDs from course-management-service", allSubjectIds.size());
    } catch (Exception e) {
      log.error("Failed to fetch all subject IDs. Cannot compute metrics. Aborting: {}", e.getMessage());
      return 0;
    }

    List<SemesterResponse> allSemesters;
    try {
      allSemesters = courseManagementClient.getAllSemesters();
      log.info("Fetched {} semesters from course-management-service", allSemesters.size());
    } catch (Exception e) {
      log.error("Failed to fetch all semesters. Cannot compute metrics. Aborting: {}", e.getMessage());
      return 0;
    }

    // Filter: only semesters from the past up to the current (present) semester.
    // Future semesters have no graded data and should not have metrics.
    int currentSemKey = Integer.MAX_VALUE;
    try {
      SemesterResponse currentSemester = courseManagementClient.getCurrentSemester();
      if (currentSemester != null && currentSemester.getSemKey() != null) {
        currentSemKey = currentSemester.getSemKey();
      }
    } catch (Exception e) {
      log.warn("Failed to get current semester for filtering. Will compute for all semesters: {}", e.getMessage());
    }

    final int maxSemKey = currentSemKey;
    List<SemesterResponse> semestersToCompute = allSemesters.stream()
        .filter(s -> s.getSemKey() != null && s.getSemKey() <= maxSemKey)
        .sorted(Comparator.comparingInt(SemesterResponse::getSemKey))
        .toList();

    log.info("Filtered to {} semesters (past to present, semKey <= {})", semestersToCompute.size(), maxSemKey);

    if (allSubjectIds.isEmpty() || semestersToCompute.isEmpty()) {
      log.warn("No subjects or semesters found. Aborting metrics computation.");
      return 0;
    }

    // Step 5: Build semester global stats for all semesters to compute.
    // Semesters with enrollment data use computed stats; others use defaults.
    Map<UUID, SemesterGlobalStats> semesterGlobalStats = new HashMap<>();
    for (SemesterResponse semester : semestersToCompute) {
      int semKey = semester.getSemKey() != null ? semester.getSemKey() : 0;
      semesterGlobalStats.put(
          semester.getId(),
          semesterGlobalStatsFromEnrollment.getOrDefault(
              semester.getId(),
              new SemesterGlobalStats(DEFAULT_SEMESTER_MEDIAN, DEFAULT_SEMESTER_MEAN,
                  DEFAULT_SEMESTER_SAMPLE_COUNT, semKey)));
    }

    // Persist semester global statistics
    saveSemesterGlobalMetrics(semesterGlobalStats);

    // Step 6: Compute global subject baselines from enrollment data
    Map<UUID, GlobalSubjectBaseline> globalBaselines = computeGlobalBaselines(enriched);
    log.info("Computed global baselines for {} subjects", globalBaselines.size());

    // Step 7: Build historical metrics map from scratch.
    // We compute in chronological order and add each computed metric to this map,
    // so Level 1 carry-forward always uses the freshest data from earlier semesters.
    Map<UUID, List<SubjectSemesterMetrics>> historicalMetricsBySubject = new HashMap<>();

    // Step 8: Compute metrics for ALL (subject, semester) pairs in chronological order.
    // This ensures Level 1 carry-forward uses newly computed metrics from earlier semesters.
    List<SubjectSemesterMetrics> metricsToSave = new ArrayList<>();
    int errorCount = 0;
    int fallbackCount = 0;
    int totalPairs = allSubjectIds.size() * semestersToCompute.size();
    log.info("Computing metrics for {} subject-semester pairs ({} subjects × {} semesters)",
        totalPairs, allSubjectIds.size(), semestersToCompute.size());

    for (SemesterResponse semester : semestersToCompute) {
      UUID semesterId = semester.getId();
      int semKey = semester.getSemKey() != null ? semester.getSemKey() : 0;
      SemesterGlobalStats semStats = semesterGlobalStats.get(semesterId);

      for (UUID subjectId : allSubjectIds) {
        try {
          List<Double> windowGrades = extractWindowGrades(
              gradesBySubject.get(subjectId), semKey);

          SubjectSemesterMetrics metrics = computeMetricsWithFallback(
              subjectId, semesterId, semKey, windowGrades, semStats,
              historicalMetricsBySubject, globalBaselines);

          if (metrics.getIsFallback()) {
            fallbackCount++;
          }

          metricsToSave.add(metrics);

          // Add to historical map so Level 1 carry-forward can find it
          // for later semesters of the same subject
          historicalMetricsBySubject.computeIfAbsent(subjectId, k -> new ArrayList<>()).add(metrics);
        } catch (Exception e) {
          log.warn("Metrics computation failed for subjectId={}, semesterId={}: [{}] {}",
              subjectId, semesterId, e.getClass().getSimpleName(), e.getMessage());
          errorCount++;
        }
      }
    }

    // Step 9: Batch save all metrics
    saveBatch(metricsToSave);
    log.info("Subject semester metrics computation complete: total={}, fallback={}, errors={}",
        metricsToSave.size(), fallbackCount, errorCount);

    return metricsToSave.size();
  }

  /**
   * Compute per-semester global statistics (median, mean, sample count).
   * These are used for shrinkage smoothing in course baseline computation.
   */
  private Map<UUID, SemesterGlobalStats> computeSemesterGlobalStats(List<EnrichedEnrollment> enriched) {
    Map<UUID, List<Double>> gradesBySemester = new HashMap<>();
    Map<UUID, Integer> semKeyBySemesterId = new HashMap<>();

    for (EnrichedEnrollment e : enriched) {
      gradesBySemester.computeIfAbsent(e.semesterId(), k -> new ArrayList<>()).add(e.finalGrade());
      semKeyBySemesterId.putIfAbsent(e.semesterId(), e.semKey());
    }

    Map<UUID, SemesterGlobalStats> result = new HashMap<>();
    for (Map.Entry<UUID, List<Double>> entry : gradesBySemester.entrySet()) {
      List<Double> grades = entry.getValue();
      if (!grades.isEmpty()) {
        double mean = calculateMean(grades);
        double median = calculateMedian(grades);
        int semKey = semKeyBySemesterId.getOrDefault(entry.getKey(), 0);
        result.put(entry.getKey(), new SemesterGlobalStats(median, mean, grades.size(), semKey));
      }
    }

    return result;
  }

  /**
   * Persist semester global statistics to grade_semester_metrics table.
   * Enables on-demand lookups with closest-semester fallback.
   */
  private void saveSemesterGlobalMetrics(Map<UUID, SemesterGlobalStats> semesterGlobalStats) {
    List<GradeSemesterMetrics> entities = semesterGlobalStats.entrySet().stream()
        .map(entry -> GradeSemesterMetrics.builder()
            .semesterId(entry.getKey())
            .semKey(entry.getValue().semKey())
            .medianGrade(entry.getValue().median())
            .meanGrade(entry.getValue().mean())
            .sampleCount(entry.getValue().sampleCount())
            .build())
        .toList();
    gradeSemesterMetricsService.upsertAll(entities);
    log.info("Upserted {} semester global metrics to grade_semester_metrics", entities.size());
  }

  /**
   * Build index: subjectId -> List of (semKey, grade) sorted by semKey
   * This enables O(1) window extraction using binary search
   */
  private Map<UUID, List<SubjectGrade>> buildSubjectGradeIndex(List<EnrichedEnrollment> enriched) {
    Map<UUID, List<SubjectGrade>> index = new HashMap<>();

    for (EnrichedEnrollment e : enriched) {
      index.computeIfAbsent(e.subjectId(), k -> new ArrayList<>()).add(new SubjectGrade(e.semKey(), e.finalGrade()));
    }

    // Sort each subject's grades by semKey for binary search
    index.values().forEach(list -> list.sort(Comparator.comparingInt(SubjectGrade::semKey)));

    return index;
  }

  /**
   * Extract grades from 3-year window: [currentSemKey - 30, currentSemKey)
   * Uses binary search for O(log N) performance
   */
  private List<Double> extractWindowGrades(List<SubjectGrade> subjectGrades, int currentSemKey) {
    if (subjectGrades == null || subjectGrades.isEmpty()) {
      return List.of();
    }

    int windowLow = currentSemKey - WINDOW_SPAN_COURSE;

    // Binary search for start index (first grade >= windowLow)
    int startIdx = findFirstIndexGte(subjectGrades, windowLow);
    // Binary search for end index (first grade >= currentSemKey)
    int endIdx = findFirstIndexGte(subjectGrades, currentSemKey);

    if (startIdx >= endIdx || startIdx >= subjectGrades.size()) {
      return List.of();
    }

    return subjectGrades.subList(startIdx, endIdx).stream().map(SubjectGrade::grade).toList();
  }

  /**
   * Binary search: find first index where semKey >= target
   */
  private int findFirstIndexGte(List<SubjectGrade> sortedList, int target) {
    int left = 0;
    int right = sortedList.size();

    while (left < right) {
      int mid = (left + right) >>> 1;
      if (sortedList.get(mid).semKey() < target) {
        left = mid + 1;
      } else {
        right = mid;
      }
    }
    return left;
  }

  private SubjectSemesterMetrics computeMetricsWithFallback(
      UUID subjectId, UUID semesterId, int semKey, List<Double> windowGrades,
      SemesterGlobalStats semStats,
      Map<UUID, List<SubjectSemesterMetrics>> historicalMetricsBySubject,
      Map<UUID, GlobalSubjectBaseline> globalBaselines) {

    int sampleCount = windowGrades.size();

    // Level 0: Real data from 3-year window
    if (sampleCount > 0) {
      double mean = calculateMean(windowGrades);
      double median = calculateMedian(windowGrades);

      // Convert grades to 4-point scale for stdDev calculation
      List<Double> grades4pt = windowGrades.stream()
          .map(GradeConversionUtil::convertTo4Point)
          .toList();
      double mean4pt = calculateMean(grades4pt);
      double stdDev4pt = calculateStdDev(grades4pt, mean4pt);

      // Apply shrinkage smoothing to median
      double median4pt = GradeConversionUtil.convertTo4Point(median);
      double semMedian4pt = GradeConversionUtil.convertTo4Point(semStats.median());
      double smoothedMedian4pt = (median4pt * sampleCount + semMedian4pt * SHRINKAGE_K) / (sampleCount + SHRINKAGE_K);

      return SubjectSemesterMetrics.builder()
          .subjectId(subjectId)
          .semesterId(semesterId)
          .semKey(semKey)
          .meanGrade(mean)
          .stdDev(stdDev4pt)
          .sampleCount(sampleCount)
          .isFallback(false)
          .fallbackLevel(0)
          .semesterMedianGrade(semStats.median())
          .semesterMeanGrade(semStats.mean())
          .semesterSampleCount(semStats.sampleCount())
          .smoothedMedian4pt(smoothedMedian4pt)
          .build();
    }

    // Level 1: Temporal Carry-Forward — use the most recent metric from a STRICTLY EARLIER semester.
    // Filter: semKey < current semKey prevents circular carry-forward from the same semester
    // and ensures the carry-forward reflects genuinely prior data.
    List<SubjectSemesterMetrics> subjectMetrics = historicalMetricsBySubject.get(subjectId);
    if (subjectMetrics != null && !subjectMetrics.isEmpty()) {
      SubjectSemesterMetrics mostRecent = subjectMetrics.stream()
          .filter(m -> m.getSemKey() != null && m.getSemKey() < semKey)
          .max(Comparator.comparingInt(SubjectSemesterMetrics::getSemKey))
          .orElse(null);
      if (mostRecent != null) {
        int originalSampleCount = mostRecent.getSampleCount() != null ? mostRecent.getSampleCount() : 0;
        return createFallbackMetrics(
            subjectId, semesterId, semKey, mostRecent.getMeanGrade(), mostRecent.getStdDev(),
            originalSampleCount, false, 1,
            semStats.median(), semStats.mean(), semStats.sampleCount(), mostRecent.getSmoothedMedian4pt());
      }
    }

    // Level 2: Global Subject Baseline
    GlobalSubjectBaseline baseline = globalBaselines.get(subjectId);
    if (baseline != null && baseline.sampleCount() > 0) {
      double baselineMedian4pt = GradeConversionUtil.convertTo4Point(baseline.median());
      double semMedian4pt = GradeConversionUtil.convertTo4Point(semStats.median());
      double smoothedMedian4pt = (baselineMedian4pt * baseline.sampleCount() + semMedian4pt * SHRINKAGE_K)
          / (baseline.sampleCount() + SHRINKAGE_K);

      return createFallbackMetrics(
          subjectId, semesterId, semKey, baseline.mean(), baseline.stdDev(), baseline.sampleCount(), true, 2,
          semStats.median(), semStats.mean(), semStats.sampleCount(), smoothedMedian4pt);
    }

    // Level 3: System Defaults
    double defaultMedian4pt = GradeConversionUtil.convertTo4Point(SubjectSemesterMetrics.DEFAULT_MEAN_GRADE);
    double semMedian4pt = GradeConversionUtil.convertTo4Point(semStats.median());
    double smoothedMedian4pt = (defaultMedian4pt * 0 + semMedian4pt * SHRINKAGE_K) / (0 + SHRINKAGE_K);

    return createFallbackMetrics(
        subjectId, semesterId, semKey, SubjectSemesterMetrics.DEFAULT_MEAN_GRADE,
        SubjectSemesterMetrics.DEFAULT_STD_DEV, 0, true, 3,
        semStats.median(), semStats.mean(), semStats.sampleCount(), smoothedMedian4pt);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<SubjectSemesterMetrics> findBySubjectIdAndSemesterId(UUID subjectId, UUID semesterId) {
    return metricsRepository.findBySubjectIdAndSemesterId(subjectId, semesterId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SubjectSemesterMetrics> getMetricsHistoryForSubject(UUID subjectId) {
    return metricsRepository.findBySubjectIdOrderByCreatedAtAsc(subjectId);
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, SubjectSemesterMetrics> batchLoadMetrics(List<UUID> subjectIds, List<UUID> semesterIds) {
    if (subjectIds.isEmpty() || semesterIds.isEmpty()) {
      return Map.of();
    }

    List<SubjectSemesterMetrics> metrics = metricsRepository.findBySubjectIdsAndSemesterIds(subjectIds, semesterIds);

    return metrics.stream()
        .collect(Collectors.toMap(
            m -> m.getSubjectId() + "|" + m.getSemesterId(),
            m -> m,
            (a, b) -> a));
  }

  private Map<UUID, GlobalSubjectBaseline> computeGlobalBaselines(List<EnrichedEnrollment> enriched) {
    Map<UUID, List<Double>> gradesBySubject = enriched.stream()
        .collect(Collectors.groupingBy(
            EnrichedEnrollment::subjectId,
            Collectors.mapping(EnrichedEnrollment::finalGrade, Collectors.toList())));

    Map<UUID, GlobalSubjectBaseline> baselines = new HashMap<>();
    for (Map.Entry<UUID, List<Double>> entry : gradesBySubject.entrySet()) {
      List<Double> grades = entry.getValue();
      if (!grades.isEmpty()) {
        double mean = calculateMean(grades);
        double median = calculateMedian(grades);

        // Calculate stdDev on 4-point scale
        List<Double> grades4pt = grades.stream()
            .map(GradeConversionUtil::convertTo4Point)
            .toList();
        double mean4pt = calculateMean(grades4pt);
        double stdDev4pt = calculateStdDev(grades4pt, mean4pt);

        baselines.put(entry.getKey(), new GlobalSubjectBaseline(mean, median, stdDev4pt, grades.size()));
      }
    }
    return baselines;
  }

  private double calculateMean(List<Double> values) {
    return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
  }

  private double calculateMedian(List<Double> values) {
    if (values.isEmpty()) return 0.0;
    List<Double> sorted = values.stream().sorted().toList();
    int mid = sorted.size() / 2;
    return sorted.size() % 2 == 0 ? (sorted.get(mid - 1) + sorted.get(mid)) / 2.0 : sorted.get(mid);
  }

  /**
   * Calculate sample standard deviation (ddof=1) to match the Python worker's np.std(ddof=1).
   * Uses Bessel's correction: divides by (n-1) instead of n.
   */
  private double calculateStdDev(List<Double> values, double mean) {
    if (values.size() < 2) {
      return SubjectSemesterMetrics.DEFAULT_STD_DEV;
    }
    double sumSquaredDiff = values.stream().mapToDouble(v -> Math.pow(v - mean, 2)).sum();
    return Math.sqrt(sumSquaredDiff / (values.size() - 1));
  }

  private Map<UUID, ClassSectionDatasetResponse> fetchClassMetadata(List<Enrollment> enrollments) {
    List<UUID> classIds = enrollments.stream().map(Enrollment::getClassId).distinct().toList();

    if (classIds.isEmpty()) {
      return Map.of();
    }

    try {
      List<ClassSectionDatasetResponse> responses = courseManagementClient.getClassSectionsForDataset(
          com.hcmut.lms.learning.client.dto.BatchClassDatasetLookupRequest.builder().classIds(classIds).build());

      return responses.stream()
          .filter(r -> r.getClassId() != null)
          .collect(Collectors.toMap(ClassSectionDatasetResponse::getClassId, r -> r, (a, b) -> a));
    } catch (Exception e) {
      log.error("Failed to fetch class metadata: {}", e.getMessage());
      return Map.of();
    }
  }

  private List<EnrichedEnrollment> buildEnrichedEnrollments(
      List<Enrollment> enrollments,
      Map<UUID, ClassSectionDatasetResponse> classMetaMap) {
    return enrollments.stream().filter(e -> {
      ClassSectionDatasetResponse meta = classMetaMap.get(e.getClassId());
      return meta != null && meta.getSubjectId() != null && meta.getSemesterId() != null && meta.getSemKey() != null && e.getFinalGrade() != null;
    }).map(e -> {
      ClassSectionDatasetResponse meta = classMetaMap.get(e.getClassId());
      return new EnrichedEnrollment(
          e.getStudentId(), e.getClassId(), meta.getSubjectId(), meta.getSemesterId(), meta.getSemKey(),
          meta.getCredits() != null ? meta.getCredits() : 0, e.getFinalGrade(), meta.getCurriculumSectionId());
    }).toList();
  }

  private void saveBatch(List<SubjectSemesterMetrics> entities) {
    for (int i = 0; i < entities.size(); i += BATCH_SIZE) {
      int end = Math.min(i + BATCH_SIZE, entities.size());
      List<SubjectSemesterMetrics> batch = entities.subList(i, end);

      UUID[] subjectIds = batch.stream().map(SubjectSemesterMetrics::getSubjectId).toArray(UUID[]::new);
      UUID[] semesterIds = batch.stream().map(SubjectSemesterMetrics::getSemesterId).toArray(UUID[]::new);
      Integer[] semKeys = batch.stream().map(SubjectSemesterMetrics::getSemKey).toArray(Integer[]::new);
      Double[] meanGrades = batch.stream().map(SubjectSemesterMetrics::getMeanGrade).toArray(Double[]::new);
      Double[] stdDevs = batch.stream().map(SubjectSemesterMetrics::getStdDev).toArray(Double[]::new);
      Integer[] sampleCounts = batch.stream().map(SubjectSemesterMetrics::getSampleCount).toArray(Integer[]::new);
      Boolean[] isFallbacks = batch.stream().map(SubjectSemesterMetrics::getIsFallback).toArray(Boolean[]::new);
      Integer[] fallbackLevels = batch.stream().map(SubjectSemesterMetrics::getFallbackLevel).toArray(Integer[]::new);
      Double[] semesterMedianGrades = batch.stream().map(SubjectSemesterMetrics::getSemesterMedianGrade).toArray(Double[]::new);
      Double[] semesterMeanGrades = batch.stream().map(SubjectSemesterMetrics::getSemesterMeanGrade).toArray(Double[]::new);
      Integer[] semesterSampleCounts = batch.stream().map(SubjectSemesterMetrics::getSemesterSampleCount).toArray(Integer[]::new);
      Double[] smoothedMedian4pts = batch.stream().map(SubjectSemesterMetrics::getSmoothedMedian4pt).toArray(Double[]::new);

      metricsRepository.batchUpsert(
          subjectIds, semesterIds, semKeys, meanGrades, stdDevs, sampleCounts,
          isFallbacks, fallbackLevels, semesterMedianGrades, semesterMeanGrades,
          semesterSampleCounts, smoothedMedian4pts);

      log.debug("Upserted batch {}/{}", end, entities.size());
    }
  }

  private SubjectSemesterMetrics createFallbackMetrics(
      UUID subjectId, UUID semesterId, int semKey, double meanGrade, double stdDev,
      int sampleCount, boolean isFallback, int fallbackLevel,
      double semesterMedian, double semesterMean, int semesterSampleCount,
      double smoothedMedian4pt) {
    return SubjectSemesterMetrics.builder()
        .subjectId(subjectId)
        .semesterId(semesterId)
        .semKey(semKey)
        .meanGrade(meanGrade)
        .stdDev(stdDev)
        .sampleCount(sampleCount)
        .isFallback(isFallback)
        .fallbackLevel(fallbackLevel)
        .semesterMedianGrade(semesterMedian)
        .semesterMeanGrade(semesterMean)
        .semesterSampleCount(semesterSampleCount)
        .smoothedMedian4pt(smoothedMedian4pt)
        .build();
  }

  private record EnrichedEnrollment(UUID studentId, UUID classId, UUID subjectId, UUID semesterId, int semKey,
                                    int credits, double finalGrade, UUID curriculumSectionId) {
  }

  private record SubjectGrade(int semKey, double grade) {
  }

  private record GlobalSubjectBaseline(double mean, double median, double stdDev, int sampleCount) {
  }

  private record SemesterGlobalStats(double median, double mean, int sampleCount, int semKey) {
  }

  @Override
  @Transactional(readOnly = true)
  public Map<UUID, String> getBatchDifficulty(List<UUID> subjectIds) {
    if (subjectIds == null || subjectIds.isEmpty()) {
      return Map.of();
    }

    // Find the most recent metrics for each subject (highest semKey)
    Map<UUID, SubjectSemesterMetrics> latestBySubject = new HashMap<>();
    for (UUID subjectId : subjectIds) {
      List<SubjectSemesterMetrics> history = metricsRepository.findBySubjectIdOrderByCreatedAtAsc(subjectId);
      if (!history.isEmpty()) {
        SubjectSemesterMetrics latest = history.getLast();
        latestBySubject.put(subjectId, latest);
      }
    }

    Map<UUID, String> result = new HashMap<>();
    for (UUID subjectId : subjectIds) {
      SubjectSemesterMetrics metrics = latestBySubject.get(subjectId);
      if (metrics == null) {
        result.put(subjectId, "medium");
      } else {
        double meanGrade = metrics.getMeanGrade();
        if (meanGrade < 5.0) {
          result.put(subjectId, "hard");
        } else if (meanGrade <= 8.0) {
          result.put(subjectId, "medium");
        } else {
          result.put(subjectId, "easy");
        }
      }
    }
    return result;
  }
}
