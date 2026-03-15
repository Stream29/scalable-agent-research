package ai.dify.stream.shell.implementation

import ai.dify.stream.shell.implementation.platform.OperatingSystemFamily
import ai.dify.stream.shell.implementation.platform.PlatformShell
import com.pty4j.PtyProcessBuilder
import java.nio.file.Path

internal fun startPtyProcess(
    platformShell: PlatformShell,
    command: String,
    workingDirectory: Path,
): Process = PtyProcessBuilder(platformShell.commandLine(command).toTypedArray()).apply {
    setDirectory(workingDirectory.toString())
    setEnvironment(buildMap {
        putAll(System.getenv())
        if (OperatingSystemFamily.current != OperatingSystemFamily.WINDOWS) {
            put("TERM", getOrDefault("TERM", "xterm-256color"))
        }
    })
    setConsole(false)
    setRedirectErrorStream(false)
    setInitialColumns(120)
    setInitialRows(40)
    setWindowsAnsiColorEnabled(false)
}.start()

