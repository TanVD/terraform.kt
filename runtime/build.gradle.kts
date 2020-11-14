import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":entities"))
    implementation(project(":wrapper"))
}

publishJar {
    bintray {
        username = "tanvd"
        repository = "io.terraformkt"
        info {
            description = "Terraform.kt runtime for Terraform execution"
            githubRepo = "https://github.com/anstkras/terraform.kt"
            githubRepo = "https://github.com/anstkras/terraform.kt"
            labels.addAll(listOf("kotlin", "terraform", "web", "devops"))
        }
    }
}
