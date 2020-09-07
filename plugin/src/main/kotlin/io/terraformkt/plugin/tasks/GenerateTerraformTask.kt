package io.terraformkt.plugin.tasks

import io.terraformkt.TerraformGenerator
import io.terraformkt.plugin.terraformKt
import io.terraformkt.utils.normalize
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateTerraformTask : DefaultTask() {
    init {
        group = "terraformkt"
    }

    @get:Input
    val tfVersion: String?
        get() = terraformKt.tfVersion

    @get:Input
    val tfProvider: String?
        get() = terraformKt.tfProvider

    @get:Input
    val schemaVersion: String?
        get() = terraformKt.schemaVersion

    @get:OutputDirectory
    val generationPath: File?
        get() = terraformKt.generationPath

    @TaskAction
    fun act() {
        if (terraformKt.downLoadTerraformPath == null) {
            logger.error("downLoadTerraformPath is not specified")
        }
        if (terraformKt.tfProvider == null) {
            logger.error("tfProvider is not specified")
        }
        if (terraformKt.generationPath == null) {
            logger.error("generationPath is not specified")
        }

        try {
            TerraformGenerator(
                terraformKt.downLoadTerraformPath!!.normalize().resolve("schema.json"),
                terraformKt.generationPath!!,
                terraformKt.tfProvider!!
            ).generate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
