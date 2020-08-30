package io.terraformkt

import com.squareup.kotlinpoet.*
import io.terraformkt.terraform.TFData
import io.terraformkt.terraform.TFFile
import io.terraformkt.terraform.TFResource
import io.terraformkt.utils.Json
import io.terraformkt.utils.Text.snakeToCamelCase
import java.io.File

const val baseDirectoryName = "generated"
const val packagePrefix = "io.terraformkt.aws"
const val resourcesDirectoryName = "resource_schemas"
const val dataDirectoryName = "data_source_schemas"
const val provider = "aws"

fun main() {
    val jsonString = File("src/main/resources/schema.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val schema = Json.parse<Schema>(jsonString)
    val generatedDirectory = File(baseDirectoryName)
    if (!generatedDirectory.exists()) {
        generatedDirectory.mkdirs()
    }

    println(File("$baseDirectoryName/io").mkdir())
    println(File("$baseDirectoryName/io/terraformkt").mkdir())
    println(File("$baseDirectoryName/io/terraformkt/aws").mkdir())
    println(File("$baseDirectoryName/io/terraformkt/aws/$resourcesDirectoryName").mkdir())
    println(File("$baseDirectoryName/io/terraformkt/aws/$dataDirectoryName").mkdirs())

    val resources = schema.provider_schemas.aws.resource_schemas
    val data = schema.provider_schemas.aws.data_source_schemas
    generateFiles(resources, ResourceType.RESOURCE)
    generateFiles(data, ResourceType.DATA)
//    val lambda = lambda_function("name") {
//        function_name = "function name"
//        handler = "handler"
//    }
}

fun generateFiles(resources: Map<String, Resource>, resourceType: ResourceType) {
    for (resourceName in resources.keys) {
        val className = snakeToCamelCase(removeProviderPrefix(resourceName))

        val fileBuilder = FileSpec.builder("$packagePrefix.${getDirectoryName(resourceType)}", className)
        val resourceClassBuilder = TypeSpec.classBuilder(className).primaryConstructor(
            FunSpec.constructorBuilder().addParameter("id", String::class).build()
        )
            .addClassKDoc(resourceName)
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
                    .delegate(typeToDelegate(type, isComputed)).mutable(!isComputed)
                if (attr.containsKey("description")) {
                    propertyBuilder.addKdoc(attr["description"] as String)
                }

                resourceClassBuilder.addProperty(propertyBuilder.build())
            }
        }
        val file = File("$baseDirectoryName/io/terraformkt/aws/${getDirectoryName(resourceType)}/$className.kt")
        file.createNewFile()

        fileBuilder
            .addType(resourceClassBuilder.build())
            .addClosureFunctions(removeProviderPrefix(resourceName), className)
            .build().writeTo(File("$baseDirectoryName/"))
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
    }
}

fun TypeSpec.Builder.addClassKDoc(resourceName: String): TypeSpec.Builder {
    return this.addKdoc("""Terraform $resourceName resource.
            | 
            | @see <a href="https://www.terraform.io/docs/providers/$provider/r/${removeProviderPrefix(resourceName)}.html">$resourceName</a>
        """.trimMargin())
}

fun FileSpec.Builder.addClosureFunctions(functionName: String, className: String): FileSpec.Builder {
    return this.addFunction(
        FunSpec.builder(functionName)
            .addParameter("id", String::class)
            .addParameter(
                "configure", LambdaTypeName.get(
                returnType = UNIT,
                receiver = TypeVariableName(className)))
            .addStatement("return %N(id).apply(configure)", className)
            .returns(TypeVariableName(className))
            .build()
    ).addFunction(FunSpec.builder(functionName)
        .receiver(TFFile::class)
        .addParameter("id", String::class)
        .addParameter(
            "configure", LambdaTypeName.get(
            returnType = UNIT,
            receiver = TypeVariableName(className)))
        .addStatement("add(%N(id).apply(configure))", className)
        .build())
}

fun getDirectoryName(resourceType: ResourceType): String {
    return when (resourceType) {
        ResourceType.RESOURCE -> resourcesDirectoryName
        ResourceType.DATA -> dataDirectoryName
    }
}

enum class ResourceType {
    DATA,
    RESOURCE
}
