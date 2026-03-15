package ai.dify.stream.scripting

import ai.dify.stream.scripting.contract.ScriptContext

public object AgentScriptContext: ScriptContext, FileOperationsScriptContext by FileOperationsScriptContextImpl {
    override val defaultImports: List<String> = FileOperationsScriptContextImpl.defaultImports
    override val llmDescription: String = """
        This tool can be used to execute Kotlin scripts.
        
        You can use it to access JVM environment and access JDK functionalities.
        
        For file access, there's some shorthand methods available.
        """.trimIndent() + FileOperationsScriptContextImpl.llmDescription
}