CREATE TABLE records(id uuid PRIMARY KEY,kind varchar(60) NOT NULL,data jsonb NOT NULL,version bigint NOT NULL DEFAULT 0,created_at timestamptz NOT NULL DEFAULT now());
CREATE INDEX records_kind_created ON records(kind,created_at);
CREATE INDEX records_patient ON records((data->>'patientId'));
CREATE TABLE outbox(id uuid PRIMARY KEY,topic varchar(120) NOT NULL,aggregate_id uuid NOT NULL,payload jsonb NOT NULL,created_at timestamptz NOT NULL DEFAULT now(),sent_at timestamptz);
CREATE INDEX outbox_pending ON outbox(created_at) WHERE sent_at IS NULL;
CREATE TABLE audit_log(id bigserial PRIMARY KEY,actor varchar(200) NOT NULL,action varchar(200) NOT NULL,aggregate_id uuid,occurred_at timestamptz NOT NULL DEFAULT now());
CREATE TABLE consumed_events(event_id uuid PRIMARY KEY,consumed_at timestamptz NOT NULL DEFAULT now());
