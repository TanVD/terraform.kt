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
        get() = terraformKt.provider.version

    @get:Input
    val tfProvider: String?
        get() = terraformKt.provider.name

    @get:Input
    val schemaVersion: String?
        get() = terraformKt.terraform.version

    @get:OutputDirectory
    val generationPath: File?
        get() = terraformKt.generationPath

    @TaskAction
    fun act() {
        if (terraformKt.terraform.downloadPath == null) {
            logger.error("downLoadTerraformPath is not specified")
        }
        if (tfProvider == null) {
            logger.error("tfProvider is not specified")
        }
        if (generationPath == null) {
            logger.error("generationPath is not specified")
        }

        try {
            TerraformGenerator(
                terraformKt.terraform.downloadPath!!.normalize().resolve("schema.json"),
                terraformKt.generationPath!!,
                tfProvider!!
            ).generate()
        } catch (e: Exception) {
            logger.error("Exception happened during generation of Terraform DSL", e)
        }
    }
}
