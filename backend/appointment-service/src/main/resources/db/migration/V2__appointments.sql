CREATE EXTENSION IF NOT EXISTS btree_gist;
CREATE TABLE slots(id uuid PRIMARY KEY,doctor_id uuid NOT NULL,starts_at timestamptz NOT NULL,ends_at timestamptz NOT NULL,CHECK(ends_at>starts_at),EXCLUDE USING gist(doctor_id WITH =,tstzrange(starts_at,ends_at,'[)') WITH &&));
CREATE TABLE bookings(id uuid PRIMARY KEY,slot_id uuid NOT NULL REFERENCES slots(id),patient_id uuid NOT NULL,status text NOT NULL CHECK(status IN('BOOKED','CANCELLED','COMPLETED')));
CREATE UNIQUE INDEX one_booking_per_slot ON bookings(slot_id) WHERE status='BOOKED';
CREATE INDEX bookings_patient ON bookings(patient_id);
