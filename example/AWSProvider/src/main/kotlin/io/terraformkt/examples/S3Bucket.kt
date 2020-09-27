package io.terraformkt.examples

import io.terraformkt.aws.data.elb.elb_service_account
import io.terraformkt.aws.provider.provider
import io.terraformkt.aws.resource.s3.s3_bucket
import io.terraformkt.aws.resource.s3.s3_bucket_public_access_block
import io.terraformkt.runtime.terraform

fun main() {
    terraform {
        tf("s3_bucket") {
            provider {
                region = "us-east-2"
                profile = "default"
            }
            s3_bucket("example_s3_bucket") {
                acl = "private"
                versioning {
                    enabled = true
                }
            }
            s3_bucket_public_access_block("example_s3_bucket_public_access_block") {
                bucket = "aws_s3_bucket.s3_bucket.id"
            }
            elb_service_account("example_elb_service_account") {
            }
        }
    }.plan()
}
