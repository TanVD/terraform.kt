package io.terraformkt.examples

import io.terraformkt.aws.resource.apigateway.*
import io.terraformkt.hcl.ref
import io.terraformkt.runtime.terraform

fun apiGateway() {
    terraform {
        tf("api_gateway") {
            val rest_api = api_gateway_rest_api("example_api") {
                name = "example API"
            }

            val resource = api_gateway_resource("resource_example") {
                rest_api_id = rest_api::id.ref
                parent_id = rest_api::root_resource_id.ref
                path_part = "example_resource"
            }

            api_gateway_method_response("example_response") {
                http_method = "GET"
                rest_api_id = rest_api::id.ref
                resource_id = resource::id.ref
                responseParameters(mapOf("key" to true))
                status_code = "200"
            }

            val method = api_gateway_method("method_example") {
                rest_api_id = rest_api::id.ref
                resource_id = resource::id.ref
                http_method = "GET"
                authorization = "NONE"
            }

            api_gateway_integration("integration_example") {
                rest_api_id = rest_api::id.ref
                resource_id = resource::id.ref
                http_method = method::http_method.ref
                type = "MOCK"
            }

            api_gateway_deployment("example") {
                rest_api_id = "example_id"
                variables(
                    mapOf(
                        "deployed_at" to eval(timestamp()),
                        "key" to "value"
                    )
                )
                lifecycle {
                    create_before_destroy = true
                }
            }
        }
    }.generate()
}

private fun timestamp() = "timestamp()"

private fun eval(func: String) = "\${$func}"
