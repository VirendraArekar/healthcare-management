ALTER TABLE users ADD COLUMN version bigint NOT NULL DEFAULT 0;ALTER TABLE users ADD COLUMN created_at timestamptz NOT NULL DEFAULT now();
