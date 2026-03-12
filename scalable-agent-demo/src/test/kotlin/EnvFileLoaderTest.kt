package ai.dify.stream

import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EnvFileLoaderTest {
    @Test
    fun `parse supports export comments and quoted values`() {
        val parsed = EnvFileLoader.parse(
            """
            # comment
            export ANTHROPIC_API_KEY=sk-test
            CHAT_SESSION_ID = demo-session
            CHAT_MEMORY_WINDOW_SIZE=25 # inline comment
            DOUBLE_QUOTED="line1\nline2"
            SINGLE_QUOTED=' spaced value '
            INVALID_LINE
            """.trimIndent()
        )

        assertEquals("sk-test", parsed["ANTHROPIC_API_KEY"])
        assertEquals("demo-session", parsed["CHAT_SESSION_ID"])
        assertEquals("25", parsed["CHAT_MEMORY_WINDOW_SIZE"])
        assertEquals("line1\nline2", parsed["DOUBLE_QUOTED"])
        assertEquals(" spaced value ", parsed["SINGLE_QUOTED"])
    }

    @Test
    fun `load finds dot env in parent directory`() {
        val root = createTempDirectory("env-loader-test")
        val nested = root.resolve("a").resolve("b").createDirectories()
        val envFile = root.resolve(".env")
        envFile.writeText("ANTHROPIC_API_KEY=from-parent")

        val loaded = EnvFileLoader.load(nested)

        assertNotNull(loaded)
        assertEquals(envFile.toAbsolutePath().normalize(), loaded.path)
        assertEquals("from-parent", loaded.values["ANTHROPIC_API_KEY"])
    }
}
