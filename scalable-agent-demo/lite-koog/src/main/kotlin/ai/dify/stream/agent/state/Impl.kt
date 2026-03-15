package ai.dify.stream.agent.state

import ai.koog.agents.core.tools.Tool
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import ai.koog.prompt.params.LLMParams


public fun AgentState(
    history: List<Message> = emptyList(),
    llmParams: LLMParams = LLMParams(),
    promptExecutor: PromptExecutor,
    model: LLModel,
    tools: List<Tool<*, *>>,
): AgentState = AgentStateImpl(
    history = history,
    llmParams = llmParams,
    promptExecutor = promptExecutor,
    model = model,
    tools = tools,
)

public fun AgentState.forkMutable(): MutableAgentState = MutableAgentStateImpl(
    history = history.toMutableList(),
    llmParams = llmParams,
    promptExecutor = promptExecutor,
    model = model,
    tools = tools.toMutableList(),
)

public fun AgentState.immutable(): AgentState = AgentStateImpl(
    history = history.toList(),
    llmParams = llmParams,
    promptExecutor = promptExecutor,
    model = model,
    tools = tools.toList(),
)

private data class AgentStateImpl(
    override val history: List<Message>,
    override val llmParams: LLMParams,
    override val promptExecutor: PromptExecutor,
    override val model: LLModel,
    override val tools: List<Tool<*, *>>,
) : AgentState

private data class MutableAgentStateImpl(
    override var history: MutableList<Message>,
    override var llmParams: LLMParams,
    override var promptExecutor: PromptExecutor,
    override var model: LLModel,
    override var tools: MutableList<Tool<*, *>>,
) : MutableAgentState
