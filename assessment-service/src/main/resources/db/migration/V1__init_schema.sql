CREATE SCHEMA IF NOT EXISTS assessment_db AUTHORIZATION lms_user;

CREATE TABLE "question_banks" (
    "id"          uuid PRIMARY KEY,
    "name"        varchar,
    "description" varchar,
    "is_public"   bool,
    "owner_id"    uuid        NOT NULL,
    "created_at"  timestamptz NOT NULL DEFAULT (now()),
    "updated_at"  timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "questions" (
    "id"              uuid PRIMARY KEY,
    "difficult_level" varchar,
    "point"           numeric(5,3),
    "question_type"   varchar,
    "content"         text,
    "required"        bool,
    "created_at"      timestamptz NOT NULL DEFAULT (now()),
    "updated_at"      timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "mcq_questions" (
    "id"       uuid PRIMARY KEY,
    "allow_multi_answer" bool,
    "shuffle_option"    bool
);

CREATE TABLE "answer_options" (
    "id"              uuid PRIMARY KEY,
    "mcq_question_id" uuid NOT NULL,
    "content"         text,
    "correct"         bool,
    "order_index"     int NOT NULL,
    "explanation"     text,
    "created_at"      timestamptz NOT NULL DEFAULT (now()),
    "updated_at"      timestamptz NOT NULL DEFAULT (now())
);

ALTER TABLE "answer_options" ADD CONSTRAINT unq_question_id_order_index UNIQUE (mcq_question_id, order_index);

CREATE TABLE "coding_questions" (
    "id"             uuid PRIMARY KEY,
    "problem_description"     text,
    "execution_time_limit"    int,
    "execution_memory_limit"  int,
    "language"           varchar(50),
    "initial_code"       text
);

CREATE TABLE "testcases" (
    "id"                 uuid PRIMARY KEY,
    "coding_question_id" uuid        NOT NULL,
    "input"              text,
    "expected"           text,
    "is_hidden"          bool,
    "created_at"         timestamptz NOT NULL DEFAULT (now()),
    "updated_at"         timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "essay_questions" (
    "id"  uuid PRIMARY KEY,
    "sample_answer" text,
    "max_file_size" int
);

CREATE TABLE "essay_accepted_file_types" (
    "id"                uuid PRIMARY KEY,
    "essay_question_id" uuid        NOT NULL,
    "file_type"         varchar,
    "created_at"        timestamptz NOT NULL DEFAULT (now()),
    "updated_at"        timestamptz NOT NULL DEFAULT (now())
);

ALTER TABLE "essay_accepted_file_types"
    ADD CONSTRAINT "unq_essay_id_file_type" UNIQUE ("essay_question_id", "file_type");

CREATE TABLE "assessments" (
    "id"                  uuid PRIMARY KEY,
    "class_id"            uuid        NOT NULL,
    "title"               varchar,
    "assessment_type"     varchar,
    "assessment_status"   varchar,
    "grading_rule"        varchar,
    "max_attempts"        int,
    "time_limit"          int,
    "passing_score"       int,
    "start_time"          timestamp,
    "close_time"          timestamp,
    "show_correct_answers" bool,
    "can_review"          bool,
    "created_at"          timestamptz NOT NULL DEFAULT (now()),
    "updated_at"          timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "assessment_questions" (
    "id" uuid primary key,
    "assessment_id" uuid        NOT NULL,
    "question_id"   uuid        NOT NULL,
    "order_index"   int,
    "created_at"    timestamptz NOT NULL DEFAULT (now()),
    "updated_at"    timestamptz NOT NULL DEFAULT (now()),
    unique ("assessment_id", "question_id")
);

CREATE TABLE "question_banks_questions" (
    "question_banks_id" uuid,
    "questions_id"      uuid,
    PRIMARY KEY ("question_banks_id", "questions_id")
);

-- Foreign Keys
ALTER TABLE "question_banks_questions"
    ADD FOREIGN KEY ("question_banks_id") REFERENCES "question_banks" ("id") DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "question_banks_questions"
    ADD FOREIGN KEY ("questions_id") REFERENCES "questions" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "mcq_questions"
    ADD FOREIGN KEY ("id") REFERENCES "questions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "answer_options"
    ADD FOREIGN KEY ("mcq_question_id") REFERENCES "mcq_questions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "coding_questions"
    ADD FOREIGN KEY ("id") REFERENCES "questions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "testcases"
    ADD FOREIGN KEY ("coding_question_id") REFERENCES "coding_questions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "essay_questions"
    ADD FOREIGN KEY ("id") REFERENCES "questions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "essay_accepted_file_types"
    ADD FOREIGN KEY ("essay_question_id") REFERENCES "essay_questions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "assessment_questions"
    ADD FOREIGN KEY ("assessment_id") REFERENCES "assessments" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;
ALTER TABLE "assessment_questions"
    ADD FOREIGN KEY ("question_id") REFERENCES "questions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;
