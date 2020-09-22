package io.terraformkt.example

import io.terraformkt.local.provider.provider
import io.terraformkt.local.resource.file.file
import io.terraformkt.runtime.terraform
import java.io.File

fun main() {
    terraform {
        tf("my_file") {
            provider {
                version = "~> 1.4"
            }
            file("hello") {
                content = "Hello, Terraform"
                filename = "hello.txt"
            }
        }
    }.apply(File("build/localProvider/tf/terraform"))
}

