package com.hcmut.lms.personalization.application.service.impl.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executor;

import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.GradePredictionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PredictiveModelAnalyzerServiceTest {

    @Mock private LearningServiceClient learningServiceClient;
    @Mock private Executor taskExecutor;

    @InjectMocks
    private PredictiveModelAnalyzerService predictiveModelAnalyzer;

    @BeforeEach
    void setUp() {
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));
    }

    @Test void analyze_shouldReturnSkipped_whenNoRemainingSubjects() {
        Map<String, Object> result = predictiveModelAnalyzer.analyze(
            UUID.randomUUID(), Collections.emptyList(), new HashMap<>(),
            BigDecimal.valueOf(3.0), 60, 30, BigDecimal.valueOf(3.5), 18);
        assertNotNull(result);
        assertTrue("predictive_unavailable".equals(result.get("method")));
    }

    @Test void analyze_shouldReturnPredictions_whenValidSubjects() {
        UUID subjectId = UUID.randomUUID();
        GradePredictionResponse prediction = new GradePredictionResponse();
        prediction.setSubjectId(subjectId);
        prediction.setCorrectedPredictedGrade(7.5);
        prediction.setRawPredictedGrade(7.0);
        prediction.setResidualStd(1.0);
        when(learningServiceClient.predictGrade(any(), any(), any(), any()))
            .thenReturn(prediction);

        Map<UUID, Integer> credits = Map.of(subjectId, 3);
        Map<String, Object> result = predictiveModelAnalyzer.analyze(
            UUID.randomUUID(), List.of(subjectId), credits,
            BigDecimal.valueOf(3.0), 60, 30, BigDecimal.valueOf(3.5), 18);
        assertNotNull(result);
        assertTrue(result.containsKey("probabilityScore"));
        assertTrue(result.get("probabilityScore") instanceof Double);
    }

    @Test void analyze_shouldHandleNullGpa() {
        UUID subjectId = UUID.randomUUID();
        GradePredictionResponse prediction = new GradePredictionResponse();
        prediction.setSubjectId(subjectId);
        prediction.setCorrectedPredictedGrade(7.0);
        prediction.setResidualStd(1.0);
        when(learningServiceClient.predictGrade(any(), any(), any(), any()))
            .thenReturn(prediction);

        Map<UUID, Integer> credits = Map.of(subjectId, 3);
        Map<String, Object> result = predictiveModelAnalyzer.analyze(
            UUID.randomUUID(), List.of(subjectId), credits,
            null, 0, 30, BigDecimal.valueOf(3.0), null);
        assertNotNull(result);
        assertTrue("predictive_fallback".equals(result.get("method")));
    }

    @Test void analyze_shouldHandleNullPredictions_whenClientReturnsNull() {
        UUID subjectId = UUID.randomUUID();
        when(learningServiceClient.predictGrade(any(), any(), any(), any()))
            .thenReturn(null);

        Map<UUID, Integer> credits = Map.of(subjectId, 3);
        Map<String, Object> result = predictiveModelAnalyzer.analyze(
            UUID.randomUUID(), List.of(subjectId), credits,
            BigDecimal.valueOf(3.0), 60, 30, BigDecimal.valueOf(3.5), 18);
        assertNotNull(result);
        assertTrue("predictive_unavailable".equals(result.get("method")));
    }

    @Test void analyze_shouldHandlePredictGradeException_whenClientThrows() {
        UUID subjectId = UUID.randomUUID();
        when(learningServiceClient.predictGrade(any(), any(), any(), any()))
            .thenThrow(new RuntimeException("Service down"));

        Map<UUID, Integer> credits = Map.of(subjectId, 3);
        Map<String, Object> result = predictiveModelAnalyzer.analyze(
            UUID.randomUUID(), List.of(subjectId), credits,
            BigDecimal.valueOf(3.0), 60, 30, BigDecimal.valueOf(3.5), 18);
        assertNotNull(result);
        assertTrue("predictive_unavailable".equals(result.get("method")));
    }

    @Test void analyze_shouldFallbackToRawGrade_whenCorrectedGradeIsNull() {
        UUID subjectId = UUID.randomUUID();
        GradePredictionResponse prediction = new GradePredictionResponse();
        prediction.setSubjectId(subjectId);
        prediction.setCorrectedPredictedGrade(null);
        prediction.setRawPredictedGrade(7.0);
        prediction.setResidualStd(1.5);
        when(learningServiceClient.predictGrade(any(), any(), any(), any()))
            .thenReturn(prediction);

        Map<UUID, Integer> credits = Map.of(subjectId, 3);
        Map<String, Object> result = predictiveModelAnalyzer.analyze(
            UUID.randomUUID(), List.of(subjectId), credits,
            BigDecimal.valueOf(3.0), 60, 30, BigDecimal.valueOf(3.5), 18);
        assertNotNull(result);
        assertTrue(result.get("probabilityScore") instanceof Double);
    }

    @Test void analyze_shouldCountNullPredictions_whenBothCorrectedAndRawAreNull() {
        UUID subjectId = UUID.randomUUID();
        GradePredictionResponse prediction = new GradePredictionResponse();
        prediction.setSubjectId(subjectId);
        prediction.setCorrectedPredictedGrade(null);
        prediction.setRawPredictedGrade(null);
        prediction.setResidualStd(null);
        when(learningServiceClient.predictGrade(any(), any(), any(), any()))
            .thenReturn(prediction);

        Map<UUID, Integer> credits = Map.of(subjectId, 3);
        Map<String, Object> result = predictiveModelAnalyzer.analyze(
            UUID.randomUUID(), List.of(subjectId), credits,
            BigDecimal.valueOf(3.0), 60, 30, BigDecimal.valueOf(3.5), 18);
        assertNotNull(result);
        assertTrue("predictive_unavailable".equals(result.get("method")));
    }

    @Test void analyze_shouldHandleZeroCreditsAndZeroEarned() {
        UUID subjectId = UUID.randomUUID();
        GradePredictionResponse prediction = new GradePredictionResponse();
        prediction.setSubjectId(subjectId);
        prediction.setCorrectedPredictedGrade(7.5);
        prediction.setResidualStd(1.0);
        when(learningServiceClient.predictGrade(any(), any(), any(), any()))
            .thenReturn(prediction);

        Map<UUID, Integer> credits = Map.of(subjectId, 0);
        Map<String, Object> result = predictiveModelAnalyzer.analyze(
            UUID.randomUUID(), List.of(subjectId), credits,
            null, 0, 0, BigDecimal.valueOf(3.5), null);
        assertNotNull(result);
        assertTrue(result.get("probabilityScore") instanceof Double);
    }
}
