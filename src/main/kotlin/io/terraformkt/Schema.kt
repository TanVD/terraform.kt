package io.terraformkt

data class Schema (
    val format_version : Double,
    val provider_schemas : ProviderSchemas
)

data class ProviderSchemas(val aws: AWS)
data class AWS(val resource_schemas: ResourceSchemas)
data class ResourceSchemas(val aws_lambda_function: AWSLambdaFunction);
data class AWSLambdaFunction(val version: Int, val block: AWSLambdaFunctionBlock)
data class AWSLambdaFunctionBlock(val attributes : Map<String, Map<String, Any>>)
