-- ============================================
-- Initial Data Migration for User Management Service
-- Version: V2
-- Description: Seed data from init_data.sql
-- ============================================

--
-- Data for Name: roles; Type: TABLE DATA; Schema: user_management; Owner: lms_user
--

INSERT INTO user_management.roles (id, is_active, name, description, created_at, updated_at) VALUES ('e5909d1e-492d-4419-9766-ce69821e28ec', true, 'STUDENT', 'Student role', '2025-12-05 16:23:15.910801+00', '2025-12-05 16:23:15.910838+00')
ON CONFLICT (name) DO NOTHING;
INSERT INTO user_management.roles (id, is_active, name, description, created_at, updated_at) VALUES ('3102475a-2855-4408-9144-c2b39991cdc2', true, 'TEACHER', 'teacher role', '2025-12-05 16:23:26.899115+00', '2025-12-05 16:23:26.899139+00')
ON CONFLICT (name) DO NOTHING;
INSERT INTO user_management.roles (id, is_active, name, description, created_at, updated_at) VALUES ('6d39f7aa-2680-437b-b0bc-f35bb105b941', true, 'ADMIN', 'admin role', '2025-12-05 16:23:36.206896+00', '2025-12-05 16:23:36.206922+00')
ON CONFLICT (name) DO NOTHING;


--
-- Data for Name: users; Type: TABLE DATA; Schema: user_management; Owner: lms_user
--

INSERT INTO user_management.users (id, email, avatar_url, first_name, last_name, phone, last_login, specialization_id, role_id, created_at, updated_at) VALUES ('811ba53a-0eb3-4cc3-a6a2-49214ca25b18', 'teacher1@hcmut.edu.vn', NULL, 'PHUC', 'TRAN HONG', '0901234568', NULL, 1, '3102475a-2855-4408-9144-c2b39991cdc2', '2025-12-05 16:28:30.543739+00', '2025-12-05 16:28:30.543765+00')
ON CONFLICT (email) DO NOTHING;
INSERT INTO user_management.users (id, email, avatar_url, first_name, last_name, phone, last_login, specialization_id, role_id, created_at, updated_at) VALUES ('9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'admin@hcmut.edu.vn', NULL, 'System', 'Administrator', NULL, NULL, NULL, '6d39f7aa-2680-437b-b0bc-f35bb105b941', '2025-12-13 14:04:24.009215+00', '2025-12-13 14:04:24.009215+00')
ON CONFLICT (email) DO NOTHING;
INSERT INTO user_management.users (id, email, avatar_url, first_name, last_name, phone, last_login, specialization_id, role_id, created_at, updated_at) VALUES ('7b2d4450-da93-43af-af16-7c3faccdfd5e', 'student1@hcmut.edu.vn', NULL, 'Van', 'A', '0901234567', NULL, 1, 'e5909d1e-492d-4419-9766-ce69821e28ec', '2026-01-15 08:06:49.849703+00', '2026-01-15 08:06:49.849717+00')
ON CONFLICT (email) DO NOTHING;


--
-- Data for Name: admins; Type: TABLE DATA; Schema: user_management; Owner: lms_user
--

INSERT INTO user_management.admins (user_id, admin_code) VALUES ('9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'ADM001')
ON CONFLICT (user_id) DO NOTHING;


--
-- Data for Name: outbox; Type: TABLE DATA; Schema: user_management; Owner: lms_user
--

INSERT INTO user_management.outbox (id, aggregate_type, aggregate_id, type, payload, created_at) VALUES ('a0580b19-5187-4208-9fb4-b9eb9bd4cb7f', 'user', '8f06fef7-9024-43f9-9bbc-cf89b9d063c1', 'UserCreated', '{"email": "student1@hcmut.edu.vn", "userId": "8f06fef7-9024-43f9-9bbc-cf89b9d063c1", "eventId": "aed03cb0-04cb-4878-8c58-9a4e9547725d", "password": "password123", "roleName": "STUDENT", "timestamp": "2026-01-15T14:55:37"}', '2026-01-15 14:55:37.221602')
ON CONFLICT (id) DO NOTHING;
INSERT INTO user_management.outbox (id, aggregate_type, aggregate_id, type, payload, created_at) VALUES ('e4bbd766-564e-427c-8a8c-ccbdd408085f', 'user', '7b2d4450-da93-43af-af16-7c3faccdfd5e', 'UserCreated', '{"email": "student1@hcmut.edu.vn", "userId": "7b2d4450-da93-43af-af16-7c3faccdfd5e", "eventId": "08fe2427-c740-47de-9a76-d17ee4e2c750", "password": "password123", "roleName": "STUDENT", "timestamp": "2026-01-15T15:06:49"}', '2026-01-15 15:06:49.847842')
ON CONFLICT (id) DO NOTHING;


--
-- Data for Name: system_services; Type: TABLE DATA; Schema: user_management; Owner: lms_user
--
-- (No data)


--
-- Data for Name: service_functions; Type: TABLE DATA; Schema: user_management; Owner: lms_user
--
-- (No data)


--
-- Data for Name: roles_service_functions; Type: TABLE DATA; Schema: user_management; Owner: lms_user
--
-- (No data)


--
-- Data for Name: students; Type: TABLE DATA; Schema: user_management; Owner: lms_user
--

INSERT INTO user_management.students (user_id, student_code) VALUES ('7b2d4450-da93-43af-af16-7c3faccdfd5e', '2110001')
ON CONFLICT (user_id) DO NOTHING;


--
-- Data for Name: teachers; Type: TABLE DATA; Schema: user_management; Owner: lms_user
--

INSERT INTO user_management.teachers (user_id, teacher_code, bio) VALUES ('811ba53a-0eb3-4cc3-a6a2-49214ca25b18', 'GV001', 'Experienced teacher')
ON CONFLICT (user_id) DO NOTHING;


--
-- Data for Name: url_permissions; Type: TABLE DATA; Schema: user_management; Owner: lms_user
--
-- (No data)