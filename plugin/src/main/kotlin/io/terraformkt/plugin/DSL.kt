package io.terraformkt.plugin

import java.io.File

@DslMarker
annotation class TerraformKtDSLTag

open class TerraformKt {
    var generationPath: File? = null

    var provider: Provider = Provider()

    var terraform: Terraform = Terraform()

    open class Provider {
        var name: String? = null
        var version: String? = null
    }

    open class Terraform {
        var version: String? = null
        var downloadPath: File? = null
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
