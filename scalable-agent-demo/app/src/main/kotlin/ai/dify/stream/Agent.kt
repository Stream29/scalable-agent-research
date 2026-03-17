package ai.dify.stream

import ai.dify.stream.agent.operation.finishTool
import ai.dify.stream.agent.operation.requestLlmStructuredAndSave
import ai.dify.stream.agent.operation.resumeAgentLoopStructuredAndSave
import ai.dify.stream.agent.state.MutableAgentState
import ai.dify.stream.agent.state.forkMutable
import ai.dify.stream.agent.state.updatePrompt
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

public fun Plan.Task.message(): String =
    "现在你只需要执行${title}这个子任务，完成后立刻返回：${description}"

internal fun Plan.Task.shortMessage(): String =
    "现在你去执行${title}这个子任务"

internal suspend fun MutableAgentState.requestAnswerAndSave(
    plan: Plan,
): String {
    updatePrompt { user("你可以汇报你的工作了，范围是${plan.title}") }
    return requestLlmStructuredAndSave<String>()
}

internal suspend fun MutableAgentState.runParallelPlanAndSave(
    plan: Plan.Parallel,
): String = coroutineScope {
    plan.branches.map { task ->
        async { task to forkMutable().runTaskAndSave(task) }
    }.awaitAll().forEach { (task, response) ->
        updatePrompt { user(task.shortMessage()) }
        updatePrompt { assistant(response) }
    }
    requestAnswerAndSave(plan)
}

public suspend fun MutableAgentState.runSequentialPlanAndSave(
    plan: Plan.Sequential,
): String {
    for (step in plan.steps) {
        val taskResult = forkMutable().runTaskAndSave(step)
        updatePrompt {
            user(step.shortMessage())
            assistant(taskResult)
        }
    }
    return requestAnswerAndSave(plan)
}

public suspend fun MutableAgentState.runPlanAndSave(
    plan: Plan,
): String {
    println("Running plan: $plan")
    return when (plan) {
        is Plan.Parallel -> runParallelPlanAndSave(plan)
        is Plan.Sequential -> runSequentialPlanAndSave(plan)
        is Plan.Task -> runAtomicAndSave(plan)
    }
}

public suspend fun MutableAgentState.runTaskAndSave(
    task: Plan.Task,
): String {
    updatePrompt { user(task.message()) }
    val plan = planAndSave()
    return runPlanAndSave(plan)
}

public suspend fun MutableAgentState.runAtomicAndSave(
    task: Plan.Task,
): String {
    updatePrompt { user(task.message()) }
    return resumeAgentLoopStructuredAndSave(finishTool<String>())
}

public suspend fun MutableAgentState.planAndSave(): Plan {
    val decompose = forkMutable().run {
        updatePrompt {
            user("如果当前可以拆分成没有任何依赖关系的几个并行任务，就并行。如果当前任务很大很复杂，就拆成顺序执行的步骤。如果当前任务很简单，一眼就能看明白，就不拆分")
        }
        requestLlmStructuredAndSave<Plan.DecompositionChoice>()
    }
    updatePrompt { user("请给出当前任务的执行计划") }
    val plan = when (decompose) {
        Plan.DecompositionChoice.Parallel -> requestLlmStructuredAndSave<Plan.Parallel>()
        Plan.DecompositionChoice.Sequential -> requestLlmStructuredAndSave<Plan.Sequential>()
        Plan.DecompositionChoice.Atomic -> requestLlmStructuredAndSave<Plan.Task>()
    }
    return plan
}

public suspend fun MutableAgentState.runUserTask(message: String): String {
    updatePrompt { user(message) }
    val plan = planAndSave()
    return runPlanAndSave(plan)
}
