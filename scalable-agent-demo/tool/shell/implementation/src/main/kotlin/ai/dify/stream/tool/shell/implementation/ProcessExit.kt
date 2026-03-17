package ai.dify.stream.tool.shell.implementation

import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal suspend fun Process.awaitExitCode(): Int = suspendCancellableCoroutine { continuation ->
    val exitFuture: CompletableFuture<Process> = onExit()
    exitFuture.whenComplete { _, throwable ->
        if (throwable != null) {
            continuation.resumeWithException(throwable)
            return@whenComplete
        }
        continuation.resume(exitValue())
    }
    continuation.invokeOnCancellation {
        exitFuture.cancel(true)
    }
}
