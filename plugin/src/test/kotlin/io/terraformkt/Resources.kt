package io.terraformkt

object Resources {
    fun read(file: String) = Resources::class.java.getResourceAsStream(file).use { it.reader().readText() }
}
