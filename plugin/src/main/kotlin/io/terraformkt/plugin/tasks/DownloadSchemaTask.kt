package io.terraformkt.plugin.tasks

import io.terraformkt.plugin.terraformKt
import io.terraformkt.wrapper.TerraformWrapper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

open class DownloadSchemaTask : DefaultTask() {
    init {
        group = "terraformkt"
    }

    @get:Input
    val providerName: String?
        get() = project.terraformKt.provider.name

    @get:Input
    val providerVersion: String?
        get() = project.terraformKt.provider.version

    @get:InputDirectory
    val downloadPath: File
        get() = project.terraformKt.terraform.getDownloadPathOrDefault(project)

    @TaskAction
    fun download() {
        require(providerName != null) { "provider name is not specified" }
        require(providerVersion != null) { "provider version is not specified " }

        TerraformWrapper.Download.schema(downloadPath, providerName!!, providerVersion!!)
    }
}
