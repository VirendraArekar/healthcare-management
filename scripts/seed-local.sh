#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
# Compose is explicitly local; invoke only after services completed Flyway migrations.
docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U postgres -d patient_db < database/seed/V1__patient.sql
docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U postgres -d staff_db < database/seed/V1__staff.sql
docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U postgres -d appointment_db < database/seed/V1__appointment.sql
docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U postgres -d pharmacy_db < database/seed/V1__pharmacy.sql
docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U postgres -d lab_db < database/seed/V1__lab.sql
docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U postgres -d medical_record_db < database/seed/V1__medical_record.sql
docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U postgres -d billing_db < database/seed/V1__billing.sql
docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U postgres -d auth_db < database/seed/V1__auth.sql
