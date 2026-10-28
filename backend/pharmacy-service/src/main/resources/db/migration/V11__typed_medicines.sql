CREATE TABLE medicines(id uuid PRIMARY KEY,name varchar(200) NOT NULL,strength varchar(100) NOT NULL,unit varchar(40) NOT NULL,manufacturer varchar(150),reorder_level integer NOT NULL,status varchar(30) NOT NULL,version bigint NOT NULL DEFAULT 0,created_at timestamptz NOT NULL DEFAULT now());
INSERT INTO medicines(id,name,strength,unit,manufacturer,reorder_level,status,version,created_at) SELECT id,data->>'name',data->>'strength',data->>'unit',data->>'manufacturer',coalesce(nullif(data->>'reorderLevel','')::integer,10),coalesce(data->>'status','ACTIVE'),version,created_at FROM records WHERE kind='medicine';

CREATE UNIQUE INDEX medicine_name ON medicines(lower(name));ALTER TABLE medicines ADD CHECK(reorder_level>=0);
