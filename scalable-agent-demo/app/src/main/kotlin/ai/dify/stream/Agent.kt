package ai.dify.stream

import ai.dify.stream.agent.operation.forkedSubtask
import ai.dify.stream.agent.operation.requestLlmAndSave
import ai.dify.stream.agent.operation.requestLlmStructuredAndSave
import ai.dify.stream.agent.state.MutableAgentState
import ai.dify.stream.agent.state.updatePrompt
import ai.dify.stream.agent.state.withLlmParams
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.prompt.message.Message
import ai.koog.prompt.params.LLMParams
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable

@Serializable
internal data class Plan(
    @LLMDescription(
        "将当前任务根据依赖关系拆解成若干个串行执行的步骤。" +
                "可以并行的部分应该放在同一个步骤的多个分支里，因为步骤是串行执行的。" +
                "如果没办法拆解，只有一个步骤也可以接受。"
    )
    val steps: List<Step>
) {
    @Serializable
    internal data class Step(
        @LLMDescription("步骤标题，简短概括步骤内容")
        val title: String,
        @LLMDescription(
            "将当前步骤尽可能拆解成可以并行的分支。" +
                    "如果没办法并行，只有一个分支也可以接受"
        )
        val branches: List<Task>
    )

    @Serializable
    internal data class Task(
        @LLMDescription("任务标题，简短概括任务内容")
        val title: String,
        @LLMDescription("任务描述，说清楚任务需要做什么，需要达到什么状态或者产出什么")
        val description: String
    )
}

internal fun Plan.Task.message(): String =
    "现在你只需要执行${title}这个子任务，完成后立刻返回：${description}"

internal suspend fun MutableAgentState.runUserTask(message: String): String {
    updatePrompt {
        user(message)
        user("请给出当前任务的拆分方案")
    }
    val plan = requestLlmStructuredAndSave<Plan>().data

    for (step in plan.steps) {
        updatePrompt { user("现在去执行${step.title}这个步骤") }

        coroutineScope {
            step.branches.map { task ->
                async {
                    task to forkedSubtask(message = task.message())
                }
            }.awaitAll().forEach { (task, response) ->
                updatePrompt { user("现在去执行${task.title}这个子任务") }
                updatePrompt { assistant(response) }
            }
        }
    }
    updatePrompt { user("你可以汇报你的工作了") }
    withLlmParams(llmParams.copy(toolChoice = LLMParams.ToolChoice.None)) {
        val response = requestLlmAndSave()
        return response.filterIsInstance<Message.Assistant>().single().content
    }
}
