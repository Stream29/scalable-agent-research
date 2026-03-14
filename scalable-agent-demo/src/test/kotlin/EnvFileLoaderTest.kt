package ai.dify.stream

import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class EnvFileLoaderTest {
    @Test
    fun `load finds dot env in parent directory`() {
        val root = createTempDirectory("env-loader-test")
        val nested = root.resolve("a").resolve("b").createDirectories()
        val envFile = root.resolve(".env")
        envFile.writeText("ANTHROPIC_API_KEY=from-parent")

        val loaded = EnvFileLoader.load(nested)

        assertNotNull(loaded)
        assertEquals(envFile.toAbsolutePath().normalize(), loaded.path)
        assertEquals("from-parent", loaded.get("ANTHROPIC_API_KEY"))
    }

    @Test
    fun `load returns null when dot env is not present`() {
        val root = createTempDirectory("env-loader-missing")

        val loaded = EnvFileLoader.load(root)

        assertNull(loaded)
    }
}
