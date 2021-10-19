import io.terraformkt.plugin.terraformKt
import tanvd.kosogor.proxy.publishJar

val providerVersion = "2.78.0"

group = rootProject.group
version = "$providerVersion-${rootProject.version}"

terraformKt {
    provider {
        name = "azurerm"
        version = providerVersion
    }
}
