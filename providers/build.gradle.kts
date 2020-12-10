import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "io.terraformkt.providers"
version = "0.1.4"

plugins {
    id("tanvd.kosogor") version "1.0.10" apply true
    kotlin("jvm") version "1.3.72" apply true
    id("io.terraformkt") version "0.1.4" apply false

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
        plugin("io.terraformkt")
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.3"
            apiVersion = "1.3"
        }
    }


    dependencies {
        implementation(kotlin("stdlib"))

        implementation("io.terraformkt:runtime:0.1.4")
    }
}
