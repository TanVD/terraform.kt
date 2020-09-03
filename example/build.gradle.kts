import io.terraformkt.plugin.terraformKt

group = rootProject.group
version = rootProject.version

buildscript {
    repositories {
        mavenLocal()

        // Can't find terraformEntities without it.
        flatDir {
            dirs("../build/terraformEntities/libs")
        }
    }
    dependencies {
        classpath("io.terraformkt:terraformkt:0.1.0")
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
    jsonSchemaFile = File("tf/schema.json")
    generationPath = File("generated")
    tfVersion = "0.13.0"
    tfPath = File("tf/terraform")
}
