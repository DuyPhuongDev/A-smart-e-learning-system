-- Mock enrollments and study time for the teacher class report demo.
-- Target class: MT1007_HK251_L01.

INSERT INTO learning.enrollments (
    id,
    student_id,
    class_id,
    final_grade,
    progress_percentage,
    enrolled_at,
    created_at,
    updated_at
) VALUES
    (
        '60a7f005-1276-4056-b970-cc53033a55e1',
        '8f06fef7-9024-43f9-9bbc-cf89b9d063c1',
        'c0af4171-6a05-4a31-b368-bc40be5b9368',
        7.5,
        0.88,
        '2026-03-25 08:00:00',
        now(),
        now()
    ),
    (
        'c0010000-0000-0000-0000-000000000002',
        '9d46f868-7b0c-45cf-8d3a-a3447019377b',
        'c0af4171-6a05-4a31-b368-bc40be5b9368',
        6.75,
        0.74,
        '2026-03-25 08:05:00',
        now(),
        now()
    ),
    (
        'c0010000-0000-0000-0000-000000000003',
        '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5',
        'c0af4171-6a05-4a31-b368-bc40be5b9368',
        5.25,
        0.43,
        '2026-03-25 08:10:00',
        now(),
        now()
    ),
    (
        'c0010000-0000-0000-0000-000000000004',
        '610c2b86-0f1d-4c34-a517-aea2b92ce27d',
        'c0af4171-6a05-4a31-b368-bc40be5b9368',
        2.5,
        0.09,
        '2026-03-25 08:15:00',
        now(),
        now()
    ),
    (
        'c0010000-0000-0000-0000-000000000005',
        'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8',
        'c0af4171-6a05-4a31-b368-bc40be5b9368',
        8.75,
        0.92,
        '2026-03-25 08:20:00',
        now(),
        now()
    )
ON CONFLICT (student_id, class_id) DO UPDATE
SET
    final_grade = EXCLUDED.final_grade,
    progress_percentage = EXCLUDED.progress_percentage,
    updated_at = now();

WITH mock_sessions (
    ordinal,
    student_id,
    lecture_id,
    duration_seconds,
    started_at
) AS (
    VALUES
        (1, '8f06fef7-9024-43f9-9bbc-cf89b9d063c1'::uuid, '45d2b312-2849-4aa4-9ccc-ff881966feb9'::uuid, 2500, '2026-04-01 08:00:00'::timestamp),
        (2, '8f06fef7-9024-43f9-9bbc-cf89b9d063c1'::uuid, '3250126c-8341-4080-b964-63c5b0b18820'::uuid, 3300, '2026-04-02 08:00:00'::timestamp),
        (3, '8f06fef7-9024-43f9-9bbc-cf89b9d063c1'::uuid, '19480479-4a40-40d4-b843-2ef53beaf24c'::uuid, 2600, '2026-04-03 08:00:00'::timestamp),
        (4, '8f06fef7-9024-43f9-9bbc-cf89b9d063c1'::uuid, 'b77e943d-ac1b-4e01-b614-53c501344569'::uuid, 2100, '2026-04-04 08:00:00'::timestamp),
        (5, '8f06fef7-9024-43f9-9bbc-cf89b9d063c1'::uuid, '9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid, 2300, '2026-04-05 08:00:00'::timestamp),
        (6, '8f06fef7-9024-43f9-9bbc-cf89b9d063c1'::uuid, 'febe6123-a925-44cf-a109-aefc7cf61420'::uuid, 5000, '2026-04-06 08:00:00'::timestamp),
        (7, '8f06fef7-9024-43f9-9bbc-cf89b9d063c1'::uuid, '74e22a79-6b77-413f-9053-f6f79566226b'::uuid, 1700, '2026-04-07 08:00:00'::timestamp),
        (8, '8f06fef7-9024-43f9-9bbc-cf89b9d063c1'::uuid, '5655cc4d-1de7-4263-9950-dfa39500d821'::uuid, 2200, '2026-04-08 08:00:00'::timestamp),
        (9, '9d46f868-7b0c-45cf-8d3a-a3447019377b'::uuid, '45d2b312-2849-4aa4-9ccc-ff881966feb9'::uuid, 2000, '2026-04-01 09:00:00'::timestamp),
        (10, '9d46f868-7b0c-45cf-8d3a-a3447019377b'::uuid, '3250126c-8341-4080-b964-63c5b0b18820'::uuid, 2800, '2026-04-02 09:00:00'::timestamp),
        (11, '9d46f868-7b0c-45cf-8d3a-a3447019377b'::uuid, '19480479-4a40-40d4-b843-2ef53beaf24c'::uuid, 2200, '2026-04-03 09:00:00'::timestamp),
        (12, '9d46f868-7b0c-45cf-8d3a-a3447019377b'::uuid, 'b77e943d-ac1b-4e01-b614-53c501344569'::uuid, 1600, '2026-04-04 09:00:00'::timestamp),
        (13, '9d46f868-7b0c-45cf-8d3a-a3447019377b'::uuid, '9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid, 1800, '2026-04-05 09:00:00'::timestamp),
        (14, '9d46f868-7b0c-45cf-8d3a-a3447019377b'::uuid, 'febe6123-a925-44cf-a109-aefc7cf61420'::uuid, 4100, '2026-04-06 09:00:00'::timestamp),
        (15, '9d46f868-7b0c-45cf-8d3a-a3447019377b'::uuid, '74e22a79-6b77-413f-9053-f6f79566226b'::uuid, 1400, '2026-04-07 09:00:00'::timestamp),
        (16, '9d46f868-7b0c-45cf-8d3a-a3447019377b'::uuid, '5655cc4d-1de7-4263-9950-dfa39500d821'::uuid, 1700, '2026-04-08 09:00:00'::timestamp),
        (17, '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5'::uuid, '45d2b312-2849-4aa4-9ccc-ff881966feb9'::uuid, 1400, '2026-04-01 10:00:00'::timestamp),
        (18, '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5'::uuid, '3250126c-8341-4080-b964-63c5b0b18820'::uuid, 1200, '2026-04-02 10:00:00'::timestamp),
        (19, '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5'::uuid, '19480479-4a40-40d4-b843-2ef53beaf24c'::uuid, 1500, '2026-04-03 10:00:00'::timestamp),
        (20, '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5'::uuid, 'b77e943d-ac1b-4e01-b614-53c501344569'::uuid, 800, '2026-04-04 10:00:00'::timestamp),
        (21, '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5'::uuid, '9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid, 900, '2026-04-05 10:00:00'::timestamp),
        (22, '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5'::uuid, 'febe6123-a925-44cf-a109-aefc7cf61420'::uuid, 2300, '2026-04-06 10:00:00'::timestamp),
        (23, '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5'::uuid, '74e22a79-6b77-413f-9053-f6f79566226b'::uuid, 600, '2026-04-07 10:00:00'::timestamp),
        (24, '4d750d82-8f06-4b45-b3f1-ef2e2cb0a1e5'::uuid, '5655cc4d-1de7-4263-9950-dfa39500d821'::uuid, 900, '2026-04-08 10:00:00'::timestamp),
        (25, '610c2b86-0f1d-4c34-a517-aea2b92ce27d'::uuid, '45d2b312-2849-4aa4-9ccc-ff881966feb9'::uuid, 300, '2026-04-01 11:00:00'::timestamp),
        (26, '610c2b86-0f1d-4c34-a517-aea2b92ce27d'::uuid, '3250126c-8341-4080-b964-63c5b0b18820'::uuid, 200, '2026-04-02 11:00:00'::timestamp),
        (27, '610c2b86-0f1d-4c34-a517-aea2b92ce27d'::uuid, '19480479-4a40-40d4-b843-2ef53beaf24c'::uuid, 400, '2026-04-03 11:00:00'::timestamp),
        (28, '610c2b86-0f1d-4c34-a517-aea2b92ce27d'::uuid, 'b77e943d-ac1b-4e01-b614-53c501344569'::uuid, 100, '2026-04-04 11:00:00'::timestamp),
        (29, '610c2b86-0f1d-4c34-a517-aea2b92ce27d'::uuid, '9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid, 200, '2026-04-05 11:00:00'::timestamp),
        (30, '610c2b86-0f1d-4c34-a517-aea2b92ce27d'::uuid, 'febe6123-a925-44cf-a109-aefc7cf61420'::uuid, 600, '2026-04-06 11:00:00'::timestamp),
        (31, '610c2b86-0f1d-4c34-a517-aea2b92ce27d'::uuid, '74e22a79-6b77-413f-9053-f6f79566226b'::uuid, 100, '2026-04-07 11:00:00'::timestamp),
        (32, '610c2b86-0f1d-4c34-a517-aea2b92ce27d'::uuid, '5655cc4d-1de7-4263-9950-dfa39500d821'::uuid, 200, '2026-04-08 11:00:00'::timestamp),
        (33, 'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8'::uuid, '45d2b312-2849-4aa4-9ccc-ff881966feb9'::uuid, 2700, '2026-04-01 12:00:00'::timestamp),
        (34, 'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8'::uuid, '3250126c-8341-4080-b964-63c5b0b18820'::uuid, 3500, '2026-04-02 12:00:00'::timestamp),
        (35, 'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8'::uuid, '19480479-4a40-40d4-b843-2ef53beaf24c'::uuid, 2900, '2026-04-03 12:00:00'::timestamp),
        (36, 'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8'::uuid, 'b77e943d-ac1b-4e01-b614-53c501344569'::uuid, 2000, '2026-04-04 12:00:00'::timestamp),
        (37, 'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8'::uuid, '9c1e8ddc-00f9-4393-90b8-5400e63c3b36'::uuid, 2600, '2026-04-05 12:00:00'::timestamp),
        (38, 'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8'::uuid, 'febe6123-a925-44cf-a109-aefc7cf61420'::uuid, 5200, '2026-04-06 12:00:00'::timestamp),
        (39, 'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8'::uuid, '74e22a79-6b77-413f-9053-f6f79566226b'::uuid, 1750, '2026-04-07 12:00:00'::timestamp),
        (40, 'bb3a68f9-f28e-478a-9ff1-e2b2d0e3c5c8'::uuid, '5655cc4d-1de7-4263-9950-dfa39500d821'::uuid, 2300, '2026-04-08 12:00:00'::timestamp)
)
INSERT INTO learning.study_times (
    id,
    student_id,
    class_id,
    lecture_id,
    duration_seconds,
    started_at,
    ended_at,
    metadata,
    created_at,
    updated_at
)
SELECT
    ('f0000000-0000-0000-0000-' || lpad(ordinal::text, 12, '0'))::uuid,
    student_id,
    'c0af4171-6a05-4a31-b368-bc40be5b9368'::uuid,
    lecture_id,
    duration_seconds,
    started_at,
    started_at + (duration_seconds || ' seconds')::interval,
    jsonb_build_object('source', 'flyway_mock', 'sessionOrdinal', ordinal),
    now(),
    now()
FROM mock_sessions
ON CONFLICT (id) DO UPDATE
SET
    duration_seconds = EXCLUDED.duration_seconds,
    started_at = EXCLUDED.started_at,
    ended_at = EXCLUDED.ended_at,
    metadata = EXCLUDED.metadata,
    updated_at = now();
