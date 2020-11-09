package io.terraformkt.plugin.generators

import io.terraformkt.Schema
import io.terraformkt.utils.Json
import io.terraformkt.utils.NamesUtils
import java.io.File

class TerraformGenerator(
    private val pathToSchema: File, private val generationPath: File,
    private val providerName: String
) {
    private val packageNameProvider = NamesUtils(providerName)

    fun generate() {
        val jsonString = pathToSchema.readText()
        val schema = Json.parse<Schema>(jsonString)

        val schemas = schema.provider_schemas.values.single()
        val provider = schemas.provider
        val resources = schemas.resource_schemas
        val data = schemas.data_source_schemas

        val providerGenerator = ProviderGenerator(providerName, packageNameProvider, generationPath)
        providerGenerator.generateProvider(provider)

        val resourcesAndDataGenerator = ResourcesAndDataGenerator(
            generationPath, providerName,
            packageNameProvider
        )
        resourcesAndDataGenerator.generateResourceOrData(resources, ResourceType.RESOURCE)
        resourcesAndDataGenerator.generateResourceOrData(data, ResourceType.DATA)
    }
}
