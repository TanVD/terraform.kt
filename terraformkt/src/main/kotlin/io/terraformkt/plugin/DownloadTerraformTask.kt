package io.terraformkt.plugin

import io.terraformkt.utils.Archive
import io.terraformkt.utils.CommandLine
import io.terraformkt.utils.CommandLine.os
import io.terraformkt.utils.Downloads
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File
import java.net.URL

open class DownloadTerraformTask : DefaultTask() {
    init {
        group = "terraformkt"
    }

    @get:Input
    val version: String?
        get() = terraformKt.tfVersion

    @get:OutputFile
    val file: File?
        get() = File(System.getProperty("user.dir")).resolve(terraformKt.tfPath!!)

    @TaskAction
    fun download() {
        println("Downloading terraform version $version for OS $os")
        println(file!!.absoluteFile)
        Downloads.download(URL("https://releases.hashicorp.com/terraform/$version/terraform_${version}_$os.zip"), file!!.parentFile, Archive.ZIP)

        CommandLine.execute("chmod", listOf("+x", file!!.absolutePath), file!!.parentFile, false)

        println("Terraform version $version for OS $os successfully downloaded")
    }
}
