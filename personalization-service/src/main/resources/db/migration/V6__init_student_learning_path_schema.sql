-- ==================================================
-- Learning Paths.
-- Defines the actual course sequences recommended to the student, based on their learning goals and progress.
-- ==================================================

CREATE TABLE IF NOT EXISTS personalization.learning_paths (
    learning_path_id uuid DEFAULT gen_random_uuid() NOT NULL,
    student_id uuid NOT NULL,
    learning_goal_id uuid NOT NULL,
    curriculum_code varchar NOT NULL,

    risk_level varchar(20), -- e.g., 'low', 'medium', 'high' risk of not graduating on time
    total_credits int4 DEFAULT 0 CHECK (total_credits >= 0),
    estimated_duration_semesters int4 CHECK (estimated_duration_semesters > 0),
    predicted_gpa numeric(4, 2) CHECK (predicted_gpa >= 0 AND predicted_gpa <= 4.0),
    completion_rate numeric(5, 2) DEFAULT 0.0,
    is_active boolean DEFAULT true NOT NULL,

    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,

    CONSTRAINT learning_paths_pkey PRIMARY KEY (learning_path_id),
    -- Link to the goal that generated this path
    CONSTRAINT fk_path_learning_goal FOREIGN KEY (learning_goal_id)
        REFERENCES personalization.learning_goals(learning_goal_id) ON DELETE CASCADE
);

-- Index for retrieving all paths for a specific student/goal
CREATE INDEX idx_learning_paths_goal_id ON personalization.learning_paths(learning_goal_id);
CREATE INDEX idx_learning_paths_student_active ON personalization.learning_paths(student_id, is_active);

---

CREATE TABLE IF NOT EXISTS personalization.learning_path_sections (
    learning_path_section_id uuid DEFAULT gen_random_uuid() NOT NULL,
    learning_path_id uuid NOT NULL,

    academic_year_id uuid NOT NULL,
    academic_year_order int4 NOT NULL, -- Year 1, 2, etc.

    semester_id uuid NOT NULL,
    semester_order int4 NOT NULL, -- Global order in path (1, 2, 3...)

    total_credits int4 DEFAULT 0 CHECK (total_credits >= 0),
    difficulty_score numeric(4, 2),

    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,

    CONSTRAINT learning_path_sections_pkey PRIMARY KEY (learning_path_section_id),
    CONSTRAINT fk_sections_path FOREIGN KEY (learning_path_id)
        REFERENCES personalization.learning_paths(learning_path_id) ON DELETE CASCADE
);

-- Index for ordering semesters within a path
CREATE INDEX idx_path_sections_ordering ON personalization.learning_path_sections(learning_path_id, semester_order);

---

CREATE TABLE IF NOT EXISTS personalization.learning_path_subjects (
    learning_path_subject_id uuid DEFAULT gen_random_uuid() NOT NULL,
    learning_path_id uuid NOT NULL,
    learning_path_section_id uuid NOT NULL,

    curriculum_subject_id uuid,
    subject_id uuid NOT NULL,
    subject_code varchar(50) NOT NULL,
    subject_name varchar(255) NOT NULL,

    credits int4 NOT NULL CHECK (credits >= 0),
    difficulty_level varchar(20) NOT NULL CHECK (difficulty_level IN ('easy', 'medium', 'hard')),
    avg_pass_rate numeric(5, 2) DEFAULT 0.0,
    avg_grade numeric(4, 2) DEFAULT 0.0,
    importance_score numeric(4, 2) DEFAULT 1.0,
    prerequisites_graph jsonb DEFAULT '[]'::jsonb,

    is_completed boolean,
    study_order int4,
    completion_date timestamptz,
    completion_grade numeric(4, 2),
    predicted_grade numeric(4, 2),
    attempt_no int4,
    is_highest_result boolean,

    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,

    CONSTRAINT learning_path_subjects_pkey PRIMARY KEY (learning_path_subject_id),
    -- Intra-schema FKs
    CONSTRAINT fk_subjects_path FOREIGN KEY (learning_path_id)
        REFERENCES personalization.learning_paths(learning_path_id) ON DELETE CASCADE,
    CONSTRAINT fk_subjects_section FOREIGN KEY (learning_path_section_id)
        REFERENCES personalization.learning_path_sections(learning_path_section_id) ON DELETE CASCADE
);

-- Critical index for loading the full subject list of a specific path
CREATE INDEX idx_path_subjects_path_id ON personalization.learning_path_subjects(learning_path_id);
-- Index for grouping subjects by semester
CREATE INDEX idx_path_subjects_section_id ON personalization.learning_path_subjects(learning_path_section_id);
-- Index for completed subjects ordering
CREATE INDEX idx_path_subjects_completion ON personalization.learning_path_subjects(is_completed, study_order);
