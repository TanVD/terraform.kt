import runtime.reactive.trigger

job("Terraform.kt / Plugin / Build") {
    container("openjdk:11") {
        shellScript {
            content = """
              ./gradlew build -x test --console=plain
          """
        }
    }
}

job("Terraform.kt / Plugin / Test") {
    container("openjdk:11") {
        shellScript {
            content = """
              ./gradlew publishToMavenLocal --console=plain
              ./gradlew test --console=plain 
          """
        }
    }
}

job("Terraform.kt / Plugin / Release") {
    startOn {
        gitPush {
            enabled = false
        }
    }

    container("openjdk:11") {
        env["GRADLE_PUBLISH_KEY"] = Secrets("gradle_publish_key")
        env["GRADLE_PUBLISH_SECRET"] = Secrets("gradle_publish_secret")

        shellScript {
            content = """
              ./gradlew publish publishPlugins --console=plain
          """
        }
    }
}

job("Terraform.kt / Providers / Build") {
    container("openjdk:11") {
        workDir = "providers"

        shellScript {
            content = """
              ./gradlew build -x test --console=plain
          """
        }
    }
}

job("Terraform.kt / Providers / Release") {
    startOn {
        gitPush {
            enabled = false
        }
    }

    container("openjdk:11") {
        workDir = "providers"

        shellScript {
            content = """
              ./gradlew publish --console=plain
          """
        }
    }
}

