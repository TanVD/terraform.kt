package io.terraformkt.utils

import io.terraformkt.plugin.generators.ResourceType

class NamesUtils(private val provider: String) {
    private val mappings = getMappings()

    companion object {
        private const val PACKAGE_PREFIX = "io.terraformkt"
        private const val PATH_PREFIX = "io/terraformkt"
        private const val RESOURCES_DIRECTORY_NAME = "resource"
        private const val DATA_DIRECTORY_NAME = "data"
        private const val PROVIDER_DIRECTORY_NAME = "provider"
    }

    fun getClassFilePath(resourceType: ResourceType, className: String): String {
        return "$PATH_PREFIX/$provider/${getDirectoryName(resourceType)}/${getClassPackageName(className)}/$className.kt"
    }

    fun getProviderFilePath(): String {
        return "$PATH_PREFIX/$provider/provider/Provider.kt"
    }

    fun getPackageName(resourceType: ResourceType, className: String): String {
        return "$PACKAGE_PREFIX.$provider.${getDirectoryName(resourceType)}.${getClassPackageName(className)}"
    }

    fun getProviderPackageName(): String {
        return "$PACKAGE_PREFIX.$provider.provider"
    }

    private fun getMappings(): Map<String, String> {
        val mappings = javaClass.classLoader.getResource("package_mapping.json").readText()
        return Json.parse(mappings)
    }

    private fun getDirectoryName(resourceType: ResourceType): String {
        return when (resourceType) {
            ResourceType.RESOURCE -> RESOURCES_DIRECTORY_NAME
            ResourceType.DATA -> DATA_DIRECTORY_NAME
            ResourceType.PROVIDER -> PROVIDER_DIRECTORY_NAME
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
