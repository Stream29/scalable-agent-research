package ai.dify.stream.agent.operation

import ai.dify.stream.agent.state.AgentState
import ai.dify.stream.agent.state.forkMutable
import ai.dify.stream.agent.state.updatePrompt
import ai.koog.agents.core.tools.Tool
import ai.koog.prompt.message.Message

suspend fun AgentState.forkedSubtask(
    message: String,
    tools: List<Tool<*, *>>? = null,
): String {
    val child = forkMutable()
    if (tools != null) {
        child.tools = tools.toMutableList()
    }
    child.updatePrompt { this.user(message) }
    return child.resumeToolLoop().responseText()
}

private fun List<Message.Response>.responseText(): String =
    filterIsInstance<Message.Assistant>().lastOrNull()?.content
        ?: lastOrNull { it !is Message.Reasoning }?.content
        ?: error("LLM returned no assistant response.")