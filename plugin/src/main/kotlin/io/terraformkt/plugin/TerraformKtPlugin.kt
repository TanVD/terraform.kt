package io.terraformkt.plugin

import io.terraformkt.plugin.tasks.DownloadSchemaTask
import io.terraformkt.plugin.tasks.DownloadTerraformTask
import io.terraformkt.plugin.tasks.GenerateTerraformTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

class TerraformKtPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.afterEvaluate {
            target.mySourceSets.apply {
                this["main"].java.srcDir(terraformKt.generationPath!!)
            }

            target.configurations.getByName("implementation").dependencies.add(
                target.dependencies.add("implementation", "io.terraformkt:entities:0.1.0")
            )
        }

        target.tasks.create("generateTerraform", GenerateTerraformTask::class.java)
        target.tasks.create("downloadTerraform", DownloadTerraformTask::class.java)
        target.tasks.create("downloadSchema", DownloadSchemaTask::class.java)
    }
}