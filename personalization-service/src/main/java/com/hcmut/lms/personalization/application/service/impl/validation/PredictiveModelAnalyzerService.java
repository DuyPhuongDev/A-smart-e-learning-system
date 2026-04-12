package com.hcmut.lms.personalization.application.service.impl.validation;

import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.GradePredictionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictiveModelAnalyzerService {

  private static final double DEFAULT_STD_DEV = 0.5;
  private static final double MIN_STD_DEV_EPS = 1e-8;
  private static final int DEFAULT_CREDITS = 3;
  private static final int DEFAULT_MAIN_SEMESTER_CAP = 17;

  private final LearningServiceClient learningServiceClient;
  private final Executor taskExecutor;

  public Map<String, Object> analyze(
      UUID studentId, List<UUID> remainingSubjectIds, Map<UUID, Integer> remainingSubjectCredits,
      BigDecimal currentGpa, int earnedCredits, int remainingCredits,
      BigDecimal targetGpa, Integer mainCreditCap) {
    log.info("Analyzing with predictive model for student {}", studentId);

    Integer creditCap = mainCreditCap != null ? mainCreditCap : DEFAULT_MAIN_SEMESTER_CAP;

    try {
      List<CompletableFuture<GradePredictionResponse>> futures = new ArrayList<>();
      for (UUID subjectId : remainingSubjectIds) {
        futures.add(CompletableFuture.supplyAsync(
            () -> {
              try {
                return learningServiceClient.predictGrade(studentId, subjectId, creditCap, null);
              } catch (Exception e) {
                log.error("Failed to predict grade for subject {}: {}", subjectId, e.getMessage());
                return null;
              }
            }, taskExecutor));
      }

      CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

      List<GradePredictionResponse> predictions = futures.stream()
          .map(CompletableFuture::join)
          .filter(Objects::nonNull)
          .toList();

      double weightedPredictedSum = 0.0;
      double totalRemainingCredits = 0.0;
      double varianceWeightedSum = 0.0;
      int validPredictionCount = 0;
      int nullPredictionCount = 0;
      int totalCredits = earnedCredits + remainingCredits;

      for (GradePredictionResponse pred : predictions) {
        Double predicted = pred.getCorrectedPredictedGrade() != null ? pred.getCorrectedPredictedGrade() :
            pred.getRawPredictedGrade();
        Double stdDev = pred.getResidualStd();
        UUID subjectId = pred.getSubjectId();

        if (predicted != null) {
          int credits = remainingSubjectCredits.getOrDefault(subjectId, DEFAULT_CREDITS);
          weightedPredictedSum += predicted * credits;
          totalRemainingCredits += credits;

          // Some models return 0 for residual std; treat it as missing to avoid deterministic collapse.
          double effectiveStdDev = (stdDev != null && stdDev > MIN_STD_DEV_EPS) ? stdDev : DEFAULT_STD_DEV;
          // Credit-weighted variance: Var = Σ( (credits_i / totalCredits)² × σ_i² )
          double weight = (double) credits / totalCredits;
          varianceWeightedSum += weight * weight * effectiveStdDev * effectiveStdDev;
          validPredictionCount++;
        } else {
          nullPredictionCount++;
        }
      }

      log.info(
          "Prediction summary for student {}: total={}, valid={}, nullPredicted={}", studentId, predictions.size(),
          validPredictionCount, nullPredictionCount);

      if (validPredictionCount == 0) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("method", "predictive_fallback");
        analysis.put("probabilityScore", 0.5);
        analysis.put("note", "No valid predictions returned - using default probability");
        analysis.put("predictionsCount", predictions.size());
        return analysis;
      }

      // Credit-weighted mean of predicted grades
      double meanPredicted = totalRemainingCredits > 0
          ? weightedPredictedSum / totalRemainingCredits : 0.0;

      // Final GPA using credit-weighted average
      double finalGpaMean =
          (currentGpa.doubleValue() * earnedCredits + meanPredicted * remainingCredits) / totalCredits;

      // Correct credit-weighted variance propagation
      double finalGpaStdDev = Math.sqrt(varianceWeightedSum);

      double probabilityScore;
      if (finalGpaStdDev <= 0.0001) {
        probabilityScore = targetGpa.doubleValue() <= finalGpaMean ? 1.0 : 0.0;
      } else {
        double zScore = (targetGpa.doubleValue() - finalGpaMean) / finalGpaStdDev;
        probabilityScore = 1.0 - normalCdf(zScore);
      }

      probabilityScore = Math.max(0.0, Math.min(1.0, probabilityScore));

      Map<String, Object> analysis = new HashMap<>();
      analysis.put("method", "predictive");
      analysis.put("probabilityScore", probabilityScore);
      analysis.put("predictedFinalGpa", finalGpaMean);
      analysis.put("standardDeviation", finalGpaStdDev);
      analysis.put("predictionsCount", validPredictionCount);

      return analysis;

    } catch (Exception e) {
      log.warn("Failed to get predictions from learning-service, falling back to default", e);

      Map<String, Object> analysis = new HashMap<>();
      analysis.put("method", "predictive_fallback");
      analysis.put("probabilityScore", 0.5);
      analysis.put("note", "Prediction service unavailable - using default probability");

      return analysis;
    }
  }

  private double normalCdf(double z) {
    return 0.5 * (1.0 + erf(z / Math.sqrt(2.0)));
  }

  private double erf(double x) {
    double a1 = 0.254829592;
    double a2 = -0.284496736;
    double a3 = 1.421413741;
    double a4 = -1.453152027;
    double a5 = 1.061405429;
    double p = 0.3275911;

    int sign = x < 0 ? -1 : 1;
    x = Math.abs(x);

    double t = 1.0 / (1.0 + p * x);
    double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);

    return sign * y;
  }
}
