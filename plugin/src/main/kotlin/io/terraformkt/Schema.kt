package io.terraformkt

data class Schema(
    val format_version: Double,
    val provider_schemas: Map<String, AWS>
)

data class AWS(val resource_schemas: Map<String, Resource>, val data_source_schemas: Map<String, Resource>)
data class Resource(val version: Int, val block: ResourceBlock)
data class ResourceBlock(val attributes: Map<String, Map<String, Any>>)
