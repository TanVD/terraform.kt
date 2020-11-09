package io.terraformkt.plugin.tasks

import io.terraformkt.plugin.generators.TerraformGenerator
import io.terraformkt.plugin.terraformKt
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

open class GenerateTerraformTask : DefaultTask() {
    init {
        group = "terraformkt"
    }

    @get:Input
    val providerName: String?
        get() = project.terraformKt.provider.name

    @get:OutputDirectory
    val generationPath: File
        get() = project.terraformKt.getGenerationPathOrDefault(project)

    @TaskAction
    fun act() {
        require(providerName != null) { "provider name is not specified" }

        try {
            TerraformGenerator(
                project.terraformKt.terraform.getDownloadPathOrDefault(project).resolve("schema.json"),
                generationPath,
                providerName!!
            ).generate()
        } catch (e: Exception) {
            logger.error("Exception happened during generation of Terraform DSL", e)
        }
    }
}
