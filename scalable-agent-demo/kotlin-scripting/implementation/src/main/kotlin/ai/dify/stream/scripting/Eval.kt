package ai.dify.stream.scripting

import ai.dify.stream.scripting.contract.KotlinScriptResult
import ai.dify.stream.scripting.contract.ScriptContext
import org.jetbrains.kotlin.mainKts.MainKtsScript
import kotlin.concurrent.withLock
import kotlin.script.experimental.host.toScriptSource

public suspend fun <T : ScriptContext> T.evalInThreadCancellable(script: String): KotlinScriptResult =
    executeInThreadCancellable {
        scriptEvaluationMutex.withLock { evalUnsafe(this, script) }
    }

private fun <T : ScriptContext> evalUnsafe(scriptContext: T, script: String): KotlinScriptResult =
    captureStdout {
        host.evalWithTemplate<MainKtsScript>(
            script = script.toScriptSource(),
            compilation = scriptContext.compilationConfiguration,
            evaluation = scriptContext.evaluationConfiguration,
        )
    }.toEvalResult()