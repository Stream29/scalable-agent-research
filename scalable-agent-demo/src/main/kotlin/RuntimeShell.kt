package ai.dify.stream

import ai.koog.prompt.executor.clients.anthropic.AnthropicLLMClient
import ai.koog.prompt.executor.clients.retry.toRetryingClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import kotlin.time.measureTime

data class RuntimeContext(
    val promptExecutor: PromptExecutor,
    val coroutineContext: CoroutineContext
) : CoroutineContext by coroutineContext

inline fun runWithShell(crossinline block: suspend RuntimeContext.() -> Unit) {
    measureTime {
        runBlocking {
            SingleLLMPromptExecutor(
                AnthropicLLMClient(anthropicApiKey).toRetryingClient()
            ).use { promptExecutor ->
                block(RuntimeContext(promptExecutor, coroutineContext))
            }
        }
    }.let { println("Execution completed in $it") }
}