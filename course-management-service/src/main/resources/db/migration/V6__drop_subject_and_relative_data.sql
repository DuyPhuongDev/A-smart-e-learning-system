-- =========================================================
-- DROP SUBJECT AND RELATIVE DATA
-- Delete all subject-related data seeded in V2 to allow
-- re-initialization in V8 (without category_name column)
-- =========================================================

-- 1. Lecture content (deepest level)
DELETE FROM course_management.video_transcripts;
DELETE FROM course_management.video_lectures;
DELETE FROM course_management.text_lectures;
DELETE FROM course_management.document_lectures;
DELETE FROM course_management.lectures;

-- 2. Chapters (depends on class_sections)
DELETE FROM course_management.chapters;

-- 3. Class sections and gradings
DELETE FROM course_management.class_sections_gradings;
DELETE FROM course_management.class_sections;

-- 4. Subject curriculum relationships (depends on curriculum_subjects)
DELETE FROM course_management.subject_recommendations;
DELETE FROM course_management.subject_prerequisites;
DELETE FROM course_management.subject_parallels;
DELETE FROM course_management.curriculum_subjects;

-- 5. Graduation requirements curriculum mapping
DELETE FROM course_management.graduation_requirements_curriculums;
DELETE FROM course_management.graduation_requirements;

-- 6. Curriculum structure
DELETE FROM course_management.curriculum_sections;
DELETE FROM course_management.curriculums;

-- 7. Organization hierarchy
DELETE FROM course_management.specializations;
DELETE FROM course_management.departments;
DELETE FROM course_management.faculties;
DELETE FROM course_management.intake_years;

-- 8. Base subject data
DELETE FROM course_management.subjects_gradings;
DELETE FROM course_management.gradings;
DELETE FROM course_management.subjects;
