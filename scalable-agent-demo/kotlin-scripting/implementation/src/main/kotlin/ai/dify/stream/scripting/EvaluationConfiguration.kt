package ai.dify.stream.scripting

import ai.dify.stream.scripting.contract.ScriptContext
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.api.scriptsInstancesSharing

internal val <T : ScriptContext> T.evaluationConfiguration: ScriptEvaluationConfiguration.Builder.() -> Unit
    get() = {
        constructorArgs(emptyArray<String>())
        implicitReceivers(this@evaluationConfiguration)
        scriptsInstancesSharing(false)
    }