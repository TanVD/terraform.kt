import io.terraformkt.plugin.terraformKt

group = rootProject.group
version = rootProject.version

buildscript {
    repositories {
        mavenLocal()
        jcenter()
    }
    dependencies {
        classpath("io.terraformkt:plugin:0.1.0")
    }
}

repositories {
    mavenLocal()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
}

apply {
    plugin("io.terraformkt.gradle.plugin")
}

terraformKt {
    generationPath = File("generated")
    provider {
        name = "aws"
        version = "2.70.0"
    }
    terraform {
        version = "0.13.0"
        downloadPath = File("tf")
    }
}
