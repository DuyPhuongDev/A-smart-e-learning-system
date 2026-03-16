-- =========================================================
-- MIGRATION: Update curriculum schema
-- =========================================================
-- Changes:
--   1. Drop category_name from curriculum_subjects
-- =========================================================

-- ── 1. curriculum_subjects: drop category_name ───────────────────────────
ALTER TABLE course_management.curriculum_subjects
    DROP COLUMN IF EXISTS category_name;
