-- Mock assessments and grades for the teacher class report demo.
-- Target class: MT1007_HK251_L01.

INSERT INTO assessment_db.assessments (
    id,
    class_id,
    title,
    assessment_type,
    assessment_status,
    grading_rule,
    max_attempts,
    time_limit,
    passing_score,
    start_time,
    close_time,
    show_correct_answers,
    can_review,
    created_at,
    updated_at
) VALUES
    (
        '1c9051a1-e968-4e3d-87f5-3c1820e4a951',
        'c0af4171-6a05-4a31-b368-bc40be5b9368',
        'Quiz 1: Tong quan mon hoc',
        'QUIZ',
        'PUBLISHED',
        'HIGH_SCORE',
        2,
        30,
        5,
        '2026-04-01 08:00:00',
        '2026-05-01 23:59:00',
        true,
        true,
        '2026-04-01 07:45:00+07',
        now()
    ),
    (
        '29cde1ff-4fa2-4d30-9927-b61f83b5b388',
        'c0af4171-6a05-4a31-b368-bc40be5b9368',
        'Bai tap Lab 1',
        'ASSIGNMENT',
        'PUBLISHED',
        'LAST_ATTEMPT',
        1,
        90,
        5,
        '2026-04-03 08:00:00',
        '2026-05-03 23:59:00',
        true,
        true,
        '2026-04-03 07:45:00+07',
        now()
    )
ON CONFLICT (id) DO UPDATE
SET
    title = EXCLUDED.title,
    assessment_status = EXCLUDED.assessment_status,
    grading_rule = EXCLUDED.grading_rule,
    start_time = EXCLUDED.start_time,
    close_time = EXCLUDED.close_time,
    updated_at = now();

WITH mock_submissions (
    ordinal,
    assessment_id,
    student_id,
    attempt_no,
    submit_time,
    score,
    taken_time,
    status
) AS (
    VALUES
        (1, '1c9051a1-e968-4e3d-87f5-3c1820e4a951'::uuid, '8f06fef7-9024-43f9-9bbc-cf89b9d063c1'::uuid, 1, '2026-04-05 09:15:00+07'::timestamptz, 8.000, 1240, 'SUBMITTED'),
        (2, '29cde1ff-4fa2-4d30-9927-b61f83b5b388'::uuid, '8f06fef7-9024-43f9-9bbc-cf89b9d063c1'::uuid, 1, '2026-04-07 10:20:00+07'::timestamptz, 7.000, 2860, 'SUBMITTED'),
        (3, '1c9051a1-e968-4e3d-87f5-3c1820e4a951'::uuid, '9d46f868-7b0c-45cf-8d3a-a3447019377b'::uuid, 1, '2026-04-05 09:25:00+07'::timestamptz, 7.000, 1320, 'SUBMITTED'),
        (4, '29cde1ff-4fa2-4d30-9927-b61f83b5b388'::uuid, '9d46f868-7b0c-45cf-8d3a-a3447019377b'::uuid, 1, '2026-04-07 10:35:00+07'::timestamptz, 6.500, 3100, 'SUBMITTED'),
        (5, '1c9051a1-e968-4e3d-87f5-3c1820e4a951'::uuid, '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5'::uuid, 1, '2026-04-05 09:40:00+07'::timestamptz, 5.000, 1460, 'SUBMITTED'),
        (6, '29cde1ff-4fa2-4d30-9927-b61f83b5b388'::uuid, '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5'::uuid, 1, '2026-04-07 11:00:00+07'::timestamptz, 5.500, 3500, 'SUBMITTED'),
        (7, '1c9051a1-e968-4e3d-87f5-3c1820e4a951'::uuid, '610c2b86-0f1d-4c34-a517-aea2b92ce27d'::uuid, 1, '2026-04-05 10:10:00+07'::timestamptz, 2.000, 840, 'SUBMITTED'),
        (8, '29cde1ff-4fa2-4d30-9927-b61f83b5b388'::uuid, '610c2b86-0f1d-4c34-a517-aea2b92ce27d'::uuid, 1, '2026-04-07 11:25:00+07'::timestamptz, 3.000, 1800, 'SUBMITTED'),
        (9, '1c9051a1-e968-4e3d-87f5-3c1820e4a951'::uuid, 'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8'::uuid, 1, '2026-04-05 08:55:00+07'::timestamptz, 9.000, 1180, 'SUBMITTED'),
        (10, '29cde1ff-4fa2-4d30-9927-b61f83b5b388'::uuid, 'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8'::uuid, 1, '2026-04-07 09:50:00+07'::timestamptz, 8.500, 2700, 'SUBMITTED')
)
INSERT INTO assessment_db.assessment_submissions (
    id,
    assessment_id,
    student_id,
    attempt_no,
    submit_time,
    score,
    taken_time,
    status,
    created_at,
    updated_at
)
SELECT
    ('e0000000-0000-0000-0000-' || lpad(ordinal::text, 12, '0'))::uuid,
    assessment_id,
    student_id,
    attempt_no,
    submit_time,
    score,
    taken_time,
    status,
    now(),
    now()
FROM mock_submissions
ON CONFLICT (assessment_id, student_id, attempt_no) DO UPDATE
SET
    submit_time = EXCLUDED.submit_time,
    score = EXCLUDED.score,
    taken_time = EXCLUDED.taken_time,
    status = EXCLUDED.status,
    updated_at = now();
