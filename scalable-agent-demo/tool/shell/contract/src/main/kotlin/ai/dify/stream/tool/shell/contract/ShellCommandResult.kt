package ai.dify.stream.tool.shell.contract

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public sealed interface ShellCommandResult {
    @Serializable
    @SerialName("Success")
    public data class Success(
        val exitCode: Int,
        val stdout: String,
        val stderr: String,
    ) : ShellCommandResult

    @Serializable
    @SerialName("Timeout")
    public data class Timeout(
        val stdout: String,
        val stderr: String,
    ) : ShellCommandResult

    @Serializable
    @SerialName("Failure")
    public data class Failure(
        val message: String,
    ) : ShellCommandResult
}
