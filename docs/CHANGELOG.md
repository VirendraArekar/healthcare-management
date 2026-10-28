# Structured workflow revision

- Split business persistence contracts into `repository` interfaces, SQL `dao` implementations and typed Spring Data adapters under `repository/jpa`.
- Added normalized domain entities, mappers and incremental legacy backfill migrations instead of storing the main hospital aggregates as generic documents.
- Applied care-assignment filtering before pagination/counts, authoritative clinician-role validation and minimal reference selectors.
- Enforced working schedules/approved leave for appointments, added leave decisions and an idempotent Kafka cancellation saga, preserved occupied completed slots and rejected early completion.
- Added structured vital signs, prescription lists, sample collection metadata, laboratory transition history and second-technician verification.
- Added separate refund request, approval/rejection and durable idempotent simulated settlement.
- Added per-user notification read state and audience filtering before pagination, configurable low-stock levels and deduplicated expiry monitoring.
- Added operational reporting endpoints for utilization, inventory, expiry and balances alongside Kafka daily/monthly read models.
- Added editable catalogue records and versioned account role/status/profile bindings.
- Rebuilt the interface in violet/pink, added React SVG icons, brand imagery, responsive navigation, readable table cells, patient care/edit actions, clinical and billing forms, health/audit panels, and the developer footer.
- Added PostgreSQL/WASM migration/seed tests, an isolated real Kafka test launcher, gateway acceptance checks and a URL-encoding regression test.

See TEST-REPORT and SECURITY-REPORT for executed results and remaining release gates.
