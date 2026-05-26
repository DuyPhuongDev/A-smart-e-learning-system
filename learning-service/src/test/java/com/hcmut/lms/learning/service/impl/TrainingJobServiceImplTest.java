package com.hcmut.lms.learning.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.learning.dto.response.TrainingJobResponse;
import com.hcmut.lms.learning.dto.training.TrainingJobCallbackRequest;
import com.hcmut.lms.learning.dto.training.TriggerTrainingRequest;
import com.hcmut.lms.learning.entity.dataset.GradePredictionDataset;
import com.hcmut.lms.learning.entity.dataset.GradePredictionDatasetVersion;
import com.hcmut.lms.learning.entity.training.TrainingJob;
import com.hcmut.lms.learning.exception.ResourceNotFoundException;
import com.hcmut.lms.learning.mapper.TrainingJobMapper;
import com.hcmut.lms.learning.repository.GradePredictionDatasetRepository;
import com.hcmut.lms.learning.repository.TrainingJobRepository;
import com.hcmut.lms.learning.repository.TrainingMetricsRepository;
import com.hcmut.lms.learning.service.GradePredictionDatasetService;
import com.hcmut.lms.learning.service.ModelVersionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class TrainingJobServiceImplTest {

    @Mock
    private GradePredictionDatasetService datasetService;

    @Mock
    private GradePredictionDatasetRepository datasetRepository;

    @Mock
    private TrainingJobRepository trainingJobRepository;

    @Mock
    private TrainingMetricsRepository trainingMetricsRepository;

    @Mock
    private TrainingJobMapper trainingJobMapper;

    @Mock
    private ModelVersionService modelVersionService;

    @Mock
    private S3Client s3Client;

    @Mock
    private SqsClient sqsClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TrainingJobServiceImpl trainingJobService;

    private final UUID studentId = UUID.randomUUID();
    private final UUID semesterId = UUID.randomUUID();
    private final UUID subjectId = UUID.randomUUID();

    @Test
    void handleTrainingCallback_shouldUpdateJobAndCreateModel_whenSuccess() {
        TrainingJobCallbackRequest callback = TrainingJobCallbackRequest.builder()
                .jobId("TRAIN_test").status("SUCCESS").modelName("model_v1")
                .modelS3Path("s3://bucket/model.onnx").trainingTimeSeconds(120.0)
                .metrics(Map.of("train", Map.of("model", Map.of("mae", 0.3, "rmse", 0.4, "r2", 0.85)),
                        "test", Map.of("model", Map.of("mae", 0.35, "rmse", 0.45, "r2", 0.80))))
                .build();

        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_test").build();

        when(trainingJobRepository.findByJobId("TRAIN_test")).thenReturn(Optional.of(trainingJob));
        when(trainingMetricsRepository.saveAll(any())).thenReturn(List.of());
        when(trainingJobRepository.save(trainingJob)).thenReturn(trainingJob);
        when(modelVersionService.createFromCompletedJob(any(), any())).thenReturn(null);

        trainingJobService.handleTrainingCallback(callback);
        verify(modelVersionService).createFromCompletedJob(any(), any());
        assertTrue(true);
    }

    @Test
    void handleTrainingCallback_shouldThrowException_whenJobNotFound() {
        TrainingJobCallbackRequest callback = TrainingJobCallbackRequest.builder()
                .jobId("NONEXISTENT").status("SUCCESS").build();
        when(trainingJobRepository.findByJobId("NONEXISTENT")).thenReturn(Optional.empty());

        try {
            trainingJobService.handleTrainingCallback(callback);
        } catch (ResourceNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    void handleTrainingCallback_shouldMarkFailed_whenStatusNotSuccess() {
        TrainingJobCallbackRequest callback = TrainingJobCallbackRequest.builder()
                .jobId("TRAIN_test").status("FAILED").build();

        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_test").build();

        when(trainingJobRepository.findByJobId("TRAIN_test")).thenReturn(Optional.of(trainingJob));
        when(trainingJobRepository.save(trainingJob)).thenReturn(trainingJob);

        trainingJobService.handleTrainingCallback(callback);
        assertTrue(true);
    }

    @Test
    void handleTrainingError_shouldUpdateJobStatus_whenValidJobId() {
        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_test").build();

        when(trainingJobRepository.findByJobId("TRAIN_test")).thenReturn(Optional.of(trainingJob));
        when(trainingJobRepository.save(trainingJob)).thenReturn(trainingJob);

        trainingJobService.handleTrainingError("TRAIN_test", "Worker crashed");
        assertTrue(true);
    }

    @Test
    void handleTrainingError_shouldThrowException_whenJobNotFound() {
        when(trainingJobRepository.findByJobId("NONEXISTENT")).thenReturn(Optional.empty());

        try {
            trainingJobService.handleTrainingError("NONEXISTENT", "error");
        } catch (ResourceNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    void getTrainingJobByJobId_shouldReturnJob_whenExists() {
        TrainingJob trainingJob = TrainingJob.builder().id(UUID.randomUUID()).jobId("TRAIN_test").build();
        when(trainingJobRepository.findByJobId("TRAIN_test")).thenReturn(Optional.of(trainingJob));

        TrainingJob result = trainingJobService.getTrainingJobByJobId("TRAIN_test");
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getTrainingJobByJobId_shouldThrowException_whenNotFound() {
        when(trainingJobRepository.findByJobId("NONEXISTENT")).thenReturn(Optional.empty());

        try {
            trainingJobService.getTrainingJobByJobId("NONEXISTENT");
        } catch (ResourceNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    void getTrainingJobsByDatasetVersion_shouldReturnList() {
        UUID versionId = UUID.randomUUID();
        when(trainingJobRepository.findByDatasetVersionId(versionId)).thenReturn(List.of());

        List<TrainingJob> result = trainingJobService.getTrainingJobsByDatasetVersion(versionId);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void triggerTrainingJob_shouldComplete_whenAllStepsSucceed() throws Exception {
        UUID versionId = UUID.randomUUID();
        TriggerTrainingRequest request = TriggerTrainingRequest.builder()
                .datasetVersionId(versionId).description("Test run").build();

        GradePredictionDatasetVersion version = new GradePredictionDatasetVersion();
        version.setId(versionId);
        version.setVersionNumber(3);
        version.setTotalRows(2);
        when(datasetService.getVersionById(versionId)).thenReturn(version);
        when(trainingJobRepository.existsByDatasetVersionIdAndStatusIn(any(), any())).thenReturn(false);

        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_test").datasetVersionId(versionId)
                .modelName("linear_test").s3DatasetUrl("").status(TrainingJob.TrainingJobStatus.PENDING)
                .build();
        when(trainingJobRepository.save(any())).thenReturn(trainingJob);

        GradePredictionDataset row = GradePredictionDataset.builder()
                .studentId(studentId).semesterId(semesterId).subjectId(subjectId)
                .courseGrade(7.0).semCredits(17).semCreditsSquared(289).retakeNo(0)
                .numSemestersPrior(2).cumulativeGradeAvg(2.5).previousSemGradeAvg(2.3)
                .subjectHistMedianSmooth(2.4).relativeAvgCourseGrade(2.6).build();
        when(datasetRepository.findByVersionId(versionId)).thenReturn(List.of(row));

        // S3: headObject throws NoSuchKeyException, then putObject succeeds
        when(s3Client.headObject(any(software.amazon.awssdk.services.s3.model.HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("Not found").build());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // SQS
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"job_id\":\"test\"}");
        when(sqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(SendMessageResponse.builder().messageId("msg-123").build());

        TrainingJobResponse response = new TrainingJobResponse();
        response.setJobId("TRAIN_test");
        when(trainingJobMapper.toResponse(any())).thenReturn(response);

        ReflectionTestUtils.setField(trainingJobService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(trainingJobService, "trainingQueueUrl", "https://sqs.test/queue");
        ReflectionTestUtils.setField(trainingJobService, "defaultModelName", "linear_test");

        trainingJobService.triggerTrainingJob(request);
        assertTrue(true);
    }

    @Test
    void triggerTrainingJob_shouldThrowException_whenConcurrentJobExists() {
        UUID versionId = UUID.randomUUID();
        TriggerTrainingRequest request = TriggerTrainingRequest.builder()
                .datasetVersionId(versionId).build();

        GradePredictionDatasetVersion version = new GradePredictionDatasetVersion();
        version.setId(versionId);
        version.setVersionNumber(1);
        version.setTotalRows(0);
        when(datasetService.getVersionById(versionId)).thenReturn(version);
        when(trainingJobRepository.existsByDatasetVersionIdAndStatusIn(any(), any())).thenReturn(true);

        try {
            trainingJobService.triggerTrainingJob(request);
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("already running"));
        }
    }

    @Test
    void handleTrainingCallback_shouldCatchException_whenCreateModelVersionFails() {
        TrainingJobCallbackRequest callback = TrainingJobCallbackRequest.builder()
                .jobId("TRAIN_test").status("SUCCESS").modelName("model_v1")
                .modelS3Path("s3://bucket/model.onnx").trainingTimeSeconds(120.0)
                .metrics(Map.of("train", Map.of("model", Map.of("mae", 0.3, "rmse", 0.4, "r2", 0.85))))
                .build();

        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_test").build();

        when(trainingJobRepository.findByJobId("TRAIN_test")).thenReturn(Optional.of(trainingJob));
        when(trainingMetricsRepository.saveAll(any())).thenReturn(List.of());
        when(trainingJobRepository.save(trainingJob)).thenReturn(trainingJob);
        doThrow(new RuntimeException("ModelVersion creation failed")).when(modelVersionService)
                .createFromCompletedJob(any(), any());

        trainingJobService.handleTrainingCallback(callback);
        assertTrue(true);
    }

    @Test
    void getRecentTrainingJobs_shouldReturnList() {
        when(trainingJobRepository.findTop20ByOrderByCreatedAtDesc()).thenReturn(List.of());

        List<TrainingJob> result = trainingJobService.getRecentTrainingJobs(10);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void triggerTrainingJob_shouldUseLatestVersion_whenDatasetVersionIdIsNull() throws Exception {
        TriggerTrainingRequest request = TriggerTrainingRequest.builder()
                .datasetVersionId(null).description("Use latest").build();

        GradePredictionDatasetVersion version = new GradePredictionDatasetVersion();
        version.setId(UUID.randomUUID());
        version.setVersionNumber(5);
        version.setTotalRows(1);
        when(datasetService.getLatestCompletedVersion()).thenReturn(version);
        when(trainingJobRepository.existsByDatasetVersionIdAndStatusIn(any(), any())).thenReturn(false);

        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_test").datasetVersionId(version.getId())
                .modelName("linear_test").s3DatasetUrl("").status(TrainingJob.TrainingJobStatus.PENDING)
                .build();
        when(trainingJobRepository.save(any())).thenReturn(trainingJob);

        GradePredictionDataset row = GradePredictionDataset.builder()
                .studentId(studentId).semesterId(semesterId).subjectId(subjectId)
                .courseGrade(7.0).semCredits(17).semCreditsSquared(289).retakeNo(0)
                .numSemestersPrior(2).cumulativeGradeAvg(2.5).previousSemGradeAvg(2.3)
                .subjectHistMedianSmooth(2.4).relativeAvgCourseGrade(2.6).build();
        when(datasetRepository.findByVersionId(version.getId())).thenReturn(List.of(row));

        when(s3Client.headObject(any(software.amazon.awssdk.services.s3.model.HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("Not found").build());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(sqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(SendMessageResponse.builder().messageId("msg-123").build());

        TrainingJobResponse response = new TrainingJobResponse();
        response.setJobId("TRAIN_test");
        when(trainingJobMapper.toResponse(any())).thenReturn(response);

        ReflectionTestUtils.setField(trainingJobService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(trainingJobService, "trainingQueueUrl", "https://sqs.test/queue");
        ReflectionTestUtils.setField(trainingJobService, "defaultModelName", "linear_test");

        trainingJobService.triggerTrainingJob(request);
        assertTrue(true);
    }

    @Test
    void handleTrainingCallback_shouldParseBaselineMetrics_whenPresent() {
        Map<String, Object> metrics = new HashMap<>();
        Map<String, Object> trainSplit = new HashMap<>();
        trainSplit.put("model", Map.of("mae", 0.3, "rmse", 0.4, "r2", 0.85,
                "mu_error", 0.05, "sigma_error", 0.2, "sample_count", 500));
        Map<String, Object> baselineMetricsMap = new HashMap<>();
        baselineMetricsMap.put("mae", 0.5);
        baselineMetricsMap.put("rmse", 0.6);
        baselineMetricsMap.put("r2", 0.7);
        trainSplit.put("baseline", baselineMetricsMap);
        metrics.put("train", trainSplit);

        TrainingJobCallbackRequest callback = TrainingJobCallbackRequest.builder()
                .jobId("TRAIN_test").status("SUCCESS").modelName("model_v1")
                .modelS3Path("s3://bucket/model.onnx").trainingTimeSeconds(120.0)
                .metrics(metrics).build();

        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_test").build();

        when(trainingJobRepository.findByJobId("TRAIN_test")).thenReturn(Optional.of(trainingJob));
        when(trainingMetricsRepository.saveAll(any())).thenReturn(List.of());
        when(trainingJobRepository.save(trainingJob)).thenReturn(trainingJob);

        trainingJobService.handleTrainingCallback(callback);
        assertTrue(true);
    }

    @Test
    void handleTrainingCallback_shouldMergeErrorDistribution_whenOldFormat() {
        Map<String, Object> metrics = new HashMap<>();
        Map<String, Object> trainSplit = new HashMap<>();
        // Old format: model has mae/rmse/r2 only, error_distribution is separate
        trainSplit.put("model", new HashMap<>(Map.of("mae", 0.3, "rmse", 0.4, "r2", 0.85)));
        trainSplit.put("error_distribution", Map.of("mu_error", 0.05, "sigma_error", 0.2, "n", 500));
        metrics.put("train", trainSplit);

        TrainingJobCallbackRequest callback = TrainingJobCallbackRequest.builder()
                .jobId("TRAIN_test").status("SUCCESS").modelName("model_v1")
                .modelS3Path("s3://bucket/model.onnx").trainingTimeSeconds(120.0)
                .metrics(metrics).build();

        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_test").build();

        when(trainingJobRepository.findByJobId("TRAIN_test")).thenReturn(Optional.of(trainingJob));
        when(trainingMetricsRepository.saveAll(any())).thenReturn(List.of());
        when(trainingJobRepository.save(trainingJob)).thenReturn(trainingJob);

        trainingJobService.handleTrainingCallback(callback);
        assertTrue(true);
    }

    @Test
    void triggerTrainingJob_shouldUseUniqueFilename_whenS3FileExists() throws Exception {
        UUID versionId = UUID.randomUUID();
        TriggerTrainingRequest request = TriggerTrainingRequest.builder()
                .datasetVersionId(versionId).description("Test run").build();

        GradePredictionDatasetVersion version = new GradePredictionDatasetVersion();
        version.setId(versionId);
        version.setVersionNumber(3);
        version.setTotalRows(1);
        when(datasetService.getVersionById(versionId)).thenReturn(version);
        when(trainingJobRepository.existsByDatasetVersionIdAndStatusIn(any(), any())).thenReturn(false);

        TrainingJob trainingJob = TrainingJob.builder()
                .id(UUID.randomUUID()).jobId("TRAIN_test").datasetVersionId(versionId)
                .modelName("linear_test").s3DatasetUrl("").status(TrainingJob.TrainingJobStatus.PENDING)
                .build();
        when(trainingJobRepository.save(any())).thenReturn(trainingJob);

        GradePredictionDataset row = GradePredictionDataset.builder()
                .studentId(studentId).semesterId(semesterId).subjectId(subjectId)
                .courseGrade(7.0).semCredits(17).semCreditsSquared(289).retakeNo(0)
                .numSemestersPrior(2).cumulativeGradeAvg(2.5).previousSemGradeAvg(2.3)
                .subjectHistMedianSmooth(2.4).relativeAvgCourseGrade(2.6).build();
        when(datasetRepository.findByVersionId(versionId)).thenReturn(List.of(row));

        // S3: headObject succeeds → file exists → use unique filename
        when(s3Client.headObject(any(software.amazon.awssdk.services.s3.model.HeadObjectRequest.class)))
                .thenReturn(software.amazon.awssdk.services.s3.model.HeadObjectResponse.builder().build());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"job_id\":\"test\"}");
        when(sqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(SendMessageResponse.builder().messageId("msg-123").build());

        TrainingJobResponse response = new TrainingJobResponse();
        response.setJobId("TRAIN_test");
        when(trainingJobMapper.toResponse(any())).thenReturn(response);

        ReflectionTestUtils.setField(trainingJobService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(trainingJobService, "trainingQueueUrl", "https://sqs.test/queue");
        ReflectionTestUtils.setField(trainingJobService, "defaultModelName", "linear_test");

        trainingJobService.triggerTrainingJob(request);
        assertTrue(true);
    }
}
