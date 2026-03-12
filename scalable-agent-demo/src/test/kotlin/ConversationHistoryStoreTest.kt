package ai.dify.stream

import ai.koog.prompt.message.Message
import ai.koog.prompt.message.RequestMetaInfo
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConversationHistoryStoreTest {
    @Test
    fun `save keeps only the last configured messages`() = runTest {
        val store = ConversationHistoryStore(windowSize = 2)
        val history = listOf(
            Message.User("first", RequestMetaInfo.Empty),
            Message.User("second", RequestMetaInfo.Empty),
            Message.User("third", RequestMetaInfo.Empty),
        )

        store.save("demo", history)

        assertEquals(listOf("second", "third"), store.load("demo").map { it.content })
    }

    @Test
    fun `clear removes the stored session history`() = runTest {
        val store = ConversationHistoryStore(windowSize = 5)
        store.save("demo", listOf(Message.User("hello", RequestMetaInfo.Empty)))

        store.clear("demo")

        assertTrue(store.load("demo").isEmpty())
    }
}
