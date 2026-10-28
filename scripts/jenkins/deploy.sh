#!/bin/sh
set -eu
case "$ENVIRONMENT" in dev|uat|preprod|prod) ;; *) exit 1;; esac
python3 scripts/validate-release.py "$RELEASE_VALUES"
helm upgrade --install hospital infrastructure/kubernetes/helm/hospital-platform -n "hospital-$ENVIRONMENT" --create-namespace -f "infrastructure/kubernetes/helm/hospital-platform/values-$ENVIRONMENT.yaml" -f "$RELEASE_VALUES" --atomic --wait --timeout 10m
: "${SMOKE_URL:?Set environment-specific SMOKE_URL}"
curl --fail "$SMOKE_URL/"
