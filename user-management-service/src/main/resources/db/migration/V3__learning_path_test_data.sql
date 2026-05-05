-- test001
INSERT INTO user_management.users (id, email, avatar_url, first_name, last_name, phone, last_login, specialization_id, role_id, created_at, updated_at)
VALUES ('d036adda-78a1-4a53-ba51-1af83907c5f8', 'test.001@hcmut.edu.vn', NULL, 'First Year', '001', '0123456789', NULL, '1ba8a25e-70cd-430d-8dbe-92d12f178613', 'e5909d1e-492d-4419-9766-ce69821e28ec', now(), now())
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_management.students (user_id, student_code, intake_year_id, department_id)
VALUES ('d036adda-78a1-4a53-ba51-1af83907c5f8', '2410001', '62a1d0f9-03aa-4419-bb22-6337dfc7ab89', 'f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371')
ON CONFLICT (student_code) DO NOTHING;

-- test002
INSERT INTO user_management.users (id, email, avatar_url, first_name, last_name, phone, last_login, specialization_id, role_id, created_at, updated_at)
VALUES ('9b2c7d39-bc76-41ec-8586-d64cbc18775b', 'test.002@hcmut.edu.vn', NULL, 'FIRSTYEAR', 'STUDENT', '0900000002', NULL, '1ba8a25e-70cd-430d-8dbe-92d12f178613', 'e5909d1e-492d-4419-9766-ce69821e28ec', now(), now())
ON CONFLICT (email) DO NOTHING;
INSERT INTO user_management.students (user_id, student_code, intake_year_id, department_id)
VALUES ('9b2c7d39-bc76-41ec-8586-d64cbc18775b', '2210002', '9ab1203e-27f6-4aa9-8f91-3a1980b03ba8', 'f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371')
ON CONFLICT (student_code) DO NOTHING;

--test010
INSERT INTO user_management.users (id, email, avatar_url, first_name, last_name, phone, last_login, specialization_id, role_id, created_at, updated_at)
VALUES ('83ceb6f7-77e5-4663-947d-cc3e5f98fb59', 'test.010@hcmut.edu.vn', NULL, 'FAILED', 'SUBJECTS', '0900000010', NULL, '1ba8a25e-70cd-430d-8dbe-92d12f178613', 'e5909d1e-492d-4419-9766-ce69821e28ec', now(), now())
ON CONFLICT (email) DO NOTHING;
INSERT INTO user_management.students (user_id, student_code, intake_year_id, department_id)
VALUES ('83ceb6f7-77e5-4663-947d-cc3e5f98fb59', '2110010', '9ab1203e-27f6-4aa9-8f91-3a1980b03ba8', 'f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371')
ON CONFLICT (student_code) DO NOTHING;

--test003
INSERT INTO user_management.users (id, email, avatar_url, first_name, last_name, phone, last_login, specialization_id, role_id, created_at, updated_at)
VALUES ('37f5a1d9-83ab-45c4-85de-0205d1ca1a0b', 'test.003@hcmut.edu.vn', NULL, 'SECONDYEAR', 'STUDENT', '0900000003', NULL, '1ba8a25e-70cd-430d-8dbe-92d12f178613', 'e5909d1e-492d-4419-9766-ce69821e28ec', now(), now())
ON CONFLICT (email) DO NOTHING;
INSERT INTO user_management.students (user_id, student_code, intake_year_id, department_id)
VALUES ('37f5a1d9-83ab-45c4-85de-0205d1ca1a0b', '2210003', '9ab1203e-27f6-4aa9-8f91-3a1980b03ba8', 'f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371')
ON CONFLICT (student_code) DO NOTHING;

--test004
INSERT INTO user_management.users (id, email, avatar_url, first_name, last_name, phone, last_login, specialization_id, role_id, created_at, updated_at)
VALUES ('94bc2015-b87f-45ac-8b95-a7b45f2faa47', 'test.004@hcmut.edu.vn', NULL, 'HIGH', 'ACHIEVER', '0900000004', NULL, '1ba8a25e-70cd-430d-8dbe-92d12f178613', 'e5909d1e-492d-4419-9766-ce69821e28ec', now(), now())
ON CONFLICT (email) DO NOTHING;
INSERT INTO user_management.students (user_id, student_code, intake_year_id, department_id)
VALUES ('94bc2015-b87f-45ac-8b95-a7b45f2faa47', '2210004', '9ab1203e-27f6-4aa9-8f91-3a1980b03ba8', 'f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371')
ON CONFLICT (student_code) DO NOTHING;

--test005
INSERT INTO user_management.users (id, email, avatar_url, first_name, last_name, phone, last_login, specialization_id, role_id, created_at, updated_at)
VALUES ('4d3f8e19-d6f8-475f-b534-ffec14382540', 'test.005@hcmut.edu.vn', NULL, 'STRUGGLING', 'STUDENT', '0900000005', NULL, '1ba8a25e-70cd-430d-8dbe-92d12f178613', 'e5909d1e-492d-4419-9766-ce69821e28ec', now(), now())
ON CONFLICT (email) DO NOTHING;
INSERT INTO user_management.students (user_id, student_code, intake_year_id, department_id)
VALUES ('4d3f8e19-d6f8-475f-b534-ffec14382540', '2210005', '9ab1203e-27f6-4aa9-8f91-3a1980b03ba8', 'f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371')
ON CONFLICT (student_code) DO NOTHING;

--test008
INSERT INTO user_management.users (id, email, avatar_url, first_name, last_name, phone, last_login, specialization_id, role_id, created_at, updated_at)
VALUES ('bfde9a02-d813-4484-94a4-4ce131cc3609', 'test.008@hcmut.edu.vn', NULL, 'GENED', 'STRONG', '0900000008', NULL, '1ba8a25e-70cd-430d-8dbe-92d12f178613', 'e5909d1e-492d-4419-9766-ce69821e28ec', now(), now())
ON CONFLICT (email) DO NOTHING;
INSERT INTO user_management.students (user_id, student_code, intake_year_id, department_id)
VALUES ('bfde9a02-d813-4484-94a4-4ce131cc3609', '2110008', '9ab1203e-27f6-4aa9-8f91-3a1980b03ba8', 'f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371')
ON CONFLICT (student_code) DO NOTHING;

--test011
INSERT INTO user_management.users (id, email, avatar_url, first_name, last_name, phone, last_login, specialization_id, role_id, created_at, updated_at)
VALUES ('b54417e1-37a3-47dc-b839-2d5a9332de96', 'test.011@hcmut.edu.vn', NULL, 'GAP', 'YEAR', '0900000011', NULL, '1ba8a25e-70cd-430d-8dbe-92d12f178613', 'e5909d1e-492d-4419-9766-ce69821e28ec', now(), now())
ON CONFLICT (email) DO NOTHING;
INSERT INTO user_management.students (user_id, student_code, intake_year_id, department_id)
VALUES ('b54417e1-37a3-47dc-b839-2d5a9332de96', '2110011', '9ab1203e-27f6-4aa9-8f91-3a1980b03ba8', 'f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371')
ON CONFLICT (student_code) DO NOTHING;