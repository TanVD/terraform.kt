package io.terraformkt.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

class TerraformKtPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // TODO logging
        if (terraformKt.generationPath == null || terraformKt.jsonSchemaFile == null) {
            println("Specify generation path and json schema file")
            return
        }

        target.afterEvaluate {
            target.mySourceSets.apply {
                this["main"].java.srcDir(terraformKt.generationPath!!)
            }
        }

        target.tasks.create("generateTerraform", GenerateTerraformTask::class.java)
        target.tasks.create("downloadTerraform", DownloadTerraformTask::class.java)
        target.tasks.create("downloadSchema", DownloadSchemaTask::class.java)
    }
}
