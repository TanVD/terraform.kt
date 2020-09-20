package io.terraformkt.example

import io.terraformkt.local.provider.provider
import io.terraformkt.local.resource.file.file
import io.terraformkt.runtime.terraformFiles
import java.io.File

fun main() {
    terraformFiles {
        tf("my_file") {
            provider {
                version = "~> 1.4"
            }
            file("hello") {
                content = "Hello, Terraform"
                filename = "hello.txt"
            }
        }
    }.terraformApply(File("build/localProvider/tf/terraform"))
}

