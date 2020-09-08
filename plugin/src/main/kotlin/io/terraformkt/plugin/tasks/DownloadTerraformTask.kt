package io.terraformkt.plugin.tasks

import io.terraformkt.plugin.terraformKt
import io.terraformkt.utils.Archive
import io.terraformkt.utils.CommandLine
import io.terraformkt.utils.CommandLine.os
import io.terraformkt.utils.Downloads
import io.terraformkt.utils.normalize
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
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

    @get:OutputFile
    val file: File?
        get() = terraformKt.terraform.downloadPath!!.normalize().resolve("terraform")

    @TaskAction
    fun download() {
        if (terraformKt.terraform.downloadPath == null) {
            logger.error("downLoadTerraformPath is not specified")
        }
        if (version == null) {
            logger.error("tfVersion is not specified")
        }

        logger.lifecycle("Downloading terraform version $version for OS $os")
        Downloads.download(URL("https://releases.hashicorp.com/terraform/$version/terraform_${version}_$os.zip"), file!!.parentFile, Archive.ZIP)

        CommandLine.execute("chmod", listOf("+x", file!!.absolutePath), file!!.parentFile, false)

        logger.lifecycle("Terraform version $version for OS $os successfully downloaded")
    }
}
