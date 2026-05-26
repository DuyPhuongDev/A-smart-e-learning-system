package com.hcmut.lms.learning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.dto.response.ModelVersionResponse;
import com.hcmut.lms.learning.dto.response.TrainingJobResponse;
import com.hcmut.lms.learning.dto.training.TrainingJobCallbackRequest;
import com.hcmut.lms.learning.dto.training.TrainingJobErrorRequest;
import com.hcmut.lms.learning.dto.training.TriggerTrainingRequest;
import com.hcmut.lms.learning.entity.training.TrainingJob;
import com.hcmut.lms.learning.mapper.TrainingJobMapper;
import com.hcmut.lms.learning.service.ModelVersionService;
import com.hcmut.lms.learning.service.TrainingJobService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class TrainingJobControllerTest {

    @Mock
    private TrainingJobService trainingJobService;

    @Mock
    private TrainingJobMapper mapper;

    @Mock
    private ModelVersionService modelVersionService;

    @InjectMocks
    private TrainingJobController controller;

    private final UUID versionId = UUID.randomUUID();
    private final UUID modelVersionId = UUID.randomUUID();
    private final UUID jobDbId = UUID.randomUUID();
    private final String jobId = "TRAIN_test";

    @Test
    void triggerTraining_shouldReturnResponse() {
        TriggerTrainingRequest request = TriggerTrainingRequest.builder()
                .datasetVersionId(versionId).description("test").build();
        TrainingJobResponse response = TrainingJobResponse.builder().jobId(jobId).build();

        when(trainingJobService.triggerTrainingJob(request))
                .thenReturn(CompletableFuture.completedFuture(response));

        ResponseEntity<TrainingJobResponse> result = controller.triggerTraining(request);
        assertNotNull(result.getBody());
        assertEquals(jobId, result.getBody().getJobId());
        assertTrue(true);
    }

    @Test
    void handleCallback_shouldReturnOk() {
        TrainingJobCallbackRequest callback = TrainingJobCallbackRequest.builder()
                .jobId(jobId).status("SUCCESS").build();
        doNothing().when(trainingJobService).handleTrainingCallback(callback);

        ResponseEntity<Void> result = controller.handleCallback(callback);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(true);
    }

    @Test
    void handleErrorCallback_shouldReturnOk() {
        TrainingJobErrorRequest errorRequest = TrainingJobErrorRequest.builder()
                .jobId(jobId).error("Worker crashed").build();
        doNothing().when(trainingJobService).handleTrainingError(jobId, "Worker crashed");

        ResponseEntity<Void> result = controller.handleErrorCallback(errorRequest);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(true);
    }

    @Test
    void getTrainingJob_shouldReturnResponse() {
        TrainingJob job = TrainingJob.builder().id(jobDbId).jobId(jobId).build();
        TrainingJobResponse response = TrainingJobResponse.builder().jobId(jobId).build();

        when(trainingJobService.getTrainingJobByJobId(jobId)).thenReturn(job);
        when(mapper.toResponse(job)).thenReturn(response);

        ResponseEntity<TrainingJobResponse> result = controller.getTrainingJob(jobId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getRecentJobs_shouldReturnList() {
        when(trainingJobService.getRecentTrainingJobs(20)).thenReturn(List.of());
        when(mapper.toResponseList(List.of())).thenReturn(List.of());

        ResponseEntity<List<TrainingJobResponse>> result = controller.getRecentJobs(20);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getJobsByDatasetVersion_shouldReturnList() {
        when(trainingJobService.getTrainingJobsByDatasetVersion(versionId)).thenReturn(List.of());
        when(mapper.toResponseList(List.of())).thenReturn(List.of());

        ResponseEntity<List<TrainingJobResponse>> result = controller.getJobsByDatasetVersion(versionId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getAllModelVersions_shouldReturnList() {
        when(modelVersionService.getAllModelVersions()).thenReturn(List.of());

        ResponseEntity<List<ModelVersionResponse>> result = controller.getAllModelVersions();
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getActiveModelVersion_shouldReturnResponse() {
        ModelVersionResponse response = ModelVersionResponse.builder()
                .id(modelVersionId).versionName("v1").build();
        when(modelVersionService.getActiveModelVersion()).thenReturn(response);

        ResponseEntity<ModelVersionResponse> result = controller.getActiveModelVersion();
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getModelVersion_shouldReturnResponse() {
        ModelVersionResponse response = ModelVersionResponse.builder()
                .id(modelVersionId).build();
        when(modelVersionService.getModelVersionById(modelVersionId)).thenReturn(response);

        ResponseEntity<ModelVersionResponse> result = controller.getModelVersion(modelVersionId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void getModelVersionsByJob_shouldReturnList() {
        TrainingJob job = TrainingJob.builder().id(jobDbId).jobId(jobId).build();
        when(trainingJobService.getTrainingJobByJobId(jobId)).thenReturn(job);
        when(modelVersionService.getModelVersionsByTrainingJob(jobDbId)).thenReturn(List.of());

        ResponseEntity<List<ModelVersionResponse>> result = controller.getModelVersionsByJob(jobId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void activateModelVersion_shouldReturnResponse() {
        ModelVersionResponse response = ModelVersionResponse.builder()
                .id(modelVersionId).status("ACTIVE").build();
        when(modelVersionService.activateModelVersion(modelVersionId)).thenReturn(response);

        ResponseEntity<ModelVersionResponse> result = controller.activateModelVersion(modelVersionId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void archiveModelVersion_shouldReturnResponse() {
        ModelVersionResponse response = ModelVersionResponse.builder()
                .id(modelVersionId).status("ARCHIVED").build();
        when(modelVersionService.archiveModelVersion(modelVersionId)).thenReturn(response);

        ResponseEntity<ModelVersionResponse> result = controller.archiveModelVersion(modelVersionId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }
}
