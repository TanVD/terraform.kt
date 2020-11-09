import io.terraformkt.plugin.terraformKt

group = rootProject.group
version = rootProject.version

terraformKt {
    provider {
        name = "azurerm"
        version = "2.35.0"
    }
}

