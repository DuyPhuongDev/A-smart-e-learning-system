package com.hcmut.lms.learning.entity.training;

import com.hcmut.lms.learning.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Lưu thông tin từng phiên bản model được train thành công.
 * Mỗi ModelVersion được tạo tự động khi TrainingJob hoàn thành với trạng thái SUCCESS.
 * Tên version được tự động sinh ra theo công thức: {modelName}_{yyyyMMdd_HHmmss}
 */
@Entity
@Table(
        name = "model_version",
        schema = "learning",
        indexes = {
                @Index(name = "idx_model_version_training_job_id", columnList = "training_job_id"),
                @Index(name = "idx_model_version_status",          columnList = "status"),
                @Index(name = "idx_model_version_is_active",       columnList = "is_active"),
                @Index(name = "idx_model_version_created_at",      columnList = "created_at DESC")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ModelVersion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Tên version tự động sinh: {baseModelName}_{yyyyMMdd_HHmmss}
     * Ví dụ: linear_grade_predictor_20260305_143022
     */
    @Column(name = "version_name", nullable = false, unique = true, length = 200)
    private String versionName;

    /**
     * Tên model gốc (base name), ví dụ: linear_grade_predictor
     */
    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    /**
     * Training job đã tạo ra model version này
     */
    @Column(name = "training_job_id", nullable = false)
    private UUID trainingJobId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_job_id", insertable = false, updatable = false)
    private TrainingJob trainingJob;

    /**
     * Dataset version được dùng để train model này
     */
    @Column(name = "dataset_version_id", nullable = false)
    private UUID datasetVersionId;

    /**
     * Đường dẫn S3 đến file model (joblib)
     * Ví dụ: s3://lms-bucket/linear-grade-predictor-model/models/linear_grade_predictor_20260305.joblib
     */
    @Column(name = "s3_model_path", nullable = false, columnDefinition = "TEXT")
    private String s3ModelPath;

    /**
     * Trạng thái của model version
     */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ModelVersionStatus status = ModelVersionStatus.INACTIVE;

    /**
     * Đánh dấu model version này là version đang được sử dụng mặc định cho prediction.
     * Tại một thời điểm chỉ có duy nhất một version có is_active = true.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = false;

    /**
     * Thời gian training (giây) - lấy từ callback
     */
    @Column(name = "training_time_seconds")
    private Double trainingTimeSeconds;

    /**
     * Metrics tổng hợp: MAE, RMSE, R2 trên tập test (4-point scale)
     * Snapshot từ callback, dùng để so sánh nhanh giữa các version
     */
    @Column(name = "test_mae")
    private Double testMae;

    @Column(name = "test_rmse")
    private Double testRmse;

    @Column(name = "test_r2")
    private Double testR2;

    @Column(name = "train_mae")
    private Double trainMae;

    @Column(name = "train_rmse")
    private Double trainRmse;

    @Column(name = "train_r2")
    private Double trainR2;

    /**
     * Số mẫu trong tập test
     */
    @Column(name = "sample_count")
    private Integer sampleCount;

    // ─── Residual distribution snapshot (V8) ────────────────────────────────
    // R = g_hat4 - g4 ~ N(mu_R, sigma_R^2)
    // g_tilde = g_hat4 + mu_R  (bias-corrected prediction)
    // P(g4 >= t) = 1 - Phi((t - g_tilde) / sigma_R)

    @Column(name = "test_mu_error")
    private Double testMuError;

    @Column(name = "test_sigma_error")
    private Double testSigmaError;

    @Column(name = "train_mu_error")
    private Double trainMuError;

    @Column(name = "train_sigma_error")
    private Double trainSigmaError;

    /**
     * Mô tả tùy chọn cho version này
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // ─── Lifecycle methods ──────────────────────────────────────────────────

    public void activate() {
        this.isActive = true;
        this.status = ModelVersionStatus.ACTIVE;
    }

    public void deactivate() {
        this.isActive = false;
        if (this.status == ModelVersionStatus.ACTIVE) {
            this.status = ModelVersionStatus.INACTIVE;
        }
    }

    public void archive() {
        this.isActive = false;
        this.status = ModelVersionStatus.ARCHIVED;
    }

    public enum ModelVersionStatus {
        INACTIVE,   // Đã tạo nhưng chưa được kích hoạt
        ACTIVE,     // Đang được sử dụng cho prediction
        ARCHIVED    // Đã lưu trữ, không dùng nữa
    }
}
