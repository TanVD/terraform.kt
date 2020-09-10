package io.terraformkt

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.terraformkt.hcl.HCLEntity
import io.terraformkt.terraform.TFData
import io.terraformkt.terraform.TFFile
import io.terraformkt.terraform.TFResource
import io.terraformkt.utils.Json
import io.terraformkt.utils.NamesUtils
import io.terraformkt.utils.Text.snakeToCamelCase
import io.terraformkt.utils.myMkdirs
import java.io.File

class TerraformGenerator(
    private val pathToSchema: File, private val generationPath: File,
    private val provider: String
) {
    private val packageNameProvider = NamesUtils(provider)

    fun generate() {
        val jsonString = pathToSchema.readText()
        val schema = Json.parse<Schema>(jsonString)

        val resources = schema.provider_schemas.values.single().resource_schemas
        val data = schema.provider_schemas.values.single().data_source_schemas

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

            for ((attrName, attr) in resources.getValue(resourceName).block.attributes) {
                val type = getType(attr)

                // TODO support all types
                if (type == Type.ANY) {
                    continue
                }
                val isComputed = attr["computed"] as? Boolean ?: false

                val propertyBuilder = PropertySpec
                    .builder(attrName, type.typeName)
                    .delegate(typeToDelegate(type, isComputed))
                    .mutable(!isComputed)
                if (attr.containsKey("description")) {
                    propertyBuilder.addKdoc(attr["description"] as String)
                }

                resourceClassBuilder.addProperty(propertyBuilder.build())
            }


            val file = generationPath.resolve(packageNameProvider.getClassFilePath(resourceType, className))
            file.parentFile.myMkdirs()
            file.createNewFile()

            fileBuilder
                .addType(resourceClassBuilder.build())
                .addClosureFunctions(removeProviderPrefix(resourceName), className)
            file.writeText(fileBuilder.build().toString())
        }
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
                .addStatement("%N(%N(id).apply(configure))", TFFile::add.name, className)
                .build()
        )
    }

    enum class ResourceType {
        DATA,
        RESOURCE
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
