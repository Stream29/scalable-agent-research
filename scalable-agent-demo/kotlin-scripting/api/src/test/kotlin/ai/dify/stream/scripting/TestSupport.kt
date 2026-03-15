package ai.dify.stream.scripting

import ai.dify.stream.scripting.contract.KotlinScriptParams
import ai.dify.stream.scripting.contract.KotlinScriptResult
import ai.dify.stream.scripting.contract.ScriptContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object TestScriptContext : ScriptContext {
    override val defaultImports: List<String> = listOf(UUID::class.qualifiedName!!)
    override val llmDescription: String = "Test script context"
}

val testScriptTool = KotlinScriptTool(TestScriptContext)

suspend fun executeScript(script: String): KotlinScriptResult = withContext(Dispatchers.Default) {
    testScriptTool.execute(KotlinScriptParams(script = script))
}