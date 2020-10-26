package io.terraformkt.hcl

import io.terraformkt.utils.withIndent

open class HCLMapField<T>(name: String, private val map: Map<String, T>) : HCLEntity.Inner(name) {
    override fun render(): String {
        return """
            |${tf_name} {
            |${renderMap(map)}
            |}
            """.trimMargin()
    }

    companion object {
        fun renderMap(map: Map<String, *>): String {
            return map.entries.joinToString(separator = "\n") { (key, value) -> "$key = \"$value\"" }.withIndent()
        }
    }
}
