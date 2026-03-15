package ai.dify.stream.scripting

public interface ScriptContext {
    public val defaultImports: List<String>
    public val systemPromptInjection: String
}

public class CompositeScriptContext(
    receiverTypeName: String,
    modulesInStableOrder: List<ScriptContext>,
    roleConstraints: List<String> = emptyList(),
) : ScriptContext {
    private val modules: List<ScriptContext> = modulesInStableOrder.toList()
    private val constraints: List<String> = roleConstraints.toList()

    override val defaultImports: List<String> = modules
        .flatMap { module -> module.defaultImports }
        .distinct()

    override val systemPromptInjection: String = buildString {
        appendLine("## Script receiver API (implicit receiver = $receiverTypeName):")
        appendLine()
        appendLine("You can call methods on `$receiverTypeName` in your script without `this`.")
        appendLine("Referencing `this` directly is also allowed when you need the receiver instance.")

        if (constraints.isNotEmpty()) {
            appendLine()
            appendLine("Role constraints:")
            constraints.forEach { constraint ->
                appendLine("- $constraint")
            }
        }

        val moduleInjections = modules
            .map { module -> module.systemPromptInjection.trim() }
            .filter { injection -> injection.isNotBlank() }

        if (moduleInjections.isNotEmpty()) {
            appendLine()
            append(moduleInjections.joinToString(separator = "\n\n"))
        }
    }.trim()
}
