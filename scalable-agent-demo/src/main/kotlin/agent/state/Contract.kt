package ai.dify.stream.agent.state

import ai.koog.agents.core.tools.Tool
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.dsl.PromptBuilder
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

