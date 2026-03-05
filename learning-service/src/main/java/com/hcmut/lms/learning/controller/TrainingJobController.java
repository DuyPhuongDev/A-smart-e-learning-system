package com.hcmut.lms.learning.controller;

import com.hcmut.lms.learning.dto.response.ModelVersionResponse;
import com.hcmut.lms.learning.dto.response.TrainingJobResponse;
import com.hcmut.lms.learning.dto.training.*;
import com.hcmut.lms.learning.mapper.TrainingJobMapper;
import com.hcmut.lms.learning.service.ModelVersionService;
import com.hcmut.lms.learning.service.TrainingJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("${prefix-api:}/training")
@RequiredArgsConstructor
@Slf4j
public class TrainingJobController {

    private final TrainingJobService trainingJobService;
    private final TrainingJobMapper mapper;
    private final ModelVersionService modelVersionService;

    // ─────────────────────────────────────────────────────────────────────────
    // Training Job endpoints
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/trigger")
    public ResponseEntity<TrainingJobResponse> triggerTraining(
            @RequestBody TriggerTrainingRequest request) {
        log.info("Training job trigger requested: datasetVersionId={}", request.getDatasetVersionId());

        CompletableFuture<TrainingJobResponse> futureResponse = trainingJobService.triggerTrainingJob(request);

        try {
            TrainingJobResponse response = futureResponse.get();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to trigger training job", e);
            throw new RuntimeException("Failed to trigger training job: " + e.getMessage(), e);
        }
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> handleCallback(
            @RequestBody TrainingJobCallbackRequest callback) {
        log.info("Training callback received: jobId={}, status={}",
                callback.getJobId(), callback.getStatus());

        trainingJobService.handleTrainingCallback(callback);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/callback/error")
    public ResponseEntity<Void> handleErrorCallback(
            @RequestBody TrainingJobErrorRequest errorRequest) {
        log.warn("Training error callback: jobId={}, error={}",
                errorRequest.getJobId(), errorRequest.getError());

        trainingJobService.handleTrainingError(
                errorRequest.getJobId(),
                errorRequest.getError());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<TrainingJobResponse> getTrainingJob(
            @PathVariable String jobId) {
        log.info("Get training job: jobId={}", jobId);
        var job = trainingJobService.getTrainingJobByJobId(jobId);
        return ResponseEntity.ok(mapper.toResponse(job));
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<TrainingJobResponse>> getRecentJobs(
            @RequestParam(defaultValue = "20") int limit) {
        log.info("Get recent training jobs: limit={}", limit);
        var jobs = trainingJobService.getRecentTrainingJobs(limit);
        return ResponseEntity.ok(mapper.toResponseList(jobs));
    }

    @GetMapping("/jobs/dataset/{versionId}")
    public ResponseEntity<List<TrainingJobResponse>> getJobsByDatasetVersion(
            @PathVariable UUID versionId) {
        log.info("Get training jobs by dataset version: versionId={}", versionId);
        var jobs = trainingJobService.getTrainingJobsByDatasetVersion(versionId);
        return ResponseEntity.ok(mapper.toResponseList(jobs));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Model Version endpoints
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Lấy danh sách tất cả model version, sắp xếp mới nhất trước.
     */
    @GetMapping("/models")
    public ResponseEntity<List<ModelVersionResponse>> getAllModelVersions() {
        log.info("Get all model versions");
        return ResponseEntity.ok(modelVersionService.getAllModelVersions());
    }

    /**
     * Lấy model version đang được kích hoạt (dùng cho prediction).
     * NOTE: Literal path "/models/active" được Spring ưu tiên hơn path variable "/models/{id}".
     */
    @GetMapping("/models/active")
    public ResponseEntity<ModelVersionResponse> getActiveModelVersion() {
        log.info("Get active model version");
        return ResponseEntity.ok(modelVersionService.getActiveModelVersion());
    }

    /**
     * Lấy thông tin chi tiết một model version theo ID.
     * NOTE: Đặt sau GET /models/active để tránh xung đột routing.
     */
    @GetMapping("/models/{modelVersionId}")
    public ResponseEntity<ModelVersionResponse> getModelVersion(
            @PathVariable UUID modelVersionId) {
        log.info("Get model version: id={}", modelVersionId);
        return ResponseEntity.ok(modelVersionService.getModelVersionById(modelVersionId));
    }

    /**
     * Lấy danh sách model version theo training job ID.
     */
    @GetMapping("/jobs/{jobId}/models")
    public ResponseEntity<List<ModelVersionResponse>> getModelVersionsByJob(
            @PathVariable String jobId) {
        log.info("Get model versions by jobId: jobId={}", jobId);
        var trainingJob = trainingJobService.getTrainingJobByJobId(jobId);
        return ResponseEntity.ok(modelVersionService.getModelVersionsByTrainingJob(trainingJob.getId()));
    }

    /**
     * Kích hoạt một model version để dùng cho prediction.
     * Tự động hủy kích hoạt version đang active trước đó.
     */
    @PutMapping("/models/{modelVersionId}/activate")
    public ResponseEntity<ModelVersionResponse> activateModelVersion(
            @PathVariable UUID modelVersionId) {
        log.info("Activate model version: id={}", modelVersionId);
        return ResponseEntity.ok(modelVersionService.activateModelVersion(modelVersionId));
    }

    /**
     * Lưu trữ (archive) một model version, không dùng cho prediction nữa.
     */
    @PutMapping("/models/{modelVersionId}/archive")
    public ResponseEntity<ModelVersionResponse> archiveModelVersion(
            @PathVariable UUID modelVersionId) {
        log.info("Archive model version: id={}", modelVersionId);
        return ResponseEntity.ok(modelVersionService.archiveModelVersion(modelVersionId));
    }
}
