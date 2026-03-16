-- =========================================================
-- MIGRATION: Update curriculum schema
-- =========================================================
-- Changes:
--   1. Add priority_weight to curriculum_sections
-- =========================================================

-- ── 1. curriculum_sections: add priority_weight ───────────────────────────
ALTER TABLE course_management.curriculum_sections
    ADD COLUMN IF NOT EXISTS priority_weight int4 NULL;