package io.terraformkt.plugin

import io.terraformkt.TerraformGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateTerraform : DefaultTask() {
    init {
        group = "terraformkt"
    }

    @get:InputFile
    val jsonSchemaFile: File?
        get() = terraformKt.jsonSchemaFile

    @get:OutputDirectory
    val generationPath: File?
        get() = terraformKt.generationPath

    @TaskAction
    fun act() {
        throw IllegalAccessError();
//        try {
//            TerraformGenerator(terraformKt.jsonSchemaFile!!, terraformKt.generationPath!!).generate()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }
}
