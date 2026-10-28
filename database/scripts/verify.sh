#!/bin/sh
set -eu
docker compose exec -T postgres psql -U postgres -d auth_db -c "select version,description,success from flyway_schema_history order by installed_rank;"
docker compose exec -T postgres psql -U postgres -d patient_db -c "select version,description,success from flyway_schema_history order by installed_rank;"
docker compose exec -T postgres psql -U postgres -d staff_db -c "select version,description,success from flyway_schema_history order by installed_rank;"
docker compose exec -T postgres psql -U postgres -d appointment_db -c "select version,description,success from flyway_schema_history order by installed_rank;"
docker compose exec -T postgres psql -U postgres -d medical_record_db -c "select version,description,success from flyway_schema_history order by installed_rank;"
docker compose exec -T postgres psql -U postgres -d lab_db -c "select version,description,success from flyway_schema_history order by installed_rank;"
docker compose exec -T postgres psql -U postgres -d pharmacy_db -c "select version,description,success from flyway_schema_history order by installed_rank;"
docker compose exec -T postgres psql -U postgres -d billing_db -c "select version,description,success from flyway_schema_history order by installed_rank;"
docker compose exec -T postgres psql -U postgres -d notification_db -c "select version,description,success from flyway_schema_history order by installed_rank;"
docker compose exec -T postgres psql -U postgres -d reporting_db -c "select version,description,success from flyway_schema_history order by installed_rank;"
