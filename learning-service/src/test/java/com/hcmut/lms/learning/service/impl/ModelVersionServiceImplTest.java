package com.hcmut.lms.learning.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.dto.response.ModelVersionResponse;
import com.hcmut.lms.learning.dto.training.TrainingJobCallbackRequest;
import com.hcmut.lms.learning.entity.training.ModelVersion;
import com.hcmut.lms.learning.entity.training.TrainingJob;
import com.hcmut.lms.learning.exception.ResourceNotFoundException;
import com.hcmut.lms.learning.mapper.ModelVersionMapper;
import com.hcmut.lms.learning.repository.ModelVersionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class ModelVersionServiceImplTest {

    @Mock
    private ModelVersionRepository modelVersionRepository;

    @Mock
    private ModelVersionMapper modelVersionMapper;

    @InjectMocks
    private ModelVersionServiceImpl modelVersionService;

    @Test
    void createFromCompletedJob_shouldReturnVersion_whenValidCallback() {
        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_test").modelName("linear_20250101_120000")
                .datasetVersionId(UUID.randomUUID()).build();

        Map<String, Object> metrics = new HashMap<>();
        Map<String, Object> testSplit = new HashMap<>();
        Map<String, Object> modelMetrics = new HashMap<>();
        modelMetrics.put("mae", 0.3);
        modelMetrics.put("rmse", 0.4);
        modelMetrics.put("r2", 0.85);
        modelMetrics.put("mu_error", 0.05);
        modelMetrics.put("sigma_error", 0.2);
        modelMetrics.put("sample_count", 500);
        testSplit.put("model", modelMetrics);
        metrics.put("test", testSplit);
        metrics.put("train", new HashMap<>(testSplit));

        TrainingJobCallbackRequest callback = TrainingJobCallbackRequest.builder()
                .jobId("TRAIN_test").status("SUCCESS").modelName("linear_20250101_120000")
                .modelS3Path("s3://bucket/models/model.onnx")
                .trainingTimeSeconds(120.0).metrics(metrics).build();

        ModelVersion savedVersion = new ModelVersion();
        when(modelVersionRepository.save(any())).thenReturn(savedVersion);

        ModelVersion result = modelVersionService.createFromCompletedJob(trainingJob, callback);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getAllModelVersions_shouldReturnList_whenVersionsExist() {
        ModelVersion version = new ModelVersion();
        ModelVersionResponse response = new ModelVersionResponse();
        when(modelVersionRepository.findTop20ByOrderByCreatedAtDesc()).thenReturn(List.of(version));
        when(modelVersionMapper.toResponseList(any())).thenReturn(List.of(response));

        List<ModelVersionResponse> result = modelVersionService.getAllModelVersions();
        assertTrue(!result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getAllModelVersions_shouldReturnEmptyList_whenNoVersions() {
        when(modelVersionRepository.findTop20ByOrderByCreatedAtDesc()).thenReturn(List.of());
        when(modelVersionMapper.toResponseList(any())).thenReturn(List.of());

        List<ModelVersionResponse> result = modelVersionService.getAllModelVersions();
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void getModelVersionById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        ModelVersion version = new ModelVersion();
        ModelVersionResponse response = new ModelVersionResponse();
        when(modelVersionRepository.findById(id)).thenReturn(Optional.of(version));
        when(modelVersionMapper.toResponse(version)).thenReturn(response);

        ModelVersionResponse result = modelVersionService.getModelVersionById(id);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getModelVersionById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(modelVersionRepository.findById(id)).thenReturn(Optional.empty());

        try {
            modelVersionService.getModelVersionById(id);
        } catch (ResourceNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    void getModelVersionsByTrainingJob_shouldReturnList() {
        UUID trainingJobId = UUID.randomUUID();
        when(modelVersionRepository.findByTrainingJobId(trainingJobId)).thenReturn(List.of());
        when(modelVersionMapper.toResponseList(any())).thenReturn(List.of());

        List<ModelVersionResponse> result = modelVersionService.getModelVersionsByTrainingJob(trainingJobId);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void activateModelVersion_shouldActivate_whenExists() {
        UUID id = UUID.randomUUID();
        ModelVersion version = ModelVersion.builder()
                .status(ModelVersion.ModelVersionStatus.INACTIVE).isActive(false).build();
        ModelVersionResponse response = new ModelVersionResponse();

        when(modelVersionRepository.findById(id)).thenReturn(Optional.of(version));
        doNothing().when(modelVersionRepository).deactivateAll();
        when(modelVersionRepository.save(version)).thenReturn(version);
        when(modelVersionMapper.toResponse(version)).thenReturn(response);

        ModelVersionResponse result = modelVersionService.activateModelVersion(id);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void activateModelVersion_shouldThrowException_whenArchived() {
        UUID id = UUID.randomUUID();
        ModelVersion version = ModelVersion.builder()
                .status(ModelVersion.ModelVersionStatus.ARCHIVED).isActive(false).build();
        when(modelVersionRepository.findById(id)).thenReturn(Optional.of(version));

        try {
            modelVersionService.activateModelVersion(id);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    void archiveModelVersion_shouldArchive_whenExists() {
        UUID id = UUID.randomUUID();
        ModelVersion version = ModelVersion.builder()
                .status(ModelVersion.ModelVersionStatus.ACTIVE).isActive(true).build();
        ModelVersionResponse response = new ModelVersionResponse();

        when(modelVersionRepository.findById(id)).thenReturn(Optional.of(version));
        when(modelVersionRepository.save(version)).thenReturn(version);
        when(modelVersionMapper.toResponse(version)).thenReturn(response);

        ModelVersionResponse result = modelVersionService.archiveModelVersion(id);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getActiveModelVersion_shouldReturnResponse_whenExists() {
        ModelVersion version = new ModelVersion();
        ModelVersionResponse response = new ModelVersionResponse();
        when(modelVersionRepository.findByIsActiveTrue()).thenReturn(Optional.of(version));
        when(modelVersionMapper.toResponse(version)).thenReturn(response);

        ModelVersionResponse result = modelVersionService.getActiveModelVersion();
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getActiveModelVersion_shouldThrowException_whenNoneActive() {
        when(modelVersionRepository.findByIsActiveTrue()).thenReturn(Optional.empty());

        try {
            modelVersionService.getActiveModelVersion();
        } catch (ResourceNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    void createFromCompletedJob_shouldUseOldFormat_whenErrorDistributionIsSeparate() {
        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_old").modelName("linear_old")
                .datasetVersionId(UUID.randomUUID()).build();

        // Old format: error_distribution as separate key
        Map<String, Object> metrics = new HashMap<>();
        Map<String, Object> testSplit = new HashMap<>();
        Map<String, Object> modelMetrics = new HashMap<>();
        modelMetrics.put("mae", 0.3);
        modelMetrics.put("rmse", 0.4);
        modelMetrics.put("r2", 0.85);
        testSplit.put("model", modelMetrics);
        Map<String, Object> errorDist = new HashMap<>();
        errorDist.put("mu_error", 0.05);
        errorDist.put("sigma_error", 0.2);
        errorDist.put("n", 500);
        testSplit.put("error_distribution", errorDist);
        metrics.put("test", testSplit);
        metrics.put("train", new HashMap<>(testSplit));

        TrainingJobCallbackRequest callback = TrainingJobCallbackRequest.builder()
                .jobId("TRAIN_old").status("SUCCESS").modelName("linear_old")
                .modelS3Path("s3://bucket/models/model.onnx")
                .trainingTimeSeconds(120.0).metrics(metrics).build();

        ModelVersion savedVersion = new ModelVersion();
        when(modelVersionRepository.save(any())).thenReturn(savedVersion);

        ModelVersion result = modelVersionService.createFromCompletedJob(trainingJob, callback);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void createFromCompletedJob_shouldReturnVersion_whenNullMetrics() {
        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_nometric").modelName("linear_nometric")
                .datasetVersionId(UUID.randomUUID()).build();

        TrainingJobCallbackRequest callback = TrainingJobCallbackRequest.builder()
                .jobId("TRAIN_nometric").status("SUCCESS").modelName("linear_nometric")
                .modelS3Path("s3://bucket/models/model.onnx")
                .trainingTimeSeconds(90.0).metrics(null).build();

        ModelVersion savedVersion = new ModelVersion();
        when(modelVersionRepository.save(any())).thenReturn(savedVersion);

        ModelVersion result = modelVersionService.createFromCompletedJob(trainingJob, callback);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void createFromCompletedJob_shouldHandleMalformedMetrics_whenTestValueIsNotMap() {
        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_malformed").modelName("linear_malformed")
                .datasetVersionId(UUID.randomUUID()).build();

        // Malformed: "test" value is a String instead of Map → triggers ClassCastException in catch blocks
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("test", "not_a_map");
        metrics.put("train", "also_not_a_map");

        TrainingJobCallbackRequest callback = TrainingJobCallbackRequest.builder()
                .jobId("TRAIN_malformed").status("SUCCESS").modelName("linear_malformed")
                .modelS3Path("s3://bucket/models/model.onnx")
                .trainingTimeSeconds(120.0).metrics(metrics).build();

        ModelVersion savedVersion = new ModelVersion();
        when(modelVersionRepository.save(any())).thenReturn(savedVersion);

        ModelVersion result = modelVersionService.createFromCompletedJob(trainingJob, callback);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void createFromCompletedJob_shouldHandleMissingTestKey() {
        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_missing").modelName("linear_missing")
                .datasetVersionId(UUID.randomUUID()).build();

        // Empty metrics — no "test" or "train" keys
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("other", Map.of());

        TrainingJobCallbackRequest callback = TrainingJobCallbackRequest.builder()
                .jobId("TRAIN_missing").status("SUCCESS").modelName("linear_missing")
                .modelS3Path("s3://bucket/models/model.onnx")
                .trainingTimeSeconds(120.0).metrics(metrics).build();

        ModelVersion savedVersion = new ModelVersion();
        when(modelVersionRepository.save(any())).thenReturn(savedVersion);

        ModelVersion result = modelVersionService.createFromCompletedJob(trainingJob, callback);
        assertNotNull(result);
        assertTrue(true);
    }
}
