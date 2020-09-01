import io.terraformkt.plugin.terraformKt

group = rootProject.group
version = rootProject.version

buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("io.terraformkt:terraform.kt:0.1.0")
    }
}

plugins {
    id("java-gradle-plugin")
    id("maven")
}

repositories {
    jcenter()
}

dependencies {
    implementation(project(":terraformEntities"))
}

apply {
    plugin("io.terraformkt.gradle.plugin")
}

terraformKt {
    jsonSchemaFile = File("terraformkt/src/main/resources/schema.json")
    generationPath = File("example/generated")
    //sourcePath = File("generated")
}
