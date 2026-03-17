package ai.dify.stream.tool.shell.implementation.platform

import java.nio.file.Files
import java.nio.file.Path

internal fun detectPosixShell(
    operatingSystemFamily: OperatingSystemFamily,
): PlatformShell {
    if (operatingSystemFamily == OperatingSystemFamily.MAC_OS) {
        val zsh = findExecutable("zsh")
            ?: listOf("/bin/zsh", "/usr/bin/zsh").firstOrNull { Files.isRegularFile(Path.of(it)) }
        if (zsh != null) {
            return ZshShell(executablePath = zsh)
        }
    }

    val bash = findExecutable("bash")
        ?: listOf("/bin/bash", "/usr/bin/bash").firstOrNull { Files.isRegularFile(Path.of(it)) }
    if (bash != null) {
        return BashShell(executablePath = bash)
    }

    val shell = findExecutable("sh")
        ?: listOf("/bin/sh", "/usr/bin/sh").firstOrNull { Files.isRegularFile(Path.of(it)) }
        ?: "/bin/sh"
    return PosixShShell(executablePath = shell)
}
