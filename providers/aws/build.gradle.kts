import io.terraformkt.plugin.terraformKt

group = rootProject.group
version = rootProject.version

terraformKt {
    provider {
        name = "aws"
        version = "3.14.1"
    }
}

