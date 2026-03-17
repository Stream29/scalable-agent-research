package ai.dify.stream.tool.kotlinscript

import ai.dify.stream.tool.kotlinscript.contract.KotlinScriptParams
import ai.dify.stream.tool.kotlinscript.contract.KotlinScriptResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun executeScript(script: String): KotlinScriptResult = withContext(Dispatchers.Default) {
    KotlinScriptTool.execute(KotlinScriptParams(script = script))
}