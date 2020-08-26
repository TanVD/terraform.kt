package io.terraformkt

import com.squareup.kotlinpoet.FunSpec
import io.terraformkt.hcl.Json
import io.terraformkt.hcl.Schema
import java.io.File

fun main() {
    val jsonString = File("src/main/resources/schema.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val schema = Json.parse<Schema>(jsonString)
    println(schema.provider_schemas.aws.resource_schemas.aws_lambda_function.block.attributes)

    val lambda = FunSpec.builder("lambda")
        .returns(Char::class)
        .addStatement("val ")
        .build()
}
