-- ============================================================
-- V7: Create model_version table
-- Lưu thông tin các phiên bản model được train thành công.
-- Mỗi version được tạo tự động khi TrainingJob hoàn thành với status SUCCESS.
-- Tên version được sinh tự động: {modelName}_{yyyyMMdd_HHmmss}
-- ============================================================

CREATE TABLE IF NOT EXISTS learning.model_version (
    id                      uuid        NOT NULL DEFAULT gen_random_uuid(),
    version_name            varchar(200) NOT NULL,
    model_name              varchar(100) NOT NULL,
    training_job_id         uuid        NOT NULL,
    dataset_version_id      uuid        NOT NULL,
    s3_model_path           text        NOT NULL,
    status                  varchar(20) NOT NULL DEFAULT 'INACTIVE',
    is_active               boolean     NOT NULL DEFAULT false,
    training_time_seconds   float8      NULL,

    -- Metrics snapshot (4-point scale) - từ callback của Fargate worker
    test_mae                float8      NULL,
    test_rmse               float8      NULL,
    test_r2                 float8      NULL,
    train_mae               float8      NULL,
    train_rmse              float8      NULL,
    train_r2                float8      NULL,
    sample_count            int4        NULL,

    description             text        NULL,
    created_at              timestamptz NOT NULL DEFAULT now(),
    updated_at              timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT model_version_pkey PRIMARY KEY (id),
    CONSTRAINT model_version_version_name_unique UNIQUE (version_name),
    CONSTRAINT model_version_training_job_fkey
        FOREIGN KEY (training_job_id)
        REFERENCES learning.training_job(id),
    CONSTRAINT model_version_dataset_version_fkey
        FOREIGN KEY (dataset_version_id)
        REFERENCES learning.grade_prediction_dataset_version(id),
    CONSTRAINT model_version_status_chk
        CHECK (status IN ('INACTIVE', 'ACTIVE', 'ARCHIVED'))
);

CREATE INDEX idx_model_version_training_job_id
    ON learning.model_version (training_job_id);

CREATE INDEX idx_model_version_dataset_version_id
    ON learning.model_version (dataset_version_id);

CREATE INDEX idx_model_version_status
    ON learning.model_version (status);

-- Index riêng cho is_active để query nhanh version đang active
CREATE INDEX idx_model_version_is_active
    ON learning.model_version (is_active)
    WHERE is_active = true;

CREATE INDEX idx_model_version_created_at
    ON learning.model_version (created_at DESC);

COMMENT ON TABLE learning.model_version
    IS 'Lưu thông tin từng phiên bản model ML được train thành công. Được tạo tự động sau mỗi TrainingJob SUCCESS.';

COMMENT ON COLUMN learning.model_version.version_name
    IS 'Tên version tự động sinh theo công thức: {modelName}_{yyyyMMdd_HHmmss}. Ví dụ: linear_grade_predictor_20260305_143022';

COMMENT ON COLUMN learning.model_version.training_job_id
    IS 'Training job đã tạo ra model version này. Dùng để truy vết nguồn gốc của model.';

COMMENT ON COLUMN learning.model_version.dataset_version_id
    IS 'Dataset version được dùng để train model. Dùng để truy vết dữ liệu đầu vào.';

COMMENT ON COLUMN learning.model_version.is_active
    IS 'TRUE nếu version này đang được dùng làm mặc định cho prediction. Chỉ có duy nhất 1 version có is_active = true tại một thời điểm (đảm bảo bởi application layer).';

COMMENT ON COLUMN learning.model_version.status
    IS 'INACTIVE: chưa kích hoạt, ACTIVE: đang dùng cho prediction, ARCHIVED: đã lưu trữ không dùng nữa';

-- ============================================================
-- Add description column to training_job table
-- Cho phép lưu mô tả tùy chọn cho mỗi training run
-- ============================================================

ALTER TABLE learning.training_job
    ADD COLUMN description text NULL;

COMMENT ON COLUMN learning.training_job.description
    IS 'Mô tả tùy chọn cho training run này, do người dùng cung cấp khi trigger job';
