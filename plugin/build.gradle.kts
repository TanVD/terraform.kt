import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.proxy.publishPlugin

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(kotlin("stdlib"))

    implementation(kotlin("reflect"))

    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(kotlin("gradle-plugin-api"))

    implementation(project(":entities"))
    implementation(project(":wrapper"))

    implementation("com.squareup", "kotlinpoet", "1.6.0")
    implementation("com.squareup.moshi", "moshi", "1.8.0")
    implementation("com.squareup.moshi", "moshi-kotlin", "1.8.0")


    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.6.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.0")
}

publishJar {
    bintray {
        username = "tanvd"
        repository = "io.terraformkt"
        info {
            description = "Terraform.kt Gradle plugin"
            githubRepo = "https://github.com/anstkras/terraform.kt"
            githubRepo = "https://github.com/anstkras/terraform.kt"
            labels.addAll(listOf("kotlin", "terraform", "web", "devops"))
        }
    }
}

publishPlugin {
    id = "io.terraformkt"
    displayName = "Terraform.kt"
    implementationClass = "io.terraformkt.plugin.TerraformKtPlugin"
    version = project.version.toString()
}

tasks.withType<Test> {
    useJUnitPlatform()
    maxHeapSize = "4g"

    testLogging {
        events("passed", "skipped", "failed")
    }
}

