CREATE TABLE prescriptions(id uuid PRIMARY KEY,patient_id uuid NOT NULL,doctor_id uuid,medicine varchar(200) NOT NULL,quantity integer NOT NULL,valid_until date NOT NULL,instructions text,status varchar(30) NOT NULL,version bigint NOT NULL DEFAULT 0,created_at timestamptz NOT NULL DEFAULT now());
INSERT INTO prescriptions(id,patient_id,doctor_id,medicine,quantity,valid_until,instructions,status,version,created_at) SELECT id,nullif(data->>'patientId','')::uuid,nullif(data->>'doctorId','')::uuid,data->>'medicine',coalesce(nullif(data->>'quantity','')::integer,0),nullif(data->>'validUntil','')::date,data->>'instructions',coalesce(data->>'status','ACTIVE'),version,created_at FROM records WHERE kind='prescription';
CREATE INDEX prescriptions_owner_created ON prescriptions(patient_id,created_at);

ALTER TABLE prescriptions ADD CHECK(quantity>0);CREATE INDEX prescriptions_patient ON prescriptions(patient_id,created_at);

UPDATE prescriptions SET status='NEEDS_REVIEW' WHERE doctor_id IS NULL;
