package io.terraformkt.runtime

import io.terraformkt.terraform.TFFile
import io.terraformkt.wrapper.TerraformWrapper
import java.io.File

class TerraformFiles {
    private val terraformFiles = mutableListOf<TFFile>()

    fun addFiles(vararg tfFiles: TFFile) {
        terraformFiles.addAll(tfFiles.asIterable())
    }

    fun tf(name: String, configure: TFFile.() -> Unit) = terraformFiles.add(TFFile(name).apply(configure))

    fun writeFilesToDirectory(directory: File) {
        terraformFiles.forEach { file -> file.writeToDirectory(directory) }
    }

    fun terraformApply(terraformExecutable: File) {
        TerraformWrapper.applyTerraform(terraformFiles, terraformExecutable, File(terraformExecutable.parentFile, "tfFiles"))
    }
}

fun terraformFiles(configure: TerraformFiles.() -> Unit) = TerraformFiles().apply(configure)
