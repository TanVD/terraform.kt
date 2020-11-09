rootProject.name = "providers"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
include(":aws")
include(":azure")
include(":gcp")
