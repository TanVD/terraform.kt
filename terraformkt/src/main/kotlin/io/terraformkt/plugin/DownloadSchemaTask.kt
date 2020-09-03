package io.terraformkt.plugin

import io.terraformkt.utils.CommandLine
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

open class DownloadSchemaTask : DefaultTask() {
    init {
        group = "terraformkt"
        outputs.upToDateWhen { false }
    }

    @get:InputDirectory
    var root: File = File(System.getProperty("user.dir")).resolve(terraformKt.tfPath!!.parentFile!!)

    @TaskAction
    fun execOperation() {
        val terraformPath = File(System.getProperty("user.dir")).resolve(terraformKt.tfPath!!)!!.absolutePath
        CommandLine.executeOrFail(terraformPath, listOf("init"), root, redirectStdout = true, redirectErr = true)
        CommandLine.executeOrFailToFile(terraformPath, listOf("providers", "schema", "-json"), root,
            root.resolve("schema.json"), redirectErr = true)

    }
}
