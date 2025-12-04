-- =====================================================
-- USER MANAGEMENT SCHEMA - CREATE TABLES
-- =====================================================
-- Creates all tables in user_management schema
-- No CASCADE at database level (only in JPA code)
-- =====================================================

-- Create schema if not exists
CREATE SCHEMA IF NOT EXISTS user_management;

-- Set search path to user_management schema
SET search_path TO user_management;

CREATE TABLE "roles" (
  "id" uuid PRIMARY KEY,
  "is_active" bool DEFAULT true,
  "name" varchar,
  "description" varchar,
  "created_at" timestamptz NOT NULL DEFAULT (now()),
  "updated_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "users" (
  "id" uuid PRIMARY KEY,
  "email" varchar UNIQUE,
  "avatar_url" varchar,
  "first_name" varchar,
  "last_name" varchar,
  "phone" varchar,
  "last_login" timestamptz,
  "specialization_id" int,
  "role_id" uuid NOT NULL,
  "created_at" timestamptz NOT NULL DEFAULT (now()),
  "updated_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "students" (
  "user_id" uuid PRIMARY KEY,
  "student_code" varchar UNIQUE NOT NULL
);

CREATE TABLE "teachers" (
  "user_id" uuid PRIMARY KEY,
  "teacher_code" varchar UNIQUE NOT NULL,
  "bio" text
);

CREATE TABLE "admins" (
  "user_id" uuid PRIMARY KEY,
  "admin_code" varchar UNIQUE
);

CREATE TABLE "system_services" (
  "id" uuid PRIMARY KEY,
  "name" varchar,
  "description" varchar,
  "created_at" timestamptz NOT NULL DEFAULT (now()),
  "updated_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "service_functions" (
  "id" uuid PRIMARY KEY,
  "name" varchar,
  "description" varchar,
  "system_service_id" uuid NOT NULL,
  "created_at" timestamptz NOT NULL DEFAULT (now()),
  "updated_at" timestamptz NOT NULL DEFAULT (now())
);

CREATE TABLE "url_permissions" (
  "id" uuid PRIMARY KEY,
  "url_pattern" varchar,
  "http_method" varchar,
  "service_function_id" uuid NOT NULL,
  "created_at" timestamptz NOT NULL DEFAULT (now()),
  "updated_at" timestamptz NOT NULL DEFAULT (now())
);

ALTER TABLE "users" ADD FOREIGN KEY ("role_id") REFERENCES "roles" ("id");

ALTER TABLE "service_functions" ADD FOREIGN KEY ("system_service_id") REFERENCES "system_services" ("id");

ALTER TABLE "students" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "teachers" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "admins" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "url_permissions" ADD FOREIGN KEY ("service_function_id") REFERENCES "service_functions" ("id");

CREATE TABLE "roles_service_functions" (
  "roles_id" uuid,
  "service_functions_id" uuid,
  PRIMARY KEY ("roles_id", "service_functions_id")
);

ALTER TABLE "roles_service_functions" ADD FOREIGN KEY ("roles_id") REFERENCES "roles" ("id");

ALTER TABLE "roles_service_functions" ADD FOREIGN KEY ("service_functions_id") REFERENCES "service_functions" ("id");

