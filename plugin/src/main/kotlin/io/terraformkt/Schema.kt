package io.terraformkt

data class Schema(
    val format_version: Double,
    val provider_schemas: Map<String, Schemas>
)

data class Schemas(val provider: Configuration, val resource_schemas: Map<String, Configuration>, val data_source_schemas: Map<String, Configuration>)

data class Configuration(val version: Int, val block: ConfigurationBlock)
data class ConfigurationBlock(val attributes: Map<String, Map<String, Any>>)
