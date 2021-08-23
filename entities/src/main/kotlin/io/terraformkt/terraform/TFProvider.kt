package io.terraformkt.terraform

import io.terraformkt.hcl.*
import io.terraformkt.utils.withIndent

open class TFProvider(private val tf_provider: String) : HCLEntity.Named() {
    var alias by text()

    override val hcl_name: String
        get() = if (::alias.isSet) {
            "$tf_provider.$alias"
        } else {
            tf_provider
        }

    override val hcl_ref: String
        get() = hcl_name

    override val myOwner: HCLNamed?
        get() = this

    override fun render(): String {
        return """
            |provider "$tf_provider" {
            |${super.render().withIndent()}
            |}
            """.trimMargin()
    }
}
