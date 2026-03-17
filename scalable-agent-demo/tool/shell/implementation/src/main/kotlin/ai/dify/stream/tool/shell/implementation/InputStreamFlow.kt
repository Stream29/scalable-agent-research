package ai.dify.stream.tool.shell.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runInterruptible
import java.io.IOException
import java.io.InputStream

internal fun InputStream.asFlow(): Flow<String> = flow {
    bufferedReader().use { reader ->
        val buffer = CharArray(8 * 1024)
        try {
            while (true) {
                val length = runInterruptible { reader.read(buffer) }
                if (length < 0) break
                if (length == 0) continue
                emit(String(buffer, 0, length))
            }
        } catch (_: IOException) {
            // PTY streams may throw when the process is torn down. Treat that as end-of-stream.
        }
    }
}.flowOn(Dispatchers.IO)
