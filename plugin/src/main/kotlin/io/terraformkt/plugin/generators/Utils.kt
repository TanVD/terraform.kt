package io.terraformkt.plugin.generators

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.terraformkt.BlockType
import io.terraformkt.hcl.HCLEntity
import io.terraformkt.utils.Text

internal fun TypeSpec.Builder.addAttribute(attributeName: String, attribute: Map<String, Any>) {
    val type = getFieldType(attribute)

    // TODO support map of objects
    if (type == FieldTypeWithoutDelegate.ANY) {
        return
    }

    if (type == FieldTypeWithoutDelegate.OBJECT_LIST) {
        val typeMap = attribute["type"] as ArrayList<*>
        val attributes = (typeMap[1] as ArrayList<*>)[1] as Map<String, Any>
        this.addType(generateObject(attributeName, attributes))
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
        if (attribute.containsKey("description")) {
            propertyBuilder.addKdoc(attribute["description"] as String)
        }
        this.addProperty(propertyBuilder.build())
    }
}

internal fun TypeSpec.Builder.addAttribute(attributeName: String, attribute: Any) {
    val type = getFieldType(attribute)

    // TODO support map of objects
    if (type == FieldTypeWithoutDelegate.ANY) {
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
        this.addProperty(propertyBuilder.build())
    }
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
                ) // TODO find better way to get type parameter
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

internal fun generateBlockTypeClass(blockTypeName: String, attributes: Map<String, Map<String, Any>>): TypeSpec {
    val blockTypeClassName = Text.snakeToCamelCase(blockTypeName)
    val blockTypeClassBuilder = TypeSpec.classBuilder(blockTypeClassName)
        .superclass(HCLEntity.Inner::class)
        .addSuperclassConstructorParameter("\"$blockTypeName\"")

    for ((attributeName, attribute) in attributes) {
        blockTypeClassBuilder.addAttribute(attributeName, attribute)
    }

    return blockTypeClassBuilder.build()
}

internal fun generateObject(blockTypeName: String, attributes: Map<String, Any>): TypeSpec {
    val blockTypeClassName = Text.snakeToCamelCase(blockTypeName)
    val blockTypeClassBuilder = TypeSpec.classBuilder(blockTypeClassName)
        .superclass(HCLEntity.Inner::class)
        .addSuperclassConstructorParameter("\"$blockTypeName\"")

    for ((attributeName, attribute) in attributes) {
        blockTypeClassBuilder.addAttribute(attributeName, attribute)
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

internal fun generateBlockTypes(blockTypes: Map<String, BlockType>, resourceClassBuilder: TypeSpec.Builder) {
    for ((blockTypeName, blockType) in blockTypes) {
        if (blockType.nesting_mode == "map") {
            // TODO support map
            continue
        }
        if (blockType.block.attributes == null) {
            // TODO support other cases
            continue
        }
        resourceClassBuilder.addType(generateBlockTypeClass(blockTypeName, blockType.block.attributes))
        resourceClassBuilder.addBlockTypeFunction(blockTypeName)
    }
}
