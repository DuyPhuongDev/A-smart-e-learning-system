-- V12: Create grade_semester_metrics table for persisting per-semester global grade statistics
-- Used for shrinkage smoothing in grade prediction fallback chain
-- Avoids recomputation and provides real data for on-demand queries

CREATE TABLE learning.grade_semester_metrics (
    id              uuid        NOT NULL,
    semester_id     uuid        NOT NULL,
    sem_key         int4        NOT NULL,

    -- Global grade statistics (10-point scale)
    median_grade    float8      NOT NULL DEFAULT 6.0,
    mean_grade      float8      NOT NULL DEFAULT 6.0,
    sample_count    int4        NOT NULL DEFAULT 0,

    created_at      timestamptz NOT NULL DEFAULT now(),
    updated_at      timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT grade_semester_metrics_pkey PRIMARY KEY (id),
    CONSTRAINT grade_semester_metrics_semester_unique UNIQUE (semester_id)
);

-- Index for proximity lookups (find closest semester by semKey)
CREATE INDEX idx_grade_semester_metrics_semkey ON learning.grade_semester_metrics(sem_key);

-- ============================================================
-- Column comments
-- ============================================================

COMMENT ON TABLE learning.grade_semester_metrics
    IS 'Per-semester global grade statistics for shrinkage smoothing in grade prediction. Persisted to avoid recomputation and provide real data for on-demand queries.';

COMMENT ON COLUMN learning.grade_semester_metrics.semester_id
    IS 'FK -> course_management.semesters. The semester these statistics apply to. Unique constraint ensures one row per semester.';

COMMENT ON COLUMN learning.grade_semester_metrics.sem_key
    IS 'Semester key for chronological ordering and proximity lookups. Higher values represent later semesters.';

COMMENT ON COLUMN learning.grade_semester_metrics.median_grade
    IS 'Median grade across all subjects in this semester (10-point scale). Used for shrinkage smoothing.';

COMMENT ON COLUMN learning.grade_semester_metrics.mean_grade
    IS 'Mean grade across all subjects in this semester (10-point scale).';

COMMENT ON COLUMN learning.grade_semester_metrics.sample_count
    IS 'Total number of graded enrollments in this semester across all subjects.';