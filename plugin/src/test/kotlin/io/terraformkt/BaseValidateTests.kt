package io.terraformkt

import org.gradle.testkit.runner.GradleRunner
import java.io.File

abstract class BaseValidateTests(private val provider: String) {
    private val projectDir = File("../example")
    private val runner: GradleRunner
        get() = GradleRunner
            .create()
            .withDebug(true)
            .withProjectDir(projectDir)

    /** Get actual generated file */
    protected fun actual(file: String): String {
        return File(projectDir, "/$provider/terraform/$file.tf").readText()
    }

    /** Get expected content of Terraform file */
    protected fun expected(file: String): String {
        return Resources.read("/generated/$provider/$file/$file.tf")
    }
}
