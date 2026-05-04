ALTER TABLE "submission_testcase_results"
    ADD COLUMN IF NOT EXISTS "verdict" varchar(16);

CREATE INDEX IF NOT EXISTS "idx_submission_testcase_results_coding_submission"
    ON "submission_testcase_results" ("coding_submission_id");

DROP TABLE IF EXISTS assessments_gradings;