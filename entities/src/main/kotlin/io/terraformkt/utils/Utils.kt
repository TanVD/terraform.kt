package io.terraformkt.utils

import io.terraformkt.hcl.HCLMapField

/**
 * Unlink is used to restyle old `"${ref}"` style refs to Terraform 12 style `ref`
 */
fun unlink(field: String): String {
    if (isLink(field)) return field.drop(2).dropLast(1)
    return field
}

fun isLink(field: String) = field.startsWith("\${") && field.endsWith("}")

fun link(field: String) = "\${$field}"
fun link(vararg parts: String?) = link(parts.filterNotNull().joinToString(separator = "."))

fun toText(value: String) = if (isLink(value)) unlink(value) else "\"$value\""

// Suppose any can be int, string, bool and map.
fun anyToText(any: Any): String {
    if (any is String) {
        return toText(any)
    }
    if (any is Map<*, *>) {
        return "{${HCLMapField.renderMap(any as Map<String, Any>)}}"
    }
    return "$any"
}
