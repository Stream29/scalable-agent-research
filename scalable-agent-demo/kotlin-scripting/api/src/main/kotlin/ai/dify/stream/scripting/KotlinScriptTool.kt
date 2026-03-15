package ai.dify.stream.scripting

import ai.dify.stream.scripting.contract.KotlinScriptParams
import ai.dify.stream.scripting.contract.KotlinScriptResult
import ai.koog.agents.core.tools.Tool
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds


public object KotlinScriptTool : Tool<KotlinScriptParams, KotlinScriptResult>(
    name = "executeKotlinScript",
    description = AgentScriptContext.llmDescription,
    argsSerializer = KotlinScriptParams.serializer(),
    resultSerializer = KotlinScriptResult.serializer(),
) {
    override suspend fun execute(args: KotlinScriptParams): KotlinScriptResult {
        return withTimeout(args.timeoutSeconds.seconds) {
            AgentScriptContext.evalInThreadCancellable(args.script)
        }
    }
}
