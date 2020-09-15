package io.terraformkt.example

import io.terraformkt.aws.resource.lambda.lambda_function
import io.terraformkt.runtime.terraform

fun main() {
    terraform {
        tf("my_file") {
            lambda_function("my_lambda_function") {
                this.description = "My Description"
            }
        }
        tf("other_file") {
            lambda_function("other_lambda_function") {
                this.description = "Other Description"
            }
        }
    }.apply()
}

