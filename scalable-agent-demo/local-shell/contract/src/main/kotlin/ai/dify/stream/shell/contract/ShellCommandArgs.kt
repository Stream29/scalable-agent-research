package ai.dify.stream.shell.contract

import kotlinx.serialization.Serializable

@Serializable
public data class ShellCommandArgs(
    val command: String,
    val workingDirectory: String,
    val timeoutSeconds: Int = 30,
    val skipControlChars: Boolean = true,
)
