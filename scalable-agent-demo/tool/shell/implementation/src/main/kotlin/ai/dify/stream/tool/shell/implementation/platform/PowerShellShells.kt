package ai.dify.stream.tool.shell.implementation.platform

public data class PowerShell7Shell(
    override val executablePath: String,
) : PlatformShell {
    override val name: String = "PowerShell 7"
    override val syntaxName: String = "PowerShell"

    override fun commandLine(command: String): List<String> = powerShellCommandLine(
        executablePath = executablePath,
        command = command,
    )
}

public data class WindowsPowerShellShell(
    override val executablePath: String,
) : PlatformShell {
    override val name: String = "Windows PowerShell"
    override val syntaxName: String = "PowerShell"

    override fun commandLine(command: String): List<String> = powerShellCommandLine(
        executablePath = executablePath,
        command = command,
    )
}

private fun powerShellCommandLine(
    executablePath: String,
    command: String,
): List<String> = listOf(
    executablePath,
    "-NoLogo",
    "-NoProfile",
    "-NonInteractive",
    "-Command",
    command,
)
