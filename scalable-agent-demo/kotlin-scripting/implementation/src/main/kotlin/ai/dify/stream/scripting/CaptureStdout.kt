package ai.dify.stream.scripting

import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal inline fun <T> captureStdout(block: () -> T): WithStdout<T> {
    val originalOut = System.out
    val outputStream = ByteArrayOutputStream()
    val printStream = PrintStream(outputStream, true, Charsets.UTF_8)
    try {
        System.setOut(printStream)
        val result = block()
        printStream.flush()
        val stdout = outputStream.toString(Charsets.UTF_8)
        return WithStdout(result, stdout)
    } finally {
        System.setOut(originalOut)
        printStream.close()
    }
}

internal data class WithStdout<T>(val value: T, val stdout: String)