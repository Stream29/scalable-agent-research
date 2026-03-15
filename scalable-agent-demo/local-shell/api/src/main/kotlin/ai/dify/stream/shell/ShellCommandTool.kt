package ai.dify.stream.shell

import ai.dify.stream.shell.contract.ShellCommandArgs
import ai.dify.stream.shell.contract.ShellCommandResult
import ai.dify.stream.shell.contract.ShellCommandResult.Failure
import ai.dify.stream.shell.implementation.execute
import ai.dify.stream.shell.implementation.platform.PlatformShell
import ai.koog.agents.core.tools.Tool
import kotlinx.coroutines.CancellationException
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path

private const val shellCommandToolName: String = "executeShellCommand"
private val shellCommandToolDescription: String = """
    Runs a command in a fresh local shell process.
    Execution uses a fresh PTY, so terminal-aware programs can run, but every call still starts from a new isolated shell.
    On this machine commands use `${PlatformShell.current.name}`, so write commands with `${PlatformShell.current.syntaxName}` syntax.
    Each call is isolated: `cd`, shell variables, aliases, and functions do not persist to later calls.
    Pass `workingDirectory` on every call instead of relying on `cd` or tool state.
    Relative paths are resolved from the current process working directory.
    `skipControlChars` defaults to true and removes ANSI/OSC terminal control sequences from stdout and stderr.
    Some programs may merge stderr into stdout when attached to a PTY.
    Prefer non-interactive commands that print plain text.
""".trimIndent()

public object ShellCommandTool : Tool<ShellCommandArgs, ShellCommandResult>(
    name = shellCommandToolName,
    description = shellCommandToolDescription,
    argsSerializer = ShellCommandArgs.serializer(),
    resultSerializer = ShellCommandResult.serializer(),
) {
    override suspend fun execute(args: ShellCommandArgs): ShellCommandResult {
        requirePositiveTimeout(args.timeoutSeconds) { message ->
            return Failure(message = message)
        }
        val workingDirectory = resolveWorkingDirectory(args.workingDirectory) { message ->
            return Failure(message = message)
        }

        return try {
            PlatformShell.current.execute(
                command = args.command,
                workingDirectory = workingDirectory,
                timeoutSeconds = args.timeoutSeconds,
                skipControlChars = args.skipControlChars,
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Failure(message = exception.stackTraceToString())
        }
    }
}

private inline fun requirePositiveTimeout(
    timeoutSeconds: Int,
    failure: (String) -> Nothing,
) {
    if (timeoutSeconds < 1)
        failure("timeoutSeconds must be positive.")
}

private inline fun resolveWorkingDirectory(
    workingDirectory: String,
    failure: (String) -> Nothing,
): Path {
    if (workingDirectory.isBlank()) failure("workingDirectory must not be blank.")
    val normalizedPath = try {
        Path.of(workingDirectory).toAbsolutePath().normalize()
    } catch (exception: InvalidPathException) {
        failure(exception.message!!)
    }
    if (!Files.exists(normalizedPath)) failure("Directory does not exist: $normalizedPath")
    if (!Files.isDirectory(normalizedPath)) failure("Not a directory: $normalizedPath")
    return normalizedPath
}
