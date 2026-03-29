-- ===============================
-- V2 - Improve learning_progresses schema
-- ===============================

ALTER TABLE learning.learning_progresses
DROP COLUMN IF EXISTS completion_date;

ALTER TABLE learning.learning_progresses DROP COLUMN IF EXISTS last_accessed_at;

ALTER TABLE learning.learning_progresses
    ADD COLUMN content_type varchar(20) NOT NULL DEFAULT 'VIDEO',
ADD COLUMN position_unit varchar(20);


ALTER TABLE learning.learning_progresses
ALTER COLUMN progress_percentage
TYPE numeric(5,2)
USING ROUND(progress_percentage::numeric, 2);


DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_student_lecture'
    ) THEN
ALTER TABLE learning.learning_progresses
    ADD CONSTRAINT uq_student_lecture
        UNIQUE (student_id, lecture_id);
END IF;
END $$;


CREATE INDEX IF NOT EXISTS idx_lp_student
    ON learning.learning_progresses(student_id);

CREATE INDEX IF NOT EXISTS idx_lp_lecture
    ON learning.learning_progresses(lecture_id);

-- Composite index for resume query
CREATE INDEX IF NOT EXISTS idx_lp_student_lecture
    ON learning.learning_progresses(student_id, lecture_id);



CREATE TABLE learning.study_times (
                                      id uuid PRIMARY KEY,
                                      student_id uuid NOT NULL,
                                      class_id uuid NOT NULL,
                                      lecture_id uuid NOT NULL,
                                      duration_seconds int NOT NULL,
                                      started_at timestamp NOT NULL,
                                      ended_at timestamp,
                                      metadata jsonb,
                                      created_at timestamptz NOT NULL DEFAULT (now()),
                                      updated_at timestamptz NOT NULL DEFAULT (now())
);

-- Indexes for performance
CREATE INDEX idx_study_time_student ON learning.study_times(student_id);
CREATE INDEX idx_study_time_class ON learning.study_times(class_id);
CREATE INDEX idx_study_time_lecture ON learning.study_times(lecture_id);
CREATE INDEX idx_study_time_started_at ON learning.study_times(started_at);

-- Composite index for common queries
CREATE INDEX idx_study_time_student_class_started ON learning.study_times(student_id, class_id, started_at);

-- GIN index for JSONB metadata queries
CREATE INDEX idx_study_time_metadata ON learning.study_times USING GIN (metadata);
