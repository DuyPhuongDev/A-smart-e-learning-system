package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.dto.internal.CachedModel;
import com.hcmut.lms.learning.dto.internal.PredictionResult;
import com.hcmut.lms.learning.dto.request.BatchGradePredictionRequest;
import com.hcmut.lms.learning.dto.request.RawFeaturePredictionRequest;
import com.hcmut.lms.learning.dto.response.BatchGradePredictionResponse;
import com.hcmut.lms.learning.dto.response.GradePredictionResponse;
import com.hcmut.lms.learning.entity.subject.SubjectSemesterMetrics;
import com.hcmut.lms.learning.exception.PredictionFailedException;
import com.hcmut.lms.learning.mapper.GradePredictionMapper;
import com.hcmut.lms.learning.service.FeatureExtractionService;
import com.hcmut.lms.learning.service.GradePredictionService;
import com.hcmut.lms.learning.service.ModelCacheService;
import com.hcmut.lms.learning.service.OnnxInferenceService;
import com.hcmut.lms.learning.service.SubjectSemesterMetricsService;
import com.hcmut.lms.learning.util.GradeConversionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradePredictionServiceImpl implements GradePredictionService {

  private final ModelCacheService modelCacheService;
  private final OnnxInferenceService onnxInferenceService;
  private final FeatureExtractionService featureExtractionService;
  private final GradePredictionMapper gradePredictionMapper;
  private final CourseManagementClient courseManagementClient;
  private final SubjectSemesterMetricsService subjectSemesterMetricsService;

  private static final double DEFAULT_THRESHOLD = 2.0; // Pass threshold on 4-point scale

  @Override
  public GradePredictionResponse predictGrade(
      UUID studentId, UUID subjectId, Integer plannedSemesterCredits,
      Double threshold) {
    log.info("Predicting grade for studentId={}, subjectId={}", studentId, subjectId);

    double t = resolveThreshold(threshold);
    UUID currentSemesterId = getCurrentSemesterId();

    // Extract features first to check if student has history
    Map<String, Object> features = featureExtractionService.extractFeatures(
        studentId, subjectId, plannedSemesterCredits);
    log.debug("Extracted {} features", features.size());
    if (log.isDebugEnabled()) {
      log.debug(
          "Extracted features for studentId={}, subjectId={}: {}", studentId, subjectId,
          formatFeaturesForLog(features));
    }

    // Check if this is a first-semester student (no prior history)
    boolean isFirstSemesterStudent = isFirstSemesterStudent(features);

    if (isFirstSemesterStudent) {
      log.info("Student {} has no prior history, using SubjectSemesterMetrics", studentId);

      // Fetch SubjectSemesterMetrics for this subject and current semester
      SubjectSemesterMetrics metrics = subjectSemesterMetricsService.getOrComputeMetrics(subjectId, currentSemesterId);

      if (metrics == null || metrics.getFallbackLevel() == null || metrics.getFallbackLevel() > 1) {
        throw new PredictionFailedException(
            String.format("SubjectSemesterMetrics not available for first-semester student prediction: " +
                "subjectId=%s, semesterId=%s, fallbackLevel=%s", subjectId, currentSemesterId,
                metrics != null ? metrics.getFallbackLevel() : "null"));
      }

      PredictionResult result = predictFromSubjectMetrics(studentId, subjectId, currentSemesterId, metrics, t);

      log.info(
          "Metrics-based prediction: mean={}, std={}, P(g>={})={}", result.getRawPredictedGrade(),
          result.getResidualStd(), t, result.getProbabilityAboveThreshold());

      return gradePredictionMapper.toResponse(result);
    }

    // Regular ML model path for students with history
    return runModelPrediction(studentId, subjectId, currentSemesterId, features, t);
  }

  @Override
  public GradePredictionResponse predictGradeFromRawFeatures(RawFeaturePredictionRequest request) {
    Map<String, Object> features = buildFeatureMap(request);
    double t = resolveThreshold(request.getThreshold());

    log.info("Predicting grade from raw features (threshold={})", t);
    if (log.isDebugEnabled()) {
      log.debug("Raw features: {}", formatFeaturesForLog(features));
    }

    CachedModel model = getActiveModelWithLog();
    double gHat4 = runInference(model, features);

    PredictionResult result = buildPredictionResult(null, null, null, gHat4, t, model);
    return gradePredictionMapper.toResponse(result);
  }

  @Override
  public BatchGradePredictionResponse predictGradeBatch(BatchGradePredictionRequest request) {
    if (request == null || request.getPredictions() == null || request.getPredictions().isEmpty()) {
      return BatchGradePredictionResponse.builder().predictions(Collections.emptyList()).build();
    }

    List<GradePredictionResponse> responses = request.getPredictions()
        .stream()
        .map(item -> predictGrade(item.getStudentId(), item.getSubjectId(), item.getPlannedSemesterCredits(), null))
        .toList();

    return BatchGradePredictionResponse.builder().predictions(responses).build();
  }

  private double resolveThreshold(Double threshold) {
    return threshold != null ? threshold : DEFAULT_THRESHOLD;
  }

  /**
   * Check if student has no prior enrollment history.
   * First semester if: no prior semesters AND all GPA-related features are 0.
   */
  private boolean isFirstSemesterStudent(Map<String, Object> features) {
    Integer numSemestersPrior = (Integer) features.getOrDefault("num_semesters_prior", 0);
    Double cumulativeGradeAvg = (Double) features.getOrDefault("cumulative_grade_avg", 0.0);
    Double previousSemGradeAvg = (Double) features.getOrDefault("previous_sem_grade_avg", 0.0);

    return numSemestersPrior != null && numSemestersPrior == 0 && cumulativeGradeAvg != null && cumulativeGradeAvg == 0.0 && previousSemGradeAvg != null && previousSemGradeAvg == 0.0;
  }

  /**
   * Predict grade for first-semester student using SubjectSemesterMetrics normal distribution.
   * Uses the mean and stdDev from historical course data.
   */
  private PredictionResult predictFromSubjectMetrics(
      UUID studentId, UUID subjectId, UUID semesterId,
      SubjectSemesterMetrics metrics, Double threshold) {

    // Convert mean from 10-point to 4-point scale
    double meanGrade4pt = GradeConversionUtil.convertTo4Point(metrics.getMeanGrade());

    // stdDev is already stored on 4-point scale (no conversion needed)
    double stdDev4pt = metrics.getStdDev();

    // For first-semester students, use the historical mean as prediction
    // Calculate probability above threshold using normal distribution
    double probability = calculateProbabilityAboveThreshold(threshold, meanGrade4pt, stdDev4pt);

    // Calculate confidence intervals (95%)
    double lowerBound95 = Math.max(0.0, meanGrade4pt - 1.96 * stdDev4pt);
    double upperBound95 = Math.min(4.0, meanGrade4pt + 1.96 * stdDev4pt);

    return PredictionResult.builder()
        .studentId(studentId)
        .subjectId(subjectId)
        .semesterId(semesterId)
        .rawPredictedGrade(meanGrade4pt)
        .correctedPredictedGrade(meanGrade4pt) // No residual correction for metrics-based
        .residualMean(0.0) // No ML model used
        .residualStd(stdDev4pt) // Use course stdDev as uncertainty measure
        .threshold(threshold)
        .probabilityAboveThreshold(probability)
        .lowerBound95(lowerBound95)
        .upperBound95(upperBound95)
        .modelVersionId(null) // Special value for metrics-based
        .modelVersionName("subject-semester-metrics")
        .build();
  }

  /**
   * Run ML model prediction for students with history.
   */
  private GradePredictionResponse runModelPrediction(
      UUID studentId, UUID subjectId, UUID semesterId,
      Map<String, Object> features, double threshold) {

    CachedModel model = getActiveModelWithLog();
    double gHat4 = runInference(model, features);

    PredictionResult result = buildPredictionResult(studentId, subjectId, semesterId, gHat4, threshold, model);

    return gradePredictionMapper.toResponse(result);
  }

  private CachedModel getActiveModelWithLog() {
    CachedModel model = modelCacheService.getActiveModel();
    log.info("Using model: {} (version {})", model.getVersionName(), model.getVersionId());
    return model;
  }

  private double runInference(CachedModel model, Map<String, Object> features) {
    return onnxInferenceService.predict(model.getLocalOnnxPath(), features, model.getFeatureColumns());
  }

  private PredictionResult buildPredictionResult(
      UUID studentId, UUID subjectId, UUID semesterId, double gHat4,
      double threshold, CachedModel model) {
    Double muR = model.getMetrics().getTest().getErrorDistribution().getMuError();
    Double sigmaR = model.getMetrics().getTest().getErrorDistribution().getSigmaError();

    double gTilde = gHat4 + (muR != null ? muR : 0.0);
    double correctedPrediction = clamp(gTilde);

    double probabilityAboveThreshold = calculateProbabilityAboveThreshold(threshold, gTilde, sigmaR);

    double sigma = sigmaR != null ? sigmaR : 0.0;
    double lowerBound95 = Math.max(0.0, gTilde - 1.96 * sigma);
    double upperBound95 = Math.min(4.0, gTilde + 1.96 * sigma);

    return PredictionResult.builder()
        .studentId(studentId)
        .subjectId(subjectId)
        .semesterId(semesterId)
        .rawPredictedGrade(gHat4)
        .correctedPredictedGrade(correctedPrediction)
        .residualMean(muR)
        .residualStd(sigmaR)
        .threshold(threshold)
        .probabilityAboveThreshold(probabilityAboveThreshold)
        .lowerBound95(lowerBound95)
        .upperBound95(upperBound95)
        .modelVersionId(model.getVersionId())
        .modelVersionName(model.getVersionName())
        .build();
  }

  private double calculateProbabilityAboveThreshold(double threshold, double gTilde, Double sigmaR) {
    if (sigmaR != null && sigmaR > 1e-8) {
      double z = (threshold - gTilde) / sigmaR;
      return 1.0 - normalCDF(z);
    }
    return gTilde >= threshold ? 1.0 : 0.0;
  }

  private double clamp(double value) {
    return Math.max(0.0, Math.min(4.0, value));
  }

  private UUID getCurrentSemesterId() {
    try {
      return courseManagementClient.getCurrentSemester().getId();
    } catch (Exception e) {
      log.warn("Failed to get current semester ID for response", e);
      return null;
    }
  }

  private String formatFeaturesForLog(Map<String, Object> features) {
    return features.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByKey())
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .collect(java.util.stream.Collectors.joining(", "));
  }

  private Map<String, Object> buildFeatureMap(RawFeaturePredictionRequest request) {
    int semCredits = request.getSemCredits();
    int semCreditsSquared = request.getSemCreditsSquared() != null ? request.getSemCreditsSquared() :
        semCredits * semCredits;

    return java.util.Map.of(
        "sem_credits", semCredits, "sem_credits_squared", semCreditsSquared, "retake_no", request.getRetakeNo(),
        "num_semesters_prior", request.getNumSemestersPrior(), "cumulative_grade_avg", request.getCumulativeGradeAvg(),
        "previous_sem_grade_avg", request.getPreviousSemGradeAvg(), "subject_hist_median_smooth",
        request.getSubjectHistMedianSmooth(), "relative_avg_course_grade", request.getRelativeAvgCourseGrade());
  }

  /**
   * Standard normal CDF via error function: Phi(z) = 0.5 * (1 + erf(z / sqrt(2)))
   */
  private double normalCDF(double z) {
    return 0.5 * (1.0 + erf(z / Math.sqrt(2.0)));
  }

  /**
   * Numerical error function approximation (Abramowitz & Stegun, max error 1.5e-7).
   */
  private double erf(double x) {
    double sign = x >= 0 ? 1.0 : -1.0;
    x = Math.abs(x);

    final double a1 = 0.254829592;
    final double a2 = -0.284496736;
    final double a3 = 1.421413741;
    final double a4 = -1.453152027;
    final double a5 = 1.061405429;
    final double p = 0.3275911;

    double t = 1.0 / (1.0 + p * x);
    double poly = ((((a5 * t + a4) * t + a3) * t + a2) * t + a1) * t;
    double y = 1.0 - poly * Math.exp(-x * x);

    return sign * y;
  }
}
