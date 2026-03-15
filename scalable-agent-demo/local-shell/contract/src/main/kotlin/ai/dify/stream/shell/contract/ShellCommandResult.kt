package ai.dify.stream.shell.contract

import kotlinx.serialization.Serializable

@Serializable
public sealed interface ShellCommandResult {
    @Serializable
    public data class Success(
        val exitCode: Int,
        val stdout: String,
        val stderr: String,
    ) : ShellCommandResult

    @Serializable
    public data class Timeout(
        val stdout: String,
        val stderr: String,
    ) : ShellCommandResult

    @Serializable
    public data class Failure(
        val message: String,
    ) : ShellCommandResult
}
