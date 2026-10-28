#!/bin/sh
set -eu
cd "$(dirname "$0")/../.."
[ "${CONFIRM_LOCAL_RESTORE:-}" = "OVERWRITE_LOCAL_DATABASES" ] || { echo 'Requires CONFIRM_LOCAL_RESTORE=OVERWRITE_LOCAL_DATABASES; local stack only'; exit 1; }
docker compose exec -T postgres pg_restore -U postgres --clean --if-exists -d auth_db < database/dumps/auth_db.dump
docker compose exec -T postgres pg_restore -U postgres --clean --if-exists -d patient_db < database/dumps/patient_db.dump
docker compose exec -T postgres pg_restore -U postgres --clean --if-exists -d staff_db < database/dumps/staff_db.dump
docker compose exec -T postgres pg_restore -U postgres --clean --if-exists -d appointment_db < database/dumps/appointment_db.dump
docker compose exec -T postgres pg_restore -U postgres --clean --if-exists -d medical_record_db < database/dumps/medical_record_db.dump
docker compose exec -T postgres pg_restore -U postgres --clean --if-exists -d lab_db < database/dumps/lab_db.dump
docker compose exec -T postgres pg_restore -U postgres --clean --if-exists -d pharmacy_db < database/dumps/pharmacy_db.dump
docker compose exec -T postgres pg_restore -U postgres --clean --if-exists -d billing_db < database/dumps/billing_db.dump
docker compose exec -T postgres pg_restore -U postgres --clean --if-exists -d notification_db < database/dumps/notification_db.dump
docker compose exec -T postgres pg_restore -U postgres --clean --if-exists -d reporting_db < database/dumps/reporting_db.dump
