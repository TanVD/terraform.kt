import io.terraformkt.plugin.terraformKt

group = rootProject.group
version = rootProject.version

buildscript {
    repositories {
        mavenLocal()

        // Can't find terraformEntities without it.
        flatDir {
            dirs("../build/entities/libs")
        }
    }
    dependencies {
        classpath("io.terraformkt:plugin:0.1.0")
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
    implementation(project(":entities"))
}

apply {
    plugin("io.terraformkt.gradle.plugin")
}

terraformKt {
    generationPath = File("generated1")
    tfVersion = "0.13.0"
    tfConfig = File("tf/config.tf")
}
