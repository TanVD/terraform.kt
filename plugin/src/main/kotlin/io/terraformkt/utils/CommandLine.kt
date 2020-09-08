package io.terraformkt.utils

import org.codehaus.plexus.util.Os
import org.codehaus.plexus.util.cli.*
import java.io.File
import java.io.FileWriter

internal object CommandLine {
    val os by lazy {
        when {
            Os.isFamily(Os.FAMILY_WINDOWS) -> "windows_amd64"
            Os.isFamily(Os.FAMILY_MAC) -> "darwin_amd64"
            Os.isFamily(Os.FAMILY_UNIX) -> "linux_amd64"
            else -> error("Unknown operating system. Probably your system is not supported by Terraform.")
        }
    }

    fun execute(exec: String, args: List<String>, workingDir: File, redirectStdout: Boolean = false, redirectErr: Boolean = true): Int {
        return execute(exec, args, workingDir, getConsumer(redirectStdout), getConsumer(redirectErr))
    }

    fun execute(exec: String, args: List<String>, workingDir: File, stdoutConsumer: StreamConsumer, errConsumer: StreamConsumer): Int {
        return CommandLineUtils.executeCommandLine(
            Commandline().apply {
                workingDirectory = workingDir
                executable = exec
                addArguments(args.toTypedArray())
            }, stdoutConsumer, errConsumer
        )
    }

    fun executeOrFail(exec: String, args: List<String>, workingDir: File, redirectStdout: Boolean = false, redirectErr: Boolean = true) {
        executeOrFail(exec, args, workingDir, getConsumer(redirectStdout), getConsumer(redirectErr))
    }

    fun executeOrFailToFile(exec: String, args: List<String>, workingDir: File, stdoutFile: File, redirectErr: Boolean = true) {
        executeOrFail(exec, args, workingDir, WriterStreamConsumer(FileWriter(stdoutFile)), getConsumer(redirectErr))
    }

    fun executeOrFail(exec: String, args: List<String>, workingDir: File, stdoutConsumer: StreamConsumer, errConsumer: StreamConsumer) {
        val returnCode = execute(exec, args, workingDir, stdoutConsumer, errConsumer)
        if (returnCode != 0) {
            error("Command failed: '$exec ${args.joinToString { " " }}'")
        }
    }

    private fun getConsumer(redirectOutput: Boolean): StreamConsumer {
        return if (redirectOutput)
            DefaultConsumer()
        else
            CommandLineUtils.StringStreamConsumer()
    }
}
