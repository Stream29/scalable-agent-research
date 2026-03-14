package ai.dify.stream

import ai.dify.stream.agent.state.AgentState
import ai.dify.stream.agent.state.update
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.ext.tool.SayToUser
import kotlinx.serialization.Serializable

fun main() = runWithShell {
    var state = AgentState(
        promptExecutor = promptExecutor,
        model = agentModel,
        llmParams = agentLlmParams,
        tools = listOf(SayToUser),
    )

    while (true) {
        print("User: ")
        val input = readln()
        state = state.update {
            val response = runUserTask(input)
            println("Assistant: $response")
        }
    }
}
