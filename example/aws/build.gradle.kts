import io.terraformkt.plugin.terraformKt

group = rootProject.group
version = rootProject.version

plugins {
    application apply true
    id("tanvd.kosogor") version "1.0.9" apply true
    kotlin("jvm") version "1.3.72" apply true
    id("io.terraformkt") version "0.1.3" apply true
}

repositories {
    mavenLocal()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.terraformkt:runtime:0.1.3")
}

terraformKt {
    provider {
        name = "aws"
        version = "2.70.0"
    }
}

application {
    mainClass.set("io.terraformkt.examples.MainKt")
}
