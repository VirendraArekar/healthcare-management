CREATE TABLE users(id uuid PRIMARY KEY,email varchar(254) UNIQUE NOT NULL,password_hash text NOT NULL,role varchar(30) NOT NULL,patient_id uuid,enabled boolean NOT NULL DEFAULT true);
CREATE TABLE refresh_tokens(id uuid PRIMARY KEY,user_id uuid NOT NULL REFERENCES users(id),token_hash varchar(64) UNIQUE NOT NULL,expires_at timestamptz NOT NULL,revoked boolean NOT NULL DEFAULT false);
CREATE INDEX refresh_user ON refresh_tokens(user_id);
