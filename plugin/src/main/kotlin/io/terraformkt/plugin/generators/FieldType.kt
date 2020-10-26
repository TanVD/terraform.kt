package io.terraformkt.plugin.generators

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import io.terraformkt.hcl.HCLEntity
import io.terraformkt.hcl.HCLMapField

interface FieldType {
    fun hasDelegate(): Boolean
}

enum class FieldTypeWithDelegate(val delegateName: String, val typeName: TypeName) : FieldType {
    STRING(HCLEntity::text.name, com.squareup.kotlinpoet.STRING),
    NUMBER(HCLEntity::int.name, INT),
    BOOL(HCLEntity::bool.name, BOOLEAN),
    STRING_LIST(
        HCLEntity::textList.name, Array<String>::
        class.asClassName().parameterizedBy(com.squareup.kotlinpoet.STRING)
    ),
    NUMBER_LIST(
        HCLEntity::intList.name, Array<Int>::
        class.asClassName().parameterizedBy(INT)
    ),
    BOOL_LIST(
        HCLEntity::boolList.name, Array<Boolean>::
        class.asClassName().parameterizedBy(BOOLEAN)
    );

    override fun hasDelegate(): Boolean {
        return true
    }
}

enum class FieldTypeWithoutDelegate(val typeName: TypeName) : FieldType {
    OBJECT_LIST(HCLEntity.Inner::class.asClassName()),
    STRING_MAP(HCLMapField::class.asClassName().parameterizedBy(com.squareup.kotlinpoet.STRING)),
    NUMBER_MAP(HCLMapField::class.asClassName().parameterizedBy(INT)),
    BOOL_MAP(HCLMapField::class.asClassName().parameterizedBy(BOOLEAN)),
    ANY(com.squareup.kotlinpoet.ANY);

    override fun hasDelegate(): Boolean {
        return false
    }
}

internal fun getFieldType(attr: Map<String, Any>): FieldType {
    require(attr.containsKey("type")) {
        "No type parameter for the attribute."
    }

    return getFieldType(attr["type"]!!)
}

internal fun getFieldType(type: Any): FieldType {
    if (type is String) {
        when (type) {
            "string" -> return FieldTypeWithDelegate.STRING
            "number" -> return FieldTypeWithDelegate.NUMBER
            "bool" -> return FieldTypeWithDelegate.BOOL
        }
    }

    if (type is ArrayList<*>) {
        if ((type[0] == "list" || type[0] == "set")) {
            if (type[1] is String) {
                when (type[1]) {
                    "string" -> return FieldTypeWithDelegate.STRING_LIST
                    "number" -> return FieldTypeWithDelegate.NUMBER_LIST
                    "bool" -> return FieldTypeWithDelegate.BOOL_LIST
                }
            }

            if (type[1] is ArrayList<*> && (type[1] as ArrayList<*>)[0] == "object") {
                return FieldTypeWithoutDelegate.OBJECT_LIST
            }
        }

        if ((type[0] == "map") && type[1] is String) {
            when (type[1]) {
                "string" -> return FieldTypeWithoutDelegate.STRING_MAP
                "number" -> return FieldTypeWithoutDelegate.NUMBER_MAP
                "bool" -> return FieldTypeWithoutDelegate.BOOL_MAP
            }
        }
    }

    // TODO support map of objects.
    return FieldTypeWithoutDelegate.ANY
}

fun typeToDelegate(type: FieldTypeWithDelegate): String {
    return type.delegateName + "()"
}
