-- ==================================================
-- Learning Goals
-- Captures the student's intent, preferences, and constraints.
-- ==================================================
CREATE TABLE IF NOT EXISTS personalization.learning_goals (
    learning_goal_id uuid DEFAULT gen_random_uuid() NOT NULL,
    student_id uuid NOT NULL, -- Links to user_management.students(user_id)

    -- Goal Parameters
    specialization_id varchar(100),
    target_gpa numeric(2, 1) CHECK (target_gpa >= 0 AND target_gpa <= 4.0),
    expected_completed_semester uuid NOT NULL, -- Links to course_management.semesters(id)

    -- Intensity Preferences
    pref_main_sem_learn_intensity varchar(10) DEFAULT 'Standard',
    planned_summer_sem_count int4 DEFAULT 0 CHECK (planned_summer_sem_count >= 0),

    -- Target Occupation Preferences
    target_occupation_code varchar(10), -- Links to personalization.occupation_data(onetsoc_code)

    -- Priorities (Ranking 1, 2, 3...)
    attempt_target_gpa_order int4,
    focus_on_target_occupation int4,
    completed_on_time int4,
    is_active boolean DEFAULT true NOT NULL,

    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,

    CONSTRAINT learning_goals_pkey PRIMARY KEY (learning_goal_id)
);

-- Index for quick lookup of a student's current goals
CREATE INDEX idx_learning_goals_student_id ON personalization.learning_goals(student_id);
CREATE INDEX idx_learning_goals_student_active ON personalization.learning_goals(student_id, is_active);

---

CREATE TABLE IF NOT EXISTS personalization.preferred_summer_semesters (
    preferred_summer_semester_id uuid DEFAULT gen_random_uuid() NOT NULL,
    learning_goal_id uuid NOT NULL,
    semester_id uuid NOT NULL,      -- Links to course_management.semesters(id)

    learning_intensity varchar(10) DEFAULT 'Light',

    CONSTRAINT preferred_summer_semesters_pkey PRIMARY KEY (preferred_summer_semester_id),
    -- FK to learning_goals within the same schema
    CONSTRAINT fk_learning_goal FOREIGN KEY (learning_goal_id)
        REFERENCES personalization.learning_goals(learning_goal_id) ON DELETE CASCADE,
    CONSTRAINT unique_student_summer UNIQUE (learning_goal_id, semester_id)
);

-- Index for joining summer preferences when calculating course loads
CREATE INDEX idx_pref_summer_goal_id ON personalization.preferred_summer_semesters(learning_goal_id);

---

CREATE TABLE IF NOT EXISTS personalization.graduation_requirement_status (
    graduation_requirement_status_id uuid DEFAULT gen_random_uuid() NOT NULL,
    student_id uuid NOT NULL,
    graduation_requirement_id uuid NOT NULL,

    is_completed boolean DEFAULT false,
    completion_semester_id uuid, -- Optional: when they actually finished it

    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,

    CONSTRAINT grad_req_status_pkey PRIMARY KEY (graduation_requirement_status_id),
    CONSTRAINT unique_student_requirement UNIQUE (student_id, graduation_requirement_id)
);

-- Index to quickly check a student's remaining requirements
CREATE INDEX idx_grad_req_student_completion ON personalization.graduation_requirement_status(student_id, is_completed);