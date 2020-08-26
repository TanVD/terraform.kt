package io.terraformkt

import com.squareup.kotlinpoet.*
import io.terraformkt.utils.Json
import io.terraformkt.utils.Text.snakeToCamelCase
import java.io.File

const val baseDirectoryName = "generated"
const val resourcesDirectoryName = "resource_schemas"
const val dataDirectoryName = "data_source_schemas"

fun main() {
    val jsonString = File("src/main/resources/schema.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val schema = Json.parse<Schema>(jsonString)
    val generatedDirectory = File(baseDirectoryName)
    if (!generatedDirectory.exists()) {
        generatedDirectory.mkdir()
    }

    val resources = schema.provider_schemas.aws.resource_schemas
    val data = schema.provider_schemas.aws.data_source_schemas
    generateFiles(resourcesDirectoryName, resources)
    generateFiles(dataDirectoryName, data)
}

fun generateFiles(path: String, resources: Map<String, Resource>) {
    val directory = File("$baseDirectoryName/$path")
    if (!directory.exists()) {
        directory.mkdir()
    }
    for (resourceName in resources.keys) {
        val className = snakeToCamelCase(resourceName)
        val resourceClass = TypeSpec.classBuilder(className)
        for (attr in resources[resourceName]!!.block.attributes) {
            if (attr.value.containsKey("type") && attr.value["type"] is String) {
                val type = attr.value["type"] as String
                var isComputed = false
                if (attr.value.containsKey("computed")) {
                    isComputed = attr.value["computed"] as Boolean
                }
                resourceClass.addProperty(
                    PropertySpec.builder(attr.key, typeToKotlinType(type))
                        .delegate(typeToDelegate(type, isComputed)).build()
                )
            }
        }
        File("$baseDirectoryName/$path/$className.kt").writeText(resourceClass.build().toString(), Charsets.UTF_8)
    }
}

fun typeToDelegate(type: String, isComputed: Boolean): String {
    var delegateName = when (type) {
        "string" -> "text"
        "number" -> "int"
        "bool" -> "bool"
        else -> ""
    }
    delegateName += if (isComputed) "(computed = true)" else "()"

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
