package io.terraformkt.plugin.generators

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import io.terraformkt.hcl.HCLEntity
import io.terraformkt.hcl.HCLMapField

enum class FieldType(val delegateName: String?, val typeName: TypeName) {
    STRING(HCLEntity::text.name, com.squareup.kotlinpoet.STRING),
    NUMBER(HCLEntity::int.name, INT),
    BOOL(HCLEntity::bool.name, BOOLEAN),
    STRING_LIST(HCLEntity::textList.name, Array<String>::class.asClassName().parameterizedBy(com.squareup.kotlinpoet.STRING)),
    NUMBER_LIST(HCLEntity::intList.name, Array<Int>::class.asClassName().parameterizedBy(INT)),
    BOOL_LIST(HCLEntity::boolList.name, Array<Boolean>::class.asClassName().parameterizedBy(BOOLEAN)),
    OBJECT_LIST(null, HCLEntity.Inner::class.asClassName()), // TODO don't use null
    STRING_MAP(null, HCLMapField::class.asClassName().parameterizedBy(com.squareup.kotlinpoet.STRING)),
    NUMBER_MAP(null, HCLMapField::class.asClassName().parameterizedBy(INT)),
    BOOL_MAP(null, HCLMapField::class.asClassName().parameterizedBy(BOOLEAN)),
    ANY("", com.squareup.kotlinpoet.ANY)
}

internal fun getType(attr: Map<String, Any>): FieldType {
    require(attr.containsKey("type")) {
        "No type parameter for the attribute."
    }

    if (attr["type"] is String) {
        when (attr["type"]) {
            "string" -> return FieldType.STRING
            "number" -> return FieldType.NUMBER
            "bool" -> return FieldType.BOOL
        }
    }

    if (attr["type"] is ArrayList<*>) {
        val typeMap = attr["type"] as ArrayList<*>
        if ((typeMap[0] == "list" || typeMap[0] == "set") && typeMap[1] is String) {
            when (typeMap[1]) {
                "string" -> return FieldType.STRING_LIST
                "number" -> return FieldType.NUMBER_LIST
                "bool" -> return FieldType.BOOL_LIST
            }
        }

        if (typeMap[1] is ArrayList<*> && (typeMap[1] as ArrayList<*>)[0] == "object") {
            return FieldType.OBJECT_LIST
        }

        if ((typeMap[0] == "map") && typeMap[1] is String) {
            when (typeMap[1]) {
                "string" -> return FieldType.STRING_MAP
                "number" -> return FieldType.NUMBER_MAP
                "bool" -> return FieldType.BOOL_MAP
            }
        }
    }

    // TODO support map of objects.
    return FieldType.ANY
}

internal fun getType(attr: Any): FieldType {
    if (attr is String) {
        when (attr) {
            "string" -> return FieldType.STRING
            "number" -> return FieldType.NUMBER
            "bool" -> return FieldType.BOOL
        }
    }

    if (attr is ArrayList<*>) {
        val typeMap = attr
        if ((typeMap[0] == "list" || typeMap[0] == "set") && typeMap[1] is String) {
            when (typeMap[1]) {
                "string" -> return FieldType.STRING_LIST
                "number" -> return FieldType.NUMBER_LIST
                "bool" -> return FieldType.BOOL_LIST
            }
        }
    }

    // TODO support collections of objects.
    return FieldType.ANY
}

fun typeToDelegate(type: FieldType, isComputed: Boolean): String {
    return type.delegateName + if (isComputed) "(computed = true)" else "()"
}
