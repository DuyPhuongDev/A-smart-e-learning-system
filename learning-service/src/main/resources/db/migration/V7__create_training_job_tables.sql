-- ============================================================
-- V6: Create ML training job tracking tables
-- ============================================================

-- 1. Training Job table
CREATE TABLE IF NOT EXISTS learning.training_job (
    id                      uuid        NOT NULL DEFAULT gen_random_uuid(),
    job_id                  varchar(50) NOT NULL,
    dataset_version_id      uuid        NOT NULL,
    model_name              varchar(100) NOT NULL,
    s3_dataset_url          text        NOT NULL,
    s3_model_url            text        NULL,
    status                  varchar(20) NOT NULL DEFAULT 'PENDING',
    training_time_seconds   float8      NULL,
    error_message           text        NULL,
    created_at              timestamptz NOT NULL DEFAULT now(),
    updated_at              timestamptz NOT NULL DEFAULT now(),
    submitted_at            timestamptz NULL,
    completed_at            timestamptz NULL,

    CONSTRAINT training_job_pkey PRIMARY KEY (id),
    CONSTRAINT training_job_job_id_unique UNIQUE (job_id),
    CONSTRAINT training_job_dataset_version_fkey
        FOREIGN KEY (dataset_version_id)
        REFERENCES learning.grade_prediction_dataset_version(id),
    CONSTRAINT training_job_status_chk
        CHECK (status IN ('PENDING', 'QUEUED', 'RUNNING', 'COMPLETED', 'FAILED'))
);

CREATE INDEX idx_training_job_dataset_version_id
    ON learning.training_job (dataset_version_id);
CREATE INDEX idx_training_job_status
    ON learning.training_job (status);
CREATE INDEX idx_training_job_created_at
    ON learning.training_job (created_at DESC);

COMMENT ON TABLE learning.training_job
    IS 'Tracks ML model training jobs submitted to SQS/Fargate';
COMMENT ON COLUMN learning.training_job.job_id
    IS 'Unique job identifier sent to SQS (format: TRAIN_YYYYMMDD_HHMMSS_UUID)';
COMMENT ON COLUMN learning.training_job.status
    IS 'PENDING: created but not queued, QUEUED: sent to SQS, RUNNING: worker processing, COMPLETED/FAILED: terminal states';

-- 2. Training Metrics table (stores detailed metrics from callback)
CREATE TABLE IF NOT EXISTS learning.training_metrics (
    id                      uuid        NOT NULL DEFAULT gen_random_uuid(),
    training_job_id         uuid        NOT NULL,
    metric_type             varchar(20) NOT NULL,
    dataset_split           varchar(10) NOT NULL,
    scale_type              varchar(10) NOT NULL,
    mae                     float8      NULL,
    rmse                    float8      NULL,
    r2                      float8      NULL,
    mu_error                float8      NULL,
    sigma_error             float8      NULL,
    sample_count            int4        NULL,
    created_at              timestamptz NOT NULL DEFAULT now(),
    updated_at              timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT training_metrics_pkey PRIMARY KEY (id),
    CONSTRAINT training_metrics_job_fkey
        FOREIGN KEY (training_job_id)
        REFERENCES learning.training_job(id) ON DELETE CASCADE,
    CONSTRAINT training_metrics_type_chk
        CHECK (metric_type IN ('baseline', 'model')),
    CONSTRAINT training_metrics_split_chk
        CHECK (dataset_split IN ('train', 'test'))
);

CREATE INDEX idx_training_metrics_job_id
    ON learning.training_metrics (training_job_id);

COMMENT ON TABLE learning.training_metrics
    IS 'Stores detailed training/test metrics for each training job';
