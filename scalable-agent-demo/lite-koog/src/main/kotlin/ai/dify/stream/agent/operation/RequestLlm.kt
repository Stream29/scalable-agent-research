package ai.dify.stream.agent.operation

import ai.dify.stream.agent.state.AgentState
import ai.dify.stream.agent.state.MutableAgentState
import ai.dify.stream.agent.state.prompt
import ai.dify.stream.agent.state.withToolChoice
import ai.dify.stream.agent.state.withTools
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.message.Message
import ai.koog.prompt.params.LLMParams
import ai.koog.prompt.structure.StructuredResponse
import ai.koog.prompt.structure.executeStructured
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.serializer

public suspend fun AgentState.requestLlm(): List<Message.Response> =
    promptExecutor.execute(
        prompt = prompt,
        model = model,
        tools = tools.map { it.descriptor },
    )

public suspend fun MutableAgentState.requestLlmAndSave(): List<Message.Response> =
    requestLlm().also { history.addAll(it) }

public suspend inline fun <reified T> MutableAgentState.requestLlmStructured(): StructuredResponse<T> {
    return promptExecutor.executeStructured(
        prompt = Prompt(
            id = "",
            messages = history,
            params = llmParams,
        ),
        model = model,
        serializer = serializer<T>(),
    ).getOrThrow()
}

public suspend inline fun <reified T> MutableAgentState.requestLlmStructuredAndSave(): T {
    if (serializer<T>().descriptor.kind is SerialKind.ENUM) {
        withToolChoice(LLMParams.ToolChoice.Required) {
            withTools(listOf(finishTool<T>())) {
                val toolCall = requestLlmAndSave().single()
                require(toolCall is Message.Tool.Call)
                executeMultipleToolsAndSave(listOf(toolCall))
                return finishTool<T>().decodeArgs(toolCall.contentJson)
            }
        }
    }
    return requestLlmStructured<T>().also { history.add(it.message) }.data
}
