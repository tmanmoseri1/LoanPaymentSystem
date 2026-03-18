terraform {
  required_version = ">= 1.5.0"
}

# Example: AWS provider stub
provider "aws" {
  region = var.aws_region
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}
