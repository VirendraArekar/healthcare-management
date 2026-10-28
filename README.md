# Harbor Health — Hospital & Healthcare Platform
A Java 21 / Spring Boot 4.0 monorepo with eleven executable services, React/TypeScript care workspace, PostgreSQL database ownership, Kafka outbox workflows, Redis, Docker Compose and Helm delivery assets.

**A structured architecture-practice project with integrated hospital workflows, typed domain persistence and a violet/pink React interface.** Review [scope and limitations](docs/SCOPE.md), [test evidence](docs/TEST-REPORT.md), [security report](docs/SECURITY-REPORT.md) and [deployment validation](docs/DEPLOYMENT-VALIDATION.md). Real health information must not be entered.

## Quick start
Prerequisites: Docker Engine/Desktop with Compose v2, at least 8 GB available RAM (12 GB recommended), Python 3. For host development: Java 21, Maven 3.9, Node 24 LTS. The validation host used JDK 25 targeting Java 21.

```sh
./scripts/init-local.sh
# Read .env privately. It contains generated DEMO_PASSWORD and GRAFANA_PASSWORD.
docker compose up --build -d
# Initial Maven/npm downloads and eleven JVM startups can take several minutes.
docker compose ps
docker compose logs --tail=100 auth-service appointment-service api-gateway
./scripts/health.sh
./scripts/seed-local.sh  # only after all services finish their Flyway migrations
```
Open [application](http://localhost:3000), [Mailpit](http://localhost:8025), [Grafana](http://localhost:3001), [Prometheus](http://localhost:9090). Login as `admin@demo.hospital` or another role account using DEMO_PASSWORD from `.env`. Other names: `doctor`, `nurse`, `receptionist`, `pharmacist`, `lab_technician`, `billing_staff`, `patient`. Demo users exist only with `local` profile and explicit `DEMO_ENABLED=true`. Production profiles disable seeding.

Register a patient, assign an enabled doctor account, create a working schedule and publish a future slot within that schedule. Book through the receptionist page, then open notifications and Mailpit. The deterministic seed supplies two patients, two doctors, a nurse, weekly working schedules, departments, a booked appointment, care assignments, encounter, numeric vital signs, prescription, test/medicine catalogues, pending and verified lab orders, inventory, paid/unpaid invoices and outbox events for analytics. Patient account mapping is applied by the explicit seed script. Re-login after seeding so the doctor receives its staffId claim. Non-demo doctor accounts must be linked to their registered staff UUID through the account form. The `lab_technician2` account can verify a result entered by `lab_technician`; `doctor2` and `patient2` demonstrate access isolation.

## Source structure
See [maintained package tree](docs/PROJECT-STRUCTURE.md), [revision changes](docs/CHANGELOG.md) and [feature traceability](docs/FEATURE-MATRIX.md). Backend requests flow controller → service → repository; JPA/Hibernate owns typed domain aggregates and explicit SQL adapters own locking and constrained transactional tables. Events, audit, security, configuration and scheduled work have separate responsibilities. React is split into feature pages, reusable components, routes, API modules, hooks, types, config and store.

Run `python3 scripts/check-architecture.py` to check those boundaries.

## Architecture and ownership
See [architecture](docs/architecture.md).

| Service | Browser API | Owned database | Responsibility |
|---|---|---|---|
| api-gateway | /api/** | none | Token verification, routing, correlation, Redis limiter |
| auth-service | /api/auth | auth_db | BCrypt login, access JWT, refresh rotation, accounts |
| patient-service | /api/patients | patient_db | Registration, search, unique number, versioned update |
| staff-service | /api/staff, /api/departments | staff_db | Profiles, departments, cached reference data |
| appointment-service | /api/appointments | appointment_db | Availability slots, constrained booking, reschedule, cancel |
| medical-record-service | /api/medical-records | medical_record_db | Encounters, record history, observations |
| lab-service | /api/lab | lab_db | Ordered → collected → processing → result entered → verified |
| pharmacy-service | /api/pharmacy | pharmacy_db | Batch receipts, expiry checks, atomic dispensing |
| billing-service | /api/billing | billing_db | Decimal invoices, idempotent simulated payments, refund request/decision/settlement, PDF |
| notification-service | /api/notifications | notification_db | Kafka projections, durable local email jobs |
| reporting-service | /api/reports | reporting_db | Daily/monthly projections, operational reports, CSV/PDF export |

The frontend uses only same-origin `/api` through Nginx and the gateway. Domain services have no host port mappings. Gateway's localhost:8080 binding supports API development and health checks. One local PostgreSQL container hosts ten databases with separate role ownership. Local passwords share one generated value for convenience; non-local deployments require per-service secrets.

Critical writes and outbox inserts share a database transaction. Publisher locks pending events with SKIP LOCKED and waits for Kafka acknowledgement; a crash after sending can redeliver. Consumers insert event IDs and their projections in the same database transaction. Delivery is at least once, effects are deduplicated. Email sending may still duplicate if a worker crashes after SMTP acceptance and before its transaction commits. Counts are eventually consistent, not financial balances.

Booking constraints prevent overlapping doctor slots and two active bookings for a slot. Inventory updates use `quantity >= requested` and expiry predicates in PostgreSQL. Payments lock the invoice row and use unique invoice/idempotency constraints. Redis never owns those invariants.

## Repository
`frontend/` contains role navigation, forms, tables and API client. `backend/` contains the eleven executables and a shared security/outbox library. `infrastructure/` contains Compose dependencies, Nginx, Prometheus/Grafana, Helm and initial ECR Terraform. `database/` contains repeatable seeds and backup scripts. `postman/` contains collection/environments. `scripts/` contains setup, smoke, health and release validation. `docs/` contains design, evidence and runbooks.

## Build and test
```sh
mvn -B verify
cd frontend
npm ci
npm run lint
npm test
npm run build
# Running local stack and browser binaries required:
npx playwright install chromium
DEMO_PASSWORD='your-local-password' npm run test:e2e
cd ..
DEMO_PASSWORD='your-local-password' python3 scripts/smoke.py
```
Testcontainers booking test requires Docker; it reports skipped when unavailable. Do not treat skipped integration tests as passes. Test evidence is recorded in docs/TEST-REPORT.md. No 80% coverage claim is made.

For host Vite development, run `cd frontend && npm ci && npm run dev`; the proxy targets localhost:8080. For individual Java services set DB_URL, DB_USER, DB_PASSWORD, JWT_SECRET, KAFKA_BOOTSTRAP_SERVERS and REDIS_HOST. Container-only service DNS requires port-forwarding or a Compose override for host development.

## Events and Redis
Topics and DLTs are created by `kafka-init`; see `infrastructure/kafka/topics.sh` and `event-v1.schema.json`. Aggregate UUID is the partition key; ordering is per partition only. Pending outbox delivery retries on the scheduled interval. Consumers retry three times then publish to `.DLT`; inspect/fix and replay with a controlled operator procedure, preserving event IDs. Published payloads contain identifiers and event metadata, not notes or diagnoses.

Redis backs the gateway limiter and five-minute department cache. A department write invalidates the cache. Department reads fall back to PostgreSQL on Redis connection failures. Gateway rate limiter follows Spring Cloud's Redis implementation; do not assume a tested fail-open policy. Availability and selected catalogues also use bounded reference caches. Cache metrics and hit/miss/TTL-argument/invalidation/outage unit tests are included. Actual Redis expiration and gateway outage behavior still require integration checks.

## Profiles and environment isolation
Every executable includes `local`, `dev`, `uat`, `preprod`, `prod` YAML profiles. Common endpoints and secrets are environment variables. Profiles alone do not isolate environments: use separate DB credentials, data services, namespaces/clusters, network policy, workload identities and deployment permissions. No demo data outside local. Back up before migration; Flyway executes versioned SQL at app startup in local Compose. Non-local Helm values enable pre-install/pre-upgrade migration Jobs and disable application startup migration; the same tested application image runs the separate MigrateMain process. Checksums protect previously applied migrations.

## Docker and operations
```sh
docker compose logs -f --tail=100 appointment-service notification-service
docker compose stop          # preserve data
docker compose down          # preserve named volumes
./database/scripts/backup.sh
CONFIRM_LOCAL_RESTORE=OVERWRITE_LOCAL_DATABASES ./database/scripts/restore.sh
./database/scripts/verify.sh
# DESTRUCTIVE: permanently deletes local named volumes
CONFIRM_LOCAL_RESET=DELETE_LOCAL_DEMO_DATA ./scripts/reset-local.sh
```
The reset/restore scripts target this local Compose project only. Never adapt them to production without review. Each backend Dockerfile is a multistage Java 21 build with non-root runtime; frontend uses non-root Nginx. Tagged versions are pinned but base image digests still need registry resolution and scanning.

Health/readiness/liveness endpoints are public and return no dependency details. Other actuator endpoints require ADMIN in domain services. Logs use structured JSON and correlation IDs; request bodies and tokens are not explicitly logged. Set up authenticated Prometheus scraping before expecting application metrics (see infrastructure/prometheus/README.md). Grafana includes JVM and HTTP panels. Loki, OTel collectors, consumer-lag exporters and alert routing are not implemented.

## Kubernetes / Helm
The chart deploys application workloads; it does **not** provision PostgreSQL, Kafka, Redis or Mailpit. Provision those first in reachable networks and create each DB/owner using your secrets process.
```sh
helm lint infrastructure/kubernetes/helm/hospital-platform
helm template hospital infrastructure/kubernetes/helm/hospital-platform -f infrastructure/kubernetes/helm/hospital-platform/values-local.yaml > /tmp/hospital.yaml
# kind create cluster --name hospital  # optional local cluster
# Build/load your 12 images with kind load docker-image, then set images.* in a private values file.
# Create hospital-local namespace and hospital-local-secrets via your secret manager.
helm upgrade --install hospital infrastructure/kubernetes/helm/hospital-platform --namespace hospital-local --create-namespace -f infrastructure/kubernetes/helm/hospital-platform/values-local.yaml -f /path/to/private-runtime-values.yaml --wait --timeout 10m
```
Secrets require JWT_SECRET and `<service>-db-password` keys for all ten services. Set databaseHost/kafkaBootstrapServers/redisHost/mailHost/corsOrigin. Ingress uses a pre-created hospital-tls secret and an ingress-nginx namespace. Local port-forward requires an additional NetworkPolicy allowing the chosen access route. Do not expose internal domain services via LoadBalancer.

For dev, uat, preprod and prod choose the corresponding `values-*.yaml` and namespace `hospital-<profile>`. Supply immutable `images` in a reviewed release JSON. Production uses external managed databases/Redis/Kafka where feasible, private networking, distinct secrets and workload identities. Review [release runbook](docs/RELEASE-RUNBOOK.md) and [AWS guide](AWS-README.md). Helm includes dedicated migration jobs, optional HPA and quotas. Complete egress policy, managed secret integration and backup jobs still require deployment-specific implementation and cluster validation.

## CI/CD and registry
GitHub PR workflows compile/test backend, lint/test/build frontend, scan source/secrets/config/dependencies and build Compose images. Third-party checkout/setup actions are commit pinned. Docker scanner image is version pinned. Scans fail on high/critical findings; triage them, never suppress to force green.

Deployment workflows require a self-hosted runner with Helm, cluster access and configured GitHub Environments. Configure required reviewers for UAT, preprod and production; no repository file can enforce these account settings. `releases/approved.json` must provide all 12 `images` as `registry/name@sha256:<64 hex>` and be reviewed with passing CI evidence. Validator verifies format/completeness, not provenance. Registry publishing, SBOM attestation and automatic gated promotion are not complete. Jenkins delegates checks to GitHub and deploys the same operator-approved digests. Choose a single deployment owner per environment.

## Troubleshooting and known issues
- A frontend 502 usually means gateway DNS/startup; inspect Nginx and gateway logs.
- A 503/unready domain service: inspect database credentials, Flyway migration and Kafka bootstrap DNS.
- No notifications: inspect outbox pending rows, Kafka topics/DLTs, consumer logs and consumed_events.
- Mailpit empty: inspect email_jobs attempts and SMTP host/port.
- Booking 409: slot already booked or overlapping availability. Refresh available slots.
- Lab verification 400: invalid transition or same technician as result entry.
- Payment retry: reuse the same Idempotency-Key for the same invoice; a different invoice cannot reuse it.
- A page reload requires reauthentication because tokens intentionally stay in memory.
- Prometheus targets down: configure authenticated scraping; no management bypass is provided.
- First-time database init executes once per volume. Changing .env passwords will not alter existing DB role passwords.

## Release checklist and security limits
Require reviewed code, passing required checks, current scans, immutable digests, restore rehearsal, migration compatibility, UAT acceptance, preprod load/regression validation, explicit production approval and post-deploy smoke tests. Care assignments and password recovery are now included. Full payload schema enforcement, extended audit export, observability integration and remaining advanced workflows are tracked in docs/SCOPE.md. No HIPAA/GDPR certification or production readiness is claimed.

## Developer and UI
The interface uses violet and pink variations, responsive role navigation and `react-icons` SVG icons. Each workspace page and the sign-in page includes **Developed by Virendra Arekar · 8483988837**. This is a React web application; React Native icon packages are intended for a different runtime.

## Extended validation
After starting and seeding the local stack, export `DEMO_PASSWORD` privately from your own `.env`, then run `BASE_URL=http://localhost:3000 python3 scripts/acceptance.py`. This creates synthetic records and exercises the integrated workflows. Run it only against a disposable local demo environment.

`tests/database/` contains an additional isolated PostgreSQL/WASM migration and repeatable-seed validation suite (`npm ci && npm test`). It supplements native PostgreSQL/Testcontainers checks and does not establish multi-connection locking correctness. See the test report for the exact environment used.

See [framework migration](docs/BOOT-MIGRATION.md) for the user-approved upgrade to Boot 4/Spring 7. Final verification passed 106 gateway requests across 11 workflow groups; native deployment and performance gates remain in the reports.
