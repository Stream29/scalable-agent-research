package ai.dify.stream.tool.shell.implementation.platform

public data class CommandPromptShell(
    override val executablePath: String,
) : PlatformShell {
    override val name: String = "cmd.exe"
    override val syntaxName: String = "cmd.exe"

    override fun commandLine(command: String): List<String> = listOf(
        executablePath,
        "/c",
        command,
    )
}
