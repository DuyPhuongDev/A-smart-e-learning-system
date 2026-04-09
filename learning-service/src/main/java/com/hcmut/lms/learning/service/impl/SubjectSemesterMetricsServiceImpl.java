package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.ClassSectionDatasetResponse;
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

    List<Enrollment> gradedEnrollments = enrollmentRepository.findByFinalGradeIsNotNull();
    if (gradedEnrollments.isEmpty()) {
      log.warn("No graded enrollments found. Aborting metrics computation.");
      return 0;
    }
    log.info("Loaded {} graded enrollments", gradedEnrollments.size());

    Map<UUID, ClassSectionDatasetResponse> classMetaMap = fetchClassMetadata(gradedEnrollments);

    List<EnrichedEnrollment> enriched = buildEnrichedEnrollments(gradedEnrollments, classMetaMap);
    if (enriched.isEmpty()) {
      log.warn("No enrollments could be enriched with class metadata. Aborting.");
      return 0;
    }
    log.info("Enriched {} enrollments successfully", enriched.size());

    // Build subject index for O(1) window filtering
    Map<UUID, List<SubjectGrade>> gradesBySubject = buildSubjectGradeIndex(enriched);
    log.info("Built grade index for {} subjects", gradesBySubject.size());

    // Get unique (subject, semester) pairs with their semKeys
    Map<SubjectSemKey, Integer> semKeyBySubjectSem = buildSemKeyLookup(enriched);
    log.info("Found {} unique (subject, semester) pairs", semKeyBySubjectSem.size());

    // Compute per-semester global statistics (median, mean, sample count)
    Map<UUID, SemesterGlobalStats> semesterGlobalStats = computeSemesterGlobalStats(enriched);
    log.info("Computed global statistics for {} semesters", semesterGlobalStats.size());

    // Persist semester global statistics for on-demand lookups
    saveSemesterGlobalMetrics(semesterGlobalStats);

    // Pre-load historical metrics for fallback chain
    Map<UUID, List<SubjectSemesterMetrics>> historicalMetricsBySubject = loadAllHistoricalMetrics();
    Map<UUID, GlobalSubjectBaseline> globalBaselines = computeGlobalBaselines(enriched);
    log.info("Computed global baselines for {} subjects", globalBaselines.size());

    List<SubjectSemesterMetrics> metricsToSave = new ArrayList<>();
    int errorCount = 0;
    int fallbackCount = 0;

    for (Map.Entry<SubjectSemKey, Integer> entry : semKeyBySubjectSem.entrySet()) {
      try {
        SubjectSemKey key = entry.getKey();
        int currentSemKey = entry.getValue();

        // Get all grades for this subject from 3-year window
        List<Double> windowGrades = extractWindowGrades(gradesBySubject.get(key.subjectId()), currentSemKey);

        // Get semester global stats for shrinkage smoothing
        SemesterGlobalStats semStats = semesterGlobalStats.getOrDefault(
            key.semesterId(), new SemesterGlobalStats(DEFAULT_SEMESTER_MEDIAN, DEFAULT_SEMESTER_MEAN, DEFAULT_SEMESTER_SAMPLE_COUNT, currentSemKey));

        SubjectSemesterMetrics metrics = computeMetricsWithFallback(
            key.subjectId(), key.semesterId(), currentSemKey, windowGrades, semStats, historicalMetricsBySubject, globalBaselines);

        if (metrics.getIsFallback()) {
          fallbackCount++;
        }

        metricsToSave.add(metrics);
      } catch (Exception e) {
        log.warn(
            "Metrics computation failed for subjectId={}, semesterId={}: [{}] {}", entry.getKey().subjectId(),
            entry.getKey().semesterId(), e.getClass().getSimpleName(), e.getMessage());
        errorCount++;
      }
    }

    saveBatch(metricsToSave);
    log.info(
        "Subject semester metrics computation complete: success={}, fallback={}, errors={}", metricsToSave.size(),
        fallbackCount, errorCount);

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

  private Map<SubjectSemKey, Integer> buildSemKeyLookup(List<EnrichedEnrollment> enriched) {
    return enriched.stream()
        .collect(Collectors.toMap(
            e -> new SubjectSemKey(e.subjectId(), e.semesterId()), EnrichedEnrollment::semKey,
            (existing, replacement) -> existing));
  }

  private Map<UUID, List<SubjectSemesterMetrics>> loadAllHistoricalMetrics() {
    List<SubjectSemesterMetrics> allMetrics = metricsRepository.findAll();
    return allMetrics.stream().collect(Collectors.groupingBy(SubjectSemesterMetrics::getSubjectId));
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

    // Level 1: Temporal Carry-Forward — use most recent semester by semKey, not createdAt
    // semKey reflects actual semester chronology; createdAt may not if metrics were recomputed out of order
    List<SubjectSemesterMetrics> subjectMetrics = historicalMetricsBySubject.get(subjectId);
    if (subjectMetrics != null && !subjectMetrics.isEmpty()) {
      SubjectSemesterMetrics mostRecent = subjectMetrics.stream()
          .filter(m -> m.getSemKey() != null)
          .max(Comparator.comparingInt(SubjectSemesterMetrics::getSemKey))
          .orElse(subjectMetrics.stream()
              .max(Comparator.comparing(SubjectSemesterMetrics::getCreatedAt))
              .orElse(null));
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
  public SubjectSemesterMetrics computeMetricsForSubjectSemester(UUID subjectId, UUID semesterId) {
    // Optimized: Query only relevant classes and enrollments for this subject
    log.debug("Computing metrics for subjectId={}, semesterId={}", subjectId, semesterId);

    // Get current semester info for semKey
    SemesterGlobalStats semStats = fetchSemesterStats(semesterId);
    int currentSemKey = semStats.semKey();

    // Query only class sections for this subject in the 3-year window
    List<ClassSectionDatasetResponse> subjectClasses = fetchSubjectClassesInWindow(
        subjectId, currentSemKey);

    if (subjectClasses.isEmpty()) {
      log.warn("No class sections found for subjectId={} in window", subjectId);
    }

    // Get enrollments only for these specific classes
    List<UUID> classIds = subjectClasses.stream()
        .map(ClassSectionDatasetResponse::getClassId)
        .toList();

    List<Double> windowGrades = List.of();
    if (!classIds.isEmpty()) {
      List<Enrollment> enrollments = enrollmentRepository.findByClassIdInAndFinalGradeIsNotNull(classIds);
      windowGrades = enrollments.stream()
          .map(Enrollment::getFinalGrade)
          .toList();
      log.debug("Found {} enrollments for subjectId={}", windowGrades.size(), subjectId);
    }

    // Load fallback data only if no real data available
    Map<UUID, List<SubjectSemesterMetrics>> historicalMetrics = Map.of();
    Map<UUID, GlobalSubjectBaseline> globalBaselines = Map.of();

    if (windowGrades.isEmpty()) {
      // Level 1: Only load metrics for THIS subject (not all subjects)
      List<SubjectSemesterMetrics> subjectHistory = metricsRepository.findBySubjectIdOrderByCreatedAtAsc(subjectId);
      historicalMetrics = Map.of(subjectId, subjectHistory);

      // Level 2: Compute baseline from existing metrics records (avoids loading all enrollments)
      GlobalSubjectBaseline baseline = computeBaselineFromMetrics(subjectId);
      if (baseline != null) {
        globalBaselines = Map.of(subjectId, baseline);
      }
    }

    return computeMetricsWithFallback(subjectId, semesterId, currentSemKey, windowGrades, semStats,
        historicalMetrics, globalBaselines);
  }

  /**
   * Compute a subject's global baseline from its existing SubjectSemesterMetrics records.
   * Uses weighted average of historical metrics instead of loading all enrollments.
   * Falls back to unweighted average if sample counts are unavailable.
   */
  private GlobalSubjectBaseline computeBaselineFromMetrics(UUID subjectId) {
    List<SubjectSemesterMetrics> metrics = metricsRepository.findBySubjectIdOrderByCreatedAtAsc(subjectId);
    if (metrics.isEmpty()) {
      return null;
    }

    // Use only real (non-fallback) metrics with sample count > 0 for the baseline
    List<SubjectSemesterMetrics> realMetrics = metrics.stream()
        .filter(m -> !m.getIsFallback() && m.getSampleCount() != null && m.getSampleCount() > 0)
        .toList();

    if (realMetrics.isEmpty()) {
      return null;
    }

    // Weighted average using sample counts
    double totalWeight = 0;
    double weightedMeanSum = 0;
    double weightedMedianSum = 0;
    double avgStdDev = 0;
    int totalSampleCount = 0;

    for (SubjectSemesterMetrics m : realMetrics) {
      int weight = m.getSampleCount();
      weightedMeanSum += m.getMeanGrade() * weight;
      // Approximate median from mean (smoothedMedian4pt converted back is not exact, use meanGrade)
      weightedMedianSum += m.getMeanGrade() * weight;
      totalSampleCount += weight;
      totalWeight += weight;
      avgStdDev += m.getStdDev();
    }

    double mean = weightedMeanSum / totalWeight;
    double median = weightedMedianSum / totalWeight;
    double stdDev = avgStdDev / realMetrics.size();

    return new GlobalSubjectBaseline(mean, median, stdDev, totalSampleCount);
  }

  /**
   * Fetch semester stats including semKey.
   * Looks up persisted semester global stats first, falls back to closest semester by semKey,
   * then falls back to system defaults.
   */
  private SemesterGlobalStats fetchSemesterStats(UUID semesterId) {
    // Step 1: Get semKey from API (needed for window filtering)
    int semKey = 0;
    try {
      var semester = courseManagementClient.getSemesterById(semesterId);
      if (semester != null && semester.getSemKey() != null) {
        semKey = semester.getSemKey();
      }
    } catch (Exception e) {
      log.warn("Failed to fetch semester info for semesterId={}: {}", semesterId, e.getMessage());
    }

    // Step 2: Look up persisted semester global stats (exact match)
    Optional<GradeSemesterMetrics> persisted = gradeSemesterMetricsService.findBySemesterId(semesterId);
    if (persisted.isPresent()) {
      GradeSemesterMetrics m = persisted.get();
      return new SemesterGlobalStats(m.getMedianGrade(), m.getMeanGrade(), m.getSampleCount(), semKey);
    }

    // Step 3: Fallback - find closest semester by semKey proximity
    if (semKey > 0) {
      Optional<GradeSemesterMetrics> closest = gradeSemesterMetricsService.findClosestBySemKey(semKey);
      if (closest.isPresent()) {
        GradeSemesterMetrics m = closest.get();
        log.info("Using closest semester metrics (semKey={}) for target semesterId={} (semKey={})",
            m.getSemKey(), semesterId, semKey);
        return new SemesterGlobalStats(m.getMedianGrade(), m.getMeanGrade(), m.getSampleCount(), semKey);
      }
    }

    // Step 4: Final fallback - system defaults
    log.warn("No semester metrics found for semesterId={} (semKey={}), using defaults", semesterId, semKey);
    return new SemesterGlobalStats(DEFAULT_SEMESTER_MEDIAN, DEFAULT_SEMESTER_MEAN, DEFAULT_SEMESTER_SAMPLE_COUNT, semKey);
  }

  /**
   * Fetch class sections for a subject within the 3-year window.
   */
  private List<ClassSectionDatasetResponse> fetchSubjectClassesInWindow(UUID subjectId, int currentSemKey) {
    try {
      return courseManagementClient.getClassSectionsBySubjectWindow(
          com.hcmut.lms.learning.client.dto.SubjectWindowDatasetLookupRequest.builder()
              .subjectId(subjectId)
              .targetSemKey(currentSemKey)
              .windowSpan(WINDOW_SPAN_COURSE)
              .build()
      );
    } catch (Exception e) {
      log.error("Failed to fetch class sections for subjectId={}: {}", subjectId, e.getMessage());
      return List.of();
    }
  }

  @Override
  @Transactional
  public SubjectSemesterMetrics getOrComputeMetrics(UUID subjectId, UUID semesterId) {
    Optional<SubjectSemesterMetrics> existing = metricsRepository.findBySubjectIdAndSemesterId(subjectId, semesterId);
    if (existing.isPresent()) {
      return existing.get();
    }
    SubjectSemesterMetrics computed = computeMetricsForSubjectSemester(subjectId, semesterId);
    return metricsRepository.save(computed);
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

  private record SubjectSemKey(UUID subjectId, UUID semesterId) {
  }

  private record SubjectGrade(int semKey, double grade) {
  }

  private record GlobalSubjectBaseline(double mean, double median, double stdDev, int sampleCount) {
  }

  private record SemesterGlobalStats(double median, double mean, int sampleCount, int semKey) {
  }
}
