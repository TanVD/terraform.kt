import io.terraformkt.plugin.terraformKt
import tanvd.kosogor.proxy.publishJar

val providerVersion = "3.46.0"

group = rootProject.group
version = "$providerVersion-${rootProject.version}"

terraformKt {
    provider {
        name = "google"
        version = providerVersion
    }
}

publishJar {
    bintray {
        username = "tanvd"
        repository = "io.terraformkt"
        info {
            description = "GCP provider for terraform.kt"
            vcsUrl = "https://github.com/anstkras/terraform.kt"
            githubRepo = "https://github.com/anstkras/terraform.kt"
            labels.addAll(listOf("kotlin", "terraform", "gcp", "web", "devops"))
        }
    }
}

