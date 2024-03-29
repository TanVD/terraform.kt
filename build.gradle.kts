import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import tanvd.kosogor.proxy.publishJar

group = "io.terraformkt"
version = "0.1.5"

plugins {
    id("tanvd.kosogor") version "1.0.10" apply true
    id("io.gitlab.arturbosch.detekt") version ("1.11.0") apply true
    kotlin("jvm") version "1.5.21" apply true
    `maven-publish`
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("tanvd.kosogor")
        plugin("maven-publish")
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.3"
            apiVersion = "1.3"

            freeCompilerArgs = freeCompilerArgs + listOf("-Xuse-experimental=kotlin.Experimental")
        }
    }

    publishJar {  }

    publishing {
        repositories {
            maven {
                name = "SpacePackages"
                url = uri("https://packages.jetbrains.team/maven/p/ktls/maven")

                credentials {
                    username = System.getenv("JB_SPACE_CLIENT_ID")
                    password = System.getenv("JB_SPACE_CLIENT_SECRET")
                }
            }
        }
    }

    afterEvaluate {
        System.setProperty("gradle.publish.key", System.getenv("GRADLE_PUBLISH_KEY") ?: "")
        System.setProperty("gradle.publish.secret", System.getenv("GRADLE_PUBLISH_SECRET") ?: "")
    }
}
