package ai.dify.stream

import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@LLMDescription("任务计划，将当前任务根据依赖关系拆解成若干个串行或并行执行的步骤。")
public sealed interface Plan {
    public val title: String

    @Serializable
    public sealed interface Decomposition: Plan

    @Serializable
    public data class Task(
        @LLMDescription("任务标题，简短概括任务内容")
        override val title: String,
        @LLMDescription("任务描述，说清楚任务需要做什么，需要达到什么状态或者产出什么")
        val description: String
    ) : Plan
    @Serializable
    @SerialName("Sequential")
    public data class Sequential(
        @LLMDescription("任务标题，简短概括任务内容")
        override val title: String,
        @LLMDescription("任务步骤，按顺序执行的步骤列表")
        val steps: List<Task>
    ) : Decomposition
    @Serializable
    @SerialName("Parallel")
    public data class Parallel(
        @LLMDescription("任务标题，简短概括任务内容")
        override val title: String,
        @LLMDescription("任务分支，按并行执行的分支列表")
        val branches: List<Task>
    ) : Decomposition

    public enum class DecompositionChoice {
        Parallel,
        Sequential,
        Atomic
    }
}