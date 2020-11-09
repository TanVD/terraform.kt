import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "io.terraformkt"
version = "0.1.0"

plugins {
    id("tanvd.kosogor") version "1.0.10" apply true
    id("io.gitlab.arturbosch.detekt") version ("1.11.0") apply true
    kotlin("jvm") version "1.3.72" apply true
}

allprojects {
    repositories {
        jcenter()
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("tanvd.kosogor")
        plugin("io.gitlab.arturbosch.detekt")
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.3"
            apiVersion = "1.3"

            freeCompilerArgs = freeCompilerArgs + listOf("-Xuse-experimental=kotlin.Experimental")
        }
    }


    detekt {
        parallel = true

        config = rootProject.files("detekt.yml")

        reports {
            xml {
                enabled = false
            }
            html {
                enabled = false
            }
        }
    }
}
