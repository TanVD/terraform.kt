import io.terraformkt.plugin.terraformKt
import tanvd.kosogor.proxy.publishJar

val providerVersion = "2.35.0"

group = rootProject.group
version = "$providerVersion-${rootProject.version}"

terraformKt {
    provider {
        name = "azurerm"
        version = providerVersion
    }
}

publishJar {
    bintray {
        username = "tanvd"
        repository = "io.terraformkt"
        info {
            description = "Azure provider for terraform.kt"
            vcsUrl = "https://github.com/anstkras/terraform.kt"
            githubRepo = "https://github.com/anstkras/terraform.kt"
            labels.addAll(listOf("kotlin", "terraform", "azure", "web", "devops"))
        }
    }
}
