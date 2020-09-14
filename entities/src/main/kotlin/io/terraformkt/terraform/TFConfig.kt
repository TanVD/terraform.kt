package io.terraformkt.terraform

import io.terraformkt.hcl.HCLEntity
import io.terraformkt.utils.withIndent

class TFConfig : HCLEntity.Named() {
    override val hcl_name: String = "terraform"
    override val hcl_ref: String
        get() = hcl_name

    override fun render(): String {
        return """
            |terraform {
            |${super.render().withIndent()}
            |${(backend?.render() ?: "").withIndent()}
            |}
            """.trimMargin()
    }

    var required_version by text()

    // TODO support all backends
    sealed class Backend(val type: String) : HCLEntity() {
        override fun render(): String {
            return """
            |backend "$type" {
            |${super.render().withIndent()}
            |}
            """.trimMargin()
        }

        class S3 : Backend("s3") {
            var bucket by text()
            var key by text()
            var profile by text()
            var region by text()
        }

        class Local : Backend("local") {
            var path by text()
            var workspace_dir by text()
        }

        class Remote : Backend("remote") {
            var hostname by text()
            var organization by text()
            var token by text()

            class Workspaces : HCLEntity.Inner("workspaces") {
                var name by text()
                var prefix by text()
            }

            fun workspaces(configure: Workspaces.() -> Unit) {
                inner(Workspaces().apply(configure))
            }
        }

        class Azurerm : Backend("azurerm") {
            var storage_account_name by text()
            var container_name by text()
            var key by text()
            var environment by text()
            var endpoint by text()
            var snapshot by bool()

            // Authenticating using MSI abd Service Principal
            var subscription_id by text()
            var tenant_id by text()

            // Authenticating using MSI
            var msi_endpoint by text()
            var use_msi by text()

            //  Authenticating using a SAS Token associated with the Storage Account
            var sas_token by text()

            // Authenticating using the Storage Account's Access Key
            var access_key by text()

            // Authenticating using a Service Principal
            var resource_group_name by text()
            var client_id by text()

            // Authenticating using a Service Principal with a Client Certificate
            var client_certificate_password by text()
            var client_certificate_path by text()

            // Authenticating using a Service Principal with a Client Secret
            var client_secret by text()
        }

        class GCS : Backend("gcs") {
            var bucket by text()
            var credentials by text()
            var access_token by text()
            var prefix by text()

            // TODO deprecated
            var path by text()
            var encryption_key by text()
        }
    }

    var backend: Backend? = null
}

fun terraform(configure: TFConfig.() -> Unit) = TFConfig().apply(configure)

fun TFFile.terraform(configure: TFConfig.() -> Unit) {
    add(TFConfig().apply(configure))
}
