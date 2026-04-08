-- V4: Initial schema for grade prediction features dataset (reduced column set)

CREATE TABLE learning.grade_prediction_features_dataset (
    id                          uuid        NOT NULL,
    student_id                  uuid        NOT NULL,
    semester_id                 uuid        NOT NULL,
    subject_id                  uuid        NOT NULL,

    -- Target
    course_grade                float8      NULL,

    -- Enrollment context
    sem_credits                 int4        NOT NULL DEFAULT 0,
    sem_credits_squared         int4        NOT NULL DEFAULT 0,
    retake_no                   int4        NOT NULL DEFAULT 0, -- attempt count (0 = first)

    -- Student history
    num_semesters_prior         int4        NOT NULL DEFAULT 0,
    cumulative_grade_avg        float8      NULL,
    previous_sem_grade_avg      float8      NULL,

    -- Course baseline
    course_hist_median_smooth   float8      NULL,

    -- Relative performance
    relative_avg_course_grade   float8      NOT NULL DEFAULT 0,

    created_at                  timestamptz NOT NULL DEFAULT now(),
    updated_at                  timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT grade_prediction_features_dataset_pkey PRIMARY KEY (id),
    CONSTRAINT grade_prediction_features_dataset_unique UNIQUE (student_id, semester_id, subject_id)
);

-- ============================================================
-- Column comments
-- ============================================================

-- Keys
COMMENT ON COLUMN learning.grade_prediction_features_dataset.student_id
    IS 'UUID of the student (maps to learning.enrollments.student_id).';
COMMENT ON COLUMN learning.grade_prediction_features_dataset.semester_id
    IS 'FK → course_management.semesters. Identifies the semester this row belongs to.';
COMMENT ON COLUMN learning.grade_prediction_features_dataset.subject_id
    IS 'FK → course_management.subjects. Identifies the subject being predicted.';

-- Target
COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_grade
    IS 'Final grade on 4-point scale, converted from raw 10-point score.';

-- Enrollment context
COMMENT ON COLUMN learning.grade_prediction_features_dataset.sem_credits
    IS 'Total enrolled credits in this semester: SUM(credits) over courses in (student, semester).';
COMMENT ON COLUMN learning.grade_prediction_features_dataset.sem_credits_squared
    IS 'sem_credits². Non-linear credit-load signal.';
COMMENT ON COLUMN learning.grade_prediction_features_dataset.retake_no
    IS 'Attempt count for this subject: 0 first attempt, 1 second, etc.';

-- Student history
COMMENT ON COLUMN learning.grade_prediction_features_dataset.num_semesters_prior
    IS 'Number of semesters completed before the current one.';
COMMENT ON COLUMN learning.grade_prediction_features_dataset.cumulative_grade_avg
    IS 'Credit-weighted GPA up to (but not including) the current semester (scale 4).';
COMMENT ON COLUMN learning.grade_prediction_features_dataset.previous_sem_grade_avg
    IS 'Credit-weighted average grade of the immediately preceding semester (scale 4).';

-- Subject baseline (kept column name for compatibility)
COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_hist_median_smooth
    IS 'Shrinkage-smoothed median subject grade (scale 4).';

-- Relative performance
COMMENT ON COLUMN learning.grade_prediction_features_dataset.relative_avg_course_grade
    IS 'Credit-weighted avg grade of related prior courses (same section or prereq/recommended).';
