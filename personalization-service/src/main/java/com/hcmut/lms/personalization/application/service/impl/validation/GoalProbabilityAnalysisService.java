package com.hcmut.lms.personalization.application.service.impl.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalProbabilityAnalysisService {

    private final PredictiveModelAnalyzerService predictiveModelAnalyzer;

    public Map<String, Object> analyzeProbability(
        UUID studentId, List<UUID> remainingSubjectIds,
        Map<UUID, Integer> remainingSubjectCredits,
        BigDecimal currentGpa,
        int earnedCredits,
        int remainingCredits,
        BigDecimal targetGpa, Integer mainCreditCap
    ) {
        log.info("Analyzing probability for student {}", studentId);

        if (remainingSubjectIds.isEmpty()) {
            log.info("No remaining subjects, probability is not applicable");
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("method", "skipped");
            analysis.put("probabilityScore", null);
            analysis.put("note", "No remaining subjects to predict");
            return analysis;
        }

        log.info("Using predictive model analyzer (earned credits: {})", earnedCredits);
        return predictiveModelAnalyzer.analyze(
            studentId,
            remainingSubjectIds,
            remainingSubjectCredits,
            currentGpa,
            earnedCredits,
            remainingCredits,
            targetGpa,
            mainCreditCap
        );
    }
}
