package io.terraformkt.terraform

import io.terraformkt.hcl.HCLEntity
import io.terraformkt.hcl.HCLNamed
import io.terraformkt.utils.withIndent

/** Representation of Terraform Data */
open class TFData(val tf_id: String, val tf_type: String) : HCLEntity.Named(), HCLNamed {
    override val hcl_name: String = "data.$tf_type.$tf_id"
    override val hcl_ref: String
        get() = hcl_name

    override val myOwner: HCLNamed?
        get() = this

    var provider by text()

    override fun render(): String {
        return """
            |data "$tf_type" "$tf_id" {
            |${super.render().withIndent()}
            |}
        """.trimMargin()
    }
}
