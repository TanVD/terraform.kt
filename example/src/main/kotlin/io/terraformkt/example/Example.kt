package io.terraformkt.example

import io.terraformkt.aws.resource.lambda.lambda_function
import io.terraformkt.terraform.tf

fun main() {
    val file = tf("my_file") {
        lambda_function("my_lambda_function") {
            this.description = "My Description"
        }
    }
}

