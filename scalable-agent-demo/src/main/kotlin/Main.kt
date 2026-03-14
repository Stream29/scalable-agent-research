package ai.dify.stream

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.agent.subtask
import ai.koog.agents.snapshot.feature.withPersistence
import ai.koog.prompt.message.Message


val strategy = functionalStrategy<WithMessages<String>, WithMessages<String>> { (history, message) ->
    val response = subtask<String, String>(message) { it }
    WithMessages(getHistory(), response)
}

fun main() = runWithShell {
    var history = emptyList<Message>()
    while (true) {
        val agent = AIAgent(
            promptExecutor = promptExecutor,
            agentConfig = agentConfig,
            strategy = strategy,
            toolRegistry = ToolRegistry.EMPTY,
        )
        print("User: ")
        val input = readln()
        agent.withPersistence {  }
        val (newHistory, response) = agent.runWithHistory(input, history)
        history = newHistory
        println("Assistant: $response")
    }
}
