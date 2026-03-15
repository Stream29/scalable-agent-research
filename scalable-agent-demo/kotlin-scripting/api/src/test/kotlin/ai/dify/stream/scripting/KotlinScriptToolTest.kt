package ai.dify.stream.scripting

import ai.dify.stream.scripting.contract.KotlinScriptResult
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class KotlinScriptToolTest {
    @Test
    fun executeReturnsFailureForInvalidScript() = runTest {
        assertIs<KotlinScriptResult.Failure>(
            executeScript("nonsense")
        )
    }

    @Test
    fun executeReturnsFailureForThrownException() = runTest {
        assertIs<KotlinScriptResult.Failure>(
            executeScript("throw Exception(\"boom\")")
        )
    }

    @Test
    fun executeReturnsSuccessWithDefaultImports() = runTest {
        val scriptResult = executeScript(
            """
                File(".")
            """.trimIndent()
        )
        assertIs<KotlinScriptResult.Success>(scriptResult)
        assertEquals(File(".").toString(), scriptResult.returnValue)
    }
}

