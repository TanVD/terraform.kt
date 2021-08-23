import io.terraformkt.plugin.terraformKt
import tanvd.kosogor.proxy.publishJar

val providerVersion = "3.14.1"

group = rootProject.group
version = "$providerVersion-${rootProject.version}"

terraformKt {
    provider {
        name = "aws"
        version = providerVersion
    }
}
