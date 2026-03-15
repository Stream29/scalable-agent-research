package ai.dify.stream.scripting

import ai.koog.agents.core.tools.Tool
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

public class KotlinScriptTool(
    public val scriptContext: ScriptContext,
    private val evalFunction: suspend (String) -> KotlinScriptResult,
) : Tool<KotlinScriptParams, KotlinScriptResult>(
    name = kotlinScriptToolName,
    description = "Execute Kotlin scripts with the embedded Kotlin scripting engine",
    argsSerializer = KotlinScriptParams.serializer(),
    resultSerializer = KotlinScriptResult.serializer(),
) {
    override suspend fun execute(args: KotlinScriptParams): KotlinScriptResult {
        return withTimeout(args.timeoutSeconds.seconds) {
            evalFunction(args.script)
        }
    }
}

public inline fun <reified T : ScriptContext> KotlinScriptTool(
    scriptContext: T
): KotlinScriptTool = KotlinScriptTool(
    scriptContext = scriptContext,
    evalFunction = { scriptContext.evalInThreadCancellable(it) }
)
