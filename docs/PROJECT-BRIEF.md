# MASTER PROJECT PROMPT
# Hospital & Healthcare Management Platform — Full Stack Microservices, DevSecOps, Kubernetes & AWS

## 1. Project Mission

Act as a Principal Software Architect, Senior Java Engineer, React Engineer, DevOps Engineer, DevSecOps Engineer, QA Automation Engineer, Kubernetes Engineer, and AWS Cloud Architect.

Build a complete, enterprise-style Hospital and Healthcare Management System (HHMS) from scratch using React, Spring Boot microservices, PostgreSQL, Apache Kafka, Redis, Docker, Kubernetes, GitHub Actions, Jenkins, automated testing, and security scanning.

The project must demonstrate the skills required for:

- Java Spring Boot Developer
- React + Java Full-Stack Developer
- Microservices Developer
- DevOps Engineer
- Cloud Engineer
- Platform Engineer
- DevSecOps Engineer

This is a portfolio-grade project intended for technical interviews and job applications. It must be functional, well-tested, secure by design, documented, and reproducible on a local development machine.

**Do not generate only code examples or architecture diagrams. Build an integrated application with working APIs, a functional frontend, database migrations, messaging, tests, deployment pipelines, operational documentation, and reproducible demo data.**

Start with the architecture and implementation plan, then build the application incrementally. Run available checks, fix discovered bugs, and report actual results honestly.

---

# 2. Technology Stack

Use compatible, supported versions and pin dependencies where appropriate.

## Frontend
- React
- TypeScript
- Vite
- React Router
- Axios
- TanStack Query
- Tailwind CSS
- React Hook Form
- Zod
- Recharts
- Vitest
- React Testing Library
- Mock Service Worker (MSW)
- Playwright

## Backend
- Java 21 LTS
- Spring Boot 3.x
- Spring Cloud Gateway
- Spring Security
- Spring Data JPA
- Hibernate
- Spring Kafka
- Spring Data Redis
- Spring Boot Actuator
- Spring Validation
- Springdoc OpenAPI
- Maven
- Flyway
- JUnit 5
- Mockito
- Testcontainers

Use a compatible Spring Cloud release train and verify all dependency versions. Avoid unnecessary dependencies and incompatible versions.

## Database and messaging
- PostgreSQL
- Apache Kafka using KRaft mode
- Redis
- Kafka dead-letter topics
- Transactional outbox pattern
- JSON event schemas with explicit versioning

## DevOps
- Git
- GitHub
- GitHub Actions
- Jenkins
- Docker
- Docker Compose
- Kubernetes
- Helm
- Nginx
- Terraform
- Trivy
- OWASP dependency scanning
- SonarQube or an appropriate alternative
- Prometheus
- Grafana
- Loki or an equivalent log aggregation solution
- OpenTelemetry and a local tracing backend where practical
- k6 for load testing

## Local development
- Docker Desktop or Docker Engine with Compose
- JDK 21
- Node.js LTS
- Maven
- Git
- IntelliJ IDEA or VS Code
- Optional kind or Minikube for local Kubernetes testing

No paid cloud account must be required to run the core application locally.

---

# 3. System Architecture

Build a microservices-based application with a React frontend and an API Gateway.

The frontend must communicate with backend services through the API Gateway. Internal service endpoints must not be exposed directly to the browser.

Use independent service responsibilities, clear API contracts, independent database ownership, asynchronous event processing, and environment-specific configuration.

## Required microservices

### 1. API Gateway
Service: `api-gateway`

Responsibilities:
- Single entry point for frontend APIs.
- Route requests to the correct services.
- Validate authentication tokens where appropriate.
- Apply CORS restrictions.
- Configure request timeouts.
- Implement Redis-backed rate limiting.
- Generate and propagate correlation IDs.
- Expose safe health and metrics endpoints.
- Return consistent error responses.
- Keep internal service endpoints private.

### 2. Authentication and Identity Service
Service: `auth-service`

Responsibilities:
- Login and logout.
- User and account management.
- Password hashing with BCrypt or Argon2.
- JWT access tokens.
- Refresh token rotation and revocation.
- Password reset and account recovery.
- Role-based and permission-based authorization.
- Audit authentication and security events.

Required roles:
- `ADMIN`
- `DOCTOR`
- `NURSE`
- `RECEPTIONIST`
- `PHARMACIST`
- `LAB_TECHNICIAN`
- `BILLING_STAFF`
- `PATIENT`

Use short-lived access tokens and secure refresh-token handling. Never store plaintext passwords or log authentication secrets.

### 3. Patient Service
Service: `patient-service`

Features:
- Patient registration.
- Unique patient number generation.
- Demographic and contact details.
- Emergency contacts.
- Allergy information where appropriate.
- Patient search, pagination and filtering.
- Registration status management.
- Authorized patient record access.
- Patient lifecycle events.

### 4. Staff and Doctor Service
Service: `staff-service`

Features:
- Doctor and staff registration.
- Medical specialties.
- Department management.
- Doctor availability.
- Working schedules and leave.
- Staff profiles.
- Department assignments.
- Staff and availability events.

### 5. Appointment Service
Service: `appointment-service`

Features:
- Appointment booking.
- Appointment rescheduling and cancellation.
- Doctor availability and time-slot management.
- Appointment status tracking.
- Appointment history.
- Search and pagination.
- Reminder scheduling.
- Concurrent booking prevention.
- Kafka appointment events.

Use database transactions and appropriate PostgreSQL constraints to prevent double booking. Redis must not be the sole mechanism for appointment consistency.

### 6. Medical Records Service
Service: `medical-record-service`

Features:
- Patient encounters.
- Clinical notes.
- Diagnoses.
- Treatment plans.
- Vital signs.
- Medical history.
- Record versioning and audit history.
- Authorized access to sensitive records.
- Controlled medical record creation and updates.

Use synthetic medical information only.

### 7. Laboratory Service
Service: `lab-service`

Features:
- Laboratory test catalog.
- Laboratory orders.
- Sample collection and processing.
- Test result entry.
- Reference ranges.
- Result verification.
- Laboratory status tracking.
- Authorized result access.
- Kafka laboratory events.

### 8. Pharmacy and Inventory Service
Service: `pharmacy-service`

Features:
- Medicine catalog.
- Stock management.
- Batch and expiry tracking.
- Stock receipts and adjustments.
- Prescription-linked dispensing.
- Inventory movement history.
- Low-stock alerts.
- Expiry notifications.
- Transaction-safe inventory updates.

### 9. Billing and Payment Service
Service: `billing-service`

Features:
- Invoice generation.
- Invoice line items.
- Taxes and discounts.
- Payment status.
- Outstanding balance tracking.
- Payment history.
- Refund request workflow.
- Invoice PDF generation.
- Simulated local payments.
- Billing audit history.

Do not collect or store real card details. Use a simulated payment provider for local demonstrations.

### 10. Notification Service
Service: `notification-service`

Features:
- In-app notifications.
- Appointment reminders.
- Appointment status notifications.
- Laboratory result notifications.
- Pharmacy stock alerts.
- Email delivery.
- Retry policies.
- Dead-letter handling.
- Duplicate event prevention.
- Notification history.

Use Mailpit for local email testing.

### 11. Reporting and Analytics Service
Service: `reporting-service`

Features:
- Daily and monthly appointment reports.
- Patient registration statistics.
- Doctor utilization.
- Laboratory workload.
- Pharmacy stock and expiry reports.
- Invoice and revenue reports.
- Outstanding balances.
- CSV and PDF exports.
- Kafka-driven read models where appropriate.

Clearly document eventual consistency in analytics data.

---

# 4. Frontend Requirements

Create a complete, responsive React application with separate role-based dashboards.

## Admin dashboard
- System overview.
- Staff and department management.
- User and role management.
- System health summary.
- Audit reports.
- Operational analytics.

## Receptionist dashboard
- Patient registration.
- Patient search.
- Appointment booking.
- Appointment cancellation.
- Appointment rescheduling.
- Daily calendar and availability view.

## Doctor dashboard
- Daily appointment list.
- Authorized patient record access.
- Clinical encounter creation.
- Diagnosis and treatment plans.
- Laboratory orders.
- Appointment completion.

## Nurse dashboard
- Assigned patient list.
- Vital sign recording.
- Relevant observations.
- Authorized care workflow updates.

## Laboratory dashboard
- Pending orders.
- Sample processing.
- Test result entry.
- Result verification.
- Result status tracking.

## Pharmacy dashboard
- Medicine catalog.
- Inventory levels.
- Batch and expiry management.
- Dispensing workflow.
- Low-stock alerts.

## Billing dashboard
- Invoice creation.
- Invoice and payment history.
- Simulated payments.
- Outstanding balances.
- Receipt and PDF downloads.

## Patient portal
- Profile management.
- Appointment history.
- Appointment requests and cancellations according to policy.
- Authorized medical record access.
- Verified laboratory results.
- Invoice history.
- Notifications.

## Frontend quality requirements
- Responsive layouts.
- Accessible forms and navigation.
- Form validation.
- Protected routes.
- Permission-aware navigation.
- Pagination, search and filtering.
- Loading, empty and error states.
- Centralized API client.
- Standardized error handling.
- No fake hardcoded business data in normal application flows.
- Reproducible synthetic demo data.
- Backend authorization for every protected operation.

Frontend role checks must never replace backend security enforcement.

---

# 5. Database Architecture

Use a database-per-service design.

For local development, one PostgreSQL container may host multiple independent databases to simplify setup. Each microservice must use separate database credentials and own its schema.

Required databases:

- `auth_db`
- `patient_db`
- `staff_db`
- `appointment_db`
- `medical_record_db`
- `lab_db`
- `pharmacy_db`
- `billing_db`
- `notification_db`
- `reporting_db`

Requirements:
- Use Flyway migrations.
- Use UUID identifiers.
- Keep service-owned entities within their own databases.
- Do not use cross-service database joins.
- Do not create cross-service foreign keys.
- Use DTOs rather than exposing JPA entities.
- Add indexes for common queries.
- Use pagination for large result sets.
- Enforce business-critical invariants using database constraints.
- Document migration execution and compatibility.
- Use backward-compatible migrations where practical.

Avoid distributed transactions. Use eventual consistency, idempotent consumers and Saga-style workflows where necessary.

---

# 6. Kafka Event-Driven Architecture

Use Apache Kafka in KRaft mode.

Create and document the following topics:

- `patient.registered.v1`
- `patient.updated.v1`
- `staff.created.v1`
- `appointment.booked.v1`
- `appointment.rescheduled.v1`
- `appointment.cancelled.v1`
- `medical-record.created.v1`
- `lab.order-created.v1`
- `lab.result-verified.v1`
- `pharmacy.stock-low.v1`
- `pharmacy.medicine-dispensed.v1`
- `billing.invoice-created.v1`
- `billing.payment-completed.v1`
- `notification.requested.v1`

Each event should include:
- `eventId`
- `eventType`
- `eventVersion`
- `occurredAt`
- `correlationId`
- `aggregateId`
- `payload`

Implement:
- Consumer groups.
- Event schema validation.
- Idempotent event handling.
- Retry policies.
- Dead-letter topics.
- Event correlation.
- Transactional outbox for critical events.
- Safe event payloads that minimize sensitive medical information.
- Documented partition keys and ordering assumptions.

## Required event workflow

1. Receptionist registers a patient.
2. Patient service commits the patient and an outbox record.
3. The outbox publisher sends a patient registration event to Kafka.
4. Receptionist books an appointment.
5. Appointment service validates availability and commits the appointment and its outbox record.
6. Kafka distributes the appointment event.
7. Notification service creates an in-app notification and sends a local email through Mailpit.
8. Reporting service updates its read model.
9. React displays the appointment and relevant notification.

Verify this workflow through automated integration or end-to-end tests.

---

# 7. Redis Requirements

Use Redis for well-defined, non-authoritative purposes.

Implement:
- API Gateway rate limiting.
- Doctor availability caching.
- Department and reference-data caching.
- Selected catalog caching.
- Appropriate temporary idempotency records.
- Secure short-lived recovery tokens if needed.
- Cache TTLs.
- Cache invalidation.
- Cache hit and miss metrics.
- Graceful degradation during Redis outages.

Do not use Redis as the sole source of truth for:
- Appointments.
- Medical records.
- Payments.
- Prescriptions.
- Pharmacy stock.

Avoid caching sensitive medical records by default.

Write tests for cache hits, cache misses, expiration, invalidation and dependency failures.

---

# 8. Environment Profiles and Deployment Isolation

Support these environments:

| Environment | Spring profile | Purpose |
|---|---|---|
| Local | `local` | Developer workstation |
| Development | `dev` | Integration development |
| UAT | `uat` | User acceptance testing |
| Preprod | `preprod` | Production-like release validation |
| Production | `prod` | Live system |

Use the standard term `preprod` consistently.

Create configuration files for every Spring Boot service:

- `application.yml`
- `application-local.yml`
- `application-dev.yml`
- `application-uat.yml`
- `application-preprod.yml`
- `application-prod.yml`

Keep common settings in `application.yml`. Externalize environment-specific settings and all secrets.

Configure:
- Database endpoints.
- Kafka bootstrap servers.
- Redis endpoints.
- CORS origins.
- Service URLs.
- Logging levels.
- Rate limits.
- Cache TTLs.
- Timeouts and retries.
- Observability endpoints.
- Storage configuration.
- Feature flags.

## Local
- Docker Compose.
- Local databases.
- Synthetic demo data.
- Mailpit.
- Local monitoring.
- No paid cloud dependencies.

## Development
- Separate development infrastructure and credentials.
- Frequent deployments.
- Integration testing.
- No production data.

## UAT
- Dedicated deployment and test data.
- Acceptance test scenarios.
- QA sign-off.
- Stable release candidate.
- No production patient data.

## Preprod
- Production-like configuration.
- Same tested image digest intended for Production.
- Migration rehearsal.
- Load and regression testing.
- Security verification.
- Backup/restore validation.
- Rollback rehearsal.

## Production
- Separate production infrastructure.
- Restricted network access.
- Least-privilege service accounts.
- Secure secrets management.
- Appropriate replicas and resource limits.
- Health checks.
- Monitoring and alerts.
- Backups and recovery procedures.
- Explicit release approval.
- No demo accounts or synthetic-data seeding.

Profiles are not security boundaries. Enforce environment isolation through credentials, infrastructure, access controls, namespaces or clusters, network policies and deployment permissions.

---

# 9. GitHub Actions CI/CD

Create:

- `.github/workflows/pr-validation.yml`
- `.github/workflows/backend-tests.yml`
- `.github/workflows/frontend-tests.yml`
- `.github/workflows/security-scan.yml`
- `.github/workflows/docker-build.yml`
- `.github/workflows/deploy-dev.yml`
- `.github/workflows/deploy-uat.yml`
- `.github/workflows/deploy-preprod.yml`
- `.github/workflows/deploy-production.yml`

Use reusable workflows where appropriate.

## Pull request pipeline
1. Validate code style.
2. Compile backend services.
3. Run backend tests.
4. Run frontend linting and tests.
5. Run integration tests.
6. Build the frontend.
7. Build container images.
8. Scan dependencies and images.
9. Validate infrastructure configuration.
10. Publish test, coverage and security reports.
11. Fail required checks when release gates are violated.

## Release pipeline
- Build versioned images.
- Tag with commit SHA and release version.
- Generate an SBOM.
- Push to the configured registry.
- Record immutable image digests.
- Deploy to Development.
- Promote the same tested artifact to UAT.
- Require UAT approval before Preprod.
- Require Preprod validation before Production.
- Require explicit Production approval.
- Run smoke tests after deployment.
- Record deployment metadata.
- Support rollback to a previously approved release.

Use minimal workflow permissions and pin third-party actions to verified commit SHAs.

Use GitHub Environments for deployment approvals and environment-scoped secrets.

Prefer OIDC for AWS authentication.

Never expose production secrets to untrusted pull-request workflows.

---

# 10. Jenkins CI/CD

Create:
- `Jenkinsfile`
- `jenkins/README.md`
- `scripts/jenkins/`
- A custom Jenkins agent Dockerfile only if required.

Implement stages for:
1. Checkout.
2. Backend build.
3. Backend unit tests.
4. Backend integration tests.
5. Frontend lint and tests.
6. Frontend production build.
7. Dependency and security scanning.
8. Docker image build.
9. Image publishing.
10. Deployment approval.
11. Helm deployment.
12. Post-deployment smoke tests.
13. Rollback when appropriate.

Recommended ownership:
- GitHub Actions handles pull-request checks, tests, security gates and artifact verification.
- Jenkins handles controlled release orchestration, deployment approvals and operational release tasks.

Document the integration between the two systems. Avoid duplicate builds, conflicting deployments and competing release versions.

Deploy the exact tested image digest. Never deploy code that has not passed the required checks.

Use Jenkins credentials binding and least-privilege agents. Do not expose credentials in console output.

---

# 11. Docker, Kubernetes and Helm

Create multi-stage Dockerfiles for all backend services and the frontend.

Requirements:
- Non-root runtime users.
- Pinned base images.
- Minimal runtime images.
- No embedded credentials.
- Configurable runtime environment variables.
- Vulnerability scanning.
- Health checks where appropriate.

Create Helm charts under:

`infrastructure/kubernetes/helm/hospital-platform/`

Include:
- `Chart.yaml`
- `values.yaml`
- `values-local.yaml`
- `values-dev.yaml`
- `values-uat.yaml`
- `values-preprod.yaml`
- `values-prod.yaml`
- `templates/`

Support:
- Deployments.
- Services.
- Ingress.
- ConfigMaps.
- Secret references.
- Service accounts.
- RBAC.
- Network policies.
- Resource quotas and limits.
- Liveness and readiness probes.
- Startup probes where appropriate.
- Horizontal Pod Autoscalers where justified.
- Pod disruption budgets where appropriate.
- Metrics scraping configuration.
- Controlled migration Jobs.

Provide local Kubernetes instructions for kind or Minikube.

Validate:
- Helm templates.
- Kubernetes manifests.
- Resource settings.
- Security policies.
- Container references.
- Configuration completeness.

Use separate namespaces or clusters based on the isolation requirements. Do not assume namespaces alone provide complete isolation.

For Production, document when managed PostgreSQL, Redis and Kafka services are preferable to self-managed stateful workloads.

---

# 12. Backend Testing

Implement unit, integration, security and end-to-end tests.

## Backend test tools
- JUnit 5.
- Mockito.
- Spring Boot Test.
- MockMvc or WebTestClient where appropriate.
- Testcontainers.
- REST Assured if useful.

## Test coverage

### Authentication
- Valid login.
- Invalid credentials.
- Expired tokens.
- Revoked refresh tokens.
- Unauthorized and forbidden requests.
- Role-based access control.

### Patients and medical records
- Patient registration and validation.
- Duplicate record handling.
- Sensitive data access restrictions.
- Audit logging.
- Unauthorized updates.

### Appointments
- Valid booking.
- Invalid slot.
- Concurrent double-booking prevention.
- Rescheduling.
- Cancellation.
- Event publication.

### Laboratory
- Valid order transitions.
- Result verification.
- Unauthorized result modification.
- Duplicate event handling.

### Pharmacy
- Inventory updates.
- Expired batch rejection.
- Concurrent stock updates.
- Dispensing permissions.
- Overselling prevention.

### Billing
- Invoice calculations.
- Payment idempotency.
- Duplicate payment prevention.
- Failed payment recovery.
- Refund authorization.

### Kafka and Redis
- Event publication and consumption.
- Outbox retry.
- Duplicate event handling.
- Dead-letter behavior.
- Cache invalidation.
- Cache expiration.
- Redis outage behavior.

### Gateway
- Route correctness.
- Authentication enforcement.
- Rate limiting.
- Error propagation.

Target 80% coverage for relevant business logic as an initial goal, not as a substitute for meaningful tests.

---

# 13. Frontend Testing

Use:
- Vitest.
- React Testing Library.
- MSW.
- Playwright.

Test:
- Login and logout.
- Session expiration.
- Protected routes.
- Role-specific navigation.
- Patient registration.
- Patient search.
- Appointment booking and cancellation.
- Doctor dashboard.
- Medical record forms.
- Laboratory workflows.
- Pharmacy inventory.
- Billing and invoices.
- Validation messages.
- Loading and error states.
- API timeout handling.
- Accessible forms and controls.

## End-to-end scenarios

1. Receptionist logs in.
2. Receptionist registers a synthetic patient.
3. Patient record is retrieved from the backend.
4. Receptionist books an appointment.
5. Appointment is stored in PostgreSQL.
6. Kafka processes the appointment event.
7. Notification service creates an in-app notification.
8. Email appears in Mailpit.
9. Doctor accesses an authorized patient record.
10. Laboratory user enters and verifies a test result.
11. Billing staff creates an invoice.
12. Unauthorized users are blocked from restricted resources.

Use deterministic test data and clean up test records safely.

---

# 14. Security Scanning and Defect Remediation

Integrate:
- Trivy.
- OWASP Dependency-Check or an equivalent scanner.
- Static analysis.
- Secret scanning.
- SonarQube or an equivalent code-quality tool.
- SBOM generation.
- Docker image scanning.
- Kubernetes and Helm configuration scanning.

For each finding:
1. Capture the actual scanner output.
2. Identify the affected component.
3. Classify severity and exploitability.
4. Fix the root cause where possible.
5. Add regression tests.
6. Rebuild affected artifacts.
7. Rerun the relevant scans.
8. Rerun tests.
9. Document unresolved risks and approved exceptions.

Block releases on confirmed critical vulnerabilities and on high-severity findings according to the project's documented policy.

Never suppress findings simply to make a pipeline pass.

Do not claim the system is vulnerability-free without evidence. Document scan coverage, tool versions, findings, fixes and limitations.

---

# 15. Observability and Operations

Implement:
- Spring Boot Actuator.
- Health and readiness checks.
- Prometheus metrics.
- Grafana dashboards.
- Structured logs.
- Loki or equivalent log aggregation.
- Correlation IDs.
- OpenTelemetry traces where practical.
- Kafka consumer lag monitoring.
- Redis cache metrics.
- PostgreSQL connection pool metrics.
- JVM metrics.
- Request latency and error-rate metrics.
- Alerts for critical service failures.
- Backup and restore procedures.

Protect management endpoints and avoid logging secrets or sensitive medical information.

Document troubleshooting and incident response procedures.

---

# 16. Complete Repository Structure

Use a monorepo:

`hospital-healthcare-platform/`

```text
hospital-healthcare-platform/
├── frontend/
├── backend/
│   ├── api-gateway/
│   ├── auth-service/
│   ├── patient-service/
│   ├── staff-service/
│   ├── appointment-service/
│   ├── medical-record-service/
│   ├── lab-service/
│   ├── pharmacy-service/
│   ├── billing-service/
│   ├── notification-service/
│   └── reporting-service/
├── infrastructure/
│   ├── docker/
│   ├── postgres/
│   ├── kafka/
│   ├── redis/
│   ├── nginx/
│   ├── prometheus/
│   ├── grafana/
│   ├── loki/
│   ├── otel/
│   └── kubernetes/
│       └── helm/
├── database/
│   ├── seed/
│   ├── dumps/
│   └── scripts/
├── postman/
├── scripts/
├── jenkins/
├── docs/
├── .github/
│   └── workflows/
├── docker-compose.yml
├── .env.example
├── Makefile
├── README.md
└── AWS-README.md
```

Adjust the structure when technically justified, while keeping ownership and documentation clear.

---

# 17. Root README.md — Full Deployment Guide

Create a comprehensive root `README.md`.

Include:
1. Project overview.
2. Features.
3. Technology stack.
4. Architecture diagram.
5. Microservice responsibility matrix.
6. Database ownership.
7. Kafka topics and event flow.
8. Redis strategy.
9. Repository structure.
10. Prerequisites.
11. Local setup.
12. Spring profile configuration.
13. Environment isolation.
14. Development deployment.
15. UAT deployment.
16. Preprod deployment.
17. Production deployment.
18. Docker and Docker Compose.
19. Kubernetes and Helm.
20. GitHub Actions.
21. Jenkins.
22. Container registry configuration.
23. Database migrations.
24. Automated tests.
25. Security scanning.
26. Postman collection.
27. Synthetic demo data.
28. Database dump and restore.
29. Monitoring and logs.
30. Backup and recovery.
31. Rollback procedures.
32. Troubleshooting.
33. Release checklist.
34. Security limitations.
35. Known issues.

Provide actual commands, expected results and environment-specific examples.

Clearly label destructive operations and require confirmation before production-impacting commands.

---

# 18. Separate AWS-README.md

Create a separate root file named `AWS-README.md`.

It must describe future AWS deployment without making AWS mandatory for local development.

## Reference architecture

Consider:
- Amazon ECR.
- Amazon EKS.
- Amazon RDS for PostgreSQL.
- Amazon ElastiCache for Redis.
- Amazon MSK for Kafka.
- Application Load Balancer.
- Route 53.
- AWS Certificate Manager.
- IAM roles for workloads.
- AWS Secrets Manager or Systems Manager Parameter Store.
- Amazon S3.
- AWS KMS.
- CloudWatch.
- Terraform.

Explain the trade-offs between managed services and self-managed infrastructure.

## AWS instructions

Include:
1. AWS prerequisites.
2. AWS CLI configuration.
3. IAM permissions.
4. Terraform structure.
5. VPC and subnet design.
6. EKS setup.
7. ECR repositories.
8. Image publishing.
9. RDS configuration.
10. Redis configuration.
11. Kafka configuration.
12. Secrets management.
13. DNS and TLS.
14. Helm deployment.
15. GitHub Actions OIDC.
16. Jenkins AWS integration.
17. Smoke tests.
18. Monitoring.
19. Backups.
20. Disaster recovery.
21. Rollback.
22. Cost estimation.
23. Resource cleanup.

Use parameterized infrastructure and placeholders for account IDs, region, cluster names and other operator-specific values.

Do not hardcode AWS credentials or production secrets.

Explain the costs of running EKS, Kafka, Redis and managed databases. Provide a cost-estimation process rather than unsupported exact prices.

Include cleanup procedures to prevent unnecessary ongoing charges.

---

# 19. Postman API Collection

Create a complete importable Postman package.

Required files:

```text
postman/
├── Hospital-Healthcare-API.postman_collection.json
├── Hospital-Healthcare-Local.postman_environment.json
├── Hospital-Healthcare-UAT.postman_environment.json
├── Hospital-Healthcare-Preprod.postman_environment.json
└── Hospital-Healthcare-README.md
```

Organize requests by:
- Authentication.
- Patients.
- Staff.
- Appointments.
- Medical records.
- Laboratory.
- Pharmacy.
- Billing.
- Notifications.
- Reporting.
- Gateway health checks.

For every implemented endpoint, include:
- HTTP method.
- URL.
- Required headers.
- Request body.
- Authentication requirements.
- Expected status code.
- Example response.
- Error cases.
- Postman assertions.
- Reusable environment variables.

Use `{{baseUrl}}`, generated IDs and safe environment variables.

Implement Postman tests for:
- Successful authentication.
- Unauthorized and forbidden requests.
- Patient registration.
- Patient retrieval.
- Appointment booking.
- Duplicate booking prevention.
- Cancellation.
- Laboratory workflow.
- Pharmacy inventory.
- Invoice calculations.
- Payment idempotency.
- Validation failures.

Support execution using Newman.

Never execute destructive tests against Production.

---

# 20. Synthetic Demo Data and Database Dumps

Create a repeatable local demo dataset containing:
- Users and roles.
- Synthetic patients.
- Doctors.
- Departments.
- Appointments.
- Synthetic medical records.
- Laboratory orders and results.
- Pharmacy inventory and batches.
- Invoices and simulated payments.
- Notifications.

Use deterministic IDs and consistent references between services.

Create:
- Versioned SQL seed scripts.
- Database reset scripts.
- Backup scripts.
- Restore scripts.
- Database verification scripts.
- Documentation for importing demo data.

Where appropriate, provide a PostgreSQL dump for local demonstration. If binary dumps are unsuitable for Git, provide reproducible seed scripts and exact dump/restore commands.

Never commit real patient data, production dumps, secrets, tokens or plaintext passwords.

Demo users must be restricted to local development and disabled in other environments.

Do not automatically delete or recreate databases whenever a container starts.

---

# 21. Docker Compose and Local Development

Create a complete local environment containing:
- React frontend.
- API Gateway.
- All backend services.
- PostgreSQL.
- Kafka in KRaft mode.
- Redis.
- Prometheus.
- Grafana.
- Mailpit.
- Optional centralized logging and tracing components.

Requirements:
- Named volumes.
- Internal networks.
- Health checks.
- Startup dependencies.
- Safe shutdown.
- Persistent local data.
- Minimal exposed ports.
- No hardcoded production secrets.
- Resource usage guidance.

Provide:
- `.env.example`
- `docker-compose.yml`
- Optional development override.
- Multi-stage Dockerfiles.
- Nginx configuration.
- Kafka topic initialization.
- Database initialization.
- Service health-check script.
- Makefile commands.

Document:
- Starting the system.
- Checking health.
- Viewing logs.
- Running migrations.
- Importing demo data.
- Running tests.
- Stopping the system.
- Resetting local data safely.

---

# 22. Release Lifecycle

Implement the following controlled release flow:

`Code → Pull Request → Automated Tests → Security Scans → Docker Build → Development → UAT → Preprod → Production`

Required controls:
- Reviewed pull requests.
- Required automated checks.
- Immutable image digests.
- Versioned release metadata.
- UAT acceptance.
- Preprod validation.
- Production approval.
- Post-deployment smoke tests.
- Rollback instructions.
- Database migration compatibility.
- Deployment concurrency controls.

Promote the same tested artifact across environments.

Use backward-compatible database migrations wherever possible. Do not assume an application rollback automatically reverses a database migration.

---

# 23. Final Validation and Bug Fixing

You must execute tests and validate the real project wherever tools and resources are available.

## Backend
- Compile every service.
- Run unit tests.
- Run integration tests.
- Verify migrations.
- Verify Kafka workflows.
- Verify Redis behavior.
- Test security boundaries.
- Fix defects and rerun relevant tests.

## Frontend
- Install dependencies from the lockfile.
- Run linting.
- Run TypeScript checks.
- Run component and unit tests.
- Build the production frontend.
- Run Playwright tests where supported.
- Fix defects and rerun relevant tests.

## Infrastructure
- Validate Docker Compose.
- Build images.
- Start the local stack if resources permit.
- Verify service health.
- Verify Kafka and Redis connectivity.
- Validate Helm charts.
- Validate Kubernetes manifests.
- Run security scans.

## Postman
- Validate collection and environment JSON.
- Run the collection against the local stack.
- Run Newman where available.
- Fix failed requests and assertions.

## Required reports

Create:

- `docs/TEST-REPORT.md`
- `docs/SECURITY-REPORT.md`
- `docs/DEPLOYMENT-VALIDATION.md`

Include:
- Actual commands executed.
- Test totals and failures.
- Coverage results.
- Scanner versions.
- Security findings and fixes.
- Deployment checks.
- Remaining risks.
- Steps not executed and reasons.

Never fabricate successful test results, security scans, coverage or deployment results.

Do not call the project production-ready merely because it compiles.

---

# 24. Implementation Phases

Implement the project incrementally in this order.

### Phase 1: Architecture and foundation
- Architecture diagrams.
- Repository structure.
- Docker Compose.
- PostgreSQL.
- Kafka.
- Redis.
- API Gateway.
- Authentication.
- Health checks.

### Phase 2: Core services
- Patient service.
- Staff service.
- Appointment service.
- React authentication and receptionist dashboard.
- Database migrations.
- Initial tests.

### Phase 3: Event-driven architecture
- Transactional outbox.
- Kafka consumers.
- Notification service.
- Redis caching.
- Reporting projections.
- End-to-end appointment workflow.

### Phase 4: Clinical modules
- Medical records.
- Laboratory.
- Pharmacy.
- Billing.
- Role-based dashboards.
- Business-critical tests.

### Phase 5: Full frontend
- All staff dashboards.
- Patient portal.
- Form validation.
- API integration.
- Frontend automated tests.

### Phase 6: DevOps
- GitHub Actions.
- Jenkins.
- Docker image pipeline.
- Security scanning.
- Artifact versioning.

### Phase 7: Kubernetes
- Helm charts.
- Local Kubernetes deployment.
- Environment-specific values.
- Health checks.
- Deployment and rollback procedures.

### Phase 8: Documentation and test data
- Complete `README.md`.
- Complete `AWS-README.md`.
- Postman collection.
- Synthetic demo data.
- Backup and restore scripts.

### Phase 9: Final validation
- Full feasible test suite.
- Vulnerability scanning.
- Integration testing.
- Infrastructure validation.
- Bug fixing.
- Final reports.

Do not skip earlier phases when later phases depend on them. Do not create all microservices as empty skeletons and claim the project is complete.

---

# 25. Final Deliverables

The completed repository must include:

- A working React frontend.
- Eleven integrated Spring Boot services.
- PostgreSQL database-per-service architecture.
- Kafka event-driven workflows.
- Redis caching and rate limiting.
- Dockerfiles and Docker Compose.
- Environment-specific Spring profiles.
- GitHub Actions pipelines.
- Jenkins pipeline.
- Kubernetes Helm charts.
- Automated backend tests.
- Automated frontend tests.
- End-to-end tests.
- Security scanning.
- Monitoring and logging.
- Root `README.md`.
- Separate `AWS-README.md`.
- Postman collection and environment files.
- Synthetic demo-data scripts.
- Database backup and restore scripts.
- Test report.
- Security report.
- Deployment validation report.
- Release and rollback runbooks.

## Final instruction

Begin by presenting the architecture and a practical implementation plan. Then create the repository and implement Phase 1.

Work directly with the project files, verify every major phase, preserve working code, fix discovered bugs and document any limitations.

When finished, provide a concise summary of the implementation, repository structure, startup commands, test results, security findings, Postman usage, environment deployment instructions and AWS deployment guide.

**Success means a functional, locally runnable, testable and well-documented healthcare platform that demonstrates real Spring Boot microservices, React, Kafka, Redis, Docker, Kubernetes, CI/CD, DevSecOps and cloud deployment skills.**