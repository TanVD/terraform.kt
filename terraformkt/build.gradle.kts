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

    api(project(":terraformEntities"))

    implementation("com.squareup", "kotlinpoet", "1.6.0")
    implementation("com.squareup.moshi", "moshi", "1.8.0")
    implementation("com.squareup.moshi", "moshi-kotlin", "1.8.0")
}

gradlePlugin {
    plugins {
        create("terraformKtPlugin") {
            id = "io.terraformkt.gradle.plugin"
            implementationClass = "io.terraformkt.plugin.TerraformKtPlugin"
        }
    }
}
