package com.hcmut.lms.personalization.application.service.impl.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalProbabilityAnalysisService {

    private final HistoricalDataAnalyzerService historicalDataAnalyzer;
    private final PredictiveModelAnalyzerService predictiveModelAnalyzer;

    private static final int MIN_CREDITS_FOR_PREDICTIVE = 30;

    public Map<String, Object> analyzeProbability(
        UUID studentId,
        String specializationId,
        List<UUID> remainingSubjectIds,
        Map<UUID, Integer> remainingSubjectCredits,
        BigDecimal currentGpa,
        int earnedCredits,
        int remainingCredits,
        BigDecimal targetGpa,
        int remainingSemesters,
        Integer mainCreditCap
    ) {
        log.info("Analyzing probability for student {}", studentId);

        if (earnedCredits >= MIN_CREDITS_FOR_PREDICTIVE && !remainingSubjectIds.isEmpty()) {
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
        } else {
            log.info("Using historical data analyzer (earned credits: {})", earnedCredits);
            return historicalDataAnalyzer.analyze(
                specializationId,
                targetGpa,
                remainingSemesters
            );
        }
    }
}
