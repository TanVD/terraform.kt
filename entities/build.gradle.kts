import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

buildscript {
    repositories {
        mavenLocal()
    }
}

repositories {
    mavenLocal()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
}

publishJar {
    publication {
        artifactId = "entities"
    }
}
