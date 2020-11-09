# Terraform.kt

Terraform.kt project aims to bring all the advantages of using Kotlin language to the process of writing Terraform configuration files.

## Example
For the example of generating and using Kotlin DSL see `example/aws` folder. 
To generate Kotlin DSL files run

`./gradlew generateTerraform`

For the usage of Kotlin DSL the following commands are supported:
- generate() - generates Terraform configuration files in HCL from Kotlin DSL
- plan() - generates Terraform configuration files and runs `terraform plan` against them
- apply() - generates Terraform configuration files and runs `terraform apply` against them
