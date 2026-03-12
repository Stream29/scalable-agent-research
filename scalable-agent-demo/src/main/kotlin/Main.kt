package ai.dify.stream

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import ai.koog.prompt.message.Message
import ai.koog.prompt.message.RequestMetaInfo
import java.util.UUID

private const val ApiKeyEnvName = "ANTHROPIC_API_KEY"
private const val SessionIdEnvName = "CHAT_SESSION_ID"
private const val WindowSizeEnvName = "CHAT_MEMORY_WINDOW_SIZE"
private const val DefaultSessionId = "claude-haiku-demo"
private const val DefaultWindowSize = 20
private const val ExitCommand = "/exit"
private const val SystemInstruction =
    "You are a helpful assistant. Keep answers concise unless the user asks for detail."

suspend fun main() {
    val loadedEnv = EnvFileLoader.load()
    val apiKey = resolveRequiredValue(ApiKeyEnvName, loadedEnv)
    var sessionId = resolveOptionalValue(SessionIdEnvName, loadedEnv) ?: DefaultSessionId
    val windowSize = resolveOptionalValue(WindowSizeEnvName, loadedEnv)
        ?.toIntOrNull()
        ?.takeIf { it > 0 }
        ?: DefaultWindowSize

    val historyStore = ConversationHistoryStore(windowSize)
    val basePrompt = prompt("claude-haiku-chat") {
        system(SystemInstruction)
    }

    simpleAnthropicExecutor(apiKey).use { executor ->
        println("Claude Haiku chat is ready.")
        println("Model: ${AnthropicModels.Haiku_4_5.id}")
        println("Session: $sessionId")
        println("Memory window: $windowSize messages")
        loadedEnv?.let { println("Loaded .env from: ${it.path}") }
        println("Commands: /new starts a fresh chat, /session shows the current session, $ExitCommand quits.")
        println()

        while (true) {
            print("You: ")
            val input = readlnOrNull()?.trim() ?: break

            when {
                input.isBlank() -> continue
                input.equals(ExitCommand, ignoreCase = true) ||
                    input.equals("/quit", ignoreCase = true) ||
                    input.equals("/bye", ignoreCase = true) -> break

                input.equals("/session", ignoreCase = true) -> {
                    println("Current session: $sessionId")
                    println()
                    continue
                }

                input.equals("/new", ignoreCase = true) || input.equals("/reset", ignoreCase = true) -> {
                    historyStore.clear(sessionId)
                    sessionId = "claude-haiku-${UUID.randomUUID()}"
                    println("Started a new empty session: $sessionId")
                    println()
                    continue
                }
            }

            val userMessage = Message.User(input, RequestMetaInfo.Empty)
            val history = historyStore.load(sessionId)
            val requestPrompt = basePrompt.copy(messages = basePrompt.messages + history + userMessage)

            val response = runCatching {
                executor.execute(requestPrompt, AnthropicModels.Haiku_4_5).single()
            }.getOrElse { error ->
                println("Claude request failed: ${error.message}")
                println()
                continue
            }

            historyStore.save(sessionId, history + userMessage + response)
            println("Claude: ${response.content}")
            println()
        }
    }

    println("Chat ended.")
}

private fun resolveRequiredValue(name: String, loadedEnv: LoadedEnv?): String {
    return resolveOptionalValue(name, loadedEnv)
        ?: error(
            "$name is not set. Add it to a .env file near scalable-agent-demo or export it in your shell."
        )
}

private fun resolveOptionalValue(name: String, loadedEnv: LoadedEnv?): String? {
    return System.getenv(name)
        ?.takeIf { it.isNotBlank() }
        ?: loadedEnv?.values?.get(name)?.takeIf { it.isNotBlank() }
}
