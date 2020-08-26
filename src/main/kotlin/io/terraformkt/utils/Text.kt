package io.terraformkt.utils

object Text {
    private val camelCaseRegex = Regex("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")
    private val snakeCaseRegex = Regex("_[a-zA-Z]")

    fun decamelize(text: String): List<String> = text.split(camelCaseRegex)
    fun desnake(text: String): List<String> = text.split("_")
    fun dehyphen(text: String): List<String> = text.split("-")
    fun dedot(text: String): List<String> = text.split(".")

    fun deall(text: String) = decamelize(text)
        .flatMap { desnake(it) }
        .flatMap { dehyphen(it) }
        .flatMap { dedot(it) }
        .filter { it.isNotBlank() }

    fun snakeToCamelCase(text: String): String {
        return snakeCaseRegex.replace(text) {
            it.value.replace("_", "")
                .toUpperCase()
        }.capitalize()
    }

    const val indent = 2

    fun indent(number: Int = indent) = " ".repeat(number)
}

fun String.withIndent(number: Int = Text.indent) = prependIndent(Text.indent(number))

fun String.plusIterable(value: Iterable<String>) = listOf(this) + value.toList()
