package ai.dify.stream

import ai.koog.agents.core.tools.Tool
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message

interface AgentState {
    val history: List<Message>
    val promptExecutor: PromptExecutor
    val model: LLModel
    val tools: List<Tool<*, *>>
}

interface MutableAgentState : AgentState {
    override var history: MutableList<Message>
    override var promptExecutor: PromptExecutor
    override var model: LLModel
    override var tools: MutableList<Tool<*, *>>
}

private data class AgentStateImpl(
    override val history: List<Message>,
    override val promptExecutor: PromptExecutor,
    override val model: LLModel,
    override val tools: List<Tool<*, *>>
) : AgentState

private data class MutableAgentStateImpl(
    override var history: MutableList<Message>,
    override var promptExecutor: PromptExecutor,
    override var model: LLModel,
    override var tools: MutableList<Tool<*, *>>
) : MutableAgentState

fun AgentState.mutable(): MutableAgentState = MutableAgentStateImpl(
    history = history.toMutableList(),
    promptExecutor = promptExecutor,
    model = model,
    tools = tools.toMutableList()
)

fun AgentState.toAgentState(): AgentState = AgentStateImpl(
    history = history.toList(),
    promptExecutor = promptExecutor,
    model = model,
    tools = tools.toList()
)


inline fun AgentState.update(crossinline block: MutableAgentState.() -> Unit): AgentState =
    mutable().apply(block).toAgentState()