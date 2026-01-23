-- DROP SCHEMA course_management;

CREATE SCHEMA IF NOT EXISTS course_management AUTHORIZATION lms_user;
-- course_management.academic_years definition

-- Drop table

-- DROP TABLE course_management.academic_years;

CREATE TABLE course_management.academic_years (
                                                  id uuid NOT NULL,
                                                  year_code varchar NULL,
                                                  start_date date NULL,
                                                  end_date date NULL,
                                                  created_at timestamptz DEFAULT now() NOT NULL,
                                                  updated_at timestamptz DEFAULT now() NOT NULL,
                                                  CONSTRAINT academic_years_pkey PRIMARY KEY (id)
);


-- course_management.faculties definition

-- Drop table

-- DROP TABLE course_management.faculties;

CREATE TABLE course_management.faculties (
                                             id uuid NOT NULL,
                                             "name" varchar NULL,
                                             description varchar NULL,
                                             code varchar NULL,
                                             created_at timestamptz DEFAULT now() NOT NULL,
                                             updated_at timestamptz DEFAULT now() NOT NULL,
                                             CONSTRAINT faculties_pkey PRIMARY KEY (id)
);


-- course_management.gradings definition

-- Drop table

-- DROP TABLE course_management.gradings;

CREATE TABLE course_management.gradings (
                                            id uuid NOT NULL,
                                            "name" varchar NULL,
                                            description varchar NULL,
                                            grading_type varchar NULL,
                                            created_at timestamptz DEFAULT now() NOT NULL,
                                            updated_at timestamptz DEFAULT now() NOT NULL,
                                            CONSTRAINT gradings_pkey PRIMARY KEY (id)
);


-- course_management.graduation_requirements definition

-- Drop table

-- DROP TABLE course_management.graduation_requirements;

CREATE TABLE course_management.graduation_requirements (
                                                           id uuid NOT NULL,
                                                           "name" varchar NULL,
                                                           description varchar NULL,
                                                           code varchar NULL,
                                                           thresh_hold varchar NULL,
                                                           created_at timestamptz DEFAULT now() NOT NULL,
                                                           updated_at timestamptz DEFAULT now() NOT NULL,
                                                           CONSTRAINT graduation_requirements_pkey PRIMARY KEY (id)
);


-- course_management.intake_years definition

-- Drop table

-- DROP TABLE course_management.intake_years;

CREATE TABLE course_management.intake_years (
                                                id uuid NOT NULL,
                                                start_year int4 NOT NULL,
                                                "name" varchar NULL,
                                                created_at timestamptz DEFAULT now() NOT NULL,
                                                updated_at timestamptz DEFAULT now() NOT NULL,
                                                CONSTRAINT intake_years_pkey PRIMARY KEY (id),
                                                CONSTRAINT intake_years_start_year_key UNIQUE (start_year)
);


-- course_management.subjects definition

-- Drop table

-- DROP TABLE course_management.subjects;

CREATE TABLE course_management.subjects (
                                            id uuid NOT NULL,
                                            "name" varchar NULL,
                                            code varchar NULL,
                                            description text NULL,
                                            credits int4 NULL,
                                            created_at timestamptz DEFAULT now() NOT NULL,
                                            updated_at timestamptz DEFAULT now() NOT NULL,
                                            CONSTRAINT subjects_pkey PRIMARY KEY (id)
);


-- course_management.departments definition

-- Drop table

-- DROP TABLE course_management.departments;

CREATE TABLE course_management.departments (
                                               id uuid NOT NULL,
                                               "name" varchar NULL,
                                               description varchar NULL,
                                               faculty_id uuid NOT NULL,
                                               created_at timestamptz DEFAULT now() NOT NULL,
                                               updated_at timestamptz DEFAULT now() NOT NULL,
                                               CONSTRAINT departments_pkey PRIMARY KEY (id),
                                               CONSTRAINT departments_faculty_id_fkey FOREIGN KEY (faculty_id) REFERENCES course_management.faculties(id)
);


-- course_management.semesters definition

-- Drop table

-- DROP TABLE course_management.semesters;

CREATE TABLE course_management.semesters (
                                             id uuid NOT NULL,
                                             semester_code varchar NULL,
                                             start_date date NULL,
                                             end_date date NULL,
                                             academic_year_id uuid NOT NULL,
                                             created_at timestamptz DEFAULT now() NOT NULL,
                                             updated_at timestamptz DEFAULT now() NOT NULL,
                                             CONSTRAINT semesters_pkey PRIMARY KEY (id),
                                             CONSTRAINT semesters_academic_year_id_fkey FOREIGN KEY (academic_year_id) REFERENCES course_management.academic_years(id)
);


-- course_management.specializations definition

-- Drop table

-- DROP TABLE course_management.specializations;

CREATE TABLE course_management.specializations (
                                                   id uuid NOT NULL,
                                                   code varchar NULL,
                                                   "name" varchar NULL,
                                                   description varchar NULL,
                                                   department_id uuid NOT NULL,
                                                   created_at timestamptz DEFAULT now() NOT NULL,
                                                   updated_at timestamptz DEFAULT now() NOT NULL,
                                                   CONSTRAINT specializations_code_key UNIQUE (code),
                                                   CONSTRAINT specializations_pkey PRIMARY KEY (id),
                                                   CONSTRAINT specializations_department_id_fkey FOREIGN KEY (department_id) REFERENCES course_management.departments(id)
);


-- course_management.subjects_gradings definition

-- Drop table

-- DROP TABLE course_management.subjects_gradings;

CREATE TABLE course_management.subjects_gradings (
                                                     subject_id uuid NOT NULL,
                                                     grading_id uuid NOT NULL,
                                                     weight float8 NULL,
                                                     created_at timestamptz DEFAULT now() NOT NULL,
                                                     updated_at timestamptz DEFAULT now() NOT NULL,
                                                     CONSTRAINT subjects_gradings_pkey PRIMARY KEY (subject_id, grading_id),
                                                     CONSTRAINT subjects_gradings_grading_id_fkey FOREIGN KEY (grading_id) REFERENCES course_management.gradings(id) ON DELETE CASCADE,
                                                     CONSTRAINT subjects_gradings_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES course_management.subjects(id) ON DELETE CASCADE
);


-- course_management.class_sections definition

-- Drop table

-- DROP TABLE course_management.class_sections;

CREATE TABLE course_management.class_sections (
                                                  id uuid NOT NULL,
                                                  section_name varchar NULL,
                                                  status varchar NULL,
                                                  is_official bool NOT NULL,
                                                  teacher_id uuid NULL,
                                                  subject_id uuid NULL,
                                                  semester_id uuid NULL,
                                                  created_at timestamptz DEFAULT now() NOT NULL,
                                                  updated_at timestamptz DEFAULT now() NOT NULL,
                                                  created_by uuid NOT NULL,
                                                  description text NULL,
                                                  "language" varchar NULL,
                                                  "level" varchar NULL,
                                                  thumbnail_url varchar NULL,
                                                  intro_video varchar NULL,
                                                  duration_hours int4 NULL,
                                                  objective text NULL,
                                                  topic varchar NULL,
                                                  code varchar NOT NULL,
                                                  CONSTRAINT class_sections_pkey PRIMARY KEY (id),
                                                  CONSTRAINT class_sections_unique UNIQUE (code),
                                                  CONSTRAINT class_sections_semester_id_fkey FOREIGN KEY (semester_id) REFERENCES course_management.semesters(id),
                                                  CONSTRAINT class_sections_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES course_management.subjects(id)
);


-- course_management.class_sections_gradings definition

-- Drop table

-- DROP TABLE course_management.class_sections_gradings;

CREATE TABLE course_management.class_sections_gradings (
                                                           class_section_id uuid NOT NULL,
                                                           grading_id uuid NOT NULL,
                                                           weight float8 NULL,
                                                           created_at timestamptz DEFAULT now() NOT NULL,
                                                           updated_at timestamptz DEFAULT now() NOT NULL,
                                                           CONSTRAINT class_sections_gradings_pkey PRIMARY KEY (class_section_id, grading_id),
                                                           CONSTRAINT class_sections_gradings_class_section_id_fkey FOREIGN KEY (class_section_id) REFERENCES course_management.class_sections(id) ON DELETE CASCADE,
                                                           CONSTRAINT class_sections_gradings_grading_id_fkey FOREIGN KEY (grading_id) REFERENCES course_management.gradings(id) ON DELETE CASCADE
);


-- course_management.curriculums definition

-- Drop table

-- DROP TABLE course_management.curriculums;

CREATE TABLE course_management.curriculums (
                                               code varchar NOT NULL,
                                               "name" varchar NULL,
                                               total_credits int4 NULL,
                                               description varchar NULL,
                                               "document" varchar NULL,
                                               specialization_id uuid NOT NULL,
                                               intake_year_id uuid NOT NULL,
                                               created_at timestamptz DEFAULT now() NOT NULL,
                                               updated_at timestamptz DEFAULT now() NOT NULL,
                                               CONSTRAINT curriculums_code_key UNIQUE (code),
                                               CONSTRAINT curriculums_pkey PRIMARY KEY (code, specialization_id, intake_year_id),
                                               CONSTRAINT curriculums_intake_year_id_fkey FOREIGN KEY (intake_year_id) REFERENCES course_management.intake_years(id) ON DELETE CASCADE,
                                               CONSTRAINT curriculums_specialization_id_fkey FOREIGN KEY (specialization_id) REFERENCES course_management.specializations(id) ON DELETE CASCADE
);


-- course_management.graduation_requirements_curriculums definition

-- Drop table

-- DROP TABLE course_management.graduation_requirements_curriculums;

CREATE TABLE course_management.graduation_requirements_curriculums (
                                                                       graduation_requirement_id uuid NOT NULL,
                                                                       curriculum_intake_year_id uuid NOT NULL,
                                                                       curriculum_code varchar NOT NULL,
                                                                       curriculum_specialization_id uuid NOT NULL,
                                                                       created_at timestamptz DEFAULT now() NOT NULL,
                                                                       updated_at timestamptz DEFAULT now() NOT NULL,
                                                                       CONSTRAINT graduation_requirements_curriculums_pkey PRIMARY KEY (graduation_requirement_id, curriculum_intake_year_id, curriculum_code, curriculum_specialization_id),
                                                                       CONSTRAINT graduation_requirements_curri_curriculum_intake_year_id_cu_fkey FOREIGN KEY (curriculum_code,curriculum_specialization_id,curriculum_intake_year_id) REFERENCES course_management.curriculums(code,specialization_id,intake_year_id) ON DELETE CASCADE,
                                                                       CONSTRAINT graduation_requirements_curricul_graduation_requirement_id_fkey FOREIGN KEY (graduation_requirement_id) REFERENCES course_management.graduation_requirements(id)
);


-- course_management.chapters definition

-- Drop table

-- DROP TABLE course_management.chapters;

CREATE TABLE course_management.chapters (
                                            id uuid NOT NULL,
                                            title varchar NULL,
                                            description varchar NULL,
                                            order_index int4 NULL,
                                            status varchar NULL,
                                            class_section_id uuid NOT NULL,
                                            created_at timestamptz DEFAULT now() NOT NULL,
                                            updated_at timestamptz DEFAULT now() NOT NULL,
                                            CONSTRAINT chapters_pkey PRIMARY KEY (id),
                                            CONSTRAINT chapters_class_section_id_fkey FOREIGN KEY (class_section_id) REFERENCES course_management.class_sections(id)
);


-- course_management.curriculum_sections definition

-- Drop table

-- DROP TABLE course_management.curriculum_sections;

CREATE TABLE course_management.curriculum_sections (
                                                       id uuid NOT NULL,
                                                       "name" varchar NULL,
                                                       notes varchar NULL,
                                                       required_credits int4 NULL,
                                                       display_order int4 NULL,
                                                       description varchar NULL,
                                                       curriculum_intake_year_id uuid NULL,
                                                       curriculum_code varchar NULL,
                                                       curriculum_specialization_id uuid NULL,
                                                       created_at timestamptz DEFAULT now() NOT NULL,
                                                       updated_at timestamptz DEFAULT now() NOT NULL,
                                                       CONSTRAINT curriculum_sections_pkey PRIMARY KEY (id),
                                                       CONSTRAINT curriculum_sections_curriculum_code_curriculum_specializat_fkey FOREIGN KEY (curriculum_code,curriculum_specialization_id,curriculum_intake_year_id) REFERENCES course_management.curriculums(code,specialization_id,intake_year_id)
);


-- course_management.curriculum_subjects definition

-- Drop table

-- DROP TABLE course_management.curriculum_subjects;

CREATE TABLE course_management.curriculum_subjects (
                                                       id int4 NOT NULL,
                                                       "name" varchar NULL,
                                                       curriculum_section_id uuid NOT NULL,
                                                       subject_id uuid NOT NULL,
                                                       display_order int4 NULL,
                                                       is_required bool NULL,
                                                       category_name varchar NULL,
                                                       created_at timestamptz DEFAULT now() NOT NULL,
                                                       updated_at timestamptz DEFAULT now() NOT NULL,
                                                       CONSTRAINT curriculum_subjects_pkey PRIMARY KEY (curriculum_section_id, subject_id, id),
                                                       CONSTRAINT curriculum_subjects_curriculum_section_id_fkey FOREIGN KEY (curriculum_section_id) REFERENCES course_management.curriculum_sections(id) ON DELETE CASCADE,
                                                       CONSTRAINT curriculum_subjects_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES course_management.subjects(id) ON DELETE CASCADE
);


-- course_management.lectures definition

-- Drop table

-- DROP TABLE course_management.lectures;

CREATE TABLE course_management.lectures (
                                            id uuid NOT NULL,
                                            order_index int4 NULL,
                                            title varchar NULL,
                                            description varchar NULL,
                                            is_mandatory bool NULL,
                                            lecture_type varchar NULL,
                                            completion_rate float8 NULL,
                                            view_count int4 NULL,
                                            allow_preview bool NULL,
                                            is_downloadable bool NULL,
                                            chapter_id uuid NOT NULL,
                                            created_at timestamptz DEFAULT now() NOT NULL,
                                            updated_at timestamptz DEFAULT now() NOT NULL,
                                            CONSTRAINT lectures_pkey PRIMARY KEY (id),
                                            CONSTRAINT lectures_chapter_id_fkey FOREIGN KEY (chapter_id) REFERENCES course_management.chapters(id)
);


-- course_management.subject_parallels definition

-- Drop table

-- DROP TABLE course_management.subject_parallels;

CREATE TABLE course_management.subject_parallels (
                                                     curriculum_subject_id int4 NOT NULL,
                                                     curriculum_section_id uuid NOT NULL,
                                                     subject_id uuid NOT NULL,
                                                     parallels_curriculum_subject_id int4 NOT NULL,
                                                     parallels_curriculum_section_id uuid NOT NULL,
                                                     parallels_subject_id uuid NOT NULL,
                                                     created_at timestamptz DEFAULT now() NOT NULL,
                                                     updated_at timestamptz DEFAULT now() NOT NULL,
                                                     CONSTRAINT subject_parallels_pkey PRIMARY KEY (curriculum_subject_id, curriculum_section_id, subject_id, parallels_curriculum_subject_id, parallels_curriculum_section_id, parallels_subject_id),
                                                     CONSTRAINT subject_parallels_curriculum_subject_id_subject_id_curricu_fkey FOREIGN KEY (curriculum_section_id,subject_id,curriculum_subject_id) REFERENCES course_management.curriculum_subjects(curriculum_section_id,subject_id,id) ON DELETE CASCADE,
                                                     CONSTRAINT subject_parallels_parallels_curriculum_subject_id_parallel_fkey FOREIGN KEY (parallels_curriculum_section_id,parallels_subject_id,parallels_curriculum_subject_id) REFERENCES course_management.curriculum_subjects(curriculum_section_id,subject_id,id) ON DELETE CASCADE
);


-- course_management.subject_prerequisites definition

-- Drop table

-- DROP TABLE course_management.subject_prerequisites;

CREATE TABLE course_management.subject_prerequisites (
                                                         curriculum_subject_id int4 NOT NULL,
                                                         curriculum_section_id uuid NOT NULL,
                                                         subject_id uuid NOT NULL,
                                                         prere_curriculum_subject_id int4 NOT NULL,
                                                         prere_curriculum_section_id uuid NOT NULL,
                                                         prere_subject_id uuid NOT NULL,
                                                         created_at timestamptz DEFAULT now() NOT NULL,
                                                         updated_at timestamptz DEFAULT now() NOT NULL,
                                                         CONSTRAINT subject_prerequisites_pkey PRIMARY KEY (curriculum_subject_id, curriculum_section_id, subject_id, prere_curriculum_subject_id, prere_curriculum_section_id, prere_subject_id),
                                                         CONSTRAINT subject_prerequisites_curriculum_subject_id_subject_id_cur_fkey FOREIGN KEY (curriculum_section_id,subject_id,curriculum_subject_id) REFERENCES course_management.curriculum_subjects(curriculum_section_id,subject_id,id) ON DELETE CASCADE,
                                                         CONSTRAINT subject_prerequisites_prere_curriculum_subject_id_prere_su_fkey FOREIGN KEY (prere_curriculum_section_id,prere_subject_id,prere_curriculum_subject_id) REFERENCES course_management.curriculum_subjects(curriculum_section_id,subject_id,id) ON DELETE CASCADE
);


-- course_management.subject_recommendations definition

-- Drop table

-- DROP TABLE course_management.subject_recommendations;

CREATE TABLE course_management.subject_recommendations (
                                                           curriculum_subject_id int4 NOT NULL,
                                                           curriculum_section_id uuid NOT NULL,
                                                           subject_id uuid NOT NULL,
                                                           recommend_curriculum_subject_id int4 NOT NULL,
                                                           recommend_curriculum_section_id uuid NOT NULL,
                                                           recommend_subject_id uuid NOT NULL,
                                                           created_at timestamptz DEFAULT now() NOT NULL,
                                                           updated_at timestamptz DEFAULT now() NOT NULL,
                                                           CONSTRAINT subject_recommendations_pkey PRIMARY KEY (curriculum_subject_id, curriculum_section_id, subject_id, recommend_curriculum_subject_id, recommend_curriculum_section_id, recommend_subject_id),
                                                           CONSTRAINT subject_recommendations_curriculum_subject_id_subject_id_c_fkey FOREIGN KEY (curriculum_section_id,subject_id,curriculum_subject_id) REFERENCES course_management.curriculum_subjects(curriculum_section_id,subject_id,id) ON DELETE CASCADE,
                                                           CONSTRAINT subject_recommendations_recommend_curriculum_subject_id_re_fkey FOREIGN KEY (recommend_curriculum_section_id,recommend_subject_id,recommend_curriculum_subject_id) REFERENCES course_management.curriculum_subjects(curriculum_section_id,subject_id,id) ON DELETE CASCADE
);


-- course_management.text_lectures definition

-- Drop table

-- DROP TABLE course_management.text_lectures;

CREATE TABLE course_management.text_lectures (
                                                 lecture_id uuid NOT NULL,
                                                 "content" text NULL,
                                                 word_count int4 NULL,
                                                 format_type varchar NULL,
                                                 CONSTRAINT text_lectures_pkey PRIMARY KEY (lecture_id),
                                                 CONSTRAINT text_lectures_lecture_id_fkey FOREIGN KEY (lecture_id) REFERENCES course_management.lectures(id)
);


-- course_management.video_lectures definition

-- Drop table

-- DROP TABLE course_management.video_lectures;

CREATE TABLE course_management.video_lectures (
                                                  lecture_id uuid NOT NULL,
                                                  video_url varchar NULL,
                                                  duration int4 NULL,
                                                  CONSTRAINT video_lectures_pkey PRIMARY KEY (lecture_id),
                                                  CONSTRAINT video_lectures_lecture_id_fkey FOREIGN KEY (lecture_id) REFERENCES course_management.lectures(id)
);


-- course_management.video_transcripts definition

-- Drop table

-- DROP TABLE course_management.video_transcripts;

CREATE TABLE course_management.video_transcripts (
    id uuid NOT NULL,
    video_lecture_id uuid NOT NULL,
    transcript_text text NULL,
    language_code varchar NOT NULL,
    audio_duration int4 NULL,
    word_count int4 NULL,
    start_time_seconds int4 NOT NULL DEFAULT 0, -- Thời gian bắt đầu (giây)
    end_time_seconds int4 NULL, -- Thời gian kết thúc (giây)
    segment_index int4 NULL, -- Chỉ số đoạn/phần của video (0, 1, 2, ...)
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT video_transcripts_pkey PRIMARY KEY (id),
    CONSTRAINT video_transcripts_video_lecture_id_fkey FOREIGN KEY (video_lecture_id) REFERENCES course_management.video_lectures(lecture_id) ON DELETE CASCADE
);

-- Index để tìm transcript nhanh theo video lecture và thời gian
CREATE INDEX idx_video_transcripts_video_lecture_id ON course_management.video_transcripts(video_lecture_id);
CREATE INDEX idx_video_transcripts_time_range ON course_management.video_transcripts(video_lecture_id, start_time_seconds, end_time_seconds);
CREATE INDEX idx_video_transcripts_segment ON course_management.video_transcripts(video_lecture_id, segment_index);


-- course_management.document_lectures definition

-- Drop table

-- DROP TABLE course_management.document_lectures;

CREATE TABLE course_management.document_lectures (
                                                     lecture_id uuid NOT NULL,
                                                     file_url varchar NULL,
                                                     num_pages int4 NULL,
                                                     file_format varchar NULL,
                                                     CONSTRAINT document_lectures_pkey PRIMARY KEY (lecture_id),
                                                     CONSTRAINT document_lectures_lecture_id_fkey FOREIGN KEY (lecture_id) REFERENCES course_management.lectures(id)
);