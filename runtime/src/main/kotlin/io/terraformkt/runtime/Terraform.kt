package io.terraformkt.runtime

import io.terraformkt.terraform.TFFile
import io.terraformkt.wrapper.TerraformWrapper
import java.io.File

class Terraform(
    private val terraformVersion: String = "0.13.0",
    private val terraformPath: File = File(System.getProperty("user.home")).resolve(".terraform/$terraformVersion/terraform"),
    private val terraformFilesPath: File = File(System.getProperty("user.dir")).resolve("terraform")
) {
    init {
        TerraformWrapper.downloadTerraform(terraformPath.parentFile, terraformVersion)
    }

    private val terraformFiles = mutableListOf<TFFile>()

    fun addFiles(vararg tfFiles: TFFile) {
        terraformFiles.addAll(tfFiles.asIterable())
    }

    fun tf(name: String, configure: TFFile.() -> Unit) = terraformFiles.add(TFFile(name).apply(configure))

    fun apply() {
        TerraformWrapper.terraformApply(terraformFiles, terraformPath, terraformFilesPath)
    }

    fun plan() {
        TerraformWrapper.terraformPlan(terraformFiles, terraformPath, terraformFilesPath)
    }

    fun generate(directoryToWriteFiles: File = terraformFilesPath) {
        TerraformWrapper.terraformGenerate(terraformFiles, directoryToWriteFiles)
    }
}

fun terraform(configure: Terraform.() -> Unit) = Terraform().apply(configure)
