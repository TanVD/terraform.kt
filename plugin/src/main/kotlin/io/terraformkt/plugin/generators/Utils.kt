package io.terraformkt.plugin.generators

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.terraformkt.utils.Text

internal fun TypeSpec.Builder.addAttribute(attributeName: String, attribute: Map<String, Any>) {
    val type = getType(attribute)

    val isComputed = attribute["computed"] as? Boolean ?: false

    // It means that attribute has map type
    if (type.delegateName == null) {
        this.addType(generateMapAttribute(attributeName, type.typeName))
        this.addFunction(
            generateMapClosureFunction(Text.snakeToCamelCase(attributeName))
        )
        return
    }

    val propertyBuilder = PropertySpec
        .builder(attributeName, type.typeName)
        .delegate(typeToDelegate(type, isComputed))
        .mutable(!isComputed)
    if (attribute.containsKey("description")) {
        propertyBuilder.addKdoc(attribute["description"] as String)
    }
    this.addProperty(propertyBuilder.build())
}

private fun generateMapAttribute(name: String, typeName: TypeName): TypeSpec {
    val blockTypeClassName = Text.snakeToCamelCase(name)
    val blockTypeClassBuilder = TypeSpec.classBuilder(blockTypeClassName)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("map", Map::class.asClassName().parameterizedBy(STRING, STRING))
                .build()
        )
        .superclass(typeName)
        .addSuperclassConstructorParameter("\"$name\"")
        .addSuperclassConstructorParameter("map")

    return blockTypeClassBuilder.build()
}

private fun generateMapClosureFunction(className: String): FunSpec {
    return FunSpec.builder(className.decapitalize())
        .addParameter(
            "map", Map::class.asClassName().parameterizedBy(STRING, STRING)
        )
        .addStatement("inner(%N(map))", className)
        .build()
}
