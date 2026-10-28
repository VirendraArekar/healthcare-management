#!/bin/sh
set -eu
psql -v ON_ERROR_STOP=1 --username postgres --dbname postgres --set=pw="$SERVICE_DB_PASSWORD" <<'SQL'
CREATE USER auth_user PASSWORD :'pw';
CREATE DATABASE auth_db OWNER auth_user;
REVOKE ALL ON DATABASE auth_db FROM PUBLIC;
SQL
psql -v ON_ERROR_STOP=1 --username postgres --dbname postgres --set=pw="$SERVICE_DB_PASSWORD" <<'SQL'
CREATE USER patient_user PASSWORD :'pw';
CREATE DATABASE patient_db OWNER patient_user;
REVOKE ALL ON DATABASE patient_db FROM PUBLIC;
SQL
psql -v ON_ERROR_STOP=1 --username postgres --dbname postgres --set=pw="$SERVICE_DB_PASSWORD" <<'SQL'
CREATE USER staff_user PASSWORD :'pw';
CREATE DATABASE staff_db OWNER staff_user;
REVOKE ALL ON DATABASE staff_db FROM PUBLIC;
SQL
psql -v ON_ERROR_STOP=1 --username postgres --dbname postgres --set=pw="$SERVICE_DB_PASSWORD" <<'SQL'
CREATE USER appointment_user PASSWORD :'pw';
CREATE DATABASE appointment_db OWNER appointment_user;
REVOKE ALL ON DATABASE appointment_db FROM PUBLIC;
SQL
psql -v ON_ERROR_STOP=1 --username postgres --dbname postgres --set=pw="$SERVICE_DB_PASSWORD" <<'SQL'
CREATE USER medical_record_user PASSWORD :'pw';
CREATE DATABASE medical_record_db OWNER medical_record_user;
REVOKE ALL ON DATABASE medical_record_db FROM PUBLIC;
SQL
psql -v ON_ERROR_STOP=1 --username postgres --dbname postgres --set=pw="$SERVICE_DB_PASSWORD" <<'SQL'
CREATE USER lab_user PASSWORD :'pw';
CREATE DATABASE lab_db OWNER lab_user;
REVOKE ALL ON DATABASE lab_db FROM PUBLIC;
SQL
psql -v ON_ERROR_STOP=1 --username postgres --dbname postgres --set=pw="$SERVICE_DB_PASSWORD" <<'SQL'
CREATE USER pharmacy_user PASSWORD :'pw';
CREATE DATABASE pharmacy_db OWNER pharmacy_user;
REVOKE ALL ON DATABASE pharmacy_db FROM PUBLIC;
SQL
psql -v ON_ERROR_STOP=1 --username postgres --dbname postgres --set=pw="$SERVICE_DB_PASSWORD" <<'SQL'
CREATE USER billing_user PASSWORD :'pw';
CREATE DATABASE billing_db OWNER billing_user;
REVOKE ALL ON DATABASE billing_db FROM PUBLIC;
SQL
psql -v ON_ERROR_STOP=1 --username postgres --dbname postgres --set=pw="$SERVICE_DB_PASSWORD" <<'SQL'
CREATE USER notification_user PASSWORD :'pw';
CREATE DATABASE notification_db OWNER notification_user;
REVOKE ALL ON DATABASE notification_db FROM PUBLIC;
SQL
psql -v ON_ERROR_STOP=1 --username postgres --dbname postgres --set=pw="$SERVICE_DB_PASSWORD" <<'SQL'
CREATE USER reporting_user PASSWORD :'pw';
CREATE DATABASE reporting_db OWNER reporting_user;
REVOKE ALL ON DATABASE reporting_db FROM PUBLIC;
SQL
