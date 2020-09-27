package io.terraformkt.examples

import io.terraformkt.aws.provider.provider
import io.terraformkt.aws.resource.instance.instance
import io.terraformkt.runtime.terraform

fun main() {
    terraform {
        tf("instance") {
            provider {
                region = "us-east-2"
                profile = "default"
            }
            instance("example") {
                ami = "ami-0c55b159cbfafe1f0"
                instance_type = "t2.micro"
            }
        }
    }.plan()
}
