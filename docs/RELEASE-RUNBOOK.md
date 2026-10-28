# Release, rollback and incident runbook
1. Review PR and require backend/frontend/image/security checks. Save commit and test/scan evidence.
2. Build/push images to your registry after checks; resolve immutable digests and scan every image. Generate/sign SBOM and provenance with your registry tooling. This publication automation is not supplied.
3. Write reviewed releases/<version>.json with all twelve images. `python3 scripts/validate-release.py releases/<version>.json` checks digest shape and completeness only.
4. Deploy dev with the matching profile and private values. Run synthetic smoke in an isolated environment. The provided smoke script deliberately restricts mutation testing to localhost.
5. Promote exact digests to UAT with QA acceptance, then preprod with migration, restore, regression, load and rollback rehearsal. Record evidence separately. Require explicit production approval in GitHub Environments or Jenkins input, with environment-scoped credentials.
6. Deploy with Helm --atomic --wait. Run environment-specific non-destructive health/auth/read checks. Watch latency, errors, DB pools and consumer lag.

Rollback application: `helm history hospital -n hospital-<env>` then, after operator approval, `helm rollback hospital <approved-revision> -n hospital-<env> --wait`. Preserve incident metadata. A rollback does not reverse database migrations. Prefer expand/contract migrations; restore a database only after an approved recovery decision considering RPO/RTO and downtime.

Backups: local scripts produce custom-format per-service dumps. Production needs managed PITR, encryption, tested restore, retention, restricted access and recovery drills. Cross-service backups are not globally atomic; reconcile projections by event replay and documented invariants.

Incident triage: establish correlation ID and affected interval; check health/deployment revision; check Kafka DLT/lag, DB pool and connection failures, pending outbox and email jobs. Avoid dumping clinical content into tickets/logs. Do not delete outbox or consumed-event records to force a retry. Replay fixed DLT envelopes with original event IDs, verify projections and document the decision.
