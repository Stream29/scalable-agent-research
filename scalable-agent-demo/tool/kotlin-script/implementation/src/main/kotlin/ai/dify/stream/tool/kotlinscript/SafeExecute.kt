package ai.dify.stream.tool.kotlinscript

import kotlinx.coroutines.suspendCancellableCoroutine

internal suspend inline fun <T> executeInThreadCancellable(crossinline block: () -> T): T =
    suspendCancellableCoroutine { cont ->
        val thread = Thread(
            { cont.resumeWith(runCatching { block() }) },
            "kotlin-script-eval-$nextScriptEvaluationId"
        )
        thread.isDaemon = true
        cont.invokeOnCancellation {
            thread.interrupt()
        }
        thread.start()
    }