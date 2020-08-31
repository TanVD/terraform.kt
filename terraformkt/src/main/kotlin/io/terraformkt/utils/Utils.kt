package io.terraformkt.utils

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