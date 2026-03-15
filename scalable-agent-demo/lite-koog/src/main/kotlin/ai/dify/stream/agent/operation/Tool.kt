package ai.dify.stream.agent.operation

import ai.dify.stream.agent.state.AgentState
import ai.dify.stream.agent.state.MutableAgentState
import ai.koog.agents.core.environment.ReceivedToolResult
import ai.koog.agents.core.environment.ToolResultKind
import ai.koog.agents.core.feature.model.toAgentError
import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolException
import ai.koog.prompt.message.Message
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.JsonObject
import kotlin.coroutines.cancellation.CancellationException

public suspend fun AgentState.executeMultipleTools(
    toolCalls: List<Message.Tool.Call>,
    parallel: Boolean = true,
): List<ReceivedToolResult> = if (parallel) {
    coroutineScope {
        toolCalls.map { toolCall ->
            async { executeTool(toolCall) }
        }.awaitAll()
    }
} else {
    toolCalls.map { executeTool(it) }
}

public suspend fun MutableAgentState.executeMultipleToolsAndSave(
    toolCalls: List<Message.Tool.Call>,
    parallel: Boolean = true,
): List<ReceivedToolResult> =
    executeMultipleTools(toolCalls, parallel).also { toolCallResults ->
        history.addAll(toolCallResults.map { it.toMessage() })
    }


/**
 * Copied from [ai.koog.agents.core.environment.GenericAgentEnvironment.executeTool]
 */
public suspend fun AgentState.executeTool(toolCall: Message.Tool.Call): ReceivedToolResult {
    val id = toolCall.id
    val toolName = toolCall.tool
    val toolArgsJson = try {
        toolCall.contentJson
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        return ReceivedToolResult(
            id = id,
            tool = toolName,
            toolArgs = JsonObject(emptyMap()),
            toolDescription = null,
            content = "Tool with name '$toolName' failed to parse arguments due to the error: ${e.message}",
            resultKind = ToolResultKind.Failure(e.toAgentError()),
            result = null,
        )
    }

    val tool = tools.firstOrNull { it.name == toolName }
        ?: run {
            return ReceivedToolResult(
                id = id,
                tool = toolName,
                toolArgs = toolArgsJson,
                toolDescription = null,
                content = "Tool with name '$toolName' not found in the tool registry. Use one of the available tools.",
                resultKind = ToolResultKind.Failure(null),
                result = null,
            )
        }

    val toolDescription = tool.descriptor.description

    // Tool Args
    val toolArgs = try {
        tool.decodeArgs(toolArgsJson)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        return ReceivedToolResult(
            id = id,
            tool = toolName,
            toolArgs = toolArgsJson,
            toolDescription = toolDescription,
            content = "Tool with name '$toolName' failed to parse arguments due to the error: ${e.message}",
            resultKind = ToolResultKind.Failure(e.toAgentError()),
            result = null,
        )
    }

    val toolResult = try {
        @Suppress("UNCHECKED_CAST")
        (tool as Tool<Any?, Any?>).execute(toolArgs)
    } catch (e: CancellationException) {
        throw e
    } catch (e: ToolException) {
        return ReceivedToolResult(
            id = id,
            tool = toolName,
            toolArgs = toolArgsJson,
            toolDescription = toolDescription,
            content = e.message,
            resultKind = ToolResultKind.ValidationError(e.toAgentError()),
            result = null,
        )
    } catch (e: Exception) {

        return ReceivedToolResult(
            id = id,
            tool = toolName,
            toolArgs = toolArgsJson,
            toolDescription = toolDescription,
            content = "Tool with name '$toolName' failed to execute due to the error: ${e.message}!",
            resultKind = ToolResultKind.Failure(e.toAgentError()),
            result = null
        )
    }

    val (content, result) = try {
        tool.encodeResultToStringUnsafe(toolResult) to tool.encodeResult(toolResult)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        return ReceivedToolResult(
            id = id,
            tool = toolName,
            toolArgs = toolArgsJson,
            toolDescription = toolDescription,
            content = "Tool with name '$toolName' failed to serialize result due to the error: ${e.message}!",
            resultKind = ToolResultKind.Failure(e.toAgentError()),
            result = null
        )
    }

    return ReceivedToolResult(
        id = id,
        tool = toolName,
        toolArgs = toolArgsJson,
        toolDescription = toolDescription,
        content = content,
        resultKind = ToolResultKind.Success,
        result = result
    )
}
