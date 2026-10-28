CREATE TABLE prescription_dispenses(prescription_id uuid PRIMARY KEY,total_quantity integer NOT NULL CHECK(total_quantity>=0));ALTER TABLE movements ALTER COLUMN prescription_id DROP NOT NULL;

INSERT INTO prescription_dispenses(prescription_id,total_quantity) SELECT prescription_id,sum(-quantity)::integer FROM movements WHERE prescription_id IS NOT NULL AND quantity<0 GROUP BY prescription_id ON CONFLICT DO NOTHING;
