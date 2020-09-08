package io.terraformkt.plugin

import org.gradle.api.Project
import java.io.File

@DslMarker
annotation class TerraformKtDSLTag

open class TerraformKt {
    var generationPath: File? = null

    internal fun getGenerationPathOrDefault(project: Project): File {
        val path = generationPath ?: File(project.buildDir, "kotlin-gen")
        path.mkdirs()
        return path
    }

    var provider: Provider = Provider()

    var terraform: Terraform = Terraform()

    open class Provider {
        var name: String? = null
        var version: String? = null
    }

    open class Terraform {
        var version: String = "0.13.0"
        var downloadPath: File? = null

        internal fun getDownloadPathOrDefault(project: Project): File {
            val path = downloadPath ?: File(project.buildDir, "tf")
            path.mkdirs()
            return path
        }
    }

    fun provider(configure: Provider.() -> Unit) {
        provider.configure()
    }

    fun terraform(configure: Terraform.() -> Unit) {
        terraform.configure()
    }
}

var terraformKt = TerraformKt()

@TerraformKtDSLTag
fun terraformKt(configure: TerraformKt.() -> Unit) {
    terraformKt.configure()
}
