-- =========================================================
-- PRIORITY SCHEMA
-- Table for recommended semester per curriculum subject
-- =========================================================

-- course_management.curriculum_subject_priorities definition
-- Stores recommended year/semester for each subject in each curriculum.
-- Priority is computed on demand:
--   primary_priority   = 2 * recommended_year - 1 + recommended_semester_in_year
--   secondary_priority = curriculum_sections.priority_weight

CREATE TABLE IF NOT EXISTS course_management.curriculum_subject_priorities (
    id serial NOT NULL,
    curriculum_subject_id int4 NOT NULL,
    curriculum_section_id uuid NOT NULL,
    subject_id uuid NOT NULL,
    recommended_year int4 NOT NULL,
    recommended_semester_in_year int4 NOT NULL,
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT curriculum_subject_priorities_pkey PRIMARY KEY (curriculum_subject_id, curriculum_section_id, subject_id),
    CONSTRAINT curriculum_subject_priorities_cs_fkey
        FOREIGN KEY (curriculum_section_id, subject_id, curriculum_subject_id)
            REFERENCES course_management.curriculum_subjects(curriculum_section_id, subject_id, id)
            ON DELETE CASCADE,
    CONSTRAINT curriculum_subject_priorities_year_check
        CHECK (recommended_year BETWEEN 1 AND 6),
    CONSTRAINT curriculum_subject_priorities_semester_check
        CHECK (recommended_semester_in_year IN (1, 2, 3))
);

COMMENT ON TABLE course_management.curriculum_subject_priorities IS
    'Recommended semester for each subject within a curriculum.
    Priorities are computed on demand:
      primary_priority   = 2 * recommended_year - 1 + recommended_semester_in_year  (range 1-8)
      secondary_priority = curriculum_sections.priority_weight  (range 1-13)
    To rank courses: sort by primary_priority ASC, then secondary_priority ASC.';

COMMENT ON COLUMN course_management.curriculum_subject_priorities.recommended_year IS
    'Recommended year to take the course (1-4).';

COMMENT ON COLUMN course_management.curriculum_subject_priorities.recommended_semester_in_year IS
    'Recommended semester within the year (1 or 2).';
