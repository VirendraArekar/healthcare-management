# API catalogue
Import collection and the Local environment. Copy DEMO_PASSWORD from your local .env into the secret password variable. Select an appropriate account per folder: admin/receptionist for patients and appointments; doctor for encounters and lab orders; pharmacist for inventory; billing_staff for invoices. All demo emails end in @demo.hospital. Run Login again after changing email.

Requests save generated IDs. Publish two future slots before rescheduling. Result verification must use a second LAB_TECHNICIAN account created by ADMIN. Some negative cases intentionally return 400/401/409. PATIENT endpoints require the demo seed mapping or an ADMIN-created account with patientId.

Use Newman for a role-specific folder after authenticating and exporting a temporary local environment with accessToken (never commit it):
```sh
npx newman run postman/Hospital-Healthcare-API.postman_collection.json -e /tmp/hospital-local.private.json --folder Patients
```
The full catalogue contains mutually exclusive roles and setup-dependent cases; a whole-collection run is not a deterministic acceptance suite. Use `python3 scripts/smoke.py` or the Playwright test for the integrated workflow. Never run mutation/negative tests against Production. PDF responses are binary; CSV responses are text. No real patient data or card information is permitted.
