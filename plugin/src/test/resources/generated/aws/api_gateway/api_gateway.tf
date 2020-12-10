resource "aws_api_gateway_deployment" "example" {
  rest_api_id = "example_id"
  variables = {
    "deployed_at" = timestamp()
    "key" = "value"
  }
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_integration" "integration_example" {
  http_method = aws_api_gateway_method.method_example.http_method
  resource_id = aws_api_gateway_resource.resource_example.id
  rest_api_id = aws_api_gateway_rest_api.example_api.id
  type = "MOCK"
}

resource "aws_api_gateway_method" "method_example" {
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.resource_example.id
  rest_api_id = aws_api_gateway_rest_api.example_api.id
}

resource "aws_api_gateway_method_response" "example_response" {
  http_method = "GET"
  resource_id = aws_api_gateway_resource.resource_example.id
  rest_api_id = aws_api_gateway_rest_api.example_api.id
  status_code = "200"
  response_parameters = {
    "key" = true
  }
}

resource "aws_api_gateway_resource" "resource_example" {
  parent_id = aws_api_gateway_rest_api.example_api.root_resource_id
  path_part = "example_resource"
  rest_api_id = aws_api_gateway_rest_api.example_api.id
}

resource "aws_api_gateway_rest_api" "example_api" {
  name = "example API"
}

