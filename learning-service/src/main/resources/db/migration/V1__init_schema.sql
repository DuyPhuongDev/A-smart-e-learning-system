-- Tạo schema learning
CREATE SCHEMA IF NOT EXISTS learning AUTHORIZATION lms_user;

CREATE TABLE learning.class_schedules (
  id uuid PRIMARY KEY,
  class_id uuid NOT NULL,
  start_lesson INT,
  num_of_lesson INT,
  room_code VARCHAR,
  campus_code VARCHAR,
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  created_at TIMESTAMPTZ NOT NULL DEFAULT (now()),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT (now())
);

CREATE TABLE learning.class_schedule_weeks (
  id uuid PRIMARY KEY,
  class_schedule_id uuid NOT NULL,
  week_number INT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT (now()),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT (now())
);

CREATE TABLE learning.schedule_events (
  id uuid PRIMARY KEY,
  user_id uuid NOT NULL,
  schedule_title VARCHAR,
  description TEXT,
  schedule_type VARCHAR,
  owner_type VARCHAR,
  start_date date,
  due_date date,
  is_required BOOLEAN,
  is_repeat BOOLEAN,
  location VARCHAR,
  class_section_id uuid,
  created_at TIMESTAMPTZ NOT NULL DEFAULT (now()),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT (now())
);

CREATE TABLE learning.event_resources (
  id uuid PRIMARY KEY,
  schedule_event_id uuid NOT NULL,
  resource_url VARCHAR,
  resource_type VARCHAR,
  created_at TIMESTAMPTZ NOT NULL DEFAULT (now()),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT (now())
);

CREATE TABLE learning.learning_progresses (
  id uuid PRIMARY KEY,
  student_id uuid NOT NULL,
  lecture_id uuid NOT NULL,
  completion_date date,
  current_position INT,
  progress_percentage FLOAT,
  total_time_spent INT,
  last_accessed_at TIMESTAMP,
  completed_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT (now()),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT (now())
);

CREATE TABLE learning.enrollments (
  id uuid PRIMARY KEY,
  student_id uuid NOT NULL,
  class_id uuid NOT NULL,
  enrolled_at TIMESTAMP DEFAULT (now()),
  completion_time TIMESTAMP,
  final_grade FLOAT,
  attempt_no INT DEFAULT 1,
  progress_percentage FLOAT DEFAULT 0.0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT (now()),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT (now())
);

ALTER TABLE learning.learning_progresses
ADD CONSTRAINT uq_learning_progress_student_lecture UNIQUE (student_id, lecture_id);

ALTER TABLE learning.enrollments
ADD CONSTRAINT uq_enrollments_student_class UNIQUE (student_id, class_id);

ALTER TABLE learning.class_schedule_weeks
ADD FOREIGN KEY (class_schedule_id) REFERENCES learning.class_schedules (id);

ALTER TABLE learning.event_resources
ADD FOREIGN KEY (schedule_event_id) REFERENCES learning.schedule_events (id);