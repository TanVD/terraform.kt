rootProject.name = "providers"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = uri("https://packages.jetbrains.team/maven/p/ktls/maven"))
    }
}
include(":aws")
include(":azure")
include(":gcp")
