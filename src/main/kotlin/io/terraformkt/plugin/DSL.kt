package io.terraformkt.plugin

import java.io.File

@DslMarker
annotation class RuntimeDSLTag

open class TerraformKtPluginExtension {
    var jsonSchemaFile: File? = null
    var generationPath: File? = null
}

var terraformKt = TerraformKtPluginExtension()

@RuntimeDSLTag
fun terraformKt(configure: TerraformKtPluginExtension.() -> Unit) {
    terraformKt.configure()
}
