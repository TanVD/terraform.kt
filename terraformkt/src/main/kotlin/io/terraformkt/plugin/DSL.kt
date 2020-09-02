package io.terraformkt.plugin

import java.io.File

@DslMarker
annotation class TerraformKtDSLTag

open class TerraformKtPluginExtension {
    var jsonSchemaFile: File? = null
    var generationPath: File? = null
    var tfVersion: String? = null
    var tfPath: File? = null
}

var terraformKt = TerraformKtPluginExtension()

@TerraformKtDSLTag
fun terraformKt(configure: TerraformKtPluginExtension.() -> Unit) {
    terraformKt.configure()
}
