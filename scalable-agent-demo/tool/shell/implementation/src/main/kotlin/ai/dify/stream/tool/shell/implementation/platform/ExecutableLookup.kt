package ai.dify.stream.tool.shell.implementation.platform

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

internal fun findExecutable(vararg names: String): String? {
    val pathEntries = System.getenv("PATH").orEmpty().split(File.pathSeparatorChar)
    for (entry in pathEntries) {
        if (entry.isBlank()) {
            continue
        }
        for (name in names) {
            val candidate = runCatching { Path.of(entry).resolve(name) }.getOrNull() ?: continue
            if (Files.isRegularFile(candidate)) {
                return candidate.toAbsolutePath().toString()
            }
        }
    }
    return null
}
