package ai.dify.stream.tool.kotlinscript

import ai.dify.stream.tool.kotlinscript.contract.ScriptContext
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

public interface FileOperationsScriptContext : ScriptContext {
    public fun readFileAsString(
        path: String,
        lineRange: IntRange? = null,
        charset: Charset = StandardCharsets.UTF_8,
    ): String

    public fun replaceInFile(
        path: String,
        original: String,
        replacement: String,
        replaceAll: Boolean = false,
        charset: Charset = StandardCharsets.UTF_8,
    )
}

public object FileOperationsScriptContextImpl : FileOperationsScriptContext {
    override val defaultImports: List<String> = listOf(File::class.qualifiedName!!)

    override val llmDescription: String = """
        ## File Operations API
        `FileOperationsScriptContext` is an implicit receiver in your script.
        The following methods can be called without explicit `this` or any receiver:
        ```kotlin
        readFileAsString(
            path: String,
            lineRange: IntRange? = null,
            charset: Charset = StandardCharsets.UTF_8
        ): String
        replaceInFile(
            path: String,
            original: String,
            replacement: String,
            replaceAll: Boolean = false,
            charset: Charset = StandardCharsets.UTF_8
        ): Unit
        ```
        They provide a shorthand for reading and updating files.
        You can still use the normal file operations API if you need more control.
    """.trimIndent()

    override fun readFileAsString(
        path: String,
        lineRange: IntRange?,
        charset: Charset,
    ): String {
        val file = requireReadableFile(path)
        return if (lineRange != null) {
            file.readLines(charset).subList(lineRange.first, lineRange.last).joinToString(separator = "\n")
        } else {
            file.readText(charset)
        }
    }

    override fun replaceInFile(
        path: String,
        original: String,
        replacement: String,
        replaceAll: Boolean,
        charset: Charset,
    ) {
        val file = requireReadableFile(path)
        require(file.canWrite()) { "File is not writable: $path" }
        val text = file.readText(charset)
        val replaced = if (replaceAll) text.replace(original, replacement) else text.replaceFirst(original, replacement)
        file.writeText(replaced, charset)
    }

    private fun requireReadableFile(path: String): File {
        val file = File(path)
        require(file.exists()) { "File does not exist: $path" }
        require(file.isFile) { "File is not a file: $path" }
        require(file.canRead()) { "File is not readable: $path" }
        return file
    }
}
