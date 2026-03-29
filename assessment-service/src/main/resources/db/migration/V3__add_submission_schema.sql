CREATE TABLE "assessment_submissions" (
    "id" uuid PRIMARY KEY,
    "assessment_id" uuid NOT NULL,
    "student_id" uuid NOT NULL,
    "attempt_no" int NOT NULL,
    "submit_time" timestamptz,
    "score" numeric(5,3),
    "taken_time" int,
    "status" varchar NOT NULL,
    "created_at" timestamptz NOT NULL DEFAULT (now()),
    "updated_at" timestamptz NOT NULL DEFAULT (now()),
    CONSTRAINT "chk_assessment_submissions_attempt_no" CHECK ("attempt_no" > 0),
    CONSTRAINT "chk_assessment_submissions_taken_time" CHECK ("taken_time" IS NULL OR "taken_time" >= 0),
    CONSTRAINT "unq_assessment_submissions_attempt" UNIQUE ("assessment_id", "student_id", "attempt_no")
);

CREATE TABLE "question_submissions" (
    "id" uuid PRIMARY KEY,
    "assessment_submission_id" uuid NOT NULL,
    "question_id" uuid NOT NULL,
    "score" numeric(5,3),
    "status" varchar,
    "created_at" timestamptz NOT NULL DEFAULT (now()),
    "updated_at" timestamptz NOT NULL DEFAULT (now()),
    CONSTRAINT "unq_question_submissions_per_attempt" UNIQUE ("assessment_submission_id", "question_id")
);

CREATE TABLE "mcq_submissions" (
    "id" uuid PRIMARY KEY,
    "selected_count" int,
    "created_at" timestamptz NOT NULL DEFAULT (now()),
    "updated_at" timestamptz NOT NULL DEFAULT (now()),
    CONSTRAINT "chk_mcq_submissions_selected_count" CHECK ("selected_count" IS NULL OR "selected_count" >= 0)
);

CREATE TABLE "answer_options_mcq_submissions" (
    "id" uuid PRIMARY KEY,
    "mcq_submission_id" uuid NOT NULL,
    "option_id" uuid NOT NULL,
    "created_at" timestamptz NOT NULL DEFAULT (now()),
    "updated_at" timestamptz NOT NULL DEFAULT (now()),
    CONSTRAINT "unq_mcq_submission_option" UNIQUE ("mcq_submission_id", "option_id")
);

CREATE TABLE "essay_submissions" (
    "id" uuid PRIMARY KEY,
    "answer_text" text,
    "answer_file_url" varchar,
    "file_format" varchar,
    "num_pages" int,
    "word_count" int,
    "created_at" timestamptz NOT NULL DEFAULT (now()),
    "updated_at" timestamptz NOT NULL DEFAULT (now()),
    CONSTRAINT "chk_essay_submissions_num_pages" CHECK ("num_pages" IS NULL OR "num_pages" >= 0),
    CONSTRAINT "chk_essay_submissions_word_count" CHECK ("word_count" IS NULL OR "word_count" >= 0)
);

CREATE TABLE "coding_submissions" (
    "id" uuid PRIMARY KEY,
    "input_code" text,
    "execution_language" varchar(50),
    "passed_testcases" int,
    "total_testcases" int,
    "created_at" timestamptz NOT NULL DEFAULT (now()),
    "updated_at" timestamptz NOT NULL DEFAULT (now()),
    CONSTRAINT "chk_coding_submissions_passed" CHECK ("passed_testcases" IS NULL OR "passed_testcases" >= 0),
    CONSTRAINT "chk_coding_submissions_total" CHECK ("total_testcases" IS NULL OR "total_testcases" >= 0),
    CONSTRAINT "chk_coding_submissions_passed_le_total" CHECK (
        "passed_testcases" IS NULL OR "total_testcases" IS NULL OR "passed_testcases" <= "total_testcases"
    )
);

CREATE TABLE "submission_testcase_results" (
    "id" uuid PRIMARY KEY,
    "coding_submission_id" uuid NOT NULL,
    "testcase_id" uuid NOT NULL,
    "is_pass" boolean,
    "detail_error" text,
    "output" text,
    "execution_time_ms" int,
    "created_at" timestamptz NOT NULL DEFAULT (now()),
    "updated_at" timestamptz NOT NULL DEFAULT (now()),
    CONSTRAINT "unq_submission_testcase" UNIQUE ("coding_submission_id", "testcase_id"),
    CONSTRAINT "chk_submission_testcase_exec_time" CHECK ("execution_time_ms" IS NULL OR "execution_time_ms" >= 0)
);

CREATE TABLE "feedbacks" (
    "id" uuid PRIMARY KEY,
    "content" text,
    "feedback_time" timestamptz NOT NULL DEFAULT (now()),
    "teacher_id" uuid NOT NULL,
    "question_submission_id" uuid NOT NULL,
    "created_at" timestamptz NOT NULL DEFAULT (now()),
    "updated_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "feedback_attachments" (
    "id" uuid PRIMARY KEY,
    "feedback_id" uuid NOT NULL,
    "attachment_url" varchar NOT NULL,
    "attachment_type" varchar,
    "display_order" int NOT NULL DEFAULT 0,
    "created_at" timestamptz NOT NULL DEFAULT (now()),
    "updated_at" timestamptz NOT NULL DEFAULT (now()),
    CONSTRAINT "chk_feedback_attachments_display_order" CHECK ("display_order" >= 0),
    CONSTRAINT "unq_feedback_attachment_order" UNIQUE ("feedback_id", "display_order")
);

CREATE TABLE "assessments_gradings" (
    "id" uuid PRIMARY KEY,
    "assessment_id" uuid NOT NULL,
    "weight" numeric(5,2),
    "created_at" timestamptz NOT NULL DEFAULT (now()),
    "updated_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE INDEX "idx_assessment_submissions_assessment_student"
    ON "assessment_submissions" ("assessment_id", "student_id");

CREATE INDEX "idx_question_submissions_question"
    ON "question_submissions" ("question_id");

CREATE INDEX "idx_submission_testcase_results_testcase"
    ON "submission_testcase_results" ("testcase_id");

CREATE INDEX "idx_feedbacks_question_submission"
    ON "feedbacks" ("question_submission_id");

ALTER TABLE "assessment_submissions"
    ADD FOREIGN KEY ("assessment_id") REFERENCES "assessments" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "question_submissions"
    ADD FOREIGN KEY ("assessment_submission_id") REFERENCES "assessment_submissions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "question_submissions"
    ADD FOREIGN KEY ("question_id") REFERENCES "questions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "mcq_submissions"
    ADD FOREIGN KEY ("id") REFERENCES "question_submissions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "answer_options_mcq_submissions"
    ADD FOREIGN KEY ("mcq_submission_id") REFERENCES "mcq_submissions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "answer_options_mcq_submissions"
    ADD FOREIGN KEY ("option_id") REFERENCES "answer_options" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "essay_submissions"
    ADD FOREIGN KEY ("id") REFERENCES "question_submissions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "coding_submissions"
    ADD FOREIGN KEY ("id") REFERENCES "question_submissions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "submission_testcase_results"
    ADD FOREIGN KEY ("coding_submission_id") REFERENCES "coding_submissions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "submission_testcase_results"
    ADD FOREIGN KEY ("testcase_id") REFERENCES "testcases" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "feedbacks"
    ADD FOREIGN KEY ("question_submission_id") REFERENCES "question_submissions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "feedback_attachments"
    ADD FOREIGN KEY ("feedback_id") REFERENCES "feedbacks" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "assessments_gradings"
    ADD FOREIGN KEY ("assessment_id") REFERENCES "assessments" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;
