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

apply {
    plugin("io.terraformkt.gradle.plugin")
}

dependencies {
    implementation(kotlin("stdlib"))
}

terraformKt {
    generationPath = File("generated")
    tfVersion = "0.13.0"
    tfProvider = "aws"
    schemaVersion = "2.70.0"
    downLoadTerraformPath = File("tf")
}
