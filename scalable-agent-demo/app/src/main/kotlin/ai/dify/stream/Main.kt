package ai.dify.stream

import ai.dify.stream.agent.state.AgentState
import ai.dify.stream.agent.state.update
import ai.dify.stream.scripting.KotlinScriptTool
import ai.dify.stream.shell.ShellCommandTool
import ai.koog.agents.ext.tool.SayToUser

public fun main(): Unit = runWithShell {
    var state = AgentState(
        promptExecutor = promptExecutor,
        model = agentModel,
        llmParams = agentLlmParams,
        tools = listOf(
            SayToUser,
            KotlinScriptTool,
            ShellCommandTool
        ),
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
