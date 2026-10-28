# Feature traceability

Verification labels refer to evidence in TEST-REPORT.md; they do not imply exhaustive path or load coverage.

| Feature area | Implementation | Verification |
|---|---|---|
| Login, JWT, eight roles, alternate accounts | auth controller/service/DAO/entity + auth pages/store | Gateway identities/role denial and JUnit |
| Refresh rotation/replay/logout | hashed refresh families and token service | Gateway and JUnit |
| Account creation, role/status/profile binding, version conflicts | AuthService/DAO/UserAccount DTO; administration page | Gateway account disable/conflict and safe DTOs |
| Recovery and password reset | AccountRecoveryService and recovery form | Real Mailpit delivery and single-use reset |
| Patient registration/search/contact updates | patient typed profile layers and patient page | Gateway persistence/conflicts and UI |
| Care assignment and ownership | account role authority, assigned-ID filtering before pagination | Gateway clinician/other-patient denial; JUnit |
| Staff and departments | staff typed profiles/departments; catalogue edit forms | Compile, seeded UI, cache tests and gateway staff creation |
| Working schedules and leave | typed schedules/leave, authority checks and decisions | Gateway availability/leave; ownership regression JUnit |
| Slot publication and booking | appointment controller/service/DAO/entities; slot controls | Gateway publication/duplicate conflict; SQL constraints |
| Request/approve/reschedule/cancel | request adapter and appointment workflow pages | Gateway full sequence |
| Doctor scope and completion | staff binding, care check and start-time guard | Gateway scope/early-completion rejection; JUnit |
| Daily calendar / reminders | calendar page and reminder scheduler | UI/source; reminder timing/failure cases require native UAT |
| Encounters, diagnoses, notes, treatment | normalized encounters, mappers, clinical panel | Gateway create/update/history and care isolation |
| Structured vital signs | typed numeric entity and validated DTO/form | Gateway numeric observations |
| Prescriptions and patient lists | typed prescription entity, dedicated page and authorized reference | Gateway patient portal and dispensing linkage |
| Lab catalogue/order/sample/status history | typed tests/orders, sample metadata/history tables | Gateway state chain and history |
| Independent result verification | author/verifier identity guard and verified-only patient query | Gateway two-technician tests and JUnit |
| Medicine/batch/expiry/receipt/adjustments | medicine/batch entities, stock DAO and movement history | Gateway receipts/dispensing, SQL stock invariants; adjustment edge cases need expanded UAT |
| Prescription and cumulative stock limits | guarded updates and prescription reservation | Gateway over-limit/valid/cumulative cases; JUnit/SQL |
| Reorder and expiry monitoring | configured catalogue levels, alerts, daily deduplication claims | Separate live threshold regression; scheduler timing/DLT cases need native UAT |
| Itemized invoice/tax/discount | normalized invoices/lines, decimal utility, InvoiceEditor | Gateway exact total and JUnit math |
| Payments/history/balances/PDF | row lock, unique idempotency ledger, patient billing view | Gateway payment retry/PDF/balance reports |
| Refund request/decision/settlement | typed refund entity and durable simulated settlement | Gateway approval-before-settlement and idempotent retry |
| In-app notifications and role/owner scope | typed notifications, audience DAO, UI badge | Kafka gateway notifications and patient portal |
| Per-user read history | unique notification_reads per user | Separate live two-user regression |
| SMTP retries/failure UI | durable mail jobs, worker and administrative retry endpoints | Recovery SMTP passed; delivery-crash/DLT replay cases require native UAT |
| Reporting | daily/monthly Kafka projections, current utilization/inventory/expiry/balances | Gateway reporting/CSV/PDF and live Kafka |
| Audit and health | per-service audit API, role guards, health fan-out and admin panels | Gateway audit/health endpoints; eleven liveness checks |
| Redis and rate limiter | reference TTL cache/invalidation, gateway Redis limiter | Cache JUnit and real runtime; outage/load behavior not certified |
| Violet/pink UI, SVG icons, images, developer footer | feature pages/components/images/store + responsive styles | TypeScript/build/lint, 8 UI tests and in-app browser |
| Database ownership/migrations/seeds | ten database roles, versioned SQL and normalized backfills | 13 SQL tests, repeatable seeds and real persistence |
| Docker / Kubernetes | twelve Dockerfiles, Compose and five Helm environments | Configuration/lint/template checks; native deployment not executed |
| GitHub/Jenkins/release lifecycle | tests/scans, image/SBOM/digest workflow, protected promotion scripts | Source/configuration checked; remote execution not performed |
| AWS / operations | AWS guide, ECR Terraform, backup/restore and release runbooks | Documentation/configuration; full cloud provisioning remains external |

Remaining production and infrastructure boundaries are in SCOPE.md and DEPLOYMENT-VALIDATION.md.
