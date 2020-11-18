rootProject.name = "providers"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
include(":aws")
include(":azure")
include(":gcp")
