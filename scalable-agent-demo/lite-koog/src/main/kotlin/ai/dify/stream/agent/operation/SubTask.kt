package ai.dify.stream.agent.operation

import ai.dify.stream.agent.state.AgentState
import ai.dify.stream.agent.state.forkMutable
import ai.dify.stream.agent.state.updatePrompt
import ai.koog.agents.core.tools.Tool

public suspend fun AgentState.forkedSubtask(
    message: String,
    tools: List<Tool<*, *>>? = null,
): String {
    val fork = forkMutable()
    fork.updatePrompt { user(message) }
    return fork.resumeAgentLoopStructuredAndSave<String>(tools)
}
