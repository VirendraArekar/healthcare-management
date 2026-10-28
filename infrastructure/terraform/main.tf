terraform {
  required_version = ">= 1.9.0, < 2.0.0"
  required_providers { aws = { source = "hashicorp/aws", version = "~> 5.100" } }
}
provider "aws" { region = var.region }
variable "region" { type = string }
variable "project" { type = string }
variable "environment" { type = string }
locals { services = toset(["frontend","api-gateway","auth-service","patient-service","staff-service","appointment-service","medical-record-service","lab-service","pharmacy-service","billing-service","notification-service","reporting-service"]) }
resource "aws_ecr_repository" "services" {
  for_each = local.services
  name = "${var.project}/${var.environment}/${each.key}"
  image_tag_mutability = "IMMUTABLE"
  image_scanning_configuration { scan_on_push = true }
  encryption_configuration { encryption_type = "AES256" }
  tags = { Environment = var.environment, Project = var.project }
}
output "repositories" { value = { for name, repo in aws_ecr_repository.services : name => repo.repository_url } }
