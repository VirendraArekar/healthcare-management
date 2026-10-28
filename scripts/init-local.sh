#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
if [ -e .env ]; then echo '.env already exists; refusing to overwrite'; exit 1; fi
umask 077
python3 - <<'PYCODE'
import secrets
from pathlib import Path
values={k:secrets.token_urlsafe(36) for k in ['POSTGRES_PASSWORD','SERVICE_DB_PASSWORD','JWT_SECRET','DEMO_PASSWORD','GRAFANA_PASSWORD']}
Path('.env').write_text(''.join(f'{k}={v}\n' for k,v in values.items()))
print('Created .env with random local secrets. Read DEMO_PASSWORD there to log in.')
PYCODE
