package io.terraformkt.plugin.generators

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.terraformkt.BlockType
import io.terraformkt.ConfigurationBlock
import io.terraformkt.hcl.HCLEntity
import io.terraformkt.utils.Text

internal fun TypeSpec.Builder.addAttribute(attributeName: String, attribute: Map<String, Any>) {
    require(attribute.containsKey("type") && attribute["type"] != null) {
        "No type parameter for the attribute."
    }
    val description = attribute["description"] as String?
    this.addAttribute(attributeName, attribute["type"]!!, description)
}

internal fun TypeSpec.Builder.addAttribute(attributeName: String, attribute: Any, description: String?) {
    val type = getFieldType(attribute)

    if (type == FieldTypeWithoutDelegate.OBJECT_LIST) {
        require(attribute is ArrayList<*>) {
            "Wrong structure of attribute's type."
        }
        val objectAttributes = getListObjectAttributes(attribute)
        this.addType(generateObject(attributeName, objectAttributes))
        this.addBlockTypeFunction(attributeName)
        return
    }

    // All other FieldTypeWithoutDelegate are maps.
    if (type is FieldTypeWithoutDelegate) {
        this.addType(generateMapAttribute(attributeName, type.typeName))
        this.addFunction(
            generateMapClosureFunction(Text.snakeToCamelCase(attributeName), type.typeName)
        )
        return
    }
    if (type is FieldTypeWithDelegate) {
        val propertyBuilder = PropertySpec
            .builder(attributeName, type.typeName)
            .delegate(typeToDelegate(type))
            .mutable()
        if (description != null) {
            try {
                propertyBuilder.addEscapedKdoc(description)
            } catch (e: Exception) {
                // TODO escape %
            }
        }
        this.addProperty(propertyBuilder.build())
    }
}

private fun PropertySpec.Builder.addEscapedKdoc(kdoc: String) {
    val escapedKdoc = kdoc.replace("*/", "`*`/").replace("/*", "/`*`")
    this.addKdoc(escapedKdoc)
}

private fun getListObjectAttributes(type: ArrayList<*>): Map<String, Any> {
    require(type[1] is ArrayList<*>) {
        "Wrong type structure for the list object."
    }
    val objectType = type[1] as ArrayList<*>

    require(objectType[1] is Map<*, *>) {
        "Wrong type structure for the list object."
    }
    return objectType[1] as Map<String, Any>
}

private fun generateMapAttribute(name: String, typeName: TypeName): TypeSpec {
    val blockTypeClassName = Text.snakeToCamelCase(name)
    val blockTypeClassBuilder = TypeSpec.classBuilder(blockTypeClassName)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter(
                    "map", Map::class.asClassName()
                        .parameterizedBy(
                            STRING, (typeName as ParameterizedTypeName)
                                .typeArguments.first()
                        )
                )
                .build()
        )
        .superclass(typeName)
        .addSuperclassConstructorParameter("\"$name\"")
        .addSuperclassConstructorParameter("map")

    return blockTypeClassBuilder.build()
}

private fun generateMapClosureFunction(className: String, typeName: TypeName): FunSpec {
    return FunSpec.builder(className.decapitalize())
        .addParameter(
            "map", Map::class.asClassName().parameterizedBy(
                STRING, (typeName as ParameterizedTypeName)
                    .typeArguments.first()
            )
        )
        .addStatement("inner(%N(map))", className)
        .build()
}

internal fun generateBlockTypeClass(blockTypeName: String, block: ConfigurationBlock): TypeSpec {
    val blockTypeClassName = Text.snakeToCamelCase(blockTypeName)
    val blockTypeClassBuilder = TypeSpec.classBuilder(blockTypeClassName)
        .superclass(HCLEntity.Inner::class)
        .addSuperclassConstructorParameter("\"$blockTypeName\"")

    if (block.attributes != null) {
        for ((attributeName, attribute) in block.attributes) {
            blockTypeClassBuilder.addAttribute(attributeName, attribute)
        }
    }
    if (block.block_types != null) {
        blockTypeClassBuilder.generateBlockTypes(block.block_types)
    }

    return blockTypeClassBuilder.build()
}

internal fun generateObject(blockTypeName: String, attributes: Map<String, Any>): TypeSpec {
    val blockTypeClassName = Text.snakeToCamelCase(blockTypeName)
    val blockTypeClassBuilder = TypeSpec.classBuilder(blockTypeClassName)
        .superclass(HCLEntity.Inner::class)
        .addSuperclassConstructorParameter("\"$blockTypeName\"")

    for ((attributeName, attribute) in attributes) {
        blockTypeClassBuilder.addAttribute(attributeName, attribute, null)
    }

    return blockTypeClassBuilder.build()
}

internal fun TypeSpec.Builder.addBlockTypeFunction(blockTypeName: String): TypeSpec.Builder {
    val blockTypeClassName = Text.snakeToCamelCase(blockTypeName)
    return this.addFunction(
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

internal fun TypeSpec.Builder.generateBlockTypes(blockTypes: Map<String, BlockType>) {
    for ((blockTypeName, blockType) in blockTypes) {
        if (blockType.nesting_mode == "map") {
            // Map nesting mode is not used in AWS, Azure and GCP.
            throw IllegalStateException("Map nesting mode is not supported.")
        }

        this.addType(generateBlockTypeClass(blockTypeName, blockType.block))
        this.addBlockTypeFunction(blockTypeName)
    }
}
