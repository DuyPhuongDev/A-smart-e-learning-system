-- test001
INSERT INTO authentication.user_credentials (id, user_id, email, password_hash, is_account_locked, failed_login_attempts, locked_until, last_login_at, created_at, updated_at)
VALUES ('576d0e7e-49c0-41e3-8b69-7e4230dc4b3c', 'd036adda-78a1-4a53-ba51-1af83907c5f8', 'test.001@hcmut.edu.vn', '$2a$12$sUpyBvT89YMX.ibZTh9ZT.33qJq0DxhgVBFKKxL0XBsKhM0Vt1YqC', false, 0, NULL, NULL, now(), now())
ON CONFLICT (email) DO NOTHING;

-- test002
INSERT INTO authentication.user_credentials (id, user_id, email, password_hash, is_account_locked, failed_login_attempts, locked_until, last_login_at, created_at, updated_at)
VALUES ('6596cda5-0ded-47f9-b4ac-3195b592a93d', '9b2c7d39-bc76-41ec-8586-d64cbc18775b', 'test.002@hcmut.edu.vn', '$2a$12$sUpyBvT89YMX.ibZTh9ZT.33qJq0DxhgVBFKKxL0XBsKhM0Vt1YqC', false, 0, NULL, NULL, now(), now())
ON CONFLICT (email) DO NOTHING;

--test010
INSERT INTO authentication.user_credentials (id, user_id, email, password_hash, is_account_locked, failed_login_attempts, locked_until, last_login_at, created_at, updated_at)
VALUES ('9ed2b43d-e6ab-47ac-8173-3cbd15bb901a', '83ceb6f7-77e5-4663-947d-cc3e5f98fb59', 'test.010@hcmut.edu.vn', '$2a$12$sUpyBvT89YMX.ibZTh9ZT.33qJq0DxhgVBFKKxL0XBsKhM0Vt1YqC', false, 0, NULL, NULL, now(), now())
ON CONFLICT (email) DO NOTHING;

--test003
INSERT INTO authentication.user_credentials (id, user_id, email, password_hash, is_account_locked, failed_login_attempts, locked_until, last_login_at, created_at, updated_at)
VALUES ('56048170-6df8-4cfb-bfeb-36db2106e54e', '37f5a1d9-83ab-45c4-85de-0205d1ca1a0b', 'test.003@hcmut.edu.vn', '$2a$12$sUpyBvT89YMX.ibZTh9ZT.33qJq0DxhgVBFKKxL0XBsKhM0Vt1YqC', false, 0, NULL, NULL, now(), now())
ON CONFLICT (email) DO NOTHING;

--test004
INSERT INTO authentication.user_credentials (id, user_id, email, password_hash, is_account_locked, failed_login_attempts, locked_until, last_login_at, created_at, updated_at)
VALUES ('097db681-0cff-4cbc-bc28-932da4088dd3', '94bc2015-b87f-45ac-8b95-a7b45f2faa47', 'test.004@hcmut.edu.vn', '$2a$12$sUpyBvT89YMX.ibZTh9ZT.33qJq0DxhgVBFKKxL0XBsKhM0Vt1YqC', false, 0, NULL, NULL, now(), now())
ON CONFLICT (email) DO NOTHING;

--test005
INSERT INTO authentication.user_credentials (id, user_id, email, password_hash, is_account_locked, failed_login_attempts, locked_until, last_login_at, created_at, updated_at)
VALUES ('32ba9ae2-0d61-4118-8039-71d6eefd7499', '4d3f8e19-d6f8-475f-b534-ffec14382540', 'test.005@hcmut.edu.vn', '$2a$12$sUpyBvT89YMX.ibZTh9ZT.33qJq0DxhgVBFKKxL0XBsKhM0Vt1YqC', false, 0, NULL, NULL, now(), now())
ON CONFLICT (email) DO NOTHING;

--test008
INSERT INTO authentication.user_credentials (id, user_id, email, password_hash, is_account_locked, failed_login_attempts, locked_until, last_login_at, created_at, updated_at)
VALUES ('3695be85-ec47-4341-9f6c-6e4586f3527e', 'bfde9a02-d813-4484-94a4-4ce131cc3609', 'test.008@hcmut.edu.vn', '$2a$12$sUpyBvT89YMX.ibZTh9ZT.33qJq0DxhgVBFKKxL0XBsKhM0Vt1YqC', false, 0, NULL, NULL, now(), now())
ON CONFLICT (email) DO NOTHING;

--test011
INSERT INTO authentication.user_credentials (id, user_id, email, password_hash, is_account_locked, failed_login_attempts, locked_until, last_login_at, created_at, updated_at)
VALUES ('9d1ff46a-4221-4bd1-a592-197eca976f9c', 'b54417e1-37a3-47dc-b839-2d5a9332de96', 'test.011@hcmut.edu.vn', '$2a$12$sUpyBvT89YMX.ibZTh9ZT.33qJq0DxhgVBFKKxL0XBsKhM0Vt1YqC', false, 0, NULL, NULL, now(), now())
ON CONFLICT (email) DO NOTHING;