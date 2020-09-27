package io.terraformkt.plugin.generators

import com.squareup.kotlinpoet.*
import io.terraformkt.Configuration
import io.terraformkt.Schema
import io.terraformkt.terraform.TFFile
import io.terraformkt.utils.Json
import io.terraformkt.utils.NamesUtils
import io.terraformkt.utils.Text.snakeToCamelCase
import java.io.File

class TerraformGenerator(
    private val pathToSchema: File, private val generationPath: File,
    private val providerName: String
) {
    private val packageNameProvider = NamesUtils(providerName)

    fun generate() {
        val jsonString = pathToSchema.readText()
        val schema = Json.parse<Schema>(jsonString)

        val schemas = schema.provider_schemas.values.single()
        val provider = schemas.provider
        val resources = schemas.resource_schemas
        val data = schemas.data_source_schemas

        val providerGenerator = ProviderGenerator(providerName, packageNameProvider, generationPath)
        providerGenerator.generateProvider(provider)

        generateFiles(resources, ResourceType.RESOURCE)
        generateFiles(data, ResourceType.DATA)
    }

    private fun generateFiles(resources: Map<String, Configuration>, resourceType: ResourceType) {
        for (resourceName in resources.keys) {
            val className = snakeToCamelCase(removeProviderPrefix(resourceName))

            val fileBuilder = FileSpec.builder(packageNameProvider.getPackageName(resourceType, className), className)
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

            val resource = resources.getValue(resourceName)
            if (resource.block.attributes != null) {
                for ((attributeName, attribute) in resource.block.attributes) {
                    resourceClassBuilder.addAttribute(attributeName, attribute)
                }
            }
            if (resource.block.block_types != null) {
                generateBlockTypes(resource.block.block_types, resourceClassBuilder)
            }

            fileBuilder
                .addType(resourceClassBuilder.build())
                .addClosureFunctions(removeProviderPrefix(resourceName), className)

            val file = generationPath.resolve(packageNameProvider.getClassFilePath(resourceType, className))
            file.parentFile.mkdirs()
            file.createNewFile()
            file.writeText(fileBuilder.build().toString())
        }
    }

    private fun removeProviderPrefix(resourceName: String): String {
        return resourceName.substringAfter("_")
    }

    private fun TypeSpec.Builder.addClassKDoc(resourceName: String): TypeSpec.Builder {
        return this.addKdoc(
            """Terraform $resourceName resource.
            | 
            | @see <a href="https://www.terraform.io/docs/providers/$providerName/r/${removeProviderPrefix(resourceName)}.html">$resourceName</a>
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
                .addStatement("%N(%N(id).apply(configure))", TFFile::add.name, className)
                .build()
        )
    }
}
