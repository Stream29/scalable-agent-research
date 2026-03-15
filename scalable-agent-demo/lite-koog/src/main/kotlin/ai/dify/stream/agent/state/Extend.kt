package ai.dify.stream.agent.state

import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.dsl.PromptBuilder
import ai.koog.prompt.params.LLMParams
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public val AgentState.prompt: Prompt
    get() = Prompt(
        id = "",
        messages = history,
        params = llmParams,
    )

public var MutableAgentState.prompt: Prompt
    get() = Prompt(
        id = "",
        messages = history,
        params = llmParams,
    )
    set(value) {
        history = value.messages.toMutableList()
        llmParams = value.params
    }

public fun MutableAgentState.updatePrompt(block: PromptBuilder.() -> Unit): Unit {
    prompt = Prompt.build(prompt = prompt, init = block)
}

public inline fun <T> MutableAgentState.withLlmParams(
    llmParams: LLMParams,
    block: MutableAgentState.() -> T,
): T {
    val oldParams = this.llmParams
    try {
        this.llmParams = llmParams
        return block()
    } finally {
        this.llmParams = oldParams
    }
}

public inline fun AgentState.update(block: MutableAgentState.() -> Unit): AgentState {
    @OptIn(ExperimentalContracts::class)
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val fork = forkMutable()
    block(fork)
    return fork.immutable()
}
