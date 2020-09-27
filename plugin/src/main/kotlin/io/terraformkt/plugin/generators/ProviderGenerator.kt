package io.terraformkt.plugin.generators

import com.squareup.kotlinpoet.*
import io.terraformkt.Configuration
import io.terraformkt.terraform.TFFile
import io.terraformkt.utils.NamesUtils
import java.io.File

class ProviderGenerator(private val providerName: String, private val packageNameProvider: NamesUtils, private val generationPath: File) {

    fun generateProvider(provider: Configuration) {
        val className = "Provider"

        val fileBuilder = FileSpec.builder(packageNameProvider.getProviderPackageName(), className)
        val resourceClassBuilder = TypeSpec.classBuilder(className)
            .addProviderKDoc()
            .addSuperClass(ResourceType.PROVIDER)
            .addSuperclassConstructorParameter("\"$providerName\"")

        // TODO: why isn't it specified in schema?
        resourceClassBuilder.addProperty(generateVersionProperty())

        if (provider.block.attributes != null) {
            for ((attributeName, attribute) in provider.block.attributes) {
                resourceClassBuilder.addAttribute(attributeName, attribute)
            }
        }

        if (provider.block.block_types != null) {
            generateBlockTypes(provider.block.block_types, resourceClassBuilder)
        }

        val file = generationPath.resolve(packageNameProvider.getProviderFilePath())
        file.parentFile.mkdirs()
        file.createNewFile()

        fileBuilder
            .addType(resourceClassBuilder.build())
            .addClosureFunctionsToProvider("provider", className)
        file.writeText(fileBuilder.build().toString())
    }

    private fun TypeSpec.Builder.addProviderKDoc(): TypeSpec.Builder {
        return this.addKdoc(
            """$providerName Terraform provider.
            | 
            | @see <a href="https://www.terraform.io/docs/providers/$providerName/index.html">$providerName provider</a>
        """.trimMargin()
        )
    }

    private fun FileSpec.Builder.addClosureFunctionsToProvider(functionName: String, className: String): FileSpec.Builder {
        return this.addFunction(
            FunSpec.builder(functionName)
                .addParameter(
                    "configure", LambdaTypeName.get(
                        returnType = UNIT,
                        receiver = TypeVariableName(className)
                    )
                )
                .addStatement("return %N().apply(configure)", className)
                .returns(TypeVariableName(className))
                .build()
        ).addFunction(
            FunSpec.builder(functionName)
                .receiver(TFFile::class)
                .addParameter(
                    "configure", LambdaTypeName.get(
                        returnType = UNIT,
                        receiver = TypeVariableName(className)
                    )
                )
                .addStatement("%N(%N().apply(configure))", TFFile::add.name, className)
                .build()
        )
    }

    private fun generateVersionProperty(): PropertySpec {
        val type = FieldType.STRING
        val propertyBuilder = PropertySpec
            .builder("version", type.typeName)
            .delegate(typeToDelegate(type, false))
            .mutable(true)

        return propertyBuilder.build()
    }
}


