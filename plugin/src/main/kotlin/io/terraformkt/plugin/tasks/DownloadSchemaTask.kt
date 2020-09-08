package io.terraformkt.plugin.tasks

import io.terraformkt.plugin.terraformKt
import io.terraformkt.utils.CommandLine
import io.terraformkt.utils.normalize
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class DownloadSchemaTask : DefaultTask() {
    init {
        group = "terraformkt"

        // TODO maybe remove it
        outputs.upToDateWhen { false }
    }

    @get:Input
    val providerName: String?
        get() = terraformKt.provider.name

    @get:Input
    val providerVersion: String?
        get() = terraformKt.provider.version

    @get:InputDirectory
    val downloadPath: File?
        get() = terraformKt.terraform.downloadPath!!.normalize()

    @TaskAction
    fun download() {
        require(providerName != null) { "provider name is not specified" }
        require(providerVersion != null) { "provider version is not specified " }
        require(downloadPath != null) { "terraform downloadPath is not specified" }


        val terraformPath = downloadPath!!.resolve("terraform").absolutePath
        createConfigFile(providerName!!, providerVersion!!)

        CommandLine.executeOrFail(terraformPath, listOf("init"), downloadPath!!, redirectStdout = true, redirectErr = true)
        CommandLine.executeOrFailToFile(
            terraformPath, listOf("providers", "schema", "-json"), downloadPath!!,
            downloadPath!!.resolve("schema.json"), redirectErr = true
        )
    }

    private fun createConfigFile(tfProvider: String, schemaVersion: String) {
        val configFile = terraformKt.terraform.downloadPath!!.normalize().resolve("config.tf")
        configFile.createNewFile()
        configFile.writeText(
            """
        |provider "$tfProvider" {
        |    version = "$schemaVersion"
        |}
        """.trimMargin()
        )
    }
}
