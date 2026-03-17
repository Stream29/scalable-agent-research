package ai.dify.stream.tool.kotlinscript

import ai.dify.stream.tool.kotlinscript.contract.KotlinScriptParams
import ai.dify.stream.tool.kotlinscript.contract.KotlinScriptResult
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
