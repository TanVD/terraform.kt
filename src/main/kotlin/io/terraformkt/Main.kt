package io.terraformkt

import com.squareup.kotlinpoet.*
import java.io.File

fun main() {
    val jsonString = File("src/main/resources/schema.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val schema = Json.parse<Schema>(jsonString)
    val attributes = schema.provider_schemas.aws.resource_schemas.aws_lambda_function.block.attributes
   // println(attributes)

    val lambdaFunction = TypeSpec.classBuilder("LambdaFunction")
    for (attr in attributes) {
        if (attr.value.containsKey("type") && attr.value["type"] is String) {
            val type = attr.value["type"] as String
            lambdaFunction.addProperty(PropertySpec.builder(attr.key, ANY).delegate(typeToDelegate(type)).build())
        }
    }

    println(lambdaFunction.build())
}

fun typeToDelegate(type : String) : String {
    return when(type){
        "string" -> "text()"
        "number" -> "int()"
        "bool" -> "bool()"
        else -> ""
    }
}
