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

    implementation(project(":entities"))
    implementation(project(":wrapper"))
}

publishJar {
    publication {
        artifactId = "runtime"
    }
}
