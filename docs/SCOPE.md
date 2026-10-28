# Implemented scope and release boundaries

This is a runnable hospital architecture-practice project with eleven service executables, a role-based React interface, typed core persistence, real cross-service APIs and Kafka workflows, repeatable synthetic data and automated acceptance scripts. The user-approved framework migration is documented in BOOT-MIGRATION.md. The full original brief is retained in PROJECT-BRIEF.md.

Implemented local workflows and evidence are listed in FEATURE-MATRIX.md and TEST-REPORT.md. Business services have controller/service/repository/DAO/entity/mapper/DTO/event boundaries; the stateless gateway has no fabricated persistence layer. Secondary appointment-request compatibility records retain the shared document adapter, while the principal hospital aggregates have normalized entities and tables.

Remaining boundaries are explicit:

- Native PostgreSQL multi-connection concurrency, real container builds and deployment/load/restore checks require a Docker-capable host. The final local functional run passed 106 gateway requests, plus stock-threshold and per-user read regressions.
- Payments/refunds use a durable simulated provider and settlement ledger. No real card details or external payment provider are used.
- Analytics combines eventual Kafka daily/monthly projections with current operational service reports. It is not a regulated accounting/period-close ledger. PDF exports are bounded single-page summaries, not a paginated medical-document publishing system.
- JWTs use shared HMAC for the demo; access tokens can live ten minutes after logout/role changes. Production identity/key rotation, MFA, consent/retention policies and clinical governance require additional deployment work.
- Events validate the shared envelope and reject sensitive clinical/credential fields; full per-topic payload/schema-registry enforcement remains additional work. Consumer effects are deduplicated, but SMTP may duplicate after a delivery/commit crash.
- Publishing, SBOM and digest-promotion automation is supplied; remote pipelines and protected approvals need the owner's credentials/configuration. Helm expects external stateful dependencies. Terraform provides ECR, while full VPC/EKS/RDS/MSK provisioning is not implemented.
- Prometheus/Grafana and structured correlation logs are supplied. A complete Loki/OTel/lag-exporter/alert-routing stack is not installed. Configuration scans retain medium/low policy findings.

Use synthetic records for practice. This archive is not a certified clinical deployment or a claim that every infrastructure requirement has been provisioned and production-tested.
