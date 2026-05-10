
INSERT INTO assessment_db.assessment_submissions (id,assessment_id,student_id,attempt_no,submit_time,score,actual_score,taken_time,status,created_at,updated_at) VALUES
                                                                                                                                                                     ('04b3dc1a-9caf-484f-9d14-2b61371fd226'::uuid,'f02b440a-220e-4189-8246-99b804c5475c'::uuid,'8f06fef7-9024-43f9-9bbc-cf89b9d063c1'::uuid,1,'2026-05-07 23:15:57.467972+07',0.000,0.000,59,'SUBMITTED','2026-05-07 23:14:58.389276+07','2026-05-07 23:15:57.483809+07'),
                                                                                                                                                                     ('11512e6e-075d-46ba-b614-300c880e13b9'::uuid,'777dace5-4b8b-4592-b2c6-87594e7ed92e'::uuid,'8f06fef7-9024-43f9-9bbc-cf89b9d063c1'::uuid,1,'2026-05-07 23:23:52.002614+07',10.000,10.000,21,'SUBMITTED','2026-05-07 23:23:31.087641+07','2026-05-07 23:23:52.013355+07'),
                                                                                                                                                                     ('8315342d-f553-4129-ad32-cd542e3320ce'::uuid,'66d8140a-b2a1-4801-bd2d-72688da1862f'::uuid,'8f06fef7-9024-43f9-9bbc-cf89b9d063c1'::uuid,1,'2026-05-07 23:25:23.902535+07',0.000,0.000,53,'SUBMITTED','2026-05-07 23:24:30.955279+07','2026-05-07 23:25:23.903442+07');



INSERT INTO assessment_db.question_submissions (id,assessment_submission_id,question_id,score,status,created_at,updated_at) VALUES
                                                                                                                                ('52c0c401-9eac-4cdc-8c1a-1d6c1b642459'::uuid,'04b3dc1a-9caf-484f-9d14-2b61371fd226'::uuid,'b97e3558-aa85-413e-9398-5a250990cbe3'::uuid,0.000,'INCORRECT','2026-05-07 23:15:56.276721+07','2026-05-07 23:15:57.485177+07'),
                                                                                                                                ('8dec5ae7-e62c-45cd-bd3e-7f10be10de00'::uuid,'11512e6e-075d-46ba-b614-300c880e13b9'::uuid,'ebbcb70c-1dc3-43f6-a65c-55f10a41b066'::uuid,1.000,'CORRECT','2026-05-07 23:23:33.422818+07','2026-05-07 23:23:52.018985+07'),
                                                                                                                                ('f1a44125-6824-4aba-84db-448378811453'::uuid,'11512e6e-075d-46ba-b614-300c880e13b9'::uuid,'bf980e18-a16f-4924-b37f-55043bdfa37e'::uuid,1.000,'CORRECT','2026-05-07 23:23:35.992556+07','2026-05-07 23:23:52.022007+07'),
                                                                                                                                ('2bf567b6-bcfb-4bbd-8d41-d22fd36d1214'::uuid,'11512e6e-075d-46ba-b614-300c880e13b9'::uuid,'03e278dd-b7ed-4f7b-bad8-d3a4b2dd2cc0'::uuid,1.000,'CORRECT','2026-05-07 23:23:35.997635+07','2026-05-07 23:23:52.023298+07'),
                                                                                                                                ('a10d9aea-4a85-4bab-88d5-4dc83a64d444'::uuid,'11512e6e-075d-46ba-b614-300c880e13b9'::uuid,'ae4f4561-43ea-4d4a-9451-b8e8771f3943'::uuid,1.000,'PARTIAL','2026-05-07 23:23:39.172614+07','2026-05-07 23:23:52.0247+07'),
                                                                                                                                ('b9d098e1-b6d0-47d5-9490-d67fa3d1c83a'::uuid,'11512e6e-075d-46ba-b614-300c880e13b9'::uuid,'afb7e53b-f9ec-4db8-a66d-03117921ea1e'::uuid,1.000,'CORRECT','2026-05-07 23:23:39.172207+07','2026-05-07 23:23:52.025719+07'),
                                                                                                                                ('a8f9f8fc-0b24-4e1a-bcdd-b02ea3cc1de9'::uuid,'11512e6e-075d-46ba-b614-300c880e13b9'::uuid,'66275e93-ee5f-4b38-83a8-a8b42d74b969'::uuid,1.000,'CORRECT','2026-05-07 23:23:44.655525+07','2026-05-07 23:23:52.02672+07'),
                                                                                                                                ('a06366ef-67e1-4559-ab45-cdb3ca0e9063'::uuid,'11512e6e-075d-46ba-b614-300c880e13b9'::uuid,'b84c9b5c-8485-4fbd-a1da-1c2824442804'::uuid,1.000,'CORRECT','2026-05-07 23:23:44.656517+07','2026-05-07 23:23:52.027766+07'),
                                                                                                                                ('55b04d9e-c7d9-4505-a971-7a2a85946c08'::uuid,'11512e6e-075d-46ba-b614-300c880e13b9'::uuid,'054d039c-373d-48e7-bc18-d6c9e1317e00'::uuid,1.000,'CORRECT','2026-05-07 23:23:47.071771+07','2026-05-07 23:23:52.028917+07'),
                                                                                                                                ('02eacc53-dfad-4193-b0c2-d7b8f4476a57'::uuid,'11512e6e-075d-46ba-b614-300c880e13b9'::uuid,'0d74ffb9-1043-4f70-b96d-8783b21e4fca'::uuid,2.000,'CORRECT','2026-05-07 23:23:50.165553+07','2026-05-07 23:23:52.030028+07');
INSERT INTO assessment_db.question_submissions (id,assessment_submission_id,question_id,score,status,created_at,updated_at) VALUES
    ('a6fa84cc-19f0-40da-ab88-e70d7ed519f0'::uuid,'8315342d-f553-4129-ad32-cd542e3320ce'::uuid,'18f91638-4113-45aa-9097-c50b9324007a'::uuid,0.000,'PENDING_REVIEW','2026-05-07 23:24:45.466906+07','2026-05-07 23:25:23.906071+07');


INSERT INTO assessment_db.essay_submissions (id,answer_text,num_pages,created_at,updated_at,submission_files) VALUES
                                                                                                                  ('52c0c401-9eac-4cdc-8c1a-1d6c1b642459'::uuid,NULL,NULL,'2026-05-07 23:15:56.230442+07','2026-05-07 23:15:56.230442+07','[{"url": "https://d14s8phoypchnb.cloudfront.net/assignments/submissions/c046af72-a10a-4ad1-94db-4913cb2b000a-dacn-lms-wecancode.pdf", "name": "DACN LMS wecancode.pdf", "size": 19074327, "type": "application/pdf"}]'),
                                                                                                                  ('a6fa84cc-19f0-40da-ab88-e70d7ed519f0'::uuid,'Encapsulation, Inheritance, Abstraction, Polymorphism',NULL,'2026-05-07 23:24:45.453171+07','2026-05-07 23:24:45.453171+07','[]');

INSERT INTO assessment_db.mcq_submissions (id,selected_count,created_at,updated_at) VALUES
                                                                                        ('8dec5ae7-e62c-45cd-bd3e-7f10be10de00'::uuid,1,'2026-05-07 23:23:33.406196+07','2026-05-07 23:23:33.406196+07'),
                                                                                        ('f1a44125-6824-4aba-84db-448378811453'::uuid,1,'2026-05-07 23:23:35.980723+07','2026-05-07 23:23:35.980723+07'),
                                                                                        ('2bf567b6-bcfb-4bbd-8d41-d22fd36d1214'::uuid,1,'2026-05-07 23:23:35.980748+07','2026-05-07 23:23:35.980748+07'),
                                                                                        ('a10d9aea-4a85-4bab-88d5-4dc83a64d444'::uuid,3,'2026-05-07 23:23:39.162619+07','2026-05-07 23:23:39.162619+07'),
                                                                                        ('b9d098e1-b6d0-47d5-9490-d67fa3d1c83a'::uuid,1,'2026-05-07 23:23:39.162595+07','2026-05-07 23:23:39.162595+07'),
                                                                                        ('a8f9f8fc-0b24-4e1a-bcdd-b02ea3cc1de9'::uuid,1,'2026-05-07 23:23:44.646074+07','2026-05-07 23:23:44.646074+07'),
                                                                                        ('a06366ef-67e1-4559-ab45-cdb3ca0e9063'::uuid,1,'2026-05-07 23:23:44.64607+07','2026-05-07 23:23:44.64607+07'),
                                                                                        ('55b04d9e-c7d9-4505-a971-7a2a85946c08'::uuid,1,'2026-05-07 23:23:47.059084+07','2026-05-07 23:23:47.059084+07'),
                                                                                        ('02eacc53-dfad-4193-b0c2-d7b8f4476a57'::uuid,3,'2026-05-07 23:23:50.154867+07','2026-05-07 23:23:50.154867+07');

INSERT INTO assessment_db.answer_options_mcq_submissions (id,mcq_submission_id,option_id,created_at,updated_at) VALUES
                                                                                                                    ('b5f10922-86c7-4a9a-9974-1512daaaf63c'::uuid,'8dec5ae7-e62c-45cd-bd3e-7f10be10de00'::uuid,'c383bbc4-30ec-4061-a5fe-bc628aa8c9af'::uuid,'2026-05-07 23:23:33.426793+07','2026-05-07 23:23:33.426797+07'),
                                                                                                                    ('94a843b6-9661-40a9-ab6c-b1caba939deb'::uuid,'f1a44125-6824-4aba-84db-448378811453'::uuid,'cda40aa1-026d-4dd5-862c-5038cba32aeb'::uuid,'2026-05-07 23:23:35.993889+07','2026-05-07 23:23:35.993891+07'),
                                                                                                                    ('16e86ded-3413-49e6-af33-8ba84533ca17'::uuid,'2bf567b6-bcfb-4bbd-8d41-d22fd36d1214'::uuid,'7465de6d-d8b4-4efd-ac2e-40651ed83017'::uuid,'2026-05-07 23:23:36.000457+07','2026-05-07 23:23:36.000459+07'),
                                                                                                                    ('a0f58a93-c8f9-4b24-80e0-b965b661b8c6'::uuid,'a10d9aea-4a85-4bab-88d5-4dc83a64d444'::uuid,'b616d577-9e4d-4f02-b496-d6d80d7d031d'::uuid,'2026-05-07 23:23:39.173648+07','2026-05-07 23:23:39.173649+07'),
                                                                                                                    ('2956bbb5-16e4-4eab-8817-0b401ef63aa9'::uuid,'b9d098e1-b6d0-47d5-9490-d67fa3d1c83a'::uuid,'f2f6e630-9190-49fe-ad0a-8e9719bac059'::uuid,'2026-05-07 23:23:39.173656+07','2026-05-07 23:23:39.173658+07'),
                                                                                                                    ('a260e996-68e4-48a5-9626-cc77183b5148'::uuid,'a10d9aea-4a85-4bab-88d5-4dc83a64d444'::uuid,'80f30519-bdf2-450e-b801-765b70018ab6'::uuid,'2026-05-07 23:23:39.174112+07','2026-05-07 23:23:39.174113+07'),
                                                                                                                    ('9820709d-6e00-40e1-a405-19ab3df67265'::uuid,'a10d9aea-4a85-4bab-88d5-4dc83a64d444'::uuid,'f3a9b947-c42f-4db6-8249-be517a5424d8'::uuid,'2026-05-07 23:23:39.174664+07','2026-05-07 23:23:39.174666+07'),
                                                                                                                    ('b0c4d870-c2c4-4524-960d-7180e2a5e0ca'::uuid,'a8f9f8fc-0b24-4e1a-bcdd-b02ea3cc1de9'::uuid,'03cc5f99-a064-4196-ba8d-d763a76b1305'::uuid,'2026-05-07 23:23:44.657121+07','2026-05-07 23:23:44.657123+07'),
                                                                                                                    ('eddd3c1f-1a9e-4525-9c49-841dd009dc83'::uuid,'a06366ef-67e1-4559-ab45-cdb3ca0e9063'::uuid,'d0e3be7c-9651-4dd7-baf5-3538d518ea9b'::uuid,'2026-05-07 23:23:44.65812+07','2026-05-07 23:23:44.658122+07'),
                                                                                                                    ('42956453-74a2-4124-9a34-f3dc30ade732'::uuid,'55b04d9e-c7d9-4505-a971-7a2a85946c08'::uuid,'c6a8eb69-e971-4351-a45f-9083bdb1a006'::uuid,'2026-05-07 23:23:47.074296+07','2026-05-07 23:23:47.074301+07');
INSERT INTO assessment_db.answer_options_mcq_submissions (id,mcq_submission_id,option_id,created_at,updated_at) VALUES
                                                                                                                    ('4e151c39-c36e-43cf-9e0c-475139368f4b'::uuid,'02eacc53-dfad-4193-b0c2-d7b8f4476a57'::uuid,'b703eaa1-a6e0-4191-bb22-a03775770b9e'::uuid,'2026-05-07 23:23:50.166841+07','2026-05-07 23:23:50.166843+07'),
                                                                                                                    ('ea4dcb6a-3d86-4378-b770-cfe7db606a46'::uuid,'02eacc53-dfad-4193-b0c2-d7b8f4476a57'::uuid,'4fcf0350-96eb-493d-9919-2f8b7b0b0d2c'::uuid,'2026-05-07 23:23:50.167437+07','2026-05-07 23:23:50.167439+07'),
                                                                                                                    ('31a7d74c-f1a9-49d3-88d5-bce3688039b8'::uuid,'02eacc53-dfad-4193-b0c2-d7b8f4476a57'::uuid,'ea914333-58d4-48aa-bf80-358e745d4380'::uuid,'2026-05-07 23:23:50.16815+07','2026-05-07 23:23:50.168152+07');

/* =========================
   ADDITIONAL SUBMISSIONS FOR 40 SEEDED STUDENTS
   ========================= */

WITH student_pool AS (
    SELECT
        s.user_id AS student_id,
        ROW_NUMBER() OVER (ORDER BY s.user_id) AS rn,
        CASE
            WHEN ROW_NUMBER() OVER (ORDER BY s.user_id) <= 8 THEN 'HIGH'
            WHEN ROW_NUMBER() OVER (ORDER BY s.user_id) <= 28 THEN 'MEDIUM'
            ELSE 'LOW'
            END AS performance_band
    FROM user_management.students s
    WHERE s.user_id BETWEEN '90000000-0000-4000-8000-000000000001'::uuid
              AND '90000000-0000-4000-8000-000000000040'::uuid
),
     submission_base AS (
         SELECT
             sp.*,
             ('2026-05-08 08:00:00+07'::timestamptz + (sp.rn || ' hours')::interval) AS base_time
         FROM student_pool sp
     )
INSERT INTO assessment_db.assessment_submissions
(id, assessment_id, student_id, attempt_no, submit_time, score, actual_score, taken_time, status, created_at, updated_at)
SELECT
    (substr(md5('asub-java-' || sb.student_id::text), 1, 8) || '-' ||
     substr(md5('asub-java-' || sb.student_id::text), 9, 4) || '-' ||
     substr(md5('asub-java-' || sb.student_id::text), 13, 4) || '-' ||
     substr(md5('asub-java-' || sb.student_id::text), 17, 4) || '-' ||
     substr(md5('asub-java-' || sb.student_id::text), 21, 12))::uuid AS id,
    '777dace5-4b8b-4592-b2c6-87594e7ed92e'::uuid AS assessment_id,
    sb.student_id,
    1 AS attempt_no,
    sb.base_time + interval '14 minutes' + ((sb.rn % 6) || ' minutes')::interval AS submit_time,
    CASE
        WHEN sb.performance_band = 'HIGH' THEN (8 + (sb.rn % 3))::numeric(8,3)
        WHEN sb.performance_band = 'MEDIUM' THEN (5 + (sb.rn % 4))::numeric(8,3)
        ELSE (2 + (sb.rn % 4))::numeric(8,3)
        END AS score,
    CASE
        WHEN sb.performance_band = 'HIGH' THEN (8 + (sb.rn % 3))::numeric(5,3)
        WHEN sb.performance_band = 'MEDIUM' THEN (5 + (sb.rn % 4))::numeric(5,3)
        ELSE (2 + (sb.rn % 4))::numeric(5,3)
        END AS actual_score,
    CASE
        WHEN sb.performance_band = 'HIGH' THEN 520 + (sb.rn * 7 % 180)
        WHEN sb.performance_band = 'MEDIUM' THEN 610 + (sb.rn * 11 % 220)
        ELSE 700 + (sb.rn * 13 % 140)
        END AS taken_time,
    'SUBMITTED' AS status,
    sb.base_time,
    sb.base_time + interval '14 minutes' + ((sb.rn % 6) || ' minutes')::interval
FROM submission_base sb
ON CONFLICT (assessment_id, student_id, attempt_no) DO NOTHING;

WITH student_pool AS (
    SELECT
        s.user_id AS student_id,
        ROW_NUMBER() OVER (ORDER BY s.user_id) AS rn,
        CASE
            WHEN ROW_NUMBER() OVER (ORDER BY s.user_id) <= 8 THEN 'HIGH'
            WHEN ROW_NUMBER() OVER (ORDER BY s.user_id) <= 28 THEN 'MEDIUM'
            ELSE 'LOW'
            END AS performance_band
    FROM user_management.students s
    WHERE s.user_id BETWEEN '90000000-0000-4000-8000-000000000001'::uuid
              AND '90000000-0000-4000-8000-000000000040'::uuid
),
     submission_base AS (
         SELECT
             sp.*,
             ('2026-05-08 08:00:00+07'::timestamptz + (sp.rn || ' hours')::interval) AS base_time
         FROM student_pool sp
     )
INSERT INTO assessment_db.assessment_submissions
(id, assessment_id, student_id, attempt_no, submit_time, score, actual_score, taken_time, status, created_at, updated_at)
SELECT
    (substr(md5('asub-quiz2-' || sb.student_id::text), 1, 8) || '-' ||
     substr(md5('asub-quiz2-' || sb.student_id::text), 9, 4) || '-' ||
     substr(md5('asub-quiz2-' || sb.student_id::text), 13, 4) || '-' ||
     substr(md5('asub-quiz2-' || sb.student_id::text), 17, 4) || '-' ||
     substr(md5('asub-quiz2-' || sb.student_id::text), 21, 12))::uuid AS id,
    '66d8140a-b2a1-4801-bd2d-72688da1862f'::uuid AS assessment_id,
    sb.student_id,
    1 AS attempt_no,
    sb.base_time + interval '1 day 18 minutes' AS submit_time,
    CASE
        WHEN sb.performance_band = 'HIGH' THEN (8 + (sb.rn % 3))::numeric(8,3)
        WHEN sb.performance_band = 'MEDIUM' THEN (5 + (sb.rn % 4))::numeric(8,3)
        ELSE (2 + (sb.rn % 3))::numeric(8,3)
        END AS score,
    CASE
        WHEN sb.performance_band = 'HIGH' THEN (8 + (sb.rn % 3))::numeric(5,3)
        WHEN sb.performance_band = 'MEDIUM' THEN (5 + (sb.rn % 4))::numeric(5,3)
        ELSE (2 + (sb.rn % 3))::numeric(5,3)
        END AS actual_score,
    1500 + (sb.rn * 17 % 900) AS taken_time,
    'SUBMITTED' AS status,
    sb.base_time + interval '1 day',
    sb.base_time + interval '1 day 18 minutes'
FROM submission_base sb
ON CONFLICT (assessment_id, student_id, attempt_no) DO NOTHING;

WITH student_pool AS (
    SELECT
        s.user_id AS student_id,
        ROW_NUMBER() OVER (ORDER BY s.user_id) AS rn,
        CASE
            WHEN ROW_NUMBER() OVER (ORDER BY s.user_id) <= 8 THEN 'HIGH'
            WHEN ROW_NUMBER() OVER (ORDER BY s.user_id) <= 28 THEN 'MEDIUM'
            ELSE 'LOW'
            END AS performance_band
    FROM user_management.students s
    WHERE s.user_id BETWEEN '90000000-0000-4000-8000-000000000001'::uuid
              AND '90000000-0000-4000-8000-000000000040'::uuid
),
     submission_base AS (
         SELECT
             sp.*,
             ('2026-05-08 08:00:00+07'::timestamptz + (sp.rn || ' hours')::interval) AS base_time
         FROM student_pool sp
     )
INSERT INTO assessment_db.assessment_submissions
(id, assessment_id, student_id, attempt_no, submit_time, score, actual_score, taken_time, status, created_at, updated_at)
SELECT
    (substr(md5('asub-asg1-' || sb.student_id::text), 1, 8) || '-' ||
     substr(md5('asub-asg1-' || sb.student_id::text), 9, 4) || '-' ||
     substr(md5('asub-asg1-' || sb.student_id::text), 13, 4) || '-' ||
     substr(md5('asub-asg1-' || sb.student_id::text), 17, 4) || '-' ||
     substr(md5('asub-asg1-' || sb.student_id::text), 21, 12))::uuid AS id,
    'f02b440a-220e-4189-8246-99b804c5475c'::uuid AS assessment_id,
    sb.student_id,
    1 AS attempt_no,
    sb.base_time + interval '2 days 30 minutes' AS submit_time,
    CASE
        WHEN sb.performance_band = 'HIGH' THEN (7 + (sb.rn % 4))::numeric(8,3)
        WHEN sb.performance_band = 'MEDIUM' THEN (5 + (sb.rn % 3))::numeric(8,3)
        ELSE (3 + (sb.rn % 2))::numeric(8,3)
        END AS score,
    CASE
        WHEN sb.performance_band = 'HIGH' THEN (7 + (sb.rn % 4))::numeric(5,3)
        WHEN sb.performance_band = 'MEDIUM' THEN (5 + (sb.rn % 3))::numeric(5,3)
        ELSE (3 + (sb.rn % 2))::numeric(5,3)
        END AS actual_score,
    1200 + (sb.rn * 19 % 1600) AS taken_time,
    'SUBMITTED' AS status,
    sb.base_time + interval '2 days',
    sb.base_time + interval '2 days 30 minutes'
FROM submission_base sb
ON CONFLICT (assessment_id, student_id, attempt_no) DO NOTHING;

WITH student_pool AS (
    SELECT
        s.user_id AS student_id,
        ROW_NUMBER() OVER (ORDER BY s.user_id) AS rn,
        CASE
            WHEN ROW_NUMBER() OVER (ORDER BY s.user_id) <= 8 THEN 'HIGH'
            WHEN ROW_NUMBER() OVER (ORDER BY s.user_id) <= 28 THEN 'MEDIUM'
            ELSE 'LOW'
            END AS performance_band
    FROM user_management.students s
    WHERE s.user_id BETWEEN '90000000-0000-4000-8000-000000000001'::uuid
              AND '90000000-0000-4000-8000-000000000040'::uuid
),
     java_questions AS (
         SELECT * FROM (VALUES
                            (1, 'ebbcb70c-1dc3-43f6-a65c-55f10a41b066'::uuid, 'c383bbc4-30ec-4061-a5fe-bc628aa8c9af'::uuid, 'fe19624c-5555-43d9-ad04-5c96ea7d24d5'::uuid),
                            (2, 'bf980e18-a16f-4924-b37f-55043bdfa37e'::uuid, 'cda40aa1-026d-4dd5-862c-5038cba32aeb'::uuid, 'a4507fe0-abe0-4041-a03f-f14e4887e55c'::uuid),
                            (3, '03e278dd-b7ed-4f7b-bad8-d3a4b2dd2cc0'::uuid, '7465de6d-d8b4-4efd-ac2e-40651ed83017'::uuid, '3925a216-47ee-4d33-ad76-f7b0e7755dc0'::uuid),
                            (4, 'ae4f4561-43ea-4d4a-9451-b8e8771f3943'::uuid, '80f30519-bdf2-450e-b801-765b70018ab6'::uuid, '97290ea9-3a52-4ae5-97b2-a5bee09800cb'::uuid),
                            (5, 'afb7e53b-f9ec-4db8-a66d-03117921ea1e'::uuid, 'f2f6e630-9190-49fe-ad0a-8e9719bac059'::uuid, '5f84c367-ffe0-43b3-a4d3-79cdc1631050'::uuid),
                            (6, '66275e93-ee5f-4b38-83a8-a8b42d74b969'::uuid, '03cc5f99-a064-4196-ba8d-d763a76b1305'::uuid, '42ee3453-717f-4a0e-ad20-c11853aadf0e'::uuid),
                            (7, 'b84c9b5c-8485-4fbd-a1da-1c2824442804'::uuid, 'd0e3be7c-9651-4dd7-baf5-3538d518ea9b'::uuid, 'da5f451a-ab44-4b04-bf0f-2297376eadbf'::uuid),
                            (8, '054d039c-373d-48e7-bc18-d6c9e1317e00'::uuid, 'c6a8eb69-e971-4351-a45f-9083bdb1a006'::uuid, '1131dbfa-0709-41ab-a043-9761958c466a'::uuid),
                            (9, '0d74ffb9-1043-4f70-b96d-8783b21e4fca'::uuid, 'b703eaa1-a6e0-4191-bb22-a03775770b9e'::uuid, '4c6acbf4-b8d6-40fa-a7a9-748d5d594453'::uuid)
                       ) AS t(q_order, question_id, correct_option_id, wrong_option_id)
     ),
     per_student_target AS (
         SELECT
             sp.student_id,
             sp.rn,
             CASE
                 WHEN sp.performance_band = 'HIGH' THEN 8 + (sp.rn % 2)
                 WHEN sp.performance_band = 'MEDIUM' THEN 5 + (sp.rn % 3)
                 ELSE 2 + (sp.rn % 3)
                 END AS target_correct
         FROM student_pool sp
     ),
     java_rows AS (
         SELECT
             pst.student_id,
             pst.rn,
             jq.q_order,
             jq.question_id,
             jq.correct_option_id,
             jq.wrong_option_id,
             (jq.q_order <= pst.target_correct) AS is_correct
         FROM per_student_target pst
                  CROSS JOIN java_questions jq
     ),
     base_submission AS (
         SELECT
             jr.*,
             (substr(md5('asub-java-' || jr.student_id::text), 1, 8) || '-' ||
              substr(md5('asub-java-' || jr.student_id::text), 9, 4) || '-' ||
              substr(md5('asub-java-' || jr.student_id::text), 13, 4) || '-' ||
              substr(md5('asub-java-' || jr.student_id::text), 17, 4) || '-' ||
              substr(md5('asub-java-' || jr.student_id::text), 21, 12))::uuid AS assessment_submission_id,
             ('2026-05-08 08:00:00+07'::timestamptz + (jr.rn || ' hours')::interval + interval '2 minutes' * jr.q_order) AS q_time
         FROM java_rows jr
     )
INSERT INTO assessment_db.question_submissions
(id, assessment_submission_id, question_id, score, status, created_at, updated_at)
SELECT
    (substr(md5('qsub-java-' || bs.student_id::text || '-' || bs.question_id::text), 1, 8) || '-' ||
     substr(md5('qsub-java-' || bs.student_id::text || '-' || bs.question_id::text), 9, 4) || '-' ||
     substr(md5('qsub-java-' || bs.student_id::text || '-' || bs.question_id::text), 13, 4) || '-' ||
     substr(md5('qsub-java-' || bs.student_id::text || '-' || bs.question_id::text), 17, 4) || '-' ||
     substr(md5('qsub-java-' || bs.student_id::text || '-' || bs.question_id::text), 21, 12))::uuid AS id,
    bs.assessment_submission_id,
    bs.question_id,
    CASE WHEN bs.is_correct THEN (CASE WHEN bs.q_order = 9 THEN 2 ELSE 1 END)::numeric(5,3) ELSE 0::numeric(5,3) END AS score,
    CASE
        WHEN bs.is_correct = FALSE THEN 'INCORRECT'
        WHEN bs.question_id IN ('ae4f4561-43ea-4d4a-9451-b8e8771f3943'::uuid, '0d74ffb9-1043-4f70-b96d-8783b21e4fca'::uuid)
            AND (bs.rn % 4 = 0) THEN 'PARTIAL'
        ELSE 'CORRECT'
        END AS status,
    bs.q_time,
    bs.q_time + interval '30 seconds'
FROM base_submission bs
ON CONFLICT (assessment_submission_id, question_id) DO NOTHING;

WITH student_pool AS (
    SELECT
        s.user_id AS student_id,
        ROW_NUMBER() OVER (ORDER BY s.user_id) AS rn
    FROM user_management.students s
    WHERE s.user_id BETWEEN '90000000-0000-4000-8000-000000000001'::uuid
              AND '90000000-0000-4000-8000-000000000040'::uuid
),
     java_questions AS (
         SELECT * FROM (VALUES
                            (1, 'ebbcb70c-1dc3-43f6-a65c-55f10a41b066'::uuid, 'c383bbc4-30ec-4061-a5fe-bc628aa8c9af'::uuid, 'fe19624c-5555-43d9-ad04-5c96ea7d24d5'::uuid),
                            (2, 'bf980e18-a16f-4924-b37f-55043bdfa37e'::uuid, 'cda40aa1-026d-4dd5-862c-5038cba32aeb'::uuid, 'a4507fe0-abe0-4041-a03f-f14e4887e55c'::uuid),
                            (3, '03e278dd-b7ed-4f7b-bad8-d3a4b2dd2cc0'::uuid, '7465de6d-d8b4-4efd-ac2e-40651ed83017'::uuid, '3925a216-47ee-4d33-ad76-f7b0e7755dc0'::uuid),
                            (4, 'ae4f4561-43ea-4d4a-9451-b8e8771f3943'::uuid, '80f30519-bdf2-450e-b801-765b70018ab6'::uuid, '97290ea9-3a52-4ae5-97b2-a5bee09800cb'::uuid),
                            (5, 'afb7e53b-f9ec-4db8-a66d-03117921ea1e'::uuid, 'f2f6e630-9190-49fe-ad0a-8e9719bac059'::uuid, '5f84c367-ffe0-43b3-a4d3-79cdc1631050'::uuid),
                            (6, '66275e93-ee5f-4b38-83a8-a8b42d74b969'::uuid, '03cc5f99-a064-4196-ba8d-d763a76b1305'::uuid, '42ee3453-717f-4a0e-ad20-c11853aadf0e'::uuid),
                            (7, 'b84c9b5c-8485-4fbd-a1da-1c2824442804'::uuid, 'd0e3be7c-9651-4dd7-baf5-3538d518ea9b'::uuid, 'da5f451a-ab44-4b04-bf0f-2297376eadbf'::uuid),
                            (8, '054d039c-373d-48e7-bc18-d6c9e1317e00'::uuid, 'c6a8eb69-e971-4351-a45f-9083bdb1a006'::uuid, '1131dbfa-0709-41ab-a043-9761958c466a'::uuid),
                            (9, '0d74ffb9-1043-4f70-b96d-8783b21e4fca'::uuid, 'b703eaa1-a6e0-4191-bb22-a03775770b9e'::uuid, '4c6acbf4-b8d6-40fa-a7a9-748d5d594453'::uuid)
                       ) AS t(q_order, question_id, correct_option_id, wrong_option_id)
     ),
     mcq_rows AS (
         SELECT
             sp.student_id,
             jq.q_order,
             jq.question_id,
             CASE
                 WHEN sp.rn <= 8 THEN (jq.q_order <= 8 + (sp.rn % 2))
                 WHEN sp.rn <= 28 THEN (jq.q_order <= 5 + (sp.rn % 3))
                 ELSE (jq.q_order <= 2 + (sp.rn % 3))
                 END AS is_correct,
             jq.correct_option_id,
             jq.wrong_option_id,
             (substr(md5('qsub-java-' || sp.student_id::text || '-' || jq.question_id::text), 1, 8) || '-' ||
              substr(md5('qsub-java-' || sp.student_id::text || '-' || jq.question_id::text), 9, 4) || '-' ||
              substr(md5('qsub-java-' || sp.student_id::text || '-' || jq.question_id::text), 13, 4) || '-' ||
              substr(md5('qsub-java-' || sp.student_id::text || '-' || jq.question_id::text), 17, 4) || '-' ||
              substr(md5('qsub-java-' || sp.student_id::text || '-' || jq.question_id::text), 21, 12))::uuid AS qsub_id,
             ('2026-05-08 08:00:00+07'::timestamptz + (sp.rn || ' hours')::interval + interval '2 minutes' * jq.q_order) AS q_time
         FROM student_pool sp
                  CROSS JOIN java_questions jq
     )
INSERT INTO assessment_db.mcq_submissions (id, selected_count, created_at, updated_at)
SELECT
    mr.qsub_id AS id,
    CASE WHEN mr.question_id IN ('ae4f4561-43ea-4d4a-9451-b8e8771f3943'::uuid, '0d74ffb9-1043-4f70-b96d-8783b21e4fca'::uuid)
             THEN CASE WHEN mr.is_correct THEN 2 ELSE 1 END
         ELSE 1
        END AS selected_count,
    mr.q_time,
    mr.q_time + interval '10 seconds'
FROM mcq_rows mr
ON CONFLICT (id) DO NOTHING;

WITH student_pool AS (
    SELECT
        s.user_id AS student_id,
        ROW_NUMBER() OVER (ORDER BY s.user_id) AS rn
    FROM user_management.students s
    WHERE s.user_id BETWEEN '90000000-0000-4000-8000-000000000001'::uuid
              AND '90000000-0000-4000-8000-000000000040'::uuid
),
     mcq_choice_rows AS (
         SELECT
             sp.student_id,
             jq.question_id,
             CASE
                 WHEN sp.rn <= 8 THEN (jq.q_order <= 8 + (sp.rn % 2))
                 WHEN sp.rn <= 28 THEN (jq.q_order <= 5 + (sp.rn % 3))
                 ELSE (jq.q_order <= 2 + (sp.rn % 3))
                 END AS is_correct,
             jq.correct_option_id,
             jq.wrong_option_id,
             (substr(md5('qsub-java-' || sp.student_id::text || '-' || jq.question_id::text), 1, 8) || '-' ||
              substr(md5('qsub-java-' || sp.student_id::text || '-' || jq.question_id::text), 9, 4) || '-' ||
              substr(md5('qsub-java-' || sp.student_id::text || '-' || jq.question_id::text), 13, 4) || '-' ||
              substr(md5('qsub-java-' || sp.student_id::text || '-' || jq.question_id::text), 17, 4) || '-' ||
              substr(md5('qsub-java-' || sp.student_id::text || '-' || jq.question_id::text), 21, 12))::uuid AS mcq_submission_id,
             ('2026-05-08 08:00:00+07'::timestamptz + (sp.rn || ' hours')::interval + interval '2 minutes' * jq.q_order + interval '15 seconds') AS t_choice
         FROM student_pool sp
                  JOIN (
             VALUES
                 (1, 'ebbcb70c-1dc3-43f6-a65c-55f10a41b066'::uuid, 'c383bbc4-30ec-4061-a5fe-bc628aa8c9af'::uuid, 'fe19624c-5555-43d9-ad04-5c96ea7d24d5'::uuid),
                 (2, 'bf980e18-a16f-4924-b37f-55043bdfa37e'::uuid, 'cda40aa1-026d-4dd5-862c-5038cba32aeb'::uuid, 'a4507fe0-abe0-4041-a03f-f14e4887e55c'::uuid),
                 (3, '03e278dd-b7ed-4f7b-bad8-d3a4b2dd2cc0'::uuid, '7465de6d-d8b4-4efd-ac2e-40651ed83017'::uuid, '3925a216-47ee-4d33-ad76-f7b0e7755dc0'::uuid),
                 (4, 'ae4f4561-43ea-4d4a-9451-b8e8771f3943'::uuid, '80f30519-bdf2-450e-b801-765b70018ab6'::uuid, '97290ea9-3a52-4ae5-97b2-a5bee09800cb'::uuid),
                 (5, 'afb7e53b-f9ec-4db8-a66d-03117921ea1e'::uuid, 'f2f6e630-9190-49fe-ad0a-8e9719bac059'::uuid, '5f84c367-ffe0-43b3-a4d3-79cdc1631050'::uuid),
                 (6, '66275e93-ee5f-4b38-83a8-a8b42d74b969'::uuid, '03cc5f99-a064-4196-ba8d-d763a76b1305'::uuid, '42ee3453-717f-4a0e-ad20-c11853aadf0e'::uuid),
                 (7, 'b84c9b5c-8485-4fbd-a1da-1c2824442804'::uuid, 'd0e3be7c-9651-4dd7-baf5-3538d518ea9b'::uuid, 'da5f451a-ab44-4b04-bf0f-2297376eadbf'::uuid),
                 (8, '054d039c-373d-48e7-bc18-d6c9e1317e00'::uuid, 'c6a8eb69-e971-4351-a45f-9083bdb1a006'::uuid, '1131dbfa-0709-41ab-a043-9761958c466a'::uuid),
                 (9, '0d74ffb9-1043-4f70-b96d-8783b21e4fca'::uuid, 'b703eaa1-a6e0-4191-bb22-a03775770b9e'::uuid, '4c6acbf4-b8d6-40fa-a7a9-748d5d594453'::uuid)
         ) AS jq(q_order, question_id, correct_option_id, wrong_option_id) ON TRUE
     )
INSERT INTO assessment_db.answer_options_mcq_submissions
(id, mcq_submission_id, option_id, created_at, updated_at)
SELECT
    (substr(md5('aoms-' || mcr.mcq_submission_id::text || '-' || mcr.option_id::text), 1, 8) || '-' ||
     substr(md5('aoms-' || mcr.mcq_submission_id::text || '-' || mcr.option_id::text), 9, 4) || '-' ||
     substr(md5('aoms-' || mcr.mcq_submission_id::text || '-' || mcr.option_id::text), 13, 4) || '-' ||
     substr(md5('aoms-' || mcr.mcq_submission_id::text || '-' || mcr.option_id::text), 17, 4) || '-' ||
     substr(md5('aoms-' || mcr.mcq_submission_id::text || '-' || mcr.option_id::text), 21, 12))::uuid AS id,
    mcr.mcq_submission_id,
    mcr.option_id,
    mcr.t_choice,
    mcr.t_choice + interval '2 seconds'
FROM (
         SELECT
             m.student_id,
             m.mcq_submission_id,
             CASE WHEN m.is_correct THEN m.correct_option_id ELSE m.wrong_option_id END AS option_id,
             m.t_choice
         FROM mcq_choice_rows m
     ) AS mcr
ON CONFLICT (mcq_submission_id, option_id) DO NOTHING;

WITH student_pool AS (
    SELECT
        s.user_id AS student_id,
        ROW_NUMBER() OVER (ORDER BY s.user_id) AS rn
    FROM user_management.students s
    WHERE s.user_id BETWEEN '90000000-0000-4000-8000-000000000001'::uuid
              AND '90000000-0000-4000-8000-000000000040'::uuid
),
     essay_rows AS (
         SELECT
             sp.student_id,
             sp.rn,
             (substr(md5('asub-asg1-' || sp.student_id::text), 1, 8) || '-' ||
              substr(md5('asub-asg1-' || sp.student_id::text), 9, 4) || '-' ||
              substr(md5('asub-asg1-' || sp.student_id::text), 13, 4) || '-' ||
              substr(md5('asub-asg1-' || sp.student_id::text), 17, 4) || '-' ||
              substr(md5('asub-asg1-' || sp.student_id::text), 21, 12))::uuid AS asg1_submission_id,
             (substr(md5('asub-quiz2-' || sp.student_id::text), 1, 8) || '-' ||
              substr(md5('asub-quiz2-' || sp.student_id::text), 9, 4) || '-' ||
              substr(md5('asub-quiz2-' || sp.student_id::text), 13, 4) || '-' ||
              substr(md5('asub-quiz2-' || sp.student_id::text), 17, 4) || '-' ||
              substr(md5('asub-quiz2-' || sp.student_id::text), 21, 12))::uuid AS quiz2_submission_id,
             ('2026-05-10 09:00:00+07'::timestamptz + (sp.rn || ' hours')::interval) AS t0
         FROM student_pool sp
     )
INSERT INTO assessment_db.question_submissions
(id, assessment_submission_id, question_id, score, status, created_at, updated_at)
SELECT
    qs.id,
    qs.assessment_submission_id,
    qs.question_id,
    qs.score,
    qs.status,
    qs.created_at,
    qs.updated_at
FROM (
         SELECT
             (substr(md5('qsub-asg1-' || er.student_id::text), 1, 8) || '-' ||
              substr(md5('qsub-asg1-' || er.student_id::text), 9, 4) || '-' ||
              substr(md5('qsub-asg1-' || er.student_id::text), 13, 4) || '-' ||
              substr(md5('qsub-asg1-' || er.student_id::text), 17, 4) || '-' ||
              substr(md5('qsub-asg1-' || er.student_id::text), 21, 12))::uuid AS id,
             er.asg1_submission_id AS assessment_submission_id,
             'b97e3558-aa85-413e-9398-5a250990cbe3'::uuid AS question_id,
             CASE
                 WHEN er.rn <= 8 THEN (7 + (er.rn % 4))::numeric(5,3)
                 WHEN er.rn <= 28 THEN (5 + (er.rn % 3))::numeric(5,3)
                 ELSE (3 + (er.rn % 2))::numeric(5,3)
                 END AS score,
             'PENDING_REVIEW' AS status,
             er.t0 AS created_at,
             er.t0 + interval '5 minutes' AS updated_at
         FROM essay_rows er
         UNION ALL
         SELECT
             (substr(md5('qsub-quiz2-' || er.student_id::text), 1, 8) || '-' ||
              substr(md5('qsub-quiz2-' || er.student_id::text), 9, 4) || '-' ||
              substr(md5('qsub-quiz2-' || er.student_id::text), 13, 4) || '-' ||
              substr(md5('qsub-quiz2-' || er.student_id::text), 17, 4) || '-' ||
              substr(md5('qsub-quiz2-' || er.student_id::text), 21, 12))::uuid AS id,
             er.quiz2_submission_id AS assessment_submission_id,
             '18f91638-4113-45aa-9097-c50b9324007a'::uuid AS question_id,
             CASE
                 WHEN er.rn <= 8 THEN (8 + (er.rn % 3))::numeric(5,3)
                 WHEN er.rn <= 28 THEN (5 + (er.rn % 4))::numeric(5,3)
                 ELSE (2 + (er.rn % 3))::numeric(5,3)
                 END AS score,
             'PENDING_REVIEW' AS status,
             er.t0 + interval '1 day' AS created_at,
             er.t0 + interval '1 day 8 minutes' AS updated_at
         FROM essay_rows er
     ) qs
ON CONFLICT (assessment_submission_id, question_id) DO NOTHING;

WITH student_pool AS (
    SELECT
        s.user_id AS student_id,
        ROW_NUMBER() OVER (ORDER BY s.user_id) AS rn
    FROM user_management.students s
    WHERE s.user_id BETWEEN '90000000-0000-4000-8000-000000000001'::uuid
              AND '90000000-0000-4000-8000-000000000040'::uuid
)
INSERT INTO assessment_db.essay_submissions
(id, answer_text, num_pages, created_at, updated_at, submission_files)
SELECT
    (substr(md5('qsub-asg1-' || sp.student_id::text), 1, 8) || '-' ||
     substr(md5('qsub-asg1-' || sp.student_id::text), 9, 4) || '-' ||
     substr(md5('qsub-asg1-' || sp.student_id::text), 13, 4) || '-' ||
     substr(md5('qsub-asg1-' || sp.student_id::text), 17, 4) || '-' ||
     substr(md5('qsub-asg1-' || sp.student_id::text), 21, 12))::uuid AS id,
    CASE
        WHEN sp.rn <= 8 THEN 'Phan tich OOP va ap dung Strategy/Factory pattern vao bai toan quan ly hoc tap.'
        WHEN sp.rn <= 28 THEN 'Trinh bay 4 tinh chat OOP, kem vi du trong Java va han che khi mo rong he thong.'
        ELSE 'Tom tat cac khai niem OOP co ban va vi du ngan gon.'
        END AS answer_text,
    CASE
        WHEN sp.rn <= 8 THEN 6 + (sp.rn % 4)
        WHEN sp.rn <= 28 THEN 4 + (sp.rn % 3)
        ELSE 2 + (sp.rn % 2)
        END AS num_pages,
    '2026-05-10 09:05:00+07'::timestamptz + (sp.rn || ' hours')::interval,
    '2026-05-10 09:11:00+07'::timestamptz + (sp.rn || ' hours')::interval,
    CASE
        WHEN sp.rn % 4 = 0 THEN
            '[{"url":"https://d14s8phoypchnb.cloudfront.net/assignments/submissions/report-java-design.pdf","name":"report-java-design.pdf","size":1450021,"type":"application/pdf"},{"url":"https://d14s8phoypchnb.cloudfront.net/assignments/submissions/source-code.zip","name":"source-code.zip","size":2245998,"type":"application/zip"}]'::jsonb
        ELSE
            '[{"url":"https://d14s8phoypchnb.cloudfront.net/assignments/submissions/report-java-design.pdf","name":"report-java-design.pdf","size":1450021,"type":"application/pdf"}]'::jsonb
        END AS submission_files
FROM student_pool sp
ON CONFLICT (id) DO NOTHING;

WITH student_pool AS (
    SELECT
        s.user_id AS student_id,
        ROW_NUMBER() OVER (ORDER BY s.user_id) AS rn
    FROM user_management.students s
    WHERE s.user_id BETWEEN '90000000-0000-4000-8000-000000000001'::uuid
              AND '90000000-0000-4000-8000-000000000040'::uuid
)
INSERT INTO assessment_db.essay_submissions
(id, answer_text, num_pages, created_at, updated_at, submission_files)
SELECT
    (substr(md5('qsub-quiz2-' || sp.student_id::text), 1, 8) || '-' ||
     substr(md5('qsub-quiz2-' || sp.student_id::text), 9, 4) || '-' ||
     substr(md5('qsub-quiz2-' || sp.student_id::text), 13, 4) || '-' ||
     substr(md5('qsub-quiz2-' || sp.student_id::text), 17, 4) || '-' ||
     substr(md5('qsub-quiz2-' || sp.student_id::text), 21, 12))::uuid AS id,
    'Encapsulation, Inheritance, Abstraction, Polymorphism. Vi du: he thong quan ly mon hoc su dung interface va abstract class.',
    NULL,
    '2026-05-11 09:00:00+07'::timestamptz + (sp.rn || ' hours')::interval,
    '2026-05-11 09:06:00+07'::timestamptz + (sp.rn || ' hours')::interval,
    '[]'::jsonb
FROM student_pool sp
ON CONFLICT (id) DO NOTHING;