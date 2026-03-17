package ai.dify.stream.tool.kotlinscript.contract

public interface ScriptContext {
    public val defaultImports: List<String>
    public val llmDescription: String
}