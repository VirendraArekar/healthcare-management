# Final verification — 27 October 2026

## Results

| Check | Actual result |
|---|---|
| Java 21-target Spring Boot 4 reactor | All eleven executable services and shared module compile/package |
| Final Surefire reports | 40 tests: 39 passed, 0 failures/errors, 1 skipped |
| Frontend production build / TypeScript | Passed |
| ESLint | Passed |
| Vitest / Testing Library / MSW | 8 tests passed across 3 files; no unhandled errors |
| PostgreSQL/WASM migration and repeatable-seed checks | 13 tests passed, covering all ten service schemas |
| Live gateway acceptance | 106 requests, all 11 workflow groups passed |
| Configurable reorder regression | Passed: quantity 25 is included below catalogue threshold 30 |
| Per-user notification reads | Passed: administrator read does not mark the patient's notification read |
| Service startup | All eleven application liveness endpoints healthy |
| Runtime dependency SBOM | 205 resolved components scanned; zero reported vulnerabilities |
| npm audit | Zero reported vulnerabilities |
| Docker Compose configuration | Parsed successfully |
| Helm | Five environment profiles linted and rendered successfully |

The full Boot 4 reactor run contains 39 test cases. The separately executed staff ownership regression adds one case; the final XML aggregate above includes both. The pharmacy module was rebuilt/tested after correcting threshold reporting. Runtime acceptance used the frozen built application artifacts; the targeted stock-report regression exercised the corrected pharmacy service directly with its normal bearer authorization.

## Executed commands

```sh
mvn -B -ntp clean package
mvn -B -ntp -pl backend/staff-service -am package
mvn -B -ntp -pl backend/pharmacy-service -am package
python3 scripts/check-architecture.py
cd frontend && npm run build && npm run lint && npm test
cd tests/database && npm ci && npm test
BASE_URL=http://localhost:3000 MAILPIT_API=http://localhost:8025 python3 scripts/acceptance.py
BASE_URL=http://localhost:3000 python3 scripts/verify-reorder.py
BASE_URL=http://localhost:3000 python3 scripts/verify-read-state.py
```

Validation used a workspace-local Maven dependency cache because the default user cache was read-only in the sandbox. Java 25 compiled release 21 bytecode. Test API ports were isolated loopback ports rather than the normal Compose ports shown above. Passwords came privately from a generated local .env and are absent from the archive.

## Functional coverage

The 106 gateway requests cover eleven identities across eight roles; anonymous/role rejection; invalid login; refresh rotation/reuse/logout; patient-owned portal data, verified-only lab results and contact edits; SMTP recovery and reset-token single use; versioned account disable/conflicts; patient registration/update/version conflicts; clinician role validation and assigned-care isolation; encounters/history/numeric vitals; working schedules, duplicate bookings and Kafka leave cancellation; patient appointment requests/approval/reschedule/cancel and early-completion prevention; catalogues/prescriptions/stock and cumulative dispensing caps; sample collection, lab history and independent verification; itemized decimal invoices, payment retries, PDFs and approved idempotent refund settlement; notifications, daily/monthly reports, utilization, stock/expiry/balance reports, audit fan-out and CSV/PDF exports.

SQL checks execute every migration in version order and applicable seeds twice. They also verify doctor-slot overlap and duplicate booking rejection, negative stock prevention, prescription cap updates and consumed-event uniqueness. This is real PostgreSQL SQL executing in PGlite/WASM, not H2; it does not establish native multi-connection locking correctness.

## Browser and infrastructure limits

The in-app browser verified sign-in, live dashboard data, patient listing/registration controls and the requested developer footer; screenshots accompany the deliverable. The supplied Playwright scenario could not launch headless Chromium because macOS denied Mach-port registration. Do not count it as a passed Playwright run.

Docker Desktop/Engine was unavailable. Native Postgres initialization was blocked by sandbox shared-memory permissions. Therefore container builds/startup, the native PostgreSQL Testcontainers booking test, native concurrent-writer tests, Kubernetes apply, remote CI/CD, Terraform apply, load testing and restore rehearsals were not executed here. The native booking test is the one skipped test. No code-coverage percentage is claimed.

An initial cold acceptance attempt timed out while publishing a slot on this constrained host. Reference/appointment endpoints were warmed and the complete subsequent 106-request run passed; these results are functional evidence, not a latency or capacity guarantee. A URL double-encoding defect, test fixture identities, missing frontend mock handlers, staff ownership checks, inventory threshold reporting and stale dependency packaging were corrected during validation.

Raw logs, final test XML and scanner JSON are in evidence/. Re-run the native Compose/Testcontainers and load checks on a Docker-capable host before deployment.
