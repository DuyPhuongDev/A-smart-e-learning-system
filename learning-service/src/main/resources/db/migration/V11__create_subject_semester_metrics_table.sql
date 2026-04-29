-- V11: Create subject_semester_metrics table for grade prediction model cache
-- This table stores pre-computed mean and standard deviation for each (subject, semester) pair
-- Acts as both a performance cache and historical reference

CREATE TABLE learning.subject_semester_metrics (
    id                  uuid        NOT NULL,
    subject_id          uuid        NOT NULL,
    semester_id         uuid        NOT NULL,

    -- Metrics (10-point scale)
    mean_grade          float8      NOT NULL,
    std_dev             float8      NOT NULL,

    -- Metadata
    sample_count        int4        NOT NULL DEFAULT 0,
    is_fallback         boolean     NOT NULL DEFAULT false,
    fallback_level      int4        NOT NULL DEFAULT 0,

    -- Semester-level global statistics (for shrinkage smoothing)
    semester_median_grade float8    NOT NULL DEFAULT 6.0,
    semester_mean_grade   float8    NOT NULL DEFAULT 6.0,
    semester_sample_count int4       NOT NULL DEFAULT 0,

    -- Pre-computed smoothed median (4-point scale) with shrinkage applied
    smoothed_median_4pt   float8      NOT NULL DEFAULT 0.0,
    sem_key               int4,

    created_at          timestamptz NOT NULL DEFAULT now(),
    updated_at          timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT subject_semester_metrics_pkey PRIMARY KEY (id),
    CONSTRAINT subject_semester_metrics_unique UNIQUE (subject_id, semester_id)
);

-- Indexes for common queries
CREATE INDEX idx_subject_semester_metrics_subject ON learning.subject_semester_metrics(subject_id);
CREATE INDEX idx_subject_semester_metrics_semester ON learning.subject_semester_metrics(semester_id);
CREATE INDEX idx_subject_semester_metrics_fallback ON learning.subject_semester_metrics(is_fallback);
CREATE INDEX idx_subject_semester_metrics_semkey   ON learning.subject_semester_metrics(sem_key DESC);

-- ============================================================
-- Column comments
-- ============================================================

COMMENT ON TABLE learning.subject_semester_metrics
    IS 'Pre-computed subject metrics per semester for grade prediction. Acts as both a performance cache and historical reference.';

COMMENT ON COLUMN learning.subject_semester_metrics.subject_id
    IS 'FK → course_management.subjects. The subject these metrics apply to.';

COMMENT ON COLUMN learning.subject_semester_metrics.semester_id
    IS 'FK → course_management.semesters. The semester these metrics apply to.';

COMMENT ON COLUMN learning.subject_semester_metrics.mean_grade
    IS 'Mean grade on 10-point scale (expected performance value). Fallback: 6.0 if no data.';

COMMENT ON COLUMN learning.subject_semester_metrics.std_dev
    IS 'Standard deviation of grades (measure of dispersion). Fallback: 0.7 if no data.';

COMMENT ON COLUMN learning.subject_semester_metrics.sample_count
    IS 'Number of data points used in this computation. 0 if using complete fallback.';

COMMENT ON COLUMN learning.subject_semester_metrics.is_fallback
    IS 'True if this record was computed using fallback values rather than real semester data.';

COMMENT ON COLUMN learning.subject_semester_metrics.fallback_level
    IS 'Level of fallback used: 0=real data, 1=temporal carry-forward, 2=global subject baseline, 3=system defaults.';

COMMENT ON COLUMN learning.subject_semester_metrics.semester_median_grade
    IS 'Median grade of ALL subjects in this semester (10-point scale). Used for shrinkage smoothing.';

COMMENT ON COLUMN learning.subject_semester_metrics.semester_mean_grade
    IS 'Mean grade of ALL subjects in this semester (10-point scale). For reference.';

COMMENT ON COLUMN learning.subject_semester_metrics.semester_sample_count
    IS 'Total sample count across all subjects in this semester.';

COMMENT ON COLUMN learning.subject_semester_metrics.smoothed_median_4pt
    IS 'Pre-computed smoothed median in 4-point scale using shrinkage: (median4pt * count + globalMedian4pt * K) / (count + K).';

COMMENT ON COLUMN learning.subject_semester_metrics.created_at
    IS 'Timestamp when this metrics record was created.';

COMMENT ON COLUMN learning.subject_semester_metrics.updated_at
    IS 'Timestamp when this metrics record was last updated.';

COMMENT ON COLUMN learning.subject_semester_metrics.sem_key
  IS 'Semester key for chronological ordering and temporal fallback lookups. Computed via SemKeyUtil: (2000 + YY) * 10 + S from semester code.';
