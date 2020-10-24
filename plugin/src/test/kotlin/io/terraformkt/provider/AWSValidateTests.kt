package io.terraformkt.provider

import io.terraformkt.BaseValidateTests
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

open class AWSValidateTests : BaseValidateTests("aws") {
    private val projectDir = File("../example")
    private val runner: GradleRunner
        get() = GradleRunner
            .create()
            .withDebug(true)
            .withProjectDir(projectDir)

    private val data = setOf(
        "instance",
        "s3_bucket",
        "security_group"
    )

    @Test
    fun `test generate time site example`() {
        runner.withArguments("generateTerraform").build()
        runner.withArguments("run").build()

        for (file in data) {
            Assertions.assertEquals(expected(file), actual(file))
        }
    }
}
