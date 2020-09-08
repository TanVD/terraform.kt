package io.terraformkt.utils

import io.terraformkt.TerraformGenerator

class NamesUtils(private val provider: String) {
    private val mappings = getMappings()

    companion object {
        private const val PACKAGE_PREFIX = "io.terraformkt"
        private const val PATH_PREFIX = "io/terraformkt"
        private const val RESOURCES_DIRECTORY_NAME = "resource"
        private const val DATA_DIRECTORY_NAME = "data"
    }

    fun getClassFilePath(resourceType: TerraformGenerator.ResourceType, className: String): String {
        return "$PATH_PREFIX/$provider/${getDirectoryName(resourceType)}/${getClassPackageName(className)}/$className.kt"
    }

    fun getPackageName(resourceType: TerraformGenerator.ResourceType, className: String): String {
        return "$PACKAGE_PREFIX.$provider.${getDirectoryName(resourceType)}.${getClassPackageName(className)}"
    }

    private fun getMappings(): Map<String, String> {
        val mappings = javaClass.classLoader.getResource("package_mapping.json").readText()
        return Json.parse(mappings)
    }

    private fun getDirectoryName(resourceType: TerraformGenerator.ResourceType): String {
        return when (resourceType) {
            TerraformGenerator.ResourceType.RESOURCE -> RESOURCES_DIRECTORY_NAME
            TerraformGenerator.ResourceType.DATA -> DATA_DIRECTORY_NAME
        }
    }

    private fun getClassPackageName(className: String): String {
        val packageNameEntry = mappings.entries.find { entry -> className.startsWith(entry.key) }
        if (packageNameEntry != null) {
            return packageNameEntry.value
        }
        return getFirstWordCamelCase(className).toLowerCase()
    }

    private fun getFirstWordCamelCase(word: String): String {
        return Text.decamelize(word)[0]
    }
}
