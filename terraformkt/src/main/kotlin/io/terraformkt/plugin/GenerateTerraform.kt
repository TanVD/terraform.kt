package io.terraformkt.plugin

import io.terraformkt.TerraformGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateTerraform : DefaultTask() {
    init {
        group = "terraformkt"
    }

    @get:Input
    val jsonSchemaFile: File?
        get() = terraformKt.jsonSchemaFile

    @get:OutputDirectory
    val generationPath: File?
        get() = terraformKt.generationPath

    @TaskAction
    fun act() {
        try {
            TerraformGenerator(terraformKt.jsonSchemaFile!!, terraformKt.generationPath!!).generate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}