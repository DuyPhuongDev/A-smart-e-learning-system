-- =========================================================
-- SUBJECT LEARNING OUTCOMES TABLE INITIALIZATION
-- =========================================================

-- Drop table if exists (optional, handled by migrations usually but good for init script)
-- DROP TABLE IF EXISTS course_management.subject_learning_outcomes;

CREATE TABLE IF NOT EXISTS course_management.subject_learning_outcomes (
    subject_learning_outcomes_id uuid NOT NULL,
    subject_id uuid NOT NULL,
    subject_code varchar NULL,
    subject_name varchar NULL,
    subject_name_en varchar NULL,
    subject_learning_outcomes_parent_id uuid NULL,
    code varchar NULL,
    description varchar NULL,
    description_en varchar NULL,
    display_order int4 NULL,
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT subject_learning_outcomes_pkey PRIMARY KEY (subject_learning_outcomes_id)
);

-- Add foreign key constraint
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_subject_learning_outcomes_parent') THEN
            ALTER TABLE course_management.subject_learning_outcomes
                ADD CONSTRAINT fk_subject_learning_outcomes_parent
                    FOREIGN KEY (subject_learning_outcomes_parent_id)
                        REFERENCES course_management.subject_learning_outcomes(subject_learning_outcomes_id);
        END IF;
    END $$;
