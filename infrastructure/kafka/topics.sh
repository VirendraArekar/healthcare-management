#!/bin/bash
set -euo pipefail
for topic in patient.registered.v1 patient.registered.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
for topic in patient.updated.v1 patient.updated.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
for topic in staff.created.v1 staff.created.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
for topic in appointment.booked.v1 appointment.booked.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
for topic in appointment.rescheduled.v1 appointment.rescheduled.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
for topic in appointment.cancelled.v1 appointment.cancelled.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
for topic in medical-record.created.v1 medical-record.created.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
for topic in lab.order-created.v1 lab.order-created.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
for topic in lab.result-verified.v1 lab.result-verified.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
for topic in pharmacy.stock-low.v1 pharmacy.stock-low.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
for topic in pharmacy.medicine-dispensed.v1 pharmacy.medicine-dispensed.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
for topic in billing.invoice-created.v1 billing.invoice-created.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
for topic in billing.payment-completed.v1 billing.payment-completed.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
for topic in notification.requested.v1 notification.requested.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done

for topic in staff.leave-approved.v1 staff.leave-approved.v1.DLT billing.refund-completed.v1 billing.refund-completed.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done

for topic in auth.security-event.v1 auth.security-event.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done

for topic in pharmacy.stock-expiring.v1 pharmacy.stock-expiring.v1.DLT; do /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic "$topic" --partitions 3 --replication-factor 1; done
