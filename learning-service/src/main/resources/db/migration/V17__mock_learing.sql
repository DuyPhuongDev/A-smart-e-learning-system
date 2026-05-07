/* =========================
   LEARNING SEED DATA
   40 students enrolled in Advanced Programming
   ========================= */

WITH selected_students AS (
    SELECT
        s.user_id AS student_id,
        ROW_NUMBER() OVER (ORDER BY s.user_id) AS rn
    FROM user_management.students s
    WHERE s.user_id BETWEEN '90000000-0000-4000-8000-000000000001'::uuid
              AND '90000000-0000-4000-8000-000000000040'::uuid
),
     enrollment_profiles AS (
         SELECT
             student_id,
             rn,
             CASE
                 WHEN rn <= 8 THEN 'HIGH'
                 WHEN rn <= 28 THEN 'MEDIUM'
                 ELSE 'LOW'
                 END AS activity_band,
             CASE
                 WHEN rn <= 8 THEN LEAST(100.0, 84.0 + ((rn * 3) % 17))
                 WHEN rn <= 28 THEN 42.0 + ((rn * 7) % 39)
                 ELSE 6.0 + ((rn * 5) % 24)
                 END AS target_progress,
             ('2026-02-03 08:00:00'::timestamp + (rn || ' days')::interval + ((rn % 4) || ' hours')::interval) AS enrolled_at
         FROM selected_students
     ),
     inserted_enrollments AS (
         INSERT INTO learning.enrollments (
                                           id,
                                           student_id,
                                           class_id,
                                           enrolled_at,
                                           completion_time,
                                           final_grade,
                                           attempt_no,
                                           is_passed,
                                           progress_percentage
             )
             SELECT
                 (substr(md5('enrollment-' || ep.student_id::text || '-5221a82d-b509-45b9-bbda-4c757a831ad6'), 1, 8) || '-' ||
                  substr(md5('enrollment-' || ep.student_id::text || '-5221a82d-b509-45b9-bbda-4c757a831ad6'), 9, 4) || '-' ||
                  substr(md5('enrollment-' || ep.student_id::text || '-5221a82d-b509-45b9-bbda-4c757a831ad6'), 13, 4) || '-' ||
                  substr(md5('enrollment-' || ep.student_id::text || '-5221a82d-b509-45b9-bbda-4c757a831ad6'), 17, 4) || '-' ||
                  substr(md5('enrollment-' || ep.student_id::text || '-5221a82d-b509-45b9-bbda-4c757a831ad6'), 21, 12))::uuid AS id,
                 ep.student_id,
                 '5221a82d-b509-45b9-bbda-4c757a831ad6'::uuid AS class_id,
                 ep.enrolled_at,
                 CASE
                     WHEN ep.target_progress >= 99.5 THEN ep.enrolled_at + (((18 + ep.rn) % 35) || ' days')::interval
                     ELSE NULL
                     END AS completion_time,
                 CASE
                     WHEN ep.target_progress >= 70 THEN ROUND((6.5 + ((ep.rn * 13) % 33) / 10.0)::numeric, 2)::float8
                     ELSE NULL
                     END AS final_grade,
                 1 AS attempt_no,
                 (ep.target_progress >= 70) AS is_passed,
                 ROUND(ep.target_progress::numeric, 2)::float8 AS progress_percentage
             FROM enrollment_profiles ep
             ON CONFLICT (student_id, class_id) DO NOTHING
             RETURNING student_id
     )
SELECT COUNT(*) FROM inserted_enrollments;

WITH lecture_catalog AS (
    SELECT * FROM (VALUES
                       ('bc441182-fa60-4e4f-b863-468d1b7f3d7e'::uuid, 1, 10),
                       ('c687fbc2-dd78-41a3-943f-045b9eed3d34'::uuid, 2, 11),
                       ('5531c52e-2f10-49bd-ac07-9933aadc961a'::uuid, 3, 60),
                       ('1bd88230-19ac-4d5a-93f1-44733cf34dd3'::uuid, 4, 60),
                       ('cbb8c449-2e22-4cda-ae7c-854b156c561a'::uuid, 5, 60),
                       ('6a708d03-6676-4eee-a3f7-a1680293e500'::uuid, 6, 60),
                       ('56576a1d-5b81-4e06-824c-2a0e45e5a305'::uuid, 7, 60),
                       ('f0f814ac-a731-4f96-83e3-c1a59ee880d5'::uuid, 8, 20),
                       ('ae2bf777-3781-44f5-825d-4d0e5d28ebeb'::uuid, 9, 2)
                  ) AS t(lecture_id, lecture_order, estimate_minutes)
),
     selected_students AS (
         SELECT
             s.user_id AS student_id,
             ROW_NUMBER() OVER (ORDER BY s.user_id) AS rn
         FROM user_management.students s
         WHERE s.user_id BETWEEN '90000000-0000-4000-8000-000000000001'::uuid
                   AND '90000000-0000-4000-8000-000000000040'::uuid
     ),
     progress_profiles AS (
         SELECT
             student_id,
             rn,
             CASE
                 WHEN rn <= 8 THEN LEAST(100.0, 84.0 + ((rn * 3) % 17))
                 WHEN rn <= 28 THEN 42.0 + ((rn * 7) % 39)
                 ELSE 6.0 + ((rn * 5) % 24)
                 END AS target_progress,
             CASE
                 WHEN rn <= 8 THEN 8 + (rn % 2)               -- 8-9 lectures touched
                 WHEN rn <= 28 THEN 4 + ((rn * 3) % 4)        -- 4-7 lectures touched
                 ELSE 1 + ((rn * 5) % 3)                      -- 1-3 lectures touched
                 END AS touched_lectures,
             ('2026-02-03 08:00:00'::timestamp + (rn || ' days')::interval + ((rn % 4) || ' hours')::interval) AS enrolled_at
         FROM selected_students
     ),
     progress_rows AS (
         SELECT
             pp.student_id,
             pp.rn,
             lc.lecture_id,
             lc.lecture_order,
             lc.estimate_minutes,
             pp.target_progress,
             pp.touched_lectures,
             CASE
                 WHEN lc.lecture_order < pp.touched_lectures THEN 100.0
                 WHEN lc.lecture_order = pp.touched_lectures THEN GREATEST(8.0, LEAST(99.0, pp.target_progress - ((lc.lecture_order - 1) * (100.0 / pp.touched_lectures))))
                 ELSE NULL
                 END AS lecture_progress,
             pp.enrolled_at
         FROM progress_profiles pp
                  JOIN lecture_catalog lc ON lc.lecture_order <= pp.touched_lectures
     )
INSERT INTO learning.learning_progresses (
    id,
    student_id,
    lecture_id,
    current_position,
    progress_percentage,
    total_time_spent,
    completed_at,
    content_type,
    position_unit
)
SELECT
    (substr(md5('lp-' || pr.student_id::text || '-' || pr.lecture_id::text), 1, 8) || '-' ||
     substr(md5('lp-' || pr.student_id::text || '-' || pr.lecture_id::text), 9, 4) || '-' ||
     substr(md5('lp-' || pr.student_id::text || '-' || pr.lecture_id::text), 13, 4) || '-' ||
     substr(md5('lp-' || pr.student_id::text || '-' || pr.lecture_id::text), 17, 4) || '-' ||
     substr(md5('lp-' || pr.student_id::text || '-' || pr.lecture_id::text), 21, 12))::uuid AS id,
    pr.student_id,
    pr.lecture_id,
    ROUND((COALESCE(pr.lecture_progress, 0) / 100.0 * pr.estimate_minutes * 60)::numeric)::int AS current_position,
    ROUND(COALESCE(pr.lecture_progress, 0)::numeric, 2),
    ROUND((pr.estimate_minutes * 60 * (COALESCE(pr.lecture_progress, 0) / 100.0) *
           CASE
               WHEN pr.rn <= 8 THEN 1.35
               WHEN pr.rn <= 28 THEN 1.00
               ELSE 0.72
               END)::numeric)::int AS total_time_spent,
    CASE
        WHEN pr.lecture_progress >= 99.5 THEN pr.enrolled_at + ((pr.lecture_order * 2 + (pr.rn % 4)) || ' days')::interval
        ELSE NULL
        END AS completed_at,
    CASE
        WHEN pr.lecture_order <= 2 THEN 'VIDEO'
        ELSE 'DOCUMENT'
        END AS content_type,
    'SECONDS' AS position_unit
FROM progress_rows pr
ON CONFLICT (student_id, lecture_id) DO NOTHING;

WITH lecture_catalog AS (
    SELECT * FROM (VALUES
                       ('bc441182-fa60-4e4f-b863-468d1b7f3d7e'::uuid, 1),
                       ('c687fbc2-dd78-41a3-943f-045b9eed3d34'::uuid, 2),
                       ('5531c52e-2f10-49bd-ac07-9933aadc961a'::uuid, 3),
                       ('1bd88230-19ac-4d5a-93f1-44733cf34dd3'::uuid, 4),
                       ('cbb8c449-2e22-4cda-ae7c-854b156c561a'::uuid, 5),
                       ('6a708d03-6676-4eee-a3f7-a1680293e500'::uuid, 6),
                       ('56576a1d-5b81-4e06-824c-2a0e45e5a305'::uuid, 7),
                       ('f0f814ac-a731-4f96-83e3-c1a59ee880d5'::uuid, 8),
                       ('ae2bf777-3781-44f5-825d-4d0e5d28ebeb'::uuid, 9)
                  ) AS t(lecture_id, lecture_order)
),
     selected_students AS (
         SELECT
             s.user_id AS student_id,
             ROW_NUMBER() OVER (ORDER BY s.user_id) AS rn
         FROM user_management.students s
         WHERE s.user_id BETWEEN '90000000-0000-4000-8000-000000000001'::uuid
                   AND '90000000-0000-4000-8000-000000000040'::uuid
     ),
     study_profiles AS (
         SELECT
             student_id,
             rn,
             CASE
                 WHEN rn <= 8 THEN 12 + (rn % 4)              -- 12-15 sessions
                 WHEN rn <= 28 THEN 6 + ((rn * 2) % 5)        -- 6-10 sessions
                 ELSE 2 + (rn % 4)                            -- 2-5 sessions
                 END AS sessions_count,
             CASE
                 WHEN rn <= 8 THEN 8 + (rn % 2)
                 WHEN rn <= 28 THEN 4 + ((rn * 3) % 4)
                 ELSE 1 + ((rn * 5) % 3)
                 END AS touched_lectures,
             ('2026-02-03 08:00:00'::timestamp + (rn || ' days')::interval + ((rn % 4) || ' hours')::interval) AS enrolled_at
         FROM selected_students
     ),
     study_rows AS (
         SELECT
             sp.student_id,
             sp.rn,
             gs.session_no,
             lc.lecture_id,
             (sp.enrolled_at + ((gs.session_no * 2 + (sp.rn % 3)) || ' days')::interval + ((sp.rn + gs.session_no) % 5 || ' hours')::interval) AS started_at,
             CASE
                 WHEN sp.rn <= 8 THEN 1800 + ((sp.rn * 131 + gs.session_no * 211) % 2700)   -- 30-75 mins
                 WHEN sp.rn <= 28 THEN 900 + ((sp.rn * 97 + gs.session_no * 173) % 2100)     -- 15-50 mins
                 ELSE 420 + ((sp.rn * 61 + gs.session_no * 89) % 1020)                        -- 7-24 mins
                 END AS duration_seconds
         FROM study_profiles sp
                  JOIN LATERAL generate_series(1, sp.sessions_count) AS gs(session_no) ON TRUE
                  JOIN lecture_catalog lc ON lc.lecture_order = ((gs.session_no - 1) % sp.touched_lectures) + 1
     )
INSERT INTO learning.study_times (
    id,
    student_id,
    class_id,
    lecture_id,
    duration_seconds,
    started_at,
    ended_at,
    metadata
)
SELECT
    (substr(md5('st-' || sr.student_id::text || '-' || sr.lecture_id::text || '-' || sr.session_no::text), 1, 8) || '-' ||
     substr(md5('st-' || sr.student_id::text || '-' || sr.lecture_id::text || '-' || sr.session_no::text), 9, 4) || '-' ||
     substr(md5('st-' || sr.student_id::text || '-' || sr.lecture_id::text || '-' || sr.session_no::text), 13, 4) || '-' ||
     substr(md5('st-' || sr.student_id::text || '-' || sr.lecture_id::text || '-' || sr.session_no::text), 17, 4) || '-' ||
     substr(md5('st-' || sr.student_id::text || '-' || sr.lecture_id::text || '-' || sr.session_no::text), 21, 12))::uuid AS id,
    sr.student_id,
    '5221a82d-b509-45b9-bbda-4c757a831ad6'::uuid AS class_id,
    sr.lecture_id,
    sr.duration_seconds,
    sr.started_at,
    sr.started_at + (sr.duration_seconds || ' seconds')::interval AS ended_at,
    jsonb_build_object(
            'session_no', sr.session_no,
            'device', CASE WHEN (sr.rn + sr.session_no) % 3 = 0 THEN 'MOBILE' ELSE 'WEB' END,
            'source', CASE WHEN sr.rn <= 8 THEN 'self-study-intensive' WHEN sr.rn <= 28 THEN 'regular-learning' ELSE 'quick-review' END
    ) AS metadata
FROM study_rows sr
ON CONFLICT (id) DO NOTHING;

UPDATE course_management.class_sections
SET
    current_student = LEAST(max_student, current_student + 40),
    updated_at = now()
WHERE id = '5221a82d-b509-45b9-bbda-4c757a831ad6'::uuid;
