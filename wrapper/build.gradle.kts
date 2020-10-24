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

    implementation("org.codehaus.plexus", "plexus-utils", "3.1.1")
    implementation("org.codehaus.plexus", "plexus-archiver", "4.1.0")
    implementation("org.codehaus.plexus", "plexus-container-default", "1.0-alpha-30")
}

publishJar {
    publication {
        artifactId = "wrapper"
    }
}
