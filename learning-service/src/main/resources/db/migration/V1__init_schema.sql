-- Tạo schema learning
CREATE SCHEMA IF NOT EXISTS learning AUTHORIZATION lms_user;

create table learning.class_schedules (
                                          id uuid primary key,
                                          class_id uuid  not null,
                                          start_lesson int,
                                          num_of_lesson int,
                                          room_code varchar,
                                          campus_code varchar,
                                          start_time timestamp,
                                          end_time timestamp,
                                          created_at timestamptz NOT NULL DEFAULT (now()),
                                          updated_at timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE learning.class_schedule_weeks (
                                               id uuid PRIMARY KEY,
                                               class_schedule_id uuid NOT NULL,
                                               week_number int,
                                               created_at timestamptz NOT NULL DEFAULT (now()),
                                               updated_at timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE learning.schedule_events (
                                          id uuid PRIMARY KEY,
                                          user_id uuid NOT NULL,
                                          schedule_title varchar,
                                          description text,
                                          schedule_type varchar,
                                          owner_type varchar,
                                          start_date date,
                                          due_date date,
                                          is_required boolean,
                                          is_repeat boolean,
                                          location varchar,
                                          class_section_id uuid,
                                          created_at timestamptz NOT NULL DEFAULT (now()),
                                          updated_at timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE learning.event_resources (
                                          id uuid PRIMARY KEY,
                                          schedule_event_id uuid NOT NULL,
                                          resource_url varchar,
                                          resource_type varchar,
                                          created_at timestamptz NOT NULL DEFAULT (now()),
                                          updated_at timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE learning.learning_progresses (
                                              id uuid primary key,
                                              student_id uuid NOT NULL,
                                              lecture_id uuid NOT NULL,
                                              completion_date date,
                                              current_position int,
                                              progress_percentage float,
                                              total_time_spent int,
                                              last_accessed_at timestamp,
                                              completed_at timestamptz,
                                              created_at timestamptz NOT NULL DEFAULT (now()),
                                              updated_at timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE learning.enrollments (
                                      id uuid primary key ,
                                      student_id uuid NOT NULL,
                                      class_id uuid NOT NULL,
                                      enrolled_at timestamp default (now()),
                                      completion_time timestamp,
                                      final_grade float,
                                      attempt_no int default 1,
                                      progress_percentage float default 0.0,
                                      created_at timestamptz NOT NULL DEFAULT (now()),
                                      updated_at timestamptz NOT NULL DEFAULT (now())
);

alter table learning.learning_progresses add constraint uq_learning_progress_student_lecture unique (student_id, lecture_id);

alter table learning.enrollments add constraint uq_enrollments_student_class unique (student_id, class_id);

ALTER TABLE learning.class_schedule_weeks ADD FOREIGN KEY (class_schedule_id) REFERENCES learning.class_schedules (id);

ALTER TABLE learning.event_resources ADD FOREIGN KEY (schedule_event_id) REFERENCES learning.schedule_events (id);
