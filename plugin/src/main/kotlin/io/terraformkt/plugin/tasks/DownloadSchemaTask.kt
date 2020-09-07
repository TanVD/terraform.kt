package io.terraformkt.plugin.tasks

import io.terraformkt.plugin.terraformKt
import io.terraformkt.utils.CommandLine
import io.terraformkt.utils.normalize
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class DownloadSchemaTask : DefaultTask() {
    init {
        group = "terraformkt"
        outputs.upToDateWhen { false }
    }

    @get:InputDirectory
    var root: File = terraformKt.downLoadTerraformPath!!

    @TaskAction
    fun execOperation() {
        if (terraformKt.downLoadTerraformPath == null) {
            logger.error("downLoadTerraformPath is not specified")
        }
        if (terraformKt.tfProvider == null) {
            logger.error("tfProvider is not specified")
        }
        if (terraformKt.schemaVersion == null) {
            logger.error("schemaVersion is not specified")
        }

        val terraformPath = root.resolve("terraform").absolutePath
        createConfigFile(terraformKt.tfProvider!!, terraformKt.schemaVersion!!)

        CommandLine.executeOrFail(terraformPath, listOf("init"), root, redirectStdout = true, redirectErr = true)
        CommandLine.executeOrFailToFile(
            terraformPath, listOf("providers", "schema", "-json"), root,
            root.resolve("schema.json"), redirectErr = true
        )

    }

    private fun createConfigFile(tfProvider: String, schemaVersion: String) {
        val configFile = terraformKt.downLoadTerraformPath!!.normalize().resolve("config.tf")
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
