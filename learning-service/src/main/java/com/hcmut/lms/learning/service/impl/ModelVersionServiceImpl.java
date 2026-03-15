package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.learning.dto.response.ModelVersionResponse;
import com.hcmut.lms.learning.dto.training.TrainingJobCallbackRequest;
import com.hcmut.lms.learning.entity.training.ModelVersion;
import com.hcmut.lms.learning.entity.training.TrainingJob;
import com.hcmut.lms.learning.exception.ResourceNotFoundException;
import com.hcmut.lms.learning.mapper.ModelVersionMapper;
import com.hcmut.lms.learning.repository.ModelVersionRepository;
import com.hcmut.lms.learning.service.ModelVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelVersionServiceImpl implements ModelVersionService {

    private final ModelVersionRepository modelVersionRepository;
    private final ModelVersionMapper modelVersionMapper;

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ModelVersion createFromCompletedJob(TrainingJob trainingJob,
                                               TrainingJobCallbackRequest callback) {
        log.info("Creating ModelVersion for completed job: jobId={}, modelName={}",
                trainingJob.getJobId(), callback.getModelName());

        // versionName = modelName vì modelName đã được sinh tự động kèm timestamp
        // khi trigger job: {baseName}_{yyyyMMdd_HHmmss}, ví dụ: linear_grade_predictor_20260305_143022
        // Nên dùng trực tiếp modelName làm versionName để tránh timestamp kép.
        String versionName = callback.getModelName();

        // Trích xuất metrics từ callback (4-point scale)
        Double testMae   = null, testRmse  = null, testR2    = null;
        Double trainMae  = null, trainRmse = null, trainR2   = null;
        Integer sampleCount = null;
        Double testMuError = null, testSigmaError = null;
        Double trainMuError = null, trainSigmaError = null;

        if (callback.getMetrics() != null) {
            Map<String, Object> metrics = callback.getMetrics();

            testMae   = extractMetricDouble(metrics, "test",  "model", "mae");
            testRmse  = extractMetricDouble(metrics, "test",  "model", "rmse");
            testR2    = extractMetricDouble(metrics, "test",  "model", "r2");
            trainMae  = extractMetricDouble(metrics, "train", "model", "mae");
            trainRmse = extractMetricDouble(metrics, "train", "model", "rmse");
            trainR2   = extractMetricDouble(metrics, "train", "model", "r2");
            sampleCount     = extractMetricInt(metrics,    "test",  "error_distribution", "n");
            testMuError     = extractMetricDouble(metrics, "test",  "error_distribution", "mu_error");
            testSigmaError  = extractMetricDouble(metrics, "test",  "error_distribution", "sigma_error");
            trainMuError    = extractMetricDouble(metrics, "train", "error_distribution", "mu_error");
            trainSigmaError = extractMetricDouble(metrics, "train", "error_distribution", "sigma_error");
        }

        ModelVersion modelVersion = ModelVersion.builder()
                .versionName(versionName)
                .modelName(callback.getModelName())
                .trainingJobId(trainingJob.getId())
                .datasetVersionId(trainingJob.getDatasetVersionId())
                .s3ModelPath(callback.getModelS3Path())
                .status(ModelVersion.ModelVersionStatus.INACTIVE)
                .isActive(false)
                .trainingTimeSeconds(callback.getTrainingTimeSeconds())
                .testMae(testMae)
                .testRmse(testRmse)
                .testR2(testR2)
                .trainMae(trainMae)
                .trainRmse(trainRmse)
                .trainR2(trainR2)
                .sampleCount(sampleCount)
                .testMuError(testMuError)
                .testSigmaError(testSigmaError)
                .trainMuError(trainMuError)
                .trainSigmaError(trainSigmaError)
                .description(trainingJob.getDescription())
                .build();

        ModelVersion saved = modelVersionRepository.save(modelVersion);
        log.info("Created ModelVersion: versionName={}, id={}", versionName, saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelVersionResponse> getAllModelVersions() {
        var versions = modelVersionRepository.findTop20ByOrderByCreatedAtDesc();
        return modelVersionMapper.toResponseList(versions);
    }

    @Override
    @Transactional(readOnly = true)
    public ModelVersionResponse getModelVersionById(UUID id) {
        var version = findByIdOrThrow(id);
        return modelVersionMapper.toResponse(version);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelVersionResponse> getModelVersionsByTrainingJob(UUID trainingJobId) {
        var versions = modelVersionRepository.findByTrainingJobId(trainingJobId);
        return modelVersionMapper.toResponseList(versions);
    }

    @Override
    @Transactional
    public ModelVersionResponse activateModelVersion(UUID id) {
        log.info("Activating model version: id={}", id);

        ModelVersion target = findByIdOrThrow(id);

        if (target.getStatus() == ModelVersion.ModelVersionStatus.ARCHIVED) {
            throw new IllegalStateException(
                    "Cannot activate an archived model version: " + id);
        }

        // Hủy kích hoạt tất cả version đang active
        modelVersionRepository.deactivateAll();

        // Kích hoạt version mới
        target.activate();
        ModelVersion saved = modelVersionRepository.save(target);

        log.info("Activated model version: versionName={}", saved.getVersionName());
        return modelVersionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ModelVersionResponse archiveModelVersion(UUID id) {
        log.info("Archiving model version: id={}", id);

        ModelVersion target = findByIdOrThrow(id);
        target.archive();
        ModelVersion saved = modelVersionRepository.save(target);

        log.info("Archived model version: versionName={}", saved.getVersionName());
        return modelVersionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ModelVersionResponse getActiveModelVersion() {
        return modelVersionRepository.findByIsActiveTrue()
                .map(modelVersionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No active model version found"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helper methods
    // ─────────────────────────────────────────────────────────────────────────

    private ModelVersion findByIdOrThrow(UUID id) {
        return modelVersionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Model version not found: " + id));
    }

    /**
     * Trích xuất giá trị Double từ cấu trúc metrics lồng nhau:
     * metrics -> {split} -> {group} -> {key}
     * Ví dụ: metrics.test.model.mae
     */
    @SuppressWarnings("unchecked")
    private Double extractMetricDouble(Map<String, Object> metrics,
                                       String split, String group, String key) {
        try {
            if (!metrics.containsKey(split)) return null;
            Map<String, Object> splitMap = (Map<String, Object>) metrics.get(split);

            if (!splitMap.containsKey(group)) return null;
            Map<String, Object> groupMap = (Map<String, Object>) splitMap.get(group);

            Object value = groupMap.get(key);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        } catch (Exception e) {
            log.warn("Could not extract metric [{}.{}.{}]: {}", split, group, key, e.getMessage());
        }
        return null;
    }

    /**
     * Trích xuất giá trị Integer từ cấu trúc metrics lồng nhau.
     */
    @SuppressWarnings("unchecked")
    private Integer extractMetricInt(Map<String, Object> metrics,
                                     String split, String group, String key) {
        try {
            if (!metrics.containsKey(split)) return null;
            Map<String, Object> splitMap = (Map<String, Object>) metrics.get(split);

            if (!splitMap.containsKey(group)) return null;
            Map<String, Object> groupMap = (Map<String, Object>) splitMap.get(group);

            Object value = groupMap.get(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        } catch (Exception e) {
            log.warn("Could not extract metric [{}.{}.{}]: {}", split, group, key, e.getMessage());
        }
        return null;
    }
}
