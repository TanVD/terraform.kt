import io.terraformkt.plugin.terraformKt
import tanvd.kosogor.proxy.publishJar

val providerVersion = "3.14.1"

group = rootProject.group
version = "$providerVersion-${rootProject.version}"

terraformKt {
    provider {
        name = "aws"
        version = providerVersion
    }
}

publishJar {
    bintray {
        username = "tanvd"
        repository = "io.terraformkt"
        info {
            description = "AWS provider for terraform.kt"
            vcsUrl = "https://github.com/anstkras/terraform.kt"
            githubRepo = "https://github.com/anstkras/terraform.kt"
            labels.addAll(listOf("kotlin", "terraform", "aws", "web", "devops"))
        }
    }
}
