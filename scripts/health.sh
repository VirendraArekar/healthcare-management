#!/bin/sh
set -eu
curl --fail --silent http://localhost:8080/actuator/health
curl --fail --silent http://localhost:3000/ >/dev/null
printf '\nGateway and frontend reachable. See docker compose ps and logs for internal dependencies.\n'
