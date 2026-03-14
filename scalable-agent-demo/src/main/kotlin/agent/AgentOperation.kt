package ai.dify.stream.agent

import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.message.Message

suspend fun AgentState.requestLlm(): List<Message.Response> =
    promptExecutor.execute(
        prompt = Prompt(
            id = "",
            messages = history,
            params = llmParams,
        ),
        model = model,
        tools = tools.map { it.descriptor },
    )

suspend fun MutableAgentState.requestLlmAndSave(): List<Message.Response> =
    requestLlm().also { history.addAll(it) }