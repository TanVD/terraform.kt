provider "aws" {
  profile = "default"
  region = "us-east-2"
}

resource "aws_s3_bucket" "example_s3_bucket" {
  acl = "private"
  versioning {
    enabled = true
  }
}

resource "aws_s3_bucket_public_access_block" "example_s3_bucket_public_access_block" {
  bucket = "aws_s3_bucket.s3_bucket.id"
}

data "aws_elb_service_account" "example_elb_service_account" {
  
}

