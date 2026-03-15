package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.dto.internal.CachedModel;
import com.hcmut.lms.learning.dto.internal.PredictionResult;
import com.hcmut.lms.learning.dto.request.RawFeaturePredictionRequest;
import com.hcmut.lms.learning.dto.response.GradePredictionResponse;
import com.hcmut.lms.learning.mapper.GradePredictionMapper;
import com.hcmut.lms.learning.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static final double DEFAULT_THRESHOLD = 2.0; // Pass threshold on 4-point scale

    @Override
    public GradePredictionResponse predictGrade(UUID studentId,
                                                UUID subjectId,
                                                Integer plannedSemesterCredits,
                                                Double threshold) {
        log.info("Predicting grade for studentId={}, subjectId={}", studentId, subjectId);

        double t = threshold != null ? threshold : DEFAULT_THRESHOLD;

        // Get active model
        CachedModel model = modelCacheService.getActiveModel();
        log.info("Using model: {} (version {})", model.getVersionName(), model.getVersionId());

        // Extract features — current semester resolved internally, no enrollment required
        Map<String, Object> features = featureExtractionService.extractFeatures(
                studentId, subjectId, plannedSemesterCredits);
        log.debug("Extracted {} features", features.size());
        if (log.isDebugEnabled()) {
            log.debug("Extracted features for studentId={}, subjectId={}: {}",
                    studentId, subjectId, formatFeaturesForLog(features));
        }

        // Run ONNX inference — output is g_hat4 on 4-point scale
        double gHat4 = onnxInferenceService.predict(
                model.getLocalOnnxPath(),
                features,
                model.getFeatureColumns()
        );

        // Residual distribution: R = g_hat4 - g4 ~ N(mu_R, sigma_R^2)
        Double muR    = model.getMetrics().getTest().getErrorDistribution().getMuError();
        Double sigmaR = model.getMetrics().getTest().getErrorDistribution().getSigmaError();

        // Bias-corrected prediction: g_tilde = g_hat4 + mu_R
        // g4 ~ N(g_tilde, sigma_R^2)
        double gTilde = gHat4 + (muR != null ? muR : 0.0);
        double correctedPrediction = Math.max(0.0, Math.min(4.0, gTilde));

        // P(g4 >= t) = 1 - Phi((t - g_tilde) / sigma_R)
        double probabilityAboveThreshold;
        if (sigmaR != null && sigmaR > 1e-8) {
            double z = (t - gTilde) / sigmaR;
            probabilityAboveThreshold = 1.0 - normalCDF(z);
        } else {
            // Degenerate / missing sigma: deterministic fallback
            probabilityAboveThreshold = gTilde >= t ? 1.0 : 0.0;
        }

        // 95% CI: [g_tilde ± 1.96 * sigma_R]
        double sigma = sigmaR != null ? sigmaR : 0.0;
        double lowerBound95 = Math.max(0.0, gTilde - 1.96 * sigma);
        double upperBound95 = Math.min(4.0, gTilde + 1.96 * sigma);

        log.info("Prediction: raw={}, corrected={}, P(g4>={})={}", gHat4, correctedPrediction, t, probabilityAboveThreshold);

        // Get current semester ID for response
        UUID currentSemesterId = getCurrentSemesterId();

        return gradePredictionMapper.toResponse(PredictionResult.builder()
                .studentId(studentId)
                .subjectId(subjectId)
                .semesterId(currentSemesterId)
                .rawPredictedGrade(gHat4)
                .correctedPredictedGrade(correctedPrediction)
                .residualMean(muR)
                .residualStd(sigmaR)
                .threshold(t)
                .probabilityAboveThreshold(probabilityAboveThreshold)
                .lowerBound95(lowerBound95)
                .upperBound95(upperBound95)
                .modelVersionId(model.getVersionId())
                .modelVersionName(model.getVersionName())
                .build());
    }

    @Override
    public GradePredictionResponse predictGradeFromRawFeatures(RawFeaturePredictionRequest request) {
        Map<String, Object> features = buildFeatureMap(request);
        double t = request.getThreshold() != null ? request.getThreshold() : DEFAULT_THRESHOLD;

        log.info("Predicting grade from raw features (threshold={})", t);
        if (log.isDebugEnabled()) {
            log.debug("Raw features: {}", formatFeaturesForLog(features));
        }

        CachedModel model = modelCacheService.getActiveModel();
        log.info("Using model: {} (version {})", model.getVersionName(), model.getVersionId());

        double gHat4 = onnxInferenceService.predict(
                model.getLocalOnnxPath(),
                features,
                model.getFeatureColumns()
        );

        Double muR = model.getMetrics().getTest().getErrorDistribution().getMuError();
        Double sigmaR = model.getMetrics().getTest().getErrorDistribution().getSigmaError();

        double gTilde = gHat4 + (muR != null ? muR : 0.0);
        double correctedPrediction = Math.max(0.0, Math.min(4.0, gTilde));

        double probabilityAboveThreshold;
        if (sigmaR != null && sigmaR > 1e-8) {
            double z = (t - gTilde) / sigmaR;
            probabilityAboveThreshold = 1.0 - normalCDF(z);
        } else {
            probabilityAboveThreshold = gTilde >= t ? 1.0 : 0.0;
        }

        double sigma = sigmaR != null ? sigmaR : 0.0;
        double lowerBound95 = Math.max(0.0, gTilde - 1.96 * sigma);
        double upperBound95 = Math.min(4.0, gTilde + 1.96 * sigma);

        return gradePredictionMapper.toResponse(PredictionResult.builder()
                .rawPredictedGrade(gHat4)
                .correctedPredictedGrade(correctedPrediction)
                .residualMean(muR)
                .residualStd(sigmaR)
                .threshold(t)
                .probabilityAboveThreshold(probabilityAboveThreshold)
                .lowerBound95(lowerBound95)
                .upperBound95(upperBound95)
                .modelVersionId(model.getVersionId())
                .modelVersionName(model.getVersionName())
                .build());
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
        return features.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(java.util.stream.Collectors.joining(", "));
    }

    private Map<String, Object> buildFeatureMap(RawFeaturePredictionRequest request) {
        int semCredits = request.getSemCredits();
        int semCreditsSquared = request.getSemCreditsSquared() != null
                ? request.getSemCreditsSquared()
                : semCredits * semCredits;

        return java.util.Map.of(
                "sem_credits", semCredits,
                "sem_credits_squared", semCreditsSquared,
                "retake_no", request.getRetakeNo(),
                "num_semesters_prior", request.getNumSemestersPrior(),
                "cumulative_grade_avg", request.getCumulativeGradeAvg(),
                "previous_sem_grade_avg", request.getPreviousSemGradeAvg(),
                "subject_hist_median_smooth", request.getSubjectHistMedianSmooth(),
                "relative_avg_course_grade", request.getRelativeAvgCourseGrade()
        );
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

        final double a1 =  0.254829592;
        final double a2 = -0.284496736;
        final double a3 =  1.421413741;
        final double a4 = -1.453152027;
        final double a5 =  1.061405429;
        final double p  =  0.3275911;

        double t = 1.0 / (1.0 + p * x);
        double poly = ((((a5 * t + a4) * t + a3) * t + a2) * t + a1) * t;
        double y = 1.0 - poly * Math.exp(-x * x);

        return sign * y;
    }
}
