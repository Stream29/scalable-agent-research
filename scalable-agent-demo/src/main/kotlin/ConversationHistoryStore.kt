package ai.dify.stream

import ai.koog.prompt.message.Message
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ConversationHistoryStore(
    private val windowSize: Int,
) {
    private val mutex = Mutex()
    private val histories = mutableMapOf<String, List<Message>>()

    suspend fun load(sessionId: String): List<Message> {
        return mutex.withLock {
            histories[sessionId].orEmpty()
        }
    }

    suspend fun save(sessionId: String, messages: List<Message>) {
        mutex.withLock {
            histories[sessionId] = messages.takeLast(windowSize)
        }
    }

    suspend fun clear(sessionId: String) {
        mutex.withLock {
            histories.remove(sessionId)
        }
    }
}
