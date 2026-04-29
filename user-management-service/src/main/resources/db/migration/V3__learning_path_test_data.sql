INSERT INTO user_management.users (id, email, avatar_url, first_name, last_name, phone, last_login, specialization_id, role_id, created_at, updated_at)
VALUES ('d036adda-78a1-4a53-ba51-1af83907c5f8', 'test.001@hcmut.edu.vn', NULL, 'First Year', '001', '0123456789', NULL, '1ba8a25e-70cd-430d-8dbe-92d12f178613', 'e5909d1e-492d-4419-9766-ce69821e28ec', now(), now())
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_management.students (user_id, student_code, intake_year_id, department_id)
VALUES ('d036adda-78a1-4a53-ba51-1af83907c5f8', '2410001', '62a1d0f9-03aa-4419-bb22-6337dfc7ab89', 'f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371')
ON CONFLICT (student_code) DO NOTHING;