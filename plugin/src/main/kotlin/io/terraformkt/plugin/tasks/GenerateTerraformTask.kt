package io.terraformkt.plugin.tasks

import io.terraformkt.TerraformGenerator
import io.terraformkt.plugin.terraformKt
import io.terraformkt.utils.myResolve
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
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
        try {
            TerraformGenerator(terraformKt.downLoadTerraformPath!!.myResolve().resolve("schema.json"), terraformKt.generationPath!!).generate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
