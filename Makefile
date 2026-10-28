.PHONY: init up down logs health seed test build helm-check
init:
	./scripts/init-local.sh
up:
	docker compose up --build -d
down:
	docker compose down
logs:
	docker compose logs -f --tail=100
health:
	./scripts/health.sh
seed:
	./scripts/seed-local.sh
test:
	mvn -B test
	cd frontend && npm ci && npm test && npm run build
build:
	docker compose build
helm-check:
	helm lint infrastructure/kubernetes/helm/hospital-platform
	helm template hospital infrastructure/kubernetes/helm/hospital-platform > /tmp/hospital-manifests.yaml
