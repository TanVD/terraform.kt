import io.terraformkt.plugin.terraformKt

group = rootProject.group
version = rootProject.version

plugins {
    id("tanvd.kosogor") version "1.0.9" apply true
    kotlin("jvm") version "1.3.72" apply true
    id("io.terraformkt") version "0.1.0" apply true
}

repositories {
    mavenLocal()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.terraformkt:runtime:0.1.0")
}

terraformKt {
    provider {
        name = "local"
        version = "~> 1.4"
    }
}
