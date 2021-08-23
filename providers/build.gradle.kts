import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import tanvd.kosogor.proxy.publishJar

group = "io.terraformkt.providers"
version = "0.1.5"

plugins {
    id("tanvd.kosogor") version "1.0.10" apply true
    kotlin("jvm") version "1.5.21" apply true
    id("io.terraformkt") version "0.1.5" apply false
    `maven-publish`
}

allprojects {
    repositories {
        mavenCentral()
        maven(url = uri("https://packages.jetbrains.team/maven/p/ktls/maven"))
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("tanvd.kosogor")
        plugin("io.terraformkt")
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.3"
            apiVersion = "1.3"
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



    dependencies {
        implementation(kotlin("stdlib"))

        implementation("io.terraformkt:runtime:0.1.5")
    }
}
