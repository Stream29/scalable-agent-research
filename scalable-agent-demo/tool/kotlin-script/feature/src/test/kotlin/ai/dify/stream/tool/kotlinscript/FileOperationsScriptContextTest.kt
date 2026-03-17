package ai.dify.stream.tool.kotlinscript

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class FileOperationsScriptContextTest {
    @Test
    fun readFileAsStringReadsExistingFile() = withTempFile("line-1\nline-2") { path ->
        assertEquals("line-1\nline-2", FileOperationsScriptContextImpl.readFileAsString(path.toString()))
    }

    @Test
    fun replaceInFileUpdatesFileContent() = withTempFile("before middle before") { path ->
        FileOperationsScriptContextImpl.replaceInFile(
            path = path.toString(),
            original = "before",
            replacement = "after",
        )

        assertEquals("after middle before", Files.readString(path, StandardCharsets.UTF_8))
    }
}

private inline fun withTempFile(
    initialContent: String,
    block: (Path) -> Unit,
) {
    val path = Files.createTempFile("kotlin-scripting-feature-test", ".txt")
    try {
        Files.writeString(path, initialContent, StandardCharsets.UTF_8)
        block(path)
    } finally {
        Files.deleteIfExists(path)
    }
}
