CREATE TABLE patient_contacts(patient_id uuid PRIMARY KEY,email varchar(254) NOT NULL);ALTER TABLE email_jobs ADD COLUMN recipient varchar(254);
