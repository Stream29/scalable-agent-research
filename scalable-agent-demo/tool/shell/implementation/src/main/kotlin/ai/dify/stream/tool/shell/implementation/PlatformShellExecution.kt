package ai.dify.stream.tool.shell.implementation

import ai.dify.stream.tool.shell.contract.ShellCommandResult
import ai.dify.stream.tool.shell.implementation.platform.PlatformShell
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.fold
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

public suspend fun PlatformShell.execute(
    command: String,
    workingDirectory: Path,
    timeoutSeconds: Int,
    skipControlChars: Boolean,
): ShellCommandResult = withContext(Dispatchers.IO) {
    coroutineScope {
        val process = startPtyProcess(
            platformShell = this@execute,
            command = command,
            workingDirectory = workingDirectory,
        )
        val stdoutDeferred = async {
            process.inputStream.asFlow()
                .fold(StringBuilder()) { acc, it -> acc.append(it) }
                .toString()
        }
        val stderrDeferred = async {
            process.errorStream.asFlow()
                .fold(StringBuilder()) { acc, it -> acc.append(it) }
                .toString()
        }
        val exitCodeDeferred = async { process.awaitExitCode() }

        try {
            val exitCode = withTimeout(timeoutSeconds.seconds) { exitCodeDeferred.await() }
            stdoutDeferred.join()
            stderrDeferred.join()
            ShellCommandResult.Success(
                exitCode = exitCode,
                stdout = stdoutDeferred.await().normalizeShellOutput(skipControlChars),
                stderr = stderrDeferred.await().normalizeShellOutput(skipControlChars),
            )
        } catch (_: TimeoutCancellationException) {
            process.destroyProcessTree()
            runCatching { process.waitFor(1, TimeUnit.SECONDS) }
            ShellCommandResult.Timeout(
                stdout = stdoutDeferred.await().normalizeShellOutput(skipControlChars),
                stderr = stderrDeferred.await().normalizeShellOutput(skipControlChars),
            )
        } finally {
            if (process.isAlive) {
                process.destroyProcessTree()
            }
            exitCodeDeferred.cancel()
        }
    }
}
