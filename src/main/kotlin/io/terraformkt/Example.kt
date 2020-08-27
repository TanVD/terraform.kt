package io.terraformkt

import io.terraformkt.aws.resource_schemas.lambda_function

fun main() {
    val lambda = lambda_function("name") {
        function_name = "function name"
        handler = "handler"
    }

    println(lambda.function_name)
}
