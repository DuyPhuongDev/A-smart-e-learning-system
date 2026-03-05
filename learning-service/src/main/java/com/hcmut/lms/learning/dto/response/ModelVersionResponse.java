package com.hcmut.lms.learning.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO cho ModelVersion - phiên bản model được train.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelVersionResponse {

    private UUID id;

    /**
     * Tên version tự động sinh: {modelName}_{yyyyMMdd_HHmmss}
     * Ví dụ: linear_grade_predictor_20260305_143022
     */
    private String versionName;

    /**
     * Tên model gốc (base name)
     */
    private String modelName;

    /**
     * ID của training job đã tạo ra version này
     */
    private UUID trainingJobId;

    /**
     * Job ID (chuỗi định danh của training job, ví dụ: TRAIN_20260305_143022_abc12345)
     */
    private String jobId;

    /**
     * ID của dataset version được dùng để train
     */
    private UUID datasetVersionId;

    /**
     * Đường dẫn S3 đến file model
     */
    private String s3ModelPath;

    /**
     * Trạng thái: INACTIVE, ACTIVE, ARCHIVED
     */
    private String status;

    /**
     * Model này có đang được dùng làm mặc định cho prediction không
     */
    private Boolean isActive;

    /**
     * Thời gian training (giây)
     */
    private Double trainingTimeSeconds;

    // ─── Metrics snapshot (4-point scale) ──────────────────────────────────

    private Double testMae;
    private Double testRmse;
    private Double testR2;
    private Double trainMae;
    private Double trainRmse;
    private Double trainR2;

    /**
     * Số mẫu trong tập test
     */
    private Integer sampleCount;

    private String description;

    private String createdAt;
    private String updatedAt;
}
