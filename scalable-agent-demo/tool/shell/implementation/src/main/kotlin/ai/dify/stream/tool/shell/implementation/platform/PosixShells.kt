package ai.dify.stream.tool.shell.implementation.platform

public data class BashShell(
    override val executablePath: String,
) : PlatformShell {
    override val name: String = "bash"
    override val syntaxName: String = "bash"

    override fun commandLine(command: String): List<String> = posixCommandLine(
        executablePath = executablePath,
        command = command,
    )
}

public data class ZshShell(
    override val executablePath: String,
) : PlatformShell {
    override val name: String = "zsh"
    override val syntaxName: String = "zsh"

    override fun commandLine(command: String): List<String> = posixCommandLine(
        executablePath = executablePath,
        command = command,
    )
}

public data class PosixShShell(
    override val executablePath: String,
) : PlatformShell {
    override val name: String = "sh"
    override val syntaxName: String = "POSIX sh"

    override fun commandLine(command: String): List<String> = posixCommandLine(
        executablePath = executablePath,
        command = command,
    )
}

private fun posixCommandLine(
    executablePath: String,
    command: String,
): List<String> = listOf(
    executablePath,
    "-c",
    command,
)
