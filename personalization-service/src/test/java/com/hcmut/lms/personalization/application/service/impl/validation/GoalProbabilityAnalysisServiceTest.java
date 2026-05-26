package com.hcmut.lms.personalization.application.service.impl.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoalProbabilityAnalysisServiceTest {

    @Mock private PredictiveModelAnalyzerService predictiveModelAnalyzer;

    @InjectMocks
    private GoalProbabilityAnalysisService service;

    @Test void analyzeProbability_shouldReturnSkipped_whenNoRemainingSubjects() {
        Map<String, Object> result = service.analyzeProbability(
            UUID.randomUUID(), Collections.emptyList(), new HashMap<>(),
            BigDecimal.ZERO, 0, 0, BigDecimal.ZERO, 0);
        assertTrue("skipped".equals(result.get("method")));
    }

    @Test void analyzeProbability_shouldDelegateToPredictiveModel_whenSubjectsExist() {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("method", "predictive");
        mockResult.put("probabilityScore", 0.75);
        when(predictiveModelAnalyzer.analyze(any(), any(), any(), any(), any(Integer.class), any(Integer.class), any(), any()))
            .thenReturn(mockResult);

        Map<String, Object> result = service.analyzeProbability(
            UUID.randomUUID(), List.of(UUID.randomUUID()), Map.of(UUID.randomUUID(), 3),
            BigDecimal.valueOf(3.0), 60, 30, BigDecimal.valueOf(3.5), 18);
        assertTrue("predictive".equals(result.get("method")));
    }
}
