CREATE TABLE batches(id uuid PRIMARY KEY,medicine text NOT NULL,quantity integer NOT NULL CHECK(quantity>=0),expires_on date NOT NULL);
CREATE INDEX batch_expiry ON batches(expires_on);
CREATE TABLE movements(id uuid PRIMARY KEY,batch_id uuid NOT NULL REFERENCES batches(id),prescription_id uuid NOT NULL,quantity integer NOT NULL,actor text NOT NULL,created_at timestamptz NOT NULL DEFAULT now());
