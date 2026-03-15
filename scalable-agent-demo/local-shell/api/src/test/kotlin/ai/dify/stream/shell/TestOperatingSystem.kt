package ai.dify.stream.shell

import java.nio.file.Path

internal fun isWindowsForTests(): Boolean = System.getProperty("os.name")
    .lowercase()
    .contains("win")

internal fun currentWorkingDirectoryForTests(): Path = Path.of("")
    .toAbsolutePath()
    .normalize()
