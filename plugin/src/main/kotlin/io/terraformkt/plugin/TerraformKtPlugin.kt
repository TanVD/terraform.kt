package io.terraformkt.plugin

import io.terraformkt.plugin.tasks.DownloadSchemaTask
import io.terraformkt.plugin.tasks.DownloadTerraformTask
import io.terraformkt.plugin.tasks.GenerateTerraformTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get

class TerraformKtPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // TODO logging
        if (terraformKt.generationPath == null) {
            println("Specify generation path and json schema file ${terraformKt.generationPath}")
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
