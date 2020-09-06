group = rootProject.group
version = rootProject.version

plugins {
    id("java-gradle-plugin")
    id("maven")
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    api(project(":entities"))

    implementation("com.squareup", "kotlinpoet", "1.6.0")
    implementation("com.squareup.moshi", "moshi", "1.8.0")
    implementation("com.squareup.moshi", "moshi-kotlin", "1.8.0")

    implementation("org.codehaus.plexus", "plexus-utils", "3.1.1")
    implementation("org.codehaus.plexus", "plexus-archiver", "4.1.0")
    implementation("org.codehaus.plexus", "plexus-container-default", "1.0-alpha-30")
}

gradlePlugin {
    plugins {
        create("terraformKtPlugin") {
            id = "io.terraformkt.gradle.plugin"
            implementationClass = "io.terraformkt.plugin.TerraformKtPlugin"
        }
    }
}
