package com.hcmut.lms.learning.controller;

import com.hcmut.lms.learning.dto.response.GradePredictionDatasetVersionResponse;
import com.hcmut.lms.learning.mapper.GradePredictionDatasetVersionMapper;
import com.hcmut.lms.learning.service.GradePredictionDatasetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for grade prediction dataset operations.
 */
@RestController
@RequestMapping("${prefix-api:}/grade-prediction")
@RequiredArgsConstructor
@Slf4j
public class GradePredictionDatasetController {

    private final GradePredictionDatasetService gradePredictionDatasetService;
    private final GradePredictionDatasetVersionMapper mapper;

    /**
     * Trigger a full recompute of the grade prediction feature dataset.
     * Creates a new dataset version, processes all graded enrollments and
     * persists feature vectors.
     * POST /api/learning/v1/grade-prediction/compute-dataset
     */
    @PostMapping("/compute-dataset")
    public ResponseEntity<GradePredictionDatasetVersionResponse> computeDataset() {
        log.info("Starting dataset computation process...");
        var version = gradePredictionDatasetService.computeAndSaveDataset();
        log.info("Dataset computed successfully: Version ID = {}", version.getId());
        return ResponseEntity.ok(mapper.toResponse(version));
    }

    /**
     * Get all dataset versions ordered by version number descending.
     * GET /api/learning/v1/grade-prediction/versions
     */
    @GetMapping("/versions")
    public ResponseEntity<List<GradePredictionDatasetVersionResponse>> getAllVersions() {
        var versions = gradePredictionDatasetService.getAllVersions();
        return ResponseEntity.ok(mapper.toResponseList(versions));
    }

    /**
     * Get a specific dataset version by its ID.
     * GET /api/learning/v1/grade-prediction/versions/{versionId}
     */
    @GetMapping("/versions/{versionId}")
    public ResponseEntity<GradePredictionDatasetVersionResponse> getVersionById(
            @PathVariable UUID versionId) {
        var version = gradePredictionDatasetService.getVersionById(versionId);
        return ResponseEntity.ok(mapper.toResponse(version));
    }

    /**
     * Get the latest COMPLETED dataset version (ready for model training).
     * GET /api/learning/v1/grade-prediction/versions/latest-completed
     */
    @GetMapping("/versions/latest-completed")
    public ResponseEntity<GradePredictionDatasetVersionResponse> getLatestCompletedVersion() {
        var version = gradePredictionDatasetService.getLatestCompletedVersion();
        return ResponseEntity.ok(mapper.toResponse(version));
    }
}
