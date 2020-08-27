package io.terraformkt

import com.squareup.kotlinpoet.*
import io.terraformkt.terraform.TFData
import io.terraformkt.terraform.TFResource
import io.terraformkt.utils.Json
import io.terraformkt.utils.Text.snakeToCamelCase
import java.io.File

const val baseDirectoryName = "generated"
const val resourcesDirectoryName = "resource_schemas"
const val dataDirectoryName = "data_source_schemas"
const val provider = "aws"

fun main() {
    val jsonString = File("src/main/resources/schema.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val schema = Json.parse<Schema>(jsonString)
    val generatedDirectory = File(baseDirectoryName)
    if (!generatedDirectory.exists()) {
        generatedDirectory.mkdir()
    }

    val resources = schema.provider_schemas.aws.resource_schemas
    val data = schema.provider_schemas.aws.data_source_schemas
    generateFiles(resourcesDirectoryName, resources, ResourceType.RESOURCE)
    generateFiles(dataDirectoryName, data, ResourceType.DATA)
}

fun generateFiles(path: String, resources: Map<String, Resource>, resourceType: ResourceType) {
    val directory = File("$baseDirectoryName/$path")
    if (!directory.exists()) {
        directory.mkdir()
    }
    for (resourceName in resources.keys) {
        val className = snakeToCamelCase(resourceName)

        val resourceClassBuilder = TypeSpec.classBuilder(className).primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("id", String::class).build()
        ).addClassKDoc(resourceName)

        resourceClassBuilder
            .addSuperClass(resourceType)
            .addSuperclassConstructorParameter("id").addSuperclassConstructorParameter("\"$resourceName\"")

        for ((attrName, attr) in resources[resourceName]!!.block.attributes) {
            if (attr.containsKey("type") && attr["type"] is String) {
                val type = attr["type"] as String
                var isComputed = false
                if (attr.containsKey("computed")) {
                    isComputed = attr["computed"] as Boolean
                }

                val propertyBuilder = PropertySpec.builder(attrName, typeToKotlinType(type))
                    .delegate(typeToDelegate(type, isComputed))
                if (attr.containsKey("description")) {
                    propertyBuilder.addKdoc(attr["description"] as String)
                }

                resourceClassBuilder.addProperty(propertyBuilder.build())
            }
        }
        resourceClassBuilder.addClosureFunction(resourceName, className)
        File("$baseDirectoryName/$path/$className.kt").writeText(resourceClassBuilder.build().toString(), Charsets.UTF_8)
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

fun removeProviderPrefix(resourceName: String): String {
    return resourceName.substringAfter("_")
}

fun TypeSpec.Builder.addSuperClass(resourceType: ResourceType): TypeSpec.Builder {
    return when (resourceType) {
        ResourceType.RESOURCE -> {
            this.superclass(TFResource::class)
        }
        ResourceType.DATA -> {
            this.superclass(TFData::class)
        }
        else -> {
            throw IllegalStateException("Unsupported resource type")
        }
    }
}

fun TypeSpec.Builder.addClassKDoc(resourceName: String): TypeSpec.Builder {
    return this.addKdoc("""Terraform $resourceName resource.
            | 
            | @see <a href="https://www.terraform.io/docs/providers/$provider/r/${removeProviderPrefix(resourceName)}.html">$resourceName</a>
        """.trimMargin())
}

fun TypeSpec.Builder.addClosureFunction(resourceName: String, className: String): TypeSpec.Builder {
    return this.addFunction(
        FunSpec.builder(resourceName)
            .addParameter("id", String::class)
            .addParameter(
                "configure", LambdaTypeName.get(
                returnType = UNIT,
                receiver = TypeVariableName(className)
            )
            )
            .addStatement("%N(id).apply(configure)", className)
            .build()
    )
}

enum class ResourceType {
    DATA,
    RESOURCE
}
