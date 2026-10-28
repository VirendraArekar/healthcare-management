#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
[ "${CONFIRM_LOCAL_RESET:-}" = "DELETE_LOCAL_DEMO_DATA" ] || { echo 'Destructive local reset: rerun with CONFIRM_LOCAL_RESET=DELETE_LOCAL_DEMO_DATA'; exit 1; }
docker compose down --volumes
