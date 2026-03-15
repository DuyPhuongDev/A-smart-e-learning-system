-- ============================================================
-- V5: Add versioning to grade_prediction_features_dataset
-- ============================================================

-- 1. Create version management table
CREATE TABLE learning.grade_prediction_dataset_version (
    id              uuid        NOT NULL DEFAULT gen_random_uuid(),
    version_number  int4        NOT NULL,
    description     text        NULL,
    total_rows      int4        NOT NULL DEFAULT 0,
    status          varchar(20) NOT NULL DEFAULT 'RUNNING',
    created_at      timestamptz NOT NULL DEFAULT now(),
    updated_at      timestamptz NOT NULL DEFAULT now(),
    completed_at    timestamptz NULL,

    CONSTRAINT grade_prediction_dataset_version_pkey PRIMARY KEY (id),
    CONSTRAINT grade_prediction_dataset_version_number_unique UNIQUE (version_number),
    CONSTRAINT grade_prediction_dataset_version_status_chk
        CHECK (status IN ('RUNNING', 'COMPLETED', 'FAILED'))
);

COMMENT ON TABLE learning.grade_prediction_dataset_version
    IS 'Tracks each execution run of the grade prediction dataset pipeline.';
COMMENT ON COLUMN learning.grade_prediction_dataset_version.version_number
    IS 'Auto-incremented version number, starting from 1.';
COMMENT ON COLUMN learning.grade_prediction_dataset_version.status
    IS 'RUNNING while pipeline is executing, COMPLETED on success, FAILED on error.';
COMMENT ON COLUMN learning.grade_prediction_dataset_version.total_rows
    IS 'Number of feature rows saved in this version.';
COMMENT ON COLUMN learning.grade_prediction_dataset_version.completed_at
    IS 'Timestamp when the pipeline finished (COMPLETED or FAILED).';


-- 2. Add version_id column to grade_prediction_features_dataset
ALTER TABLE learning.grade_prediction_features_dataset
    ADD COLUMN version_id uuid NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000';

-- 3. Drop the old unique constraint (student, semester, course)
ALTER TABLE learning.grade_prediction_features_dataset
    DROP CONSTRAINT grade_prediction_features_dataset_unique;

-- 4. Add new unique constraint including version_id
ALTER TABLE learning.grade_prediction_features_dataset
    ADD CONSTRAINT grade_prediction_features_dataset_unique
        UNIQUE (student_id, semester_id, subject_id, version_id);

-- 5. Add FK from dataset rows to version table
ALTER TABLE learning.grade_prediction_features_dataset
    ADD CONSTRAINT grade_prediction_features_dataset_version_id_fkey
        FOREIGN KEY (version_id)
        REFERENCES learning.grade_prediction_dataset_version(id);

-- 6. Index for fast lookup by version
CREATE INDEX idx_grade_prediction_dataset_version_id
    ON learning.grade_prediction_features_dataset (version_id);

-- Column comment
COMMENT ON COLUMN learning.grade_prediction_features_dataset.version_id
    IS 'FK → learning.grade_prediction_dataset_version. Identifies which pipeline run this row belongs to.';

