package ai.dify.stream.agent.state

import ai.koog.agents.core.tools.Tool
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import ai.koog.prompt.params.LLMParams

public interface AgentState {
    public val history: List<Message>
    public val llmParams: LLMParams
    public val promptExecutor: PromptExecutor
    public val model: LLModel
    public val tools: List<Tool<*, *>>
}

public interface MutableAgentState : AgentState {
    public override var history: MutableList<Message>
    public override var llmParams: LLMParams
    public override var promptExecutor: PromptExecutor
    public override var model: LLModel
    public override var tools: MutableList<Tool<*, *>>
}

