-- DROP SCHEMA user_management;

CREATE SCHEMA user_management AUTHORIZATION lms_user;
-- user_management.outbox definition

-- Drop table

-- DROP TABLE user_management.outbox;

CREATE TABLE user_management.outbox (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	aggregate_type varchar(100) NOT NULL,
	aggregate_id uuid NOT NULL,
	"type" varchar(100) NOT NULL,
	payload jsonb NOT NULL,
	created_at timestamp DEFAULT now() NOT NULL,
	CONSTRAINT outbox_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_outbox_aggregate_id ON user_management.outbox USING btree (aggregate_id);
CREATE INDEX idx_outbox_created_at ON user_management.outbox USING btree (created_at);


-- user_management.roles definition

-- Drop table

-- DROP TABLE user_management.roles;

CREATE TABLE user_management.roles (
	id uuid NOT NULL,
	is_active bool DEFAULT true NULL,
	"name" varchar NULL,
	description varchar NULL,
	created_at timestamptz DEFAULT now() NOT NULL,
	updated_at timestamptz DEFAULT now() NOT NULL,
	CONSTRAINT roles_name_key UNIQUE (name),
	CONSTRAINT roles_pkey PRIMARY KEY (id)
);


-- user_management.system_services definition

-- Drop table

-- DROP TABLE user_management.system_services;

CREATE TABLE user_management.system_services (
	id uuid NOT NULL,
	"name" varchar NULL,
	description varchar NULL,
	created_at timestamptz DEFAULT now() NOT NULL,
	updated_at timestamptz DEFAULT now() NOT NULL,
	CONSTRAINT system_services_pkey PRIMARY KEY (id)
);


-- user_management.service_functions definition

-- Drop table

-- DROP TABLE user_management.service_functions;

CREATE TABLE user_management.service_functions (
	id uuid NOT NULL,
	"name" varchar NULL,
	description varchar NULL,
	system_service_id uuid NOT NULL,
	created_at timestamptz DEFAULT now() NOT NULL,
	updated_at timestamptz DEFAULT now() NOT NULL,
	CONSTRAINT service_functions_pkey PRIMARY KEY (id),
	CONSTRAINT service_functions_system_service_id_fkey FOREIGN KEY (system_service_id) REFERENCES user_management.system_services(id)
);


-- user_management.url_permissions definition

-- Drop table

-- DROP TABLE user_management.url_permissions;

CREATE TABLE user_management.url_permissions (
	id uuid NOT NULL,
	url_pattern varchar NULL,
	http_method varchar NULL,
	service_function_id uuid NOT NULL,
	created_at timestamptz DEFAULT now() NOT NULL,
	updated_at timestamptz DEFAULT now() NOT NULL,
	CONSTRAINT url_permissions_pkey PRIMARY KEY (id),
	CONSTRAINT url_permissions_service_function_id_fkey FOREIGN KEY (service_function_id) REFERENCES user_management.service_functions(id)
);


-- user_management.users definition

-- Drop table

-- DROP TABLE user_management.users;

CREATE TABLE user_management.users (
	id uuid NOT NULL,
	email varchar NULL,
	avatar_url varchar NULL,
	first_name varchar NULL,
	last_name varchar NULL,
	phone varchar NULL,
	last_login timestamptz NULL,
	specialization_id int4 NULL,
	role_id uuid NOT NULL,
	created_at timestamptz DEFAULT now() NOT NULL,
	updated_at timestamptz DEFAULT now() NOT NULL,
	CONSTRAINT users_email_key UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (id),
	CONSTRAINT users_role_id_fkey FOREIGN KEY (role_id) REFERENCES user_management.roles(id)
);


-- user_management.admins definition

-- Drop table

-- DROP TABLE user_management.admins;

CREATE TABLE user_management.admins (
	user_id uuid NOT NULL,
	admin_code varchar NULL,
	CONSTRAINT admins_admin_code_key UNIQUE (admin_code),
	CONSTRAINT admins_pkey PRIMARY KEY (user_id),
	CONSTRAINT admins_user_id_fkey FOREIGN KEY (user_id) REFERENCES user_management.users(id)
);


-- user_management.roles_service_functions definition

-- Drop table

-- DROP TABLE user_management.roles_service_functions;

CREATE TABLE user_management.roles_service_functions (
	roles_id uuid NOT NULL,
	service_functions_id uuid NOT NULL,
	CONSTRAINT roles_service_functions_pkey PRIMARY KEY (roles_id, service_functions_id),
	CONSTRAINT roles_service_functions_roles_id_fkey FOREIGN KEY (roles_id) REFERENCES user_management.roles(id),
	CONSTRAINT roles_service_functions_service_functions_id_fkey FOREIGN KEY (service_functions_id) REFERENCES user_management.service_functions(id)
);


-- user_management.students definition

-- Drop table

-- DROP TABLE user_management.students;

CREATE TABLE user_management.students (
	user_id uuid NOT NULL,
	student_code varchar NOT NULL,
	CONSTRAINT students_pkey PRIMARY KEY (user_id),
	CONSTRAINT students_student_code_key UNIQUE (student_code),
	CONSTRAINT students_user_id_fkey FOREIGN KEY (user_id) REFERENCES user_management.users(id)
);


-- user_management.teachers definition

-- Drop table

-- DROP TABLE user_management.teachers;

CREATE TABLE user_management.teachers (
	user_id uuid NOT NULL,
	teacher_code varchar NOT NULL,
	bio text NULL,
	CONSTRAINT teachers_pkey PRIMARY KEY (user_id),
	CONSTRAINT teachers_teacher_code_key UNIQUE (teacher_code),
	CONSTRAINT teachers_user_id_fkey FOREIGN KEY (user_id) REFERENCES user_management.users(id)
);