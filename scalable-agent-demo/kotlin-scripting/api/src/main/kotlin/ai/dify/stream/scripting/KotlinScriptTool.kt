package ai.dify.stream.scripting

import ai.dify.stream.scripting.contract.KotlinScriptParams
import ai.dify.stream.scripting.contract.KotlinScriptResult
import ai.dify.stream.scripting.contract.ScriptContext
import ai.koog.agents.core.tools.Tool
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

private const val kotlinScriptToolName: String = "executeKotlinScript"

@Suppress("FunctionName")
public fun <T : ScriptContext> KotlinScriptTool(
    scriptContext: T,
): Tool<KotlinScriptParams, KotlinScriptResult> = object : Tool<KotlinScriptParams, KotlinScriptResult>(
    name = kotlinScriptToolName,
    description = scriptContext.llmDescription,
    argsSerializer = KotlinScriptParams.serializer(),
    resultSerializer = KotlinScriptResult.serializer(),
) {
    override suspend fun execute(args: KotlinScriptParams): KotlinScriptResult {
        return withTimeout(args.timeoutSeconds.seconds) {
            scriptContext.evalInThreadCancellable(args.script)
        }
    }
}
