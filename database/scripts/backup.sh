#!/bin/sh
set -eu
cd "$(dirname "$0")/../.."
mkdir -p database/dumps
umask 077
docker compose exec -T postgres pg_dump -U postgres -Fc auth_db > database/dumps/auth_db.dump
docker compose exec -T postgres pg_dump -U postgres -Fc patient_db > database/dumps/patient_db.dump
docker compose exec -T postgres pg_dump -U postgres -Fc staff_db > database/dumps/staff_db.dump
docker compose exec -T postgres pg_dump -U postgres -Fc appointment_db > database/dumps/appointment_db.dump
docker compose exec -T postgres pg_dump -U postgres -Fc medical_record_db > database/dumps/medical_record_db.dump
docker compose exec -T postgres pg_dump -U postgres -Fc lab_db > database/dumps/lab_db.dump
docker compose exec -T postgres pg_dump -U postgres -Fc pharmacy_db > database/dumps/pharmacy_db.dump
docker compose exec -T postgres pg_dump -U postgres -Fc billing_db > database/dumps/billing_db.dump
docker compose exec -T postgres pg_dump -U postgres -Fc notification_db > database/dumps/notification_db.dump
docker compose exec -T postgres pg_dump -U postgres -Fc reporting_db > database/dumps/reporting_db.dump
