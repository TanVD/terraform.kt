group = rootProject.group
version = rootProject.version

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":entities"))
    implementation(project(":wrapper"))
}
