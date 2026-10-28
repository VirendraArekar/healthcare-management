# AWS deployment reference — optional, incomplete infrastructure
Local development has no AWS dependency. The included Terraform creates environment-scoped immutable ECR repositories only. The remaining services below are an architecture/runbook, not an apply-ready complete cloud environment.

## Reference design
Private VPC spanning at least three AZs: public subnets for ALB/NAT as required; private subnets for EKS workloads; isolated data subnets for RDS PostgreSQL, ElastiCache Redis and MSK. ALB → ingress/frontend → gateway → private services. RDS has ten independently owned databases and credentials. MSK supplies Kafka with TLS/authentication. ACM/Route 53 supply TLS/DNS. Use Secrets Manager and workload identity, KMS encryption, S3 encrypted backups/artifacts, and CloudWatch/managed Prometheus as appropriate.

Managed RDS/ElastiCache/MSK reduce patching, backup and failover work but have baseline costs. Self-managed data services on EKS require durable storage, operators, capacity planning, restore validation and on-call ownership. Namespaces alone are not a security boundary; use separate accounts/clusters for production where needed.

## Prerequisites and identity
Install AWS CLI v2, Terraform 1.9+, Docker, kubectl and Helm 3. Use AWS IAM Identity Center/SSO or a short-lived role; never commit access keys. Set operator-specific values:
```sh
export AWS_PROFILE=hospital-dev
export AWS_REGION=<region>
aws sso login --profile "$AWS_PROFILE"
aws sts get-caller-identity
terraform -chdir=infrastructure/terraform init
terraform -chdir=infrastructure/terraform plan -var='region=<region>' -var='project=<project>' -var='environment=dev'
# Review plan, costs and target account before applying:
terraform -chdir=infrastructure/terraform apply -var='region=<region>' -var='project=<project>' -var='environment=dev'
```
Store Terraform state in an encrypted, locked remote backend with environment-specific access. A backend is not preconfigured. Restrict IAM to exact ECR resources for publishing and least-privilege cluster deployment roles. No admin credentials in CI.

## Cluster and data preparation
Provision reviewed VPC/EKS modules with supported versions, private cluster endpoints where possible, workload identity, restricted API access and node security groups. Install a VPC-aware CNI, ingress/load-balancer controller, metrics server, external secret operator, observability and policy tooling with pinned releases. Configure cluster RBAC and environment separation.

Provision encrypted multi-AZ RDS with PITR, maintenance windows and restricted security groups. Create service DB owners and databases using private administration access. Provision Redis TLS/auth and MSK TLS/SASL/IAM as selected; update application client configuration accordingly. The local config is plaintext Redis/Kafka inside an internal network and must not be copied unchanged into AWS. Create Kafka topics with production replication and retention. Use distinct per-service database secrets, a managed signing-key strategy and secret rotation.

## Publishing and deployment
```sh
aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin <account>.dkr.ecr.<region>.amazonaws.com
docker build -f backend/patient-service/Dockerfile -t <registry>/<project>/dev/patient-service:<commit> .
docker push <registry>/<project>/dev/patient-service:<commit>
aws eks update-kubeconfig --name <cluster> --region "$AWS_REGION"
```
Repeat for twelve images via controlled build infrastructure. Record every image digest; generate SBOM/provenance and scan before promotion. Use a reviewed release values JSON with registry@sha256 references. Sync environment secrets into the Helm secretName; configure external hosts, ingress domain and TLS secret/ACM integration. Use GitHub deploy workflows or Jenkins, never competing controllers.
```sh
helm upgrade --install hospital infrastructure/kubernetes/helm/hospital-platform -n hospital-dev --create-namespace -f infrastructure/kubernetes/helm/hospital-platform/values-dev.yaml -f /secure/runtime-values.yaml -f releases/approved.json --atomic --wait --timeout 10m
```
GitHub OIDC: trust only the exact repository/environment subject and sts.amazonaws.com audience; grant minimal registry/cluster permissions. The supplied workflows do not yet configure an OIDC assume-role action. Configure and review that integration before use. Jenkins should assume an environment-scoped role from its workload identity and bind a least-privilege kubeconfig.

## DNS, operations and validation
Route 53 records target ALB; ACM certificate validation and HTTPS redirect must be configured. Do not expose Kafka/Redis/DB publicly. Check DNS/TLS, gateway health, authenticated read operations and dependency readiness. Monitor HTTP/JVM metrics, traces, structured logs, Kafka lag/DLT rates, DB storage/pools and Redis availability. Add alerts and a staffed response path. Configure encrypted backups, cross-region copies when required, RPO/RTO targets and recovery drills. Test app rollback separately from DB recovery; retain compatible migrations and approved image digests.

## Cost estimation
Use AWS Pricing Calculator with region, AZ count, EKS cluster hours, node instance-hours, EBS, NAT processing, ALB hours/LCUs, RDS instance/storage/I/O/backup, Redis nodes, MSK broker/storage/traffic, ECR storage/scan, CloudWatch ingestion/retention and cross-AZ/region traffic. Estimate steady-state and peak/load-test costs, set budgets/alerts, and tag resources per environment. EKS, managed Kafka, Redis and databases incur costs while idle; do not assume scale-to-zero or a free tier. Review pricing directly before deployment rather than relying on a fixed estimate in this guide.

## Cleanup — destructive, approval required
Confirm account/region/environment and retention policy. Take/export approved backups. Delete Helm workloads and ingress/load balancers; inspect retained EBS volumes/snapshots and S3 objects; decommission data services only after explicit owner approval. Review `terraform plan -destroy` before `terraform destroy` for the ECR-only configuration; non-empty repositories intentionally block deletion. Do not enable force deletion to bypass retention. Separately remove EKS/node groups, NAT gateways, ALBs, RDS, Redis, MSK, DNS and unused snapshots/EIPs created by your infrastructure. Verify billing/resource inventories afterward. Never run cleanup against production without change approval.
