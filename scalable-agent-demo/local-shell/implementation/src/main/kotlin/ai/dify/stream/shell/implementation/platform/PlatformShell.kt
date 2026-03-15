package ai.dify.stream.shell.implementation.platform

public sealed interface PlatformShell {
    public val name: String
    public val syntaxName: String
    public val executablePath: String

    public fun commandLine(command: String): List<String>

    public companion object {
        public val current: PlatformShell = detectPlatformShell()
    }
}
