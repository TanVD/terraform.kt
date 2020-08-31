package io.terraformkt

import com.squareup.kotlinpoet.*
import io.terraformkt.terraform.TFData
import io.terraformkt.terraform.TFFile
import io.terraformkt.terraform.TFResource
import io.terraformkt.utils.Json
import io.terraformkt.utils.Text.snakeToCamelCase
import java.io.File
import java.lang.IllegalStateException

class TerraformGenerator(private val pathToSchema: File, private val generationPath: File) {
    private val packagePrefix = "io.terraformkt.aws"
    private val resourcesDirectoryName = "resource_schemas"
    private val dataDirectoryName = "data_source_schemas"
    private val provider = "aws"

    fun generate() {
        val jsonString = pathToSchema.inputStream().readBytes().toString(Charsets.UTF_8)
        val schema = Json.parse<Schema>(jsonString)
        if (!generationPath.exists()) {
            generationPath.mkdirs()
        }

        println(generationPath.resolve("io").mkdir())
        println(generationPath.resolve("io/terraformkt").mkdir())
        println(generationPath.resolve("io/terraformkt/aws").mkdir())
        println(generationPath.resolve("io/terraformkt/aws/$resourcesDirectoryName").mkdir())
        println(generationPath.resolve("io/terraformkt/aws/$dataDirectoryName").mkdir())

        val resources = schema.provider_schemas.aws.resource_schemas
        val data = schema.provider_schemas.aws.data_source_schemas

        generateFiles(resources, ResourceType.RESOURCE)
        generateFiles(data, ResourceType.DATA)
    }

    private fun generateFiles(resources: Map<String, Resource>, resourceType: ResourceType) {
        for (resourceName in resources.keys) {
            val className = snakeToCamelCase(removeProviderPrefix(resourceName))

            val fileBuilder = FileSpec.builder("$packagePrefix.${getDirectoryName(resourceType)}", className)
            val resourceClassBuilder = TypeSpec.classBuilder(className)
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameter("id", String::class)
                        .build()
                )
                .addClassKDoc(resourceName)
                .addSuperClass(resourceType)
                .addSuperclassConstructorParameter("id")
                .addSuperclassConstructorParameter("\"$resourceName\"")

            for ((attrName, attr) in resources[resourceName]!!.block.attributes) {
                val type = getType(attr)
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


            val file = generationPath.resolve("io/terraformkt/aws/${getDirectoryName(resourceType)}/$className.kt")
            file.createNewFile()

            fileBuilder
                .addType(resourceClassBuilder.build())
                .addClosureFunctions(removeProviderPrefix(resourceName), className)
                .build()
                .writeTo(generationPath)
        }
    }

    private fun getType(attr: Map<String, Any>): String {
        if (!attr.containsKey("type")) {
            throw IllegalStateException("No type parameter")
        }
        if (attr["type"] is String) {
            return attr["type"] as String
        }

        // TODO support other types
        return "any"
    }

    private fun typeToDelegate(type: String, isComputed: Boolean): String {
        var delegateName = when (type) {
            "string" -> "text"
            "number" -> "int"
            "bool" -> "bool"
            else -> ""
        }
        delegateName += if (isComputed) "(computed = true)" else "()"

        return delegateName
    }

    private fun typeToKotlinType(type: String): TypeName {
        return when (type) {
            "string" -> STRING
            "number" -> INT
            "bool" -> BOOLEAN
            else -> ANY
        }
    }

    private fun removeProviderPrefix(resourceName: String): String {
        return resourceName.substringAfter("_")
    }

    private fun TypeSpec.Builder.addSuperClass(resourceType: ResourceType): TypeSpec.Builder {
        return when (resourceType) {
            ResourceType.RESOURCE -> {
                this.superclass(TFResource::class)
            }
            ResourceType.DATA -> {
                this.superclass(TFData::class)
            }
        }
    }

    private fun TypeSpec.Builder.addClassKDoc(resourceName: String): TypeSpec.Builder {
        return this.addKdoc(
            """Terraform $resourceName resource.
            | 
            | @see <a href="https://www.terraform.io/docs/providers/$provider/r/${removeProviderPrefix(resourceName)}.html">$resourceName</a>
        """.trimMargin()
        )
    }

    private fun FileSpec.Builder.addClosureFunctions(functionName: String, className: String): FileSpec.Builder {
        return this.addFunction(
            FunSpec.builder(functionName)
                .addParameter("id", String::class)
                .addParameter(
                    "configure", LambdaTypeName.get(
                        returnType = UNIT,
                        receiver = TypeVariableName(className)
                    )
                )
                .addStatement("return %N(id).apply(configure)", className)
                .returns(TypeVariableName(className))
                .build()
        ).addFunction(
            FunSpec.builder(functionName)
                .receiver(TFFile::class)
                .addParameter("id", String::class)
                .addParameter(
                    "configure", LambdaTypeName.get(
                        returnType = UNIT,
                        receiver = TypeVariableName(className)
                    )
                )
                .addStatement("add(%N(id).apply(configure))", className)
                .build()
        )
    }

    private fun getDirectoryName(resourceType: ResourceType): String {
        return when (resourceType) {
            ResourceType.RESOURCE -> resourcesDirectoryName
            ResourceType.DATA -> dataDirectoryName
        }
    }

    enum class ResourceType {
        DATA,
        RESOURCE
    }
}
