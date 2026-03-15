package ai.dify.stream.scripting.contract

public interface ScriptContext {
    public val defaultImports: List<String>
    public val llmDescription: String
}