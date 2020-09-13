package io.terraformkt

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.terraformkt.hcl.HCLEntity
import io.terraformkt.terraform.TFData
import io.terraformkt.terraform.TFFile
import io.terraformkt.terraform.TFProvider
import io.terraformkt.terraform.TFResource
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

        generateProvider(provider)
        generateFiles(resources, ResourceType.RESOURCE)
        generateFiles(data, ResourceType.DATA)
    }

    private fun generateProvider(provider: Configuration) {
        val className = "Provider"

        val fileBuilder = FileSpec.builder(packageNameProvider.getProviderPackageName(), className)
        val resourceClassBuilder = TypeSpec.classBuilder(className)
            .addProviderKDoc()
            .addSuperClass(ResourceType.PROVIDER)
            .addSuperclassConstructorParameter("\"$providerName\"")

        for ((attributeName, attribute) in provider.block.attributes) {
            generateProperty(attributeName, attribute)?.let { resourceClassBuilder.addProperty(it) }
        }

        if (provider.block.block_types != null) {
            for ((blockTypeName, blockType) in provider.block.block_types) {
                val blockTypeClassName = snakeToCamelCase(blockTypeName)

                val blockTypeClassBuilder = TypeSpec.classBuilder(blockTypeClassName)
                    .superclass(HCLEntity.Inner::class)
                    .addSuperclassConstructorParameter("\"$blockTypeName\"")
                if (blockType.nesting_mode != "list" && blockType.nesting_mode != "set") {
                    // TODO support others
                    continue
                }
                if (blockType.block.attributes == null) {
                    // TODO support others
                    continue
                }
                for ((attributeName, attribute) in blockType.block.attributes) {
                    generateProperty(attributeName, attribute)?.let { blockTypeClassBuilder.addProperty(it) }
                }
                resourceClassBuilder.addType(blockTypeClassBuilder.build())
                resourceClassBuilder.addFunction(
                    FunSpec.builder(blockTypeClassName.decapitalize())
                        .addParameter(
                            "configure", LambdaTypeName.get(
                                returnType = UNIT,
                                receiver = TypeVariableName(blockTypeClassName)
                            )
                        )
                        .addStatement("inner(%N().apply(configure))", blockTypeClassName)
                        .build()
                )
            }
        }

        val file = generationPath.resolve(packageNameProvider.getProviderFilePath())
        file.parentFile.mkdirs()
        file.createNewFile()

        fileBuilder
            .addType(resourceClassBuilder.build())
            .addClosureFunctionsToProvider("provider", className)
        file.writeText(fileBuilder.build().toString())
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

            for ((attributeName, attribute) in resources.getValue(resourceName).block.attributes) {
                generateProperty(attributeName, attribute)?.let { resourceClassBuilder.addProperty(it) }
            }

            val file = generationPath.resolve(packageNameProvider.getClassFilePath(resourceType, className))
            file.parentFile.mkdirs()
            file.createNewFile()

            fileBuilder
                .addType(resourceClassBuilder.build())
                .addClosureFunctions(removeProviderPrefix(resourceName), className)
            file.writeText(fileBuilder.build().toString())
        }
    }

    private fun generateProperty(attributeName: String, attribute: Map<String, Any>): PropertySpec? {
        val type = getType(attribute)

        // TODO support all types
        if (type == Type.ANY) {
            return null
        }
        val isComputed = attribute["computed"] as? Boolean ?: false

        val propertyBuilder = PropertySpec
            .builder(attributeName, type.typeName)
            .delegate(typeToDelegate(type, isComputed))
            .mutable(!isComputed)
        if (attribute.containsKey("description")) {
            propertyBuilder.addKdoc(attribute["description"] as String)
        }

        return propertyBuilder.build()
    }

    private fun getType(attr: Map<String, Any>): Type {
        require(attr.containsKey("type")) {
            "No type parameter for the attribute."
        }

        if (attr["type"] is String) {
            when (attr["type"]) {
                "string" -> return Type.STRING
                "number" -> return Type.NUMBER
                "bool" -> return Type.BOOL
            }
        }

        if (attr["type"] is ArrayList<*>) {
            val typeMap = attr["type"] as ArrayList<*>
            if (typeMap[0] == "list" && typeMap[1] is String) {
                when (typeMap[1]) {
                    "string" -> return Type.STRING_LIST
                    "number" -> return Type.NUMBER_LIST
                    "bool" -> return Type.BOOL_LIST
                }
            }
        }

        // TODO support map, set and list of objects
        return Type.ANY
    }

    private fun typeToDelegate(type: Type, isComputed: Boolean): String {
        return type.delegateName + if (isComputed) "(computed = true)" else "()"
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
            ResourceType.PROVIDER -> {
                this.superclass(TFProvider::class)
            }
        }
    }

    private fun TypeSpec.Builder.addClassKDoc(resourceName: String): TypeSpec.Builder {
        return this.addKdoc(
            """Terraform $resourceName resource.
            | 
            | @see <a href="https://www.terraform.io/docs/providers/$providerName/r/${removeProviderPrefix(resourceName)}.html">$resourceName</a>
        """.trimMargin()
        )
    }

    private fun TypeSpec.Builder.addProviderKDoc(): TypeSpec.Builder {
        return this.addKdoc(
            """$providerName Terraform provider.
            | 
            | @see <a href="https://www.terraform.io/docs/providers/$providerName/index.html">$providerName provider</a>
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

    enum class ResourceType {
        DATA,
        RESOURCE,
        PROVIDER
    }

    enum class Type(val delegateName: String, val typeName: TypeName) {
        STRING(HCLEntity::text.name, com.squareup.kotlinpoet.STRING),
        NUMBER(HCLEntity::int.name, INT),
        BOOL(HCLEntity::bool.name, BOOLEAN),
        STRING_LIST(HCLEntity::textList.name, Array<String>::class.asClassName().parameterizedBy(com.squareup.kotlinpoet.STRING)),
        NUMBER_LIST(HCLEntity::intList.name, Array<Int>::class.asClassName().parameterizedBy(INT)),
        BOOL_LIST(HCLEntity::boolList.name, Array<Boolean>::class.asClassName().parameterizedBy(BOOLEAN)),

        // TODO support map and remove any
        ANY("", com.squareup.kotlinpoet.ANY)
    }
}
