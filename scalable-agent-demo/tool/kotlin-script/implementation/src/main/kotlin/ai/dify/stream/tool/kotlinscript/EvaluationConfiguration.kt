package ai.dify.stream.tool.kotlinscript

import ai.dify.stream.tool.kotlinscript.contract.ScriptContext
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