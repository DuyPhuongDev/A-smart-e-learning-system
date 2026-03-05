CREATE TABLE learning.grade_prediction_features_dataset (
    id                                            uuid NOT NULL,
    student_id                                    uuid NOT NULL,
    semester_id                                   uuid NOT NULL,
    course_id                                     uuid NOT NULL,

    -- Target
    course_grade                                  float8 NULL,
    target_gap                                    float8 NULL,

    -- Enrollment context
    sem_credits                                   int4 NOT NULL DEFAULT 0,
    sem_credits_squared                           int4 NOT NULL DEFAULT 0,
    retake_flg                                    bool NOT NULL DEFAULT false,

    -- Student history flags
    num_semesters_prior                           int4 NOT NULL DEFAULT 0,
    has_student_history                           bool NOT NULL DEFAULT false,

    -- Student cumulative performance
    cumulative_grade_avg                          float8 NULL,
    previous_sem_grade_avg                        float8 NULL,
    grade_trend                                   float8 NOT NULL DEFAULT 0,
    grade_consistency                             float8 NULL,
    historic_fail_ratio                           float8 NOT NULL DEFAULT 0,

    -- Student ranking within semester
    sem_rank_percentile                           float8 NOT NULL DEFAULT 0.5,
    gpa_rank_percentile                           float8 NOT NULL DEFAULT 0.5,

    -- Student grade distribution rates
    student_a_plus_grade_rate                     float8 NOT NULL DEFAULT 0,
    student_a_grade_rate                          float8 NOT NULL DEFAULT 0,
    student_b_plus_grade_rate                     float8 NOT NULL DEFAULT 0,
    student_b_grade_rate                          float8 NOT NULL DEFAULT 0,
    student_c_plus_grade_rate                     float8 NOT NULL DEFAULT 0,
    student_c_grade_rate                          float8 NOT NULL DEFAULT 0,
    student_d_plus_grade_rate                     float8 NOT NULL DEFAULT 0,
    student_d_grade_rate                          float8 NOT NULL DEFAULT 0,

    -- Course historical baseline
    course_hist_count                             int4 NOT NULL DEFAULT 0,
    course_hist_median_smooth                     float8 NULL,
    course_hist_missing                           bool NOT NULL DEFAULT true,

    -- Course difficulty & failure
    rank_course_difficulty                        float8 NOT NULL DEFAULT 0.5,
    course_fail_rate                              float8 NOT NULL DEFAULT 0,

    -- Course grade distribution rates
    course_a_plus_grade_rate                      float8 NOT NULL DEFAULT 0,
    course_a_grade_rate                           float8 NOT NULL DEFAULT 0,
    course_b_plus_grade_rate                      float8 NOT NULL DEFAULT 0,
    course_b_grade_rate                           float8 NOT NULL DEFAULT 0,
    course_c_plus_grade_rate                      float8 NOT NULL DEFAULT 0,
    course_c_grade_rate                           float8 NOT NULL DEFAULT 0,
    course_d_plus_grade_rate                      float8 NOT NULL DEFAULT 0,
    course_d_grade_rate                           float8 NOT NULL DEFAULT 0,

    -- Relative performance vs course peers
    has_relative_course                           bool NOT NULL DEFAULT false,
    relative_avg_course_grade                     float8 NOT NULL DEFAULT 0,
    relative_avg_course_grade_rank_percentile     float8 NOT NULL DEFAULT 0.5,

    created_at                                    timestamptz NOT NULL DEFAULT now(),
    updated_at                                    timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT grade_prediction_features_dataset_pkey PRIMARY KEY (id),
    CONSTRAINT grade_prediction_features_dataset_unique UNIQUE (student_id, semester_id, course_id),
    CONSTRAINT grade_prediction_features_dataset_semester_id_fkey FOREIGN KEY (semester_id) REFERENCES course_management.semesters(id),
    CONSTRAINT grade_prediction_features_dataset_course_id_fkey FOREIGN KEY (course_id) REFERENCES course_management.subjects(id)
);

-- ============================================================
-- Column comments
-- ============================================================

-- Keys
COMMENT ON COLUMN learning.grade_prediction_features_dataset.student_id
    IS 'UUID of the student (maps to learning.enrollments.student_id).';
COMMENT ON COLUMN learning.grade_prediction_features_dataset.semester_id
    IS 'FK → course_management.semesters. Identifies the semester this row belongs to.';
COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_id
    IS 'FK → course_management.subjects. Identifies the course being predicted.';

-- ── Targets ──────────────────────────────────────────────────────────────────
COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_grade
    IS 'Final grade on 4-point scale, converted from raw 10-point score via BK rules:
≥9.5→4.0 (A+), ≥8.5→4.0 (A), ≥8.0→3.5 (B+), ≥7.0→3.0 (B),
≥6.5→2.5 (C+), ≥5.5→2.0 (C), ≥5.0→1.5 (D+), ≥4.0→1.0 (D), else→0.0 (F).';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.target_gap
    IS 'course_grade − course_hist_median_smooth. Target for the gap-regression model.';

-- ── Enrollment context ────────────────────────────────────────────────────────
COMMENT ON COLUMN learning.grade_prediction_features_dataset.sem_credits
    IS 'Total enrolled credits in this semester: SUM(credits) over courses in (student, semester).';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.sem_credits_squared
    IS 'sem_credits². Non-linear credit-load signal.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.retake_flg
    IS 'True if course_attempt_index > 0, i.e. the student has taken this course before.
attempt_index = cumcount of (student_id, course_id) pairs ordered by sem_key.';

-- ── Student history flags ────────────────────────────────────────────────────
COMMENT ON COLUMN learning.grade_prediction_features_dataset.num_semesters_prior
    IS 'Number of semesters completed before the current one.
= semester_index − 1, where semester_index = per-student cumcount of semesters (ordered by sem_key) + 1.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.has_student_history
    IS 'num_semesters_prior > 0. False for a student''s very first semester.';

-- ── Student cumulative performance ───────────────────────────────────────────
COMMENT ON COLUMN learning.grade_prediction_features_dataset.cumulative_grade_avg
    IS 'Credit-weighted GPA up to (but not including) the current semester (scale 4):
= SUM(course_grade × credits over all prior sems) / SUM(credits over all prior sems).
NULL when no prior credits; imputed with running median of students who have history, 3-year window.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.previous_sem_grade_avg
    IS 'Credit-weighted avg grade of the immediately preceding semester (scale 4):
= SUM(course_grade × credits in sem k-1) / SUM(credits in sem k-1).
NULL on first semester; imputed with running median in 3-year window.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.grade_trend
    IS 'previous_sem_grade_avg − cumulative_grade_avg (scale 4).
Positive = improving, negative = declining. 0 when either component is unavailable.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.grade_consistency
    IS 'Population std-dev of course_grade across all prior courses (scale 4):
= SQRT( E[grade²] − E[grade]² ), requires ≥ 2 prior courses, else 0.
Lower = more consistent performance. Imputed with running median in 3-year window.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.historic_fail_ratio
    IS 'Fraction of prior credits that were failed (course_grade < 1.0 on scale 4):
= SUM(fail_credits before current sem) / SUM(total credits before current sem).
0 when no prior credits.';

-- ── Student ranking within semester ──────────────────────────────────────────
COMMENT ON COLUMN learning.grade_prediction_features_dataset.sem_rank_percentile
    IS '1 − percentile_rank(prev_sem_grade_avg) within (intake_year_id, semester_index).
0 = top of cohort, 1 = bottom. Value comes from the PREVIOUS semester''s rank. Default 0.5.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.gpa_rank_percentile
    IS '1 − percentile_rank(cumulative_grade_avg) within (intake_year_id, semester_index).
0 = top of cohort, 1 = bottom. Default 0.5 when GPA is unavailable.';

-- ── Student grade distribution rates ─────────────────────────────────────────
COMMENT ON COLUMN learning.grade_prediction_features_dataset.student_a_plus_grade_rate
    IS 'count(grade=4.0 AND raw_score≥9.5 in prior sems) / total_prior_courses.
Imputed with running mean over students with history, 3-year window.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.student_a_grade_rate
    IS 'count(course_grade≥4.0 in prior sems) / total_prior_courses (includes A+).
Imputed with running mean, 3-year window.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.student_b_plus_grade_rate
    IS 'count(course_grade≥3.5 in prior sems) / total_prior_courses.
Imputed with running mean, 3-year window.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.student_b_grade_rate
    IS 'count(course_grade≥3.0 in prior sems) / total_prior_courses.
Imputed with running mean, 3-year window.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.student_c_plus_grade_rate
    IS 'count(course_grade≥2.5 in prior sems) / total_prior_courses.
Imputed with running mean, 3-year window.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.student_c_grade_rate
    IS 'count(course_grade≥2.0 in prior sems) / total_prior_courses.
Imputed with running mean, 3-year window.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.student_d_plus_grade_rate
    IS 'count(course_grade≥1.5 in prior sems) / total_prior_courses.
Imputed with running mean, 3-year window.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.student_d_grade_rate
    IS 'count(course_grade≥1.0 in prior sems) / total_prior_courses (passed).
Imputed with running mean, 3-year window.';

-- ── Course historical baseline ────────────────────────────────────────────────
COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_hist_count
    IS 'Number of past enrollments in this course within the 3-year window:
sem_key ∈ [sem_key_current − 30, sem_key_current), where sem_key = year×10 + sem_no.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_hist_median_smooth
    IS 'Shrinkage estimator of course median grade (scale 4):
= (raw_median_past × count + global_median × k) / (count + k), k=10.
Equals global_median when course_hist_count = 0.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_hist_missing
    IS 'course_hist_count = 0. True when there is no prior enrollment data for this course in the 3-year window.';

-- ── Course difficulty & failure ───────────────────────────────────────────────
COMMENT ON COLUMN learning.grade_prediction_features_dataset.rank_course_difficulty
    IS 'percentile_rank(course_fail_rate) across all courses at the same sem_key.
Higher = harder course. Default 0.5 when course_fail_rate is unavailable.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_fail_rate
    IS 'Fraction of enrollments in this course (3-year window) where course_grade < 1.0:
= mean(is_fail) over hist window. NULL imputed with mean across courses that have history.';

-- ── Course grade distribution rates ──────────────────────────────────────────
COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_a_plus_grade_rate
    IS 'mean(raw_score≥9.5) over course enrollments in 3-year window. NULL → mean of courses with history.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_a_grade_rate
    IS 'mean(course_grade≥4.0) over course enrollments in 3-year window. NULL → mean of courses with history.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_b_plus_grade_rate
    IS 'mean(course_grade≥3.5) over course enrollments in 3-year window. NULL → mean of courses with history.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_b_grade_rate
    IS 'mean(course_grade≥3.0) over course enrollments in 3-year window. NULL → mean of courses with history.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_c_plus_grade_rate
    IS 'mean(course_grade≥2.5) over course enrollments in 3-year window. NULL → mean of courses with history.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_c_grade_rate
    IS 'mean(course_grade≥2.0) over course enrollments in 3-year window. NULL → mean of courses with history.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_d_plus_grade_rate
    IS 'mean(course_grade≥1.5) over course enrollments in 3-year window. NULL → mean of courses with history.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.course_d_grade_rate
    IS 'mean(course_grade≥1.0) over course enrollments in 3-year window. NULL → mean of courses with history.';

-- ── Relative performance vs course peers ─────────────────────────────────────
COMMENT ON COLUMN learning.grade_prediction_features_dataset.relative_avg_course_grade
    IS 'Credit-weighted avg grade of the student''s prior courses that share the same subject_category
OR are listed as PREREQUISITE / RECOMMENDED for the current course (union of both conditions).
Only courses taken in prior semesters (sem_key < current). NULL → 0.';

COMMENT ON COLUMN learning.grade_prediction_features_dataset.relative_avg_course_grade_rank_percentile
    IS 'mean( 1 − percentile_rank(course_grade within class) ) across the related prior classes above.
0 = consistently top of class, 1 = bottom. NULL → 0.5.';