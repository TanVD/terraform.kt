package io.terraformkt.runtime

import io.terraformkt.terraform.TFFile
import java.io.File

class Terraform {
    private val terraformFiles = mutableListOf<TFFile>()

    fun addFiles(vararg tfFiles: TFFile) {
        terraformFiles.addAll(tfFiles.asIterable())
    }

    fun tf(name: String, configure: TFFile.() -> Unit) = terraformFiles.add(TFFile(name).apply(configure))

    fun writeFilesToDirectory(directory: File) {
        terraformFiles.forEach { file -> file.writeToDirectory(directory) }
    }

    fun apply() {

    }
}

fun terraform(configure: Terraform.() -> Unit) = Terraform().apply(configure)
