package io.terraformkt.plugin.tasks

import io.terraformkt.plugin.terraformKt
import io.terraformkt.utils.CommandLine
import io.terraformkt.utils.myResolve
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

open class DownloadSchemaTask : DefaultTask() {
    init {
        group = "terraformkt"
        outputs.upToDateWhen { false }
    }

    @get:InputDirectory
    var root: File = terraformKt.tfConfig!!.parentFile!!.myResolve()

    @TaskAction
    fun execOperation() {
        val terraformPath = root.resolve("terraform").absolutePath
        CommandLine.executeOrFail(terraformPath, listOf("init"), root, redirectStdout = true, redirectErr = true)
        CommandLine.executeOrFailToFile(terraformPath, listOf("providers", "schema", "-json"), root,
            root.resolve("schema.json"), redirectErr = true)

    }
}
