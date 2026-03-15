package ai.dify.stream.agent.operation

import ai.dify.stream.agent.state.MutableAgentState
import ai.dify.stream.agent.state.withLlmParams
import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.annotations.InternalAgentToolsApi
import ai.koog.agents.core.tools.asToolDescriptor
import ai.koog.prompt.message.Message
import ai.koog.prompt.params.LLMParams
import kotlinx.serialization.serializer

suspend fun MutableAgentState.resumeAgentLoopAndSave(
    tools: List<Tool<*, *>>? = null,
    parallel: Boolean = true,
): List<Message.Response> {
    if (tools != null)
        this.tools = tools.toMutableList()
    while (true) {
        val responses = requestLlmAndSave()
        require(responses.isNotEmpty()) { "No responses received" }
        val toolCalls = responses.filterIsInstance<Message.Tool.Call>()
        if (toolCalls.isEmpty() && responses.any { it is Message.Assistant })
            return responses
        executeMultipleToolsAndSave(toolCalls, parallel)
    }
}

suspend inline fun <reified Output> MutableAgentState.resumeAgentLoopStructuredAndSave(
    tools: List<Tool<*, *>>? = null,
    finishTool: Tool<Output, *> = finishTool<Output>()
): Output {
    if (tools != null)
        this.tools = tools.toMutableList()
    this.tools.add(finishTool)
    while (true) {
        withLlmParams(llmParams.copy(toolChoice = LLMParams.ToolChoice.Required)) {
            val responses = requestLlmAndSave()
            require(responses.all { it is Message.Tool.Call })
            @Suppress("UNCHECKED_CAST")
            val toolCalls = responses as List<Message.Tool.Call>
            executeMultipleToolsAndSave(toolCalls)
            toolCalls.firstOrNull { it.tool == finishTool.name }?.let {
                return finishTool.decodeArgs(it.contentJson)
            }
        }
    }
}

@PublishedApi
@OptIn(InternalAgentToolsApi::class)
internal inline fun <reified T> finishTool(): Tool<T, String> = object : Tool<T, String>(
    argsSerializer = serializer(),
    resultSerializer = serializer(),
    descriptor = serializer<T>().descriptor.asToolDescriptor(
        toolName = "submit_final_result",
        toolDescription = "Call this tool when finish and provide the final result"
    )
) {
    override suspend fun execute(args: T): String = "Submitted successfully."
}