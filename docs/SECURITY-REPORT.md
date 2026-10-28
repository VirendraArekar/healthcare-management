# Final security evidence — 27 October 2026

Trivy 0.76.1 scanned the resolved CycloneDX runtime SBOM containing 205 components: **zero known vulnerabilities reported**. npm audit of the current frontend lockfile also reported **zero vulnerabilities**. This is point-in-time dependency evidence, not a complete application or container security certification.

The initial Boot 3/Spring 6 graph had four critical Spring advisories after compatible patch remediation. With user authorization, the platform moved to Boot 4.0.9, Spring 7.0.10, Cloud 2025.1.4 and Jackson 3.1.7. Final runtime archives were checked for the resolved patched libraries and exclude JUnit/Testcontainers runtime dependencies. See BOOT-MIGRATION.md.

Configuration/secret scanning reports **0 high, 0 critical and 0 detected secrets**. There are **12 medium** trusted-registry policy findings and **14 low** findings: twelve ECR customer-managed-key policy checks and two unprivileged Nginx UID/GID checks. These are retained in the report; they were not suppressed. ECR uses AES256 encryption and the upstream Nginx image uses UID/GID 101. Cluster policy, private registries/digests and KMS choices require environment-specific configuration. An online source scan hit Maven Central rate limiting; the final configuration/secret scan ran offline, and vulnerability findings came from the fully resolved runtime SBOM rather than an incomplete POM graph.

```sh
mvn -B -ntp org.cyclonedx:cyclonedx-maven-plugin:2.9.1:makeAggregateBom -DincludeTestScope=false
trivy sbom --format json --output runtime-vulnerabilities.json target/bom.json
trivy fs --offline-scan --scanners secret,misconfig --skip-dirs node_modules --skip-dirs target --skip-files .env .
cd frontend && npm audit --json
```

Live checks verified backend roles/ownership/care assignment, refresh replay protection, single-use SMTP recovery, account disable/version checks, independent lab verification, prescription caps and refund separation. Workflow events reject credential and clinical-note/result fields. Account API DTOs omit password hashes. Secrets and access/refresh tokens are excluded from source archives; browser tokens remain in memory.

Remaining release work: scan the actual OCI images/base OS layers; run native concurrency and application security testing; configure TLS, per-service production secrets, retention/consent/audit policies, backup/restore and monitoring. Demonstration JWT signing uses a shared HMAC secret and ten-minute access tokens. Logout/role changes revoke refresh tokens; issued access tokens may remain valid until expiry. Asymmetric key rotation, MFA and external identity enrollment are not implemented. Envelope checks are not a schema registry or full per-topic payload contract validator. SMTP delivery is at least once and can duplicate after a crash.

Evidence files: runtime-sbom.cdx.json, runtime-vulnerabilities.json, npm-audit.json and configuration-scan.json under evidence/.
