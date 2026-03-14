package ai.dify.stream

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.context.AIAgentFunctionalContext
import ai.koog.agents.core.agent.entity.AIAgentStateManager
import ai.koog.agents.core.agent.entity.AIAgentStorage
import ai.koog.agents.core.dsl.extension.executeMultipleTools
import ai.koog.agents.core.dsl.extension.extractToolCalls
import ai.koog.agents.core.dsl.extension.sendMultipleToolResults
import ai.koog.agents.core.tools.Tool
import ai.koog.agents.ext.agent.subtask
import ai.koog.prompt.message.Message

data class WithMessages<T>(
    val messages: List<Message>,
    val value: T
)

suspend fun AIAgentFunctionalContext.fork(partName: String): AIAgentFunctionalContext =
    copy(
        llm = llm.copy(),
        executionInfo = executionInfo.copy(
            parent = executionInfo,
            partName = partName
        ),
        storage = AIAgentStorage().apply { putAll(storage.toMap()) },
        stateManager = stateManager.withStateLock { AIAgentStateManager(it)  }
    )

suspend fun <Input, Output> AIAgent<WithMessages<Input>, WithMessages<Output>>.runWithHistory(
    input: Input,
    history: List<Message>
): WithMessages<Output> = run(WithMessages(history, input))