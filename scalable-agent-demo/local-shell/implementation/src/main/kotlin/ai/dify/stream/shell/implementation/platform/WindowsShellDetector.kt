package ai.dify.stream.shell.implementation.platform

import java.nio.file.Files
import java.nio.file.Path

internal fun detectWindowsShell(): PlatformShell {
    val powerShell7 = findExecutable("pwsh.exe", "pwsh")
    if (powerShell7 != null) {
        return PowerShell7Shell(executablePath = powerShell7)
    }

    val windowsPowerShell = findExecutable("powershell.exe", "powershell")
        ?: System.getenv("SystemRoot")
            ?.let(Path::of)
            ?.resolve("System32")
            ?.resolve("WindowsPowerShell")
            ?.resolve("v1.0")
            ?.resolve("powershell.exe")
            ?.takeIf(Files::isRegularFile)
            ?.toString()
    if (windowsPowerShell != null) {
        return WindowsPowerShellShell(executablePath = windowsPowerShell)
    }

    return CommandPromptShell(executablePath = System.getenv("ComSpec") ?: "cmd.exe")
}
