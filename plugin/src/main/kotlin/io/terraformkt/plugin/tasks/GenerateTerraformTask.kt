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
    val providerName: String?
        get() = terraformKt.provider.name

    @get:OutputDirectory
    val generationPath: File?
        get() = terraformKt.generationPath

    @TaskAction
    fun act() {
        require(providerName != null) { "provider name is not specified" }
        require(generationPath != null) { "generationPath is not specified" }

        try {
            TerraformGenerator(
                terraformKt.terraform.downloadPath!!.normalize().resolve("schema.json"),
                generationPath!!,
                providerName!!
            ).generate()
        } catch (e: Exception) {
            logger.error("Exception happened during generation of Terraform DSL", e)
        }
    }
}
