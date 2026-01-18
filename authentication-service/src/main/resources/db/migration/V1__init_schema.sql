-- DROP SCHEMA authentication;

CREATE SCHEMA authentication AUTHORIZATION lms_user;
-- authentication.outbox definition

-- Drop table

-- DROP TABLE authentication.outbox;

CREATE TABLE authentication.outbox (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	aggregate_type varchar(100) NOT NULL,
	aggregate_id uuid NOT NULL,
	"type" varchar(100) NOT NULL,
	payload jsonb NOT NULL,
	created_at timestamp DEFAULT now() NOT NULL,
	CONSTRAINT outbox_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_auth_outbox_aggregate_id ON authentication.outbox USING btree (aggregate_id);
CREATE INDEX idx_auth_outbox_created_at ON authentication.outbox USING btree (created_at);


-- authentication.user_credentials definition

-- Drop table

-- DROP TABLE authentication.user_credentials;

CREATE TABLE authentication.user_credentials (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	user_id uuid NOT NULL,
	email varchar(255) NOT NULL,
	password_hash varchar(255) NOT NULL,
	is_account_locked bool DEFAULT false NULL,
	failed_login_attempts int4 DEFAULT 0 NULL,
	locked_until timestamp NULL,
	last_login_at timestamp NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT user_credentials_email_key UNIQUE (email),
	CONSTRAINT user_credentials_pkey PRIMARY KEY (id),
	CONSTRAINT user_credentials_user_id_key UNIQUE (user_id)
);
CREATE INDEX idx_user_credentials_email ON authentication.user_credentials USING btree (email);
CREATE INDEX idx_user_credentials_user_id ON authentication.user_credentials USING btree (user_id);


-- authentication.password_reset_tokens definition

-- Drop table

-- DROP TABLE authentication.password_reset_tokens;

CREATE TABLE authentication.password_reset_tokens (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	user_id uuid NOT NULL,
	token_hash varchar(255) NOT NULL,
	expires_at timestamp NOT NULL,
	is_used bool DEFAULT false NULL,
	used_at timestamp NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT password_reset_tokens_pkey PRIMARY KEY (id),
	CONSTRAINT password_reset_tokens_token_hash_key UNIQUE (token_hash),
	CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES authentication.user_credentials(user_id) ON DELETE CASCADE
);
CREATE INDEX idx_password_reset_tokens_expires_at ON authentication.password_reset_tokens USING btree (expires_at);
CREATE INDEX idx_password_reset_tokens_token_hash ON authentication.password_reset_tokens USING btree (token_hash);
CREATE INDEX idx_password_reset_tokens_user_id ON authentication.password_reset_tokens USING btree (user_id);


-- authentication.refresh_tokens definition

-- Drop table

-- DROP TABLE authentication.refresh_tokens;

CREATE TABLE authentication.refresh_tokens (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	user_id uuid NOT NULL,
	token_hash varchar(255) NOT NULL,
	expires_at timestamp NOT NULL,
	is_revoked bool DEFAULT false NULL,
	revoked_at timestamp NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id),
	CONSTRAINT refresh_tokens_token_hash_key UNIQUE (token_hash),
	CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES authentication.user_credentials(user_id) ON DELETE CASCADE
);
CREATE INDEX idx_refresh_tokens_expires_at ON authentication.refresh_tokens USING btree (expires_at);
CREATE INDEX idx_refresh_tokens_token_hash ON authentication.refresh_tokens USING btree (token_hash);
CREATE INDEX idx_refresh_tokens_user_id ON authentication.refresh_tokens USING btree (user_id);