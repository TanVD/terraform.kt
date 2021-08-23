package io.terraformkt.plugin.tasks

import io.terraformkt.plugin.terraformKt
import io.terraformkt.wrapper.TerraformWrapper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

open class DownloadTerraformTask : DefaultTask() {
    init {
        group = "terraformkt"
    }

    @get:Input
    val version: String
        get() = project.terraformKt.terraform.version

    @get:InputDirectory
    val downloadPath: File
        get() = project.terraformKt.terraform.getDownloadPathOrDefault(project)

    @Suppress("unused")
    @get:OutputFile
    val terraformFile: File
        get() = downloadPath.resolve("terraform")

    @TaskAction
    fun download() {
        logger.lifecycle("Downloading terraform version $version")

        TerraformWrapper.Download.terraform(downloadPath, version)

        logger.lifecycle("Terraform version $version successfully downloaded")
    }
}
