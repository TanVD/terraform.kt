package io.terraformkt.plugin.tasks

import io.terraformkt.plugin.terraformKt
import io.terraformkt.utils.Archive
import io.terraformkt.utils.CommandLine
import io.terraformkt.utils.CommandLine.os
import io.terraformkt.utils.Downloads
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URL

open class DownloadTerraformTask : DefaultTask() {
    init {
        group = "terraformkt"
    }

    @get:Input
    val version: String?
        get() = terraformKt.terraform.version

    @get:InputDirectory
    val downloadPath: File
        get() = terraformKt.terraform.getDownloadPathOrDefault(project)

    @get:OutputFile
    val terraformFile: File?
        get() = downloadPath.resolve("terraform")

    @TaskAction
    fun download() {
        require(version != null) { "terraform version is not specified" }

        logger.lifecycle("Downloading terraform version $version for OS $os")
        Downloads.download(URL("https://releases.hashicorp.com/terraform/$version/terraform_${version}_$os.zip"), downloadPath, Archive.ZIP)

        CommandLine.execute("chmod", listOf("+x", terraformFile!!.absolutePath), downloadPath, false)

        logger.lifecycle("Terraform version $version for OS $os successfully downloaded")
    }
}
