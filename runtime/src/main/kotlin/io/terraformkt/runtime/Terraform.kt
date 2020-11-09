package io.terraformkt.runtime

import io.terraformkt.terraform.TFFile
import io.terraformkt.wrapper.TerraformWrapper
import java.io.File

class Terraform(
    private val version: String = "0.13.0",
    private val binPath: File = File(System.getProperty("user.home")).resolve(".terraform/$version/terraform"),
    private val filesPath: File = File(System.getProperty("user.dir")).resolve("terraform")
) {
    init {
        TerraformWrapper.downloadTerraform(binPath.parentFile, version)
    }

    private val files = ArrayList<TFFile>()

    fun addFiles(vararg tfFiles: TFFile) {
        files.addAll(tfFiles.asIterable())
    }

    fun tf(name: String, configure: TFFile.() -> Unit) = files.add(TFFile(name).apply(configure))

    fun apply() {
        TerraformWrapper.terraformApply(files, binPath, filesPath)
    }

    fun plan() {
        TerraformWrapper.terraformPlan(files, binPath, filesPath)
    }

    fun generate(directoryToWriteFiles: File = filesPath) {
        TerraformWrapper.terraformGenerate(files, directoryToWriteFiles)
    }
}

fun terraform(configure: Terraform.() -> Unit) = Terraform().apply(configure)
