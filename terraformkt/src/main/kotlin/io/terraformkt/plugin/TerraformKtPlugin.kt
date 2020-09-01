package io.terraformkt.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

class TerraformKtPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.afterEvaluate {
            target.mySourceSets.apply {
                this["main"].java.srcDir(terraformKt.generationPath!!)
            }
        }

        val generateTerraform = target.tasks.create("generateTerraform", GenerateTerraform::class.java)
    }
}