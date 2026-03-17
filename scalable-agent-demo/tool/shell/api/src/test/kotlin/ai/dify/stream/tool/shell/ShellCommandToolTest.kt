package ai.dify.stream.tool.shell

import ai.dify.stream.tool.shell.contract.ShellCommandArgs
import ai.dify.stream.tool.shell.contract.ShellCommandResult
import kotlinx.coroutines.test.runTest
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ShellCommandToolTest {
    @Test
    fun descriptorExplainsIsolationAndShellSyntax() {
        val description = ShellCommandTool.descriptor.description

        assertContains(description, "Each call is isolated")
        assertContains(description, "workingDirectory")
        assertContains(description, "Relative paths")
        assertContains(description, "skipControlChars")
        assertContains(description, "syntax")
    }

    @Test
    fun executeCapturesStdoutAndExitCode() = runTest {
        val result = assertIs<ShellCommandResult.Success>(ShellCommandTool.execute(
            ShellCommandArgs(
                command = echoCommand("hello"),
                workingDirectory = currentWorkingDirectoryForTests().toString(),
            )
        ))

        assertEquals(0, result.exitCode)
        assertEquals("hello", result.stdout.trim())
        assertEquals("", result.stderr)
    }

    @Test
    fun executeUsesWorkingDirectory() = runTest {
        withTempDirectory { directory ->
            val result = assertIs<ShellCommandResult.Success>(ShellCommandTool.execute(
                ShellCommandArgs(
                    command = currentDirectoryCommand(),
                    workingDirectory = directory.toString(),
                )
            ))

            assertEquals(0, result.exitCode)
            assertEquals(directory.toRealPath().toString(), Path.of(result.stdout.trim()).toRealPath().toString())
        }
    }

    @Test
    fun executeReturnsStructuredTimeout() = runTest {
        val result = assertIs<ShellCommandResult.Timeout>(ShellCommandTool.execute(
            ShellCommandArgs(
                command = echoThenSleepCommand(text = "before-timeout", seconds = 2),
                workingDirectory = currentWorkingDirectoryForTests().toString(),
                timeoutSeconds = 1,
            )
        ))

        assertContains(result.stdout, "before-timeout")
    }

    @Test
    fun executeSkipsControlCharsByDefault() = runTest {
        val result = assertIs<ShellCommandResult.Success>(ShellCommandTool.execute(
            ShellCommandArgs(
                command = ansiTextCommand("hello"),
                workingDirectory = currentWorkingDirectoryForTests().toString(),
            )
        ))

        assertEquals("hello", result.stdout.trim())
    }

    @Test
    fun executeKeepsControlCharsWhenRequested() = runTest {
        val result = assertIs<ShellCommandResult.Success>(ShellCommandTool.execute(
            ShellCommandArgs(
                command = ansiTextCommand("hello"),
                workingDirectory = currentWorkingDirectoryForTests().toString(),
                skipControlChars = false,
            )
        ))

        assertContains(result.stdout, "\u001B[")
    }

    @Test
    fun executeAcceptsRelativeWorkingDirectory() = runTest {
        val result = assertIs<ShellCommandResult.Success>(ShellCommandTool.execute(
            ShellCommandArgs(
                command = currentDirectoryCommand(),
                workingDirectory = ".",
            )
        ))

        assertEquals(0, result.exitCode)
        assertEquals(currentWorkingDirectoryForTests().toRealPath().toString(), Path.of(result.stdout.trim()).toRealPath().toString())
    }

    @Test
    fun executeReportsInvalidWorkingDirectory() = runTest {
        val missingDirectory = currentWorkingDirectoryForTests()
            .resolve("definitely-missing-local-shell-test-directory")

        val result = assertIs<ShellCommandResult.Failure>(ShellCommandTool.execute(
            ShellCommandArgs(
                command = echoCommand("hello"),
                workingDirectory = missingDirectory.toString(),
            )
        ))

        assertContains(result.message, "Directory does not exist")
    }

    @Test
    fun executeReportsInvalidTimeout() = runTest {
        val result = assertIs<ShellCommandResult.Failure>(ShellCommandTool.execute(
            ShellCommandArgs(
                command = echoCommand("hello"),
                workingDirectory = currentWorkingDirectoryForTests().toString(),
                timeoutSeconds = 0,
            )
        ))

        assertContains(result.message, "timeoutSeconds must be positive")
    }
}
