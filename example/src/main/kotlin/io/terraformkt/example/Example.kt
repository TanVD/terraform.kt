package io.terraformkt.example

//import io.terraformkt.aws.resource_schemas.lambda_function

import io.terraformkt.aws.resource_schemas.lambda.lambda_function
import io.terraformkt.terraform.tf

fun main() {
    val file = tf("my_file") {
        lambda_function("my_lambda_function") {
            this.description = "My Description"
        }
    }
}

