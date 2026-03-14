package ai.dify.stream.agent

import ai.koog.agents.core.tools.Tool
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import ai.koog.prompt.params.LLMParams

interface AgentState {
    val history: List<Message>
    val llmParams: LLMParams
    val promptExecutor: PromptExecutor
    val model: LLModel
    val tools: List<Tool<*, *>>
}

interface MutableAgentState : AgentState {
    override var history: MutableList<Message>
    override var llmParams: LLMParams
    override var promptExecutor: PromptExecutor
    override var model: LLModel
    override var tools: MutableList<Tool<*, *>>
}

private data class AgentStateImpl(
    override val history: List<Message>,
    override val llmParams: LLMParams,
    override val promptExecutor: PromptExecutor,
    override val model: LLModel,
    override val tools: List<Tool<*, *>>
) : AgentState

private data class MutableAgentStateImpl(
    override var history: MutableList<Message>,
    override var llmParams: LLMParams,
    override var promptExecutor: PromptExecutor,
    override var model: LLModel,
    override var tools: MutableList<Tool<*, *>>
) : MutableAgentState

fun AgentState.mutable(): MutableAgentState = MutableAgentStateImpl(
    history = history.toMutableList(),
    llmParams = llmParams,
    promptExecutor = promptExecutor,
    model = model,
    tools = tools.toMutableList()
)

fun AgentState.toAgentState(): AgentState = AgentStateImpl(
    history = history.toList(),
    llmParams = llmParams,
    promptExecutor = promptExecutor,
    model = model,
    tools = tools.toList()
)


inline fun AgentState.update(crossinline block: MutableAgentState.() -> Unit): AgentState =
    mutable().apply(block).toAgentState()