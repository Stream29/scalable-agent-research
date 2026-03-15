package ai.dify.stream.shell

import java.nio.file.Files
import java.nio.file.Path

internal inline fun withTempDirectory(block: (Path) -> Unit) {
    val directory = Files.createTempDirectory("local-shell-test")
    try {
        block(directory)
    } finally {
        Files.walk(directory).use { paths ->
            paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
        }
    }
}
