CREATE TABLE "assessment_submissions" (
                                          "id" uuid PRIMARY KEY,
                                          "assessment_id" uuid NOT NULL,
                                          "student_id" uuid NOT NULL,
                                          "attempt_no" int,
                                          "submit_time" timestamptz,
                                          "score" numeric(5,3),
                                          "taken_time" int,
                                          "status" varchar,
                                          "create_at" timestamptz NOT NULL DEFAULT (now()),
                                          "update_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "question_submissions" (
                                        "id" uuid PRIMARY KEY,
                                        "assessment_submission_id" uuid NOT NULL,
                                        "question_id" uuid NOT NULL,
                                        "attempt_no" int,
                                        "score" numeric(5,3),
                                        "create_at" timestamptz NOT NULL DEFAULT (now()),
                                        "update_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "mcq_submissions" (
                                   "id" uuid PRIMARY KEY REFERENCES "question_submissions" ("id") ON DELETE CASCADE
);

CREATE TABLE "answer_options_mcq_submissions" (
                                                  "id" uuid primary key ,
                                                  "mcq_submission_id" uuid NOT NULL,
                                                  "option_id" uuid NOT NULL,
                                                  "mcq_question_id" uuid,
                                                  "create_at" timestamptz NOT NULL DEFAULT (now()),
                                                  "update_at" timestamptz NOT NULL DEFAULT (now()),
                                                  unique ("mcq_submission_id", "option_id", "mcq_question_id")
);

CREATE TABLE "essay_submissions" (
                                     "id" uuid PRIMARY KEY REFERENCES "question_submissions" ("id") ON DELETE CASCADE,
                                     "answer_file_url" varchar,
                                     "file_format" varchar,
                                     "num_pages" int,
                                     "word_count" int
);

CREATE TABLE "coding_submissions" (
                                      "id" uuid PRIMARY KEY REFERENCES "question_submissions" ("id") ON DELETE CASCADE,
                                      "input_code" text,
                                      "passed_testcases" int
);

CREATE TABLE "submission_testcase_results" (
                                               "id" uuid NOT NULL,
                                               "coding_submission_id" uuid NOT NULL,
                                               "testcase_id" uuid NOT NULL,
                                               "coding_question_id" uuid NOT NULL,
                                               "is_pass" boolean,
                                               "detail_error" text,
                                               "output" text,
                                               "execution_time_ms" int,
                                               "create_at" timestamptz NOT NULL DEFAULT (now()),
                                               "update_at" timestamptz NOT NULL DEFAULT (now()),
                                               PRIMARY KEY ("coding_question_id", "testcase_id", "id")
);

CREATE TABLE "feedbacks" (
                             "id" uuid PRIMARY KEY,
                             "content" text,
                             "feedback_time" timestamptz,
                             "teacher_id" uuid NOT NULL,
                             "question_submission_id" uuid,
                             "create_at" timestamptz NOT NULL DEFAULT (now()),
                             "update_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "feedback_attachments" (
                                        "id" uuid PRIMARY KEY,
                                        "feedback_id" uuid NOT NULL,
                                        "attachment_url" varchar,
                                        "attachment_type" varchar,
                                        "display_order" int
);

CREATE TABLE "assessments_gradings" (
                                        "id" uuid NOT NULL,
                                        "assessment_id" uuid NOT NULL,
                                        "weight" float,
                                        "create_at" timestamptz NOT NULL DEFAULT (now()),
                                        "update_at" timestamptz NOT NULL DEFAULT (now()),
                                        PRIMARY KEY ("id", "assessment_id")
);

CREATE UNIQUE INDEX ON "question_submissions" ("assessment_submission_id", "question_id");

CREATE UNIQUE INDEX ON "feedback_attachments" ("id", "feedback_id");

ALTER TABLE "assessment_submissions" ADD FOREIGN KEY ("assessment_id") REFERENCES "assessments" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "question_submissions" ADD FOREIGN KEY ("assessment_submission_id") REFERENCES "assessment_submissions" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "question_submissions" ADD FOREIGN KEY ("question_id") REFERENCES "questions" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "answer_options_mcq_submissions" ADD FOREIGN KEY ("mcq_submission_id") REFERENCES "mcq_submissions" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "answer_options_mcq_submissions" ADD FOREIGN KEY ("mcq_question_id", "option_id") REFERENCES "answer_options" ("mcq_question_id", "id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "submission_testcase_results" ADD FOREIGN KEY ("coding_submission_id") REFERENCES "coding_submissions" ("id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "submission_testcase_results" ADD FOREIGN KEY ("testcase_id", "coding_question_id") REFERENCES "testcases" ("id", "coding_question_id") ON DELETE CASCADE DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "feedbacks" ADD FOREIGN KEY ("question_submission_id") REFERENCES "question_submissions" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "feedback_attachments" ADD FOREIGN KEY ("feedback_id") REFERENCES "feedbacks" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "assessments_gradings" ADD FOREIGN KEY ("assessment_id") REFERENCES "assessments" ("id") DEFERRABLE INITIALLY IMMEDIATE;
