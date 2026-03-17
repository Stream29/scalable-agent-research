package ai.dify.stream

import ai.dify.stream.agent.state.AgentState
import ai.dify.stream.agent.state.update
import ai.dify.stream.tool.kotlinscript.KotlinScriptTool
import ai.dify.stream.tool.shell.ShellCommandTool
import ai.koog.agents.ext.tool.SayToUser

public fun main(): Unit = runWithShell {
    var state = AgentState(
        promptExecutor = promptExecutor,
        model = agentModel,
        llmParams = agentLlmParams,
        tools = listOf(
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
