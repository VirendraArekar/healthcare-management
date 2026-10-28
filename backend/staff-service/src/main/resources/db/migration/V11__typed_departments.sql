CREATE TABLE departments(id uuid PRIMARY KEY,name varchar(100) NOT NULL,description text,status varchar(30) NOT NULL,version bigint NOT NULL DEFAULT 0,created_at timestamptz NOT NULL DEFAULT now());
INSERT INTO departments(id,name,description,status,version,created_at) SELECT id,data->>'name',data->>'description',coalesce(data->>'status','ACTIVE'),version,created_at FROM records WHERE kind='department';

CREATE UNIQUE INDEX department_name ON departments(lower(name));
