package ai.dify.stream.scripting

import ai.dify.stream.scripting.contract.KotlinScriptParams
import ai.dify.stream.scripting.contract.KotlinScriptResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun executeScript(script: String): KotlinScriptResult = withContext(Dispatchers.Default) {
    KotlinScriptTool.execute(KotlinScriptParams(script = script))
}