import io.terraformkt.plugin.terraformKt

group = "io.terraformkt"
version = "0.1.0"

buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("io.terraformkt:terraform.kt:0.1.0")
    }
}

plugins {
    id("tanvd.kosogor") version "1.0.9" apply true
    kotlin("jvm") version "1.3.72" apply true

    id("java-gradle-plugin")
    id("maven")
}

repositories {
    jcenter()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    implementation("com.squareup", "kotlinpoet", "1.6.0")
    implementation("com.squareup.moshi", "moshi", "1.8.0")
    implementation("com.squareup.moshi", "moshi-kotlin", "1.8.0")
}

//sourceSets.main {
//    java.srcDirs("generated")
//}

gradlePlugin {
    plugins {
        create("terraformKtPlugin") {
            id = "io.terraformkt.gradle.plugin"
            implementationClass = "io.terraformkt.plugin.TerraformKtPlugin"
        }
    }
}

apply {
    plugin("io.terraformkt.gradle.plugin")
}

terraformKt {
    jsonSchemaFile = File("src/main/resources/schema.json")
    generationPath = File("generated")
}
