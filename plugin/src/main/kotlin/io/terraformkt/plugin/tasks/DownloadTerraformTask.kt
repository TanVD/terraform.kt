package io.terraformkt.plugin.tasks

import io.terraformkt.wrapper.TerraformWrapper
import io.terraformkt.plugin.terraformKt
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class DownloadTerraformTask : DefaultTask() {
    init {
        group = "terraformkt"
    }

    @get:Input
    val version: String?
        get() = terraformKt.terraform.version

    @get:InputDirectory
    val downloadPath: File
        get() = terraformKt.terraform.getDownloadPathOrDefault(project)

    @get:OutputFile
    val terraformFile: File?
        get() = downloadPath.resolve("terraform")

    @TaskAction
    fun download() {
        require(version != null) { "terraform version is not specified" }

        logger.lifecycle("Downloading terraform version $version")

        TerraformWrapper.downloadTerraform(downloadPath, version!!)

        logger.lifecycle("Terraform version $version successfully downloaded")
    }
}
