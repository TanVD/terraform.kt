package io.terraformkt

import com.squareup.kotlinpoet.*
import io.terraformkt.utils.Json
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
            var isComputed = false
            if (attr.value.containsKey("computed")) {
                isComputed = attr.value["computed"] as Boolean
            }
            lambdaFunction.addProperty(PropertySpec.builder(attr.key, typeToKotlinType(type))
                .delegate(typeToDelegate(type, isComputed)).build())
        }
    }

    println(lambdaFunction.build())
}

fun typeToDelegate(type: String, isComputed: Boolean): String {
    var delegateName = when (type) {
        "string" -> "text"
        "number" -> "int"
        "bool" -> "bool"
        else -> ""
    }
    if (isComputed) {
        delegateName += "(computed = true)"
    } else {
        delegateName += "()"
    }
    return delegateName
}

fun typeToKotlinType(type: String): TypeName {
    return when (type) {
        "string" -> STRING
        "number" -> INT
        "bool" -> BOOLEAN
        else -> ANY
    }
}
