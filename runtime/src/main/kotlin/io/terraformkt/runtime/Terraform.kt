package io.terraformkt.runtime

import io.terraformkt.terraform.TFFile
import io.terraformkt.wrapper.TerraformWrapper
import java.io.File

class Terraform {
    private val terraformFiles = mutableListOf<TFFile>()

    fun addFiles(vararg tfFiles: TFFile) {
        terraformFiles.addAll(tfFiles.asIterable())
    }

    fun tf(name: String, configure: TFFile.() -> Unit) = terraformFiles.add(TFFile(name).apply(configure))

    fun apply(terraformExecutable: File) {
        TerraformWrapper.terraformApply(terraformFiles, terraformExecutable, File(terraformExecutable.parentFile, "tfFiles"))
    }

    fun plan(terraformExecutable: File) {
        TerraformWrapper.terraformPlan(terraformFiles, terraformExecutable, File(terraformExecutable.parentFile, "tfFiles"))
    }

    fun generate(directoryToWriteFiles: File) {
        TerraformWrapper.terraformGenerate(terraformFiles, directoryToWriteFiles)
    }
}

fun terraform(configure: Terraform.() -> Unit) = Terraform().apply(configure)
