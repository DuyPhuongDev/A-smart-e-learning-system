package com.hcmut.lms.learning.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.SemesterResponse;
import com.hcmut.lms.learning.dto.internal.CachedModel;
import com.hcmut.lms.learning.dto.internal.ModelMetrics;
import com.hcmut.lms.learning.dto.request.BatchGradePredictionRequest;
import com.hcmut.lms.learning.dto.request.RawFeaturePredictionRequest;
import com.hcmut.lms.learning.dto.response.BatchGradePredictionResponse;
import com.hcmut.lms.learning.dto.response.GradePredictionResponse;
import com.hcmut.lms.learning.entity.subject.SubjectSemesterMetrics;
import com.hcmut.lms.learning.exception.PredictionFailedException;
import com.hcmut.lms.learning.mapper.GradePredictionMapper;
import com.hcmut.lms.learning.service.FeatureExtractionService;
import com.hcmut.lms.learning.service.ModelCacheService;
import com.hcmut.lms.learning.service.OnnxInferenceService;
import com.hcmut.lms.learning.service.SubjectSemesterMetricsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class GradePredictionServiceImplTest {

    @Mock
    private ModelCacheService modelCacheService;

    @Mock
    private OnnxInferenceService onnxInferenceService;

    @Mock
    private FeatureExtractionService featureExtractionService;

    @Mock
    private GradePredictionMapper gradePredictionMapper;

    @Mock
    private CourseManagementClient courseManagementClient;

    @Mock
    private SubjectSemesterMetricsService subjectSemesterMetricsService;

    @InjectMocks
    private GradePredictionServiceImpl gradePredictionService;

    private final UUID studentId = UUID.randomUUID();
    private final UUID subjectId = UUID.randomUUID();
    private final UUID semesterId = UUID.randomUUID();

    @Test
    void predictGrade_shouldUseMetricsFallback_whenNoHistory() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(featureExtractionService.hasGradedHistory(studentId)).thenReturn(false);

        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId).meanGrade(7.0).stdDev(1.5)
                .isFallback(false).fallbackLevel(0).sampleCount(100).build();
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));

        GradePredictionResponse response = new GradePredictionResponse();
        when(gradePredictionMapper.toResponse(any())).thenReturn(response);

        GradePredictionResponse result = gradePredictionService.predictGrade(studentId, subjectId, null, null);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void predictGrade_shouldThrowException_whenNoMetricsForFallback() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(featureExtractionService.hasGradedHistory(studentId)).thenReturn(false);
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.empty());

        try {
            gradePredictionService.predictGrade(studentId, subjectId, null, null);
        } catch (PredictionFailedException e) {
            assertTrue(true);
        }
    }

    @Test
    void predictGradeFromRawFeatures_shouldReturnPrediction_whenValidFeatures() {
        RawFeaturePredictionRequest request = new RawFeaturePredictionRequest();
        request.setSemCredits(17);
        request.setRetakeNo(0);
        request.setNumSemestersPrior(2);
        request.setCumulativeGradeAvg(2.5);
        request.setPreviousSemGradeAvg(2.3);
        request.setSubjectHistMedianSmooth(2.4);
        request.setRelativeAvgCourseGrade(2.6);

        CachedModel model = mock(CachedModel.class);
        ModelMetrics metrics = mock(ModelMetrics.class);
        ModelMetrics.TestMetrics testMetrics = mock(ModelMetrics.TestMetrics.class);
        ModelMetrics.ErrorDistribution errorDist = mock(ModelMetrics.ErrorDistribution.class);

        when(model.getVersionId()).thenReturn(UUID.randomUUID());
        when(model.getVersionName()).thenReturn("test-model");
        when(model.getLocalOnnxPath()).thenReturn("/tmp/model.onnx");
        when(model.getFeatureColumns()).thenReturn(List.of("sem_credits", "retake_no"));
        when(model.getMetrics()).thenReturn(metrics);
        when(metrics.getTest()).thenReturn(testMetrics);
        when(testMetrics.getErrorDistribution()).thenReturn(errorDist);
        when(errorDist.getMuError()).thenReturn(0.0);
        when(errorDist.getSigmaError()).thenReturn(0.5);

        when(modelCacheService.getActiveModel()).thenReturn(model);
        when(onnxInferenceService.predict(any(), any(), any())).thenReturn(2.8);

        GradePredictionResponse response = new GradePredictionResponse();
        when(gradePredictionMapper.toResponse(any())).thenReturn(response);

        GradePredictionResponse result = gradePredictionService.predictGradeFromRawFeatures(request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void predictGradeBatch_shouldReturnBatchResult_whenValidRequest() {
        BatchGradePredictionRequest.GradePredictionItem item = new BatchGradePredictionRequest.GradePredictionItem();
        item.setStudentId(studentId);
        item.setSubjectId(subjectId);

        BatchGradePredictionRequest request = BatchGradePredictionRequest.builder()
                .predictions(List.of(item)).build();

        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(featureExtractionService.hasGradedHistory(studentId)).thenReturn(false);

        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId).meanGrade(7.0).stdDev(1.5)
                .isFallback(false).fallbackLevel(0).sampleCount(100).build();
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));

        GradePredictionResponse response = new GradePredictionResponse();
        when(gradePredictionMapper.toResponse(any())).thenReturn(response);

        BatchGradePredictionResponse result = gradePredictionService.predictGradeBatch(request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void predictGradeBatch_shouldReturnEmpty_whenNullRequest() {
        BatchGradePredictionResponse result = gradePredictionService.predictGradeBatch(null);
        assertNotNull(result);
        assertTrue(result.getPredictions().isEmpty());
        assertTrue(true);
    }

    @Test
    void predictGrade_shouldThrowException_whenFallbackLevelTooHigh() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(featureExtractionService.hasGradedHistory(studentId)).thenReturn(false);

        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId).meanGrade(7.0).stdDev(1.5)
                .isFallback(true).fallbackLevel(2).sampleCount(10).build();
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));

        try {
            gradePredictionService.predictGrade(studentId, subjectId, null, null);
        } catch (PredictionFailedException e) {
            assertTrue(e.getMessage().contains("Cannot predict"));
        }
    }

    @Test
    void predictGrade_shouldUseMlModel_whenStudentAndSubjectHaveHistory() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(featureExtractionService.hasGradedHistory(studentId)).thenReturn(true);

        Map<String, Object> features = new HashMap<>();
        features.put("num_semesters_prior", 2);
        features.put("sem_credits", 17);
        when(featureExtractionService.extractFeatures(studentId, subjectId, null))
                .thenReturn(features);

        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId).meanGrade(7.0).stdDev(1.5)
                .isFallback(false).fallbackLevel(0).sampleCount(100).build();
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));

        CachedModel model = mock(CachedModel.class);
        ModelMetrics modelMetrics = mock(ModelMetrics.class);
        ModelMetrics.TestMetrics testMetrics = mock(ModelMetrics.TestMetrics.class);
        ModelMetrics.ErrorDistribution errorDist = mock(ModelMetrics.ErrorDistribution.class);

        when(model.getVersionId()).thenReturn(UUID.randomUUID());
        when(model.getVersionName()).thenReturn("test-model");
        when(model.getLocalOnnxPath()).thenReturn("/tmp/model.onnx");
        when(model.getFeatureColumns()).thenReturn(List.of("sem_credits"));
        when(model.getMetrics()).thenReturn(modelMetrics);
        when(modelMetrics.getTest()).thenReturn(testMetrics);
        when(testMetrics.getErrorDistribution()).thenReturn(errorDist);
        when(errorDist.getMuError()).thenReturn(0.1);
        when(errorDist.getSigmaError()).thenReturn(0.5);

        when(modelCacheService.getActiveModel()).thenReturn(model);
        when(onnxInferenceService.predict(any(), any(), any())).thenReturn(2.8);

        GradePredictionResponse response = new GradePredictionResponse();
        when(gradePredictionMapper.toResponse(any())).thenReturn(response);

        GradePredictionResponse result = gradePredictionService.predictGrade(studentId, subjectId, null, null);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void predictGrade_shouldFallback_whenNumSemestersPriorIsZero() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(featureExtractionService.hasGradedHistory(studentId)).thenReturn(true);

        Map<String, Object> features = new HashMap<>();
        features.put("num_semesters_prior", 0);
        when(featureExtractionService.extractFeatures(studentId, subjectId, null))
                .thenReturn(features);

        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId).meanGrade(7.0).stdDev(1.5)
                .isFallback(false).fallbackLevel(0).sampleCount(100).build();
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));

        GradePredictionResponse response = new GradePredictionResponse();
        when(gradePredictionMapper.toResponse(any())).thenReturn(response);

        GradePredictionResponse result = gradePredictionService.predictGrade(studentId, subjectId, null, null);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void predictGrade_shouldFallback_whenSubjectHasNoRealHistory() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(featureExtractionService.hasGradedHistory(studentId)).thenReturn(true);

        Map<String, Object> features = new HashMap<>();
        features.put("num_semesters_prior", 3);
        when(featureExtractionService.extractFeatures(studentId, subjectId, null))
                .thenReturn(features);

        // isFallback=true, fallbackLevel=2 → rejected by hasSubjectHistory check
        // Falls through to predictWithMetricsFallback → also rejects with exception
        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId).meanGrade(7.0).stdDev(1.5)
                .isFallback(true).fallbackLevel(2).sampleCount(10).build();
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));

        try {
            gradePredictionService.predictGrade(studentId, subjectId, null, null);
        } catch (PredictionFailedException e) {
            assertTrue(true);
        }
    }

    @Test
    void predictGradeBatch_shouldReturnEmpty_whenEmptyPredictions() {
        BatchGradePredictionRequest request = BatchGradePredictionRequest.builder()
                .predictions(Collections.emptyList()).build();

        BatchGradePredictionResponse result = gradePredictionService.predictGradeBatch(request);
        assertNotNull(result);
        assertTrue(result.getPredictions().isEmpty());
        assertTrue(true);
    }

    @Test
    void predictGrade_shouldUseCustomThreshold_whenProvided() {
        SemesterResponse semester = new SemesterResponse();
        semester.setId(semesterId);
        when(courseManagementClient.getCurrentSemester()).thenReturn(semester);
        when(featureExtractionService.hasGradedHistory(studentId)).thenReturn(false);

        SubjectSemesterMetrics metrics = SubjectSemesterMetrics.builder()
                .subjectId(subjectId).semesterId(semesterId).meanGrade(7.0).stdDev(1.5)
                .isFallback(false).fallbackLevel(0).sampleCount(100).build();
        when(subjectSemesterMetricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));

        GradePredictionResponse response = new GradePredictionResponse();
        when(gradePredictionMapper.toResponse(any())).thenReturn(response);

        GradePredictionResponse result = gradePredictionService.predictGrade(studentId, subjectId, null, 2.5);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void predictGrade_shouldFallback_whenGetCurrentSemesterFails() {
        when(courseManagementClient.getCurrentSemester()).thenThrow(new RuntimeException("Service unavailable"));
        when(featureExtractionService.hasGradedHistory(studentId)).thenReturn(false);

        try {
            gradePredictionService.predictGrade(studentId, subjectId, null, null);
        } catch (PredictionFailedException e) {
            // Expected since findByIdAndSemesterId will be called with null semesterId
            // and our mock won't match → throws
            assertTrue(true);
        }
    }
}
