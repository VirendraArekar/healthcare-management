# Deployment validation

| Area | Executed | Remaining environment validation |
|---|---|---|
| Compose | docker compose config --quiet succeeded | Build all twelve images and start the native dependency stack |
| Application startup | Eleven real JVM services healthy on isolated loopback ports | Repeat with shipped Java 21 OCI images |
| Databases | Ten isolated PostgreSQL/WASM engines; schema and Hibernate validation; normalized records persisted | Native PostgreSQL 17 Flyway execution, concurrent transactions, backup/restore rehearsal |
| Messaging | Real KRaft Kafka, outbox delivery, consumer projections and leave cancellation exercised | Broker failure/rebalance/DLT replay, production TLS/SASL and retention |
| Redis / SMTP | Real local Redis and Mailpit; caching paths and real recovery email used | Distributed cache races, outages and production transport/authentication |
| Helm | local/dev/uat/preprod/prod lint and template succeeded | kind/EKS apply, migration Jobs, ingress/TLS, NetworkPolicy/HPA/PDB and rollback |
| CI / release | Reviewed configuration supplied; release script Python compiles | Remote GitHub/Jenkins execution and protected environment/registry credentials |
| AWS | Guide and ECR Terraform supplied | Full customer infrastructure provisioning and disaster recovery validation |

The isolated integration harness disables application Flyway only because it applies the same migration SQL to PGlite before startup; production and Compose keep Flyway enabled. It does not substitute PGlite into shipped deployment configuration. PGlite's wire multiplexer is not native PostgreSQL and is unsuitable evidence for concurrent locking or load claims.

The image-release workflow builds all twelve images, gates vulnerabilities, exports CycloneDX SBOMs, publishes immutable digests and retains values/metadata. Deployment workflows validate a reviewed digest manifest and use protected environments with Helm --atomic. Nothing was published or deployed to a remote account in this task. Self-hosted release agents require Docker, Trivy, Helm, Python and authenticated registry/cluster access.

Helm deploys application workloads and expects managed/external PostgreSQL, Kafka, Redis, mail and secrets. Terraform currently provisions ECR repositories; it is not a turnkey VPC/EKS/RDS/MSK module. Prometheus/Grafana configuration exists; authenticated metrics credentials and a complete logging/tracing/alerting installation remain operator work. See AWS-README.md and RELEASE-RUNBOOK.md.
