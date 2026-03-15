package ai.dify.stream.scripting

import ai.dify.stream.scripting.contract.KotlinScriptResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class KotlinScriptToolTest {
    @Test
    fun descriptorUsesContextDescription() {
        assertEquals(TestScriptContext.llmDescription, testScriptTool.descriptor.description)
    }

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
    fun executeReturnsSuccessWithStdoutAndDefaultImports() = runTest {
        val expectedStdout = "stdout"
        val expectedReturnValue = "123e4567-e89b-12d3-a456-426614174000"

        val scriptResult = executeScript(
            """
                UUID.fromString("$expectedReturnValue")
                    .toString()
                    .also { print("$expectedStdout") }
            """.trimIndent()
        )

        assertIs<KotlinScriptResult.Success>(scriptResult)
        assertEquals(expectedReturnValue, scriptResult.returnValue)
        assertEquals(expectedStdout, scriptResult.stdout)
    }
}

