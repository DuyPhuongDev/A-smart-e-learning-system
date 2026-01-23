-- ============================================
-- Initial Data Migration for Authentication Service
-- Version: V2
-- Description: Seed data from init_data.sql
-- ============================================

--
-- Data for Name: user_credentials; Type: TABLE DATA; Schema: authentication; Owner: lms_user
--

INSERT INTO authentication.user_credentials (id, user_id, email, password_hash, is_account_locked, failed_login_attempts, locked_until, last_login_at, created_at, updated_at) VALUES ('9ec46ee0-0a47-43b0-97d1-4f7c124e04f5', '811ba53a-0eb3-4cc3-a6a2-49214ca25b18', 'teacher@hcmut.edu.vn', '$2a$12$.SQtPKQBVUgfROS3FQKDxOWRs7//2bAdZb2AHEcv/prxm7o6wv6n2', false, 0, NULL, NULL, '2025-12-13 21:08:19.897688', '2025-12-13 21:08:19.897688')
ON CONFLICT (email) DO NOTHING;
INSERT INTO authentication.user_credentials (id, user_id, email, password_hash, is_account_locked, failed_login_attempts, locked_until, last_login_at, created_at, updated_at) VALUES ('c01bf5a1-9cf4-4d05-80eb-e87401a86f55', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'admin@hcmut.edu.vn', '$2a$12$.SQtPKQBVUgfROS3FQKDxOWRs7//2bAdZb2AHEcv/prxm7o6wv6n2', false, 0, NULL, '2026-01-15 14:55:16.085097', '2025-12-13 21:04:24.009215', '2026-01-15 14:55:16.332615')
ON CONFLICT (email) DO NOTHING;
INSERT INTO authentication.user_credentials (id, user_id, email, password_hash, is_account_locked, failed_login_attempts, locked_until, last_login_at, created_at, updated_at) VALUES ('dc527cae-0382-4b00-bded-750401c2fc50', '8f06fef7-9024-43f9-9bbc-cf89b9d063c1', 'student1@hcmut.edu.vn', '$2a$12$sUpyBvT89YMX.ibZTh9ZT.33qJq0DxhgVBFKKxL0XBsKhM0Vt1YqC', false, 0, NULL, NULL, '2026-01-15 21:39:14.082205', '2026-01-15 21:39:14.101193')
ON CONFLICT (email) DO NOTHING;


--
-- Data for Name: password_reset_tokens; Type: TABLE DATA; Schema: authentication; Owner: lms_user
--
-- (No data)


--
-- Data for Name: refresh_tokens; Type: TABLE DATA; Schema: authentication; Owner: lms_user
--

INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('25de3d7c-3615-44a8-a634-0533c4ac4afa', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '537abe7362b881551af45cc61d8ffd9be72564798fb6fa1894499d3025b3b60d', '2025-12-20 21:17:20.831232', false, NULL, '2025-12-13 21:17:20.831242')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('0b7b245e-401a-4539-8a68-1fb7d2a014e6', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'd58aff3faf156993c98851cd22656f5229b3635561c21b82555990918a6bce94', '2025-12-20 21:20:37.770123', false, NULL, '2025-12-13 21:20:37.77013')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('e774d1ea-9dd9-452a-a989-775c4f6ef5c6', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '1db15f5da730c827ba549e793f058c3e867697856611e4f09aa441bc4e8a7652', '2025-12-20 21:27:38.537815', false, NULL, '2025-12-13 21:27:38.537821')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('d1c95ca3-1b8c-4296-a2ce-05edf9961b80', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '6b29712cc0816781280bc0f9747fc55d5a0509b4db004e7c3ed952d69cdfebf0', '2025-12-20 21:29:28.108101', false, NULL, '2025-12-13 21:29:28.108107')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('74e049fd-c29c-4678-a1e7-c8acaa2b4d85', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '35ff277180709e3614633faa57aae0d18ff655204c36dc43f035f2f1e2ee7faf', '2025-12-20 22:39:26.551284', false, NULL, '2025-12-13 22:39:26.551295')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('54a318d5-3d5a-46c2-8933-0231a6c7a8b9', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '9730849df2f9839ed45bb15655764c84ba7be99b108b31ba9ecf84a3c0b983a1', '2025-12-20 22:39:52.087161', false, NULL, '2025-12-13 22:39:52.087175')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('dcc44083-b32c-45f2-80c1-30e95d161e24', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '6afecbee9ee154a4181b131c1b188a8011aaadcc7598a0822fbc2bacdff6c4ad', '2025-12-20 22:48:30.448922', false, NULL, '2025-12-13 22:48:30.448937')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('48cbb412-6a28-45a7-bbfd-1089581c1bc1', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'f85e61f6fbe9ccdaab1be91608106f66b9f87cf4ca24ff5a1b957cd24211d131', '2025-12-20 22:51:05.522625', false, NULL, '2025-12-13 22:51:05.522634')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('f7d9e520-1829-4655-9903-252368476bee', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '6bc66e55ddbfb52a8845166855a9877992c2783d0e8adead054474d26a12a4af', '2025-12-20 22:51:16.857377', false, NULL, '2025-12-13 22:51:16.857385')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('f58c9b83-59c4-4882-9890-8f3224dddea8', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '7e5d2d1059ab312052e698cf024ead88d569fdaf874035c55d88d1cc6ca2e399', '2025-12-20 23:04:45.949156', false, NULL, '2025-12-13 23:04:45.949165')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('9f473f90-8f80-4ae6-9433-cb555aa923f0', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '665ddd888aaf75634b0719e49b970e44c81860ac85ef58286ffa4fa9c91844c2', '2025-12-20 23:12:53.613762', false, NULL, '2025-12-13 23:12:53.61377')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('c158f281-405e-47fe-8352-415da2d11b5a', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'f4669f6528b8d245cd24a6ad9a033209780c1c3bbbd984e387d5c5edde6b7140', '2025-12-20 23:15:13.722866', false, NULL, '2025-12-13 23:15:13.722874')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('e73a2cd3-2d3d-4920-a678-6d14cb573aa7', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '2859d9faf31434de54a0ae27d37fc125a86e939ace80fe79eaaaf29a94051384', '2025-12-20 23:18:34.679407', false, NULL, '2025-12-13 23:18:34.679415')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('f31dcaa2-c221-475c-b898-4174f8df1c66', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '15eaa23815f97a18b56e664f1eb26115f21070083b70fcd9665103129e5596f9', '2025-12-20 23:18:42.648313', false, NULL, '2025-12-13 23:18:42.648319')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('779c490e-ee64-4c7c-ad26-59ad23131246', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '91f309ea4734b35f4cd714a79279acded74805493d2d60bc058bc01aa3dd7716', '2025-12-20 23:18:48.580452', false, NULL, '2025-12-13 23:18:48.580459')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('0f189d51-4e9b-4e8f-8cbe-c50eb82301ee', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '7a82465124ed5a723110cfdac7686f9a60cc489feac0197b45d2940a5ae22eef', '2025-12-20 23:21:08.351971', false, NULL, '2025-12-13 23:21:08.35198')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('c81590f4-5839-4327-ba9a-2607a72642c3', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'ce03293f448cecda296dd0d4a2b8bb8885c13decdde6801bce36001b548997be', '2025-12-20 23:21:28.20467', false, NULL, '2025-12-13 23:21:28.204677')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('c29a4a7b-ea41-4c4b-ad3d-91d17126e304', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '9efa682a70b406a1c03e8a36b8b0584c3603a56d6116ef6b088e1a27fe7bcdbd', '2025-12-20 23:23:30.04772', false, NULL, '2025-12-13 23:23:30.047734')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('399f1bb7-9c10-4347-bba5-ca575f2be1a6', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '1232dee57831e4672fb3ec0012754f842a54e540c12349aa492aa6975e1667bc', '2025-12-20 23:29:09.837014', false, NULL, '2025-12-13 23:29:09.837024')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('fc4749d6-e793-46fc-bc04-a03effa800c4', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'a76cfd5397397134d0d45d47f619cbd5278f3a1e56872a07a9ce76c435d6b5a3', '2025-12-20 23:31:55.151832', false, NULL, '2025-12-13 23:31:55.151841')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('32b3cbe5-b65a-4a7d-8b87-c3161755ed4f', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '9a1cf85b0bf3474105bcda1d880cfda6783d565f6eef7c09005ba9179a113ed0', '2025-12-20 23:39:21.516706', false, NULL, '2025-12-13 23:39:21.51672')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('04d7a4e5-f46d-4320-88f9-83802524a093', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '52e0e71127df634496225ab2ce15ecf3f5f86288e0e4fd6b47ddc4db59673d32', '2025-12-20 23:40:19.303419', false, NULL, '2025-12-13 23:40:19.30343')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('eacd3d5f-df44-41e7-86e8-2a6cd4d8a891', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '724892d9c37c87930fe6857496175eeae0df2c78b124b957bd3e792d4915b142', '2025-12-20 23:48:43.054734', false, NULL, '2025-12-13 23:48:43.054768')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('dbe3067b-12cd-403e-87c4-d14b8efebcfc', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'f5bce5247f2169fcd354998945e834f5449c7de90e8fc28c4bd58d3671202500', '2025-12-20 23:52:11.775995', false, NULL, '2025-12-13 23:52:11.776016')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('60cdeb25-632a-45c0-8980-6b031bb76965', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'c9ee14dce9d06804571cbbb4c4b46a0eddc4f45e0df2e61a54e7183fedb1f5be', '2025-12-20 23:52:41.416515', false, NULL, '2025-12-13 23:52:41.416525')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('93740d51-1f3e-4e3f-b936-7df84c1f2402', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '052ede0139a43830ac0ddc118acb37c42c3b3508ee2631f7bd826ae2dfa540af', '2025-12-20 23:54:48.389818', false, NULL, '2025-12-13 23:54:48.389846')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('a882a307-f07e-4985-a1a4-7f0f530e800e', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '237a1bae36cea4a5ba59a5aca4882cc01589f5bb638f99b858c40a79e8087cee', '2025-12-20 23:56:20.918315', false, NULL, '2025-12-13 23:56:20.918335')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('7fc040bd-bad0-4ff7-964f-04dea040f7fd', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '30fedac50602813d7bf1fe59d40c21ab7d6692f2207b8f4d506c0b09dc77da04', '2025-12-21 00:05:03.092599', false, NULL, '2025-12-14 00:05:03.092609')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('bdf9d29c-8b4f-4cb5-8d71-5eae6f0dadfb', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'c2c48caace43819a825b9ced1d3e5eac04addbef90f479d91a7be4282dbe8d4e', '2025-12-21 00:06:47.631983', false, NULL, '2025-12-14 00:06:47.631996')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('d1ad643b-cde7-4a80-825d-bf88aa06c92a', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '8e939c64f1c2fe1302a80c32a2111227c29b16119c0442fd0360e130c2017975', '2025-12-21 00:15:49.214258', false, NULL, '2025-12-14 00:15:49.214278')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('b54564a2-edd6-4b32-9a2c-c7697e72f571', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'eeb5ad9d01eb8f9c1a8ccc0c2bb54c0ffea853771411a368d5419250bf02cf0a', '2025-12-21 00:16:28.091742', false, NULL, '2025-12-14 00:16:28.091758')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('a4ec19bf-0099-40ad-870e-3b57650125d1', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '72dacf6833ae42a04336d87d1d875860addc22df1887078316e5b080662e1daf', '2025-12-21 00:33:13.440153', false, NULL, '2025-12-14 00:33:13.44019')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('3f8c416d-f61f-4cb5-ace2-44a66903ec2c', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', '68ada548a8122fdca3cc23687be7a8898a1ce24fbc07d70a494c706c58a39804', '2026-01-19 22:09:46.110521', false, NULL, '2026-01-12 22:09:46.110537')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('4e4e0641-f3ba-4a52-9e71-b92cbf7b2b23', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'dd31ef4403ed86b76a0d860faf8323746689b6290b664022680ac6acd3186bba', '2026-01-22 00:17:31.815522', false, NULL, '2026-01-15 00:17:31.815538')
ON CONFLICT (id) DO NOTHING;
INSERT INTO authentication.refresh_tokens (id, user_id, token_hash, expires_at, is_revoked, revoked_at, created_at) VALUES ('022259f0-834d-4566-b1bc-ff4214e52b99', '9e0a39e1-d534-4df2-b1b8-0eb0082d245b', 'ed63f4e8e6b97890840e03035bc8fbc6789321b8e6ce0548d3660e3d38af2bda', '2026-01-22 14:55:16.311617', false, NULL, '2026-01-15 14:55:16.311632')
ON CONFLICT (id) DO NOTHING;


--
-- PostgreSQL database dump complete
--
