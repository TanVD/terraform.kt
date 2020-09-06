package io.terraformkt.plugin

import java.io.File

@DslMarker
annotation class TerraformKtDSLTag

open class TerraformKtPluginExtension {
    var generationPath: File? = null
    var tfVersion: String? = null
    var tfProvider: String? = null
    var downLoadTerraformPath: File? = null
    var schemaVersion: String? = null
}

var terraformKt = TerraformKtPluginExtension()

@TerraformKtDSLTag
fun terraformKt(configure: TerraformKtPluginExtension.() -> Unit) {
    terraformKt.configure()
}
