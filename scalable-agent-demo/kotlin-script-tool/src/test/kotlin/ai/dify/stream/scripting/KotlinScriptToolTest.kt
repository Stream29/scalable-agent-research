package ai.dify.stream.scripting

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class KotlinScriptToolTest {
    class GreetingModule : ScriptContext {
        override val defaultImports: List<String> = listOf(
            "kotlin.math.PI",
            "kotlin.math.PI",
        )

        override val systemPromptInjection: String = """
            ### Greeting API
            - `greet(name: String): String`: Return a greeting string for the provided name.
        """.trimIndent()

        fun greet(name: String): String = "hello, $name"
    }

    class TestScriptContext(
        val greetingModule: GreetingModule = GreetingModule(),
    ) : ScriptContext {
        private val composition = CompositeScriptContext(
            receiverTypeName = "TestScriptContext",
            modulesInStableOrder = listOf(greetingModule),
        )

        override val defaultImports: List<String> = composition.defaultImports
        override val systemPromptInjection: String = composition.systemPromptInjection

        fun greet(name: String): String = greetingModule.greet(name)
    }

    @Test
    fun compositeScriptContextDeduplicatesImportsAndPreservesModuleOrder() {
        val context = CompositeScriptContext(
            receiverTypeName = "Receiver",
            modulesInStableOrder = listOf(
                object : ScriptContext {
                    override val defaultImports: List<String> = listOf("demo.A", "demo.B")
                    override val systemPromptInjection: String = "### Module A"
                },
                object : ScriptContext {
                    override val defaultImports: List<String> = listOf("demo.B", "demo.C")
                    override val systemPromptInjection: String = "### Module B"
                },
            ),
            roleConstraints = listOf("Do not call unavailable runtime hooks."),
        )

        assertEquals(listOf("demo.A", "demo.B", "demo.C"), context.defaultImports)
        assertTrue(context.systemPromptInjection.contains("implicit receiver = Receiver"))
        assertTrue(context.systemPromptInjection.indexOf("### Module A") < context.systemPromptInjection.indexOf("### Module B"))
        assertContains(context.systemPromptInjection, "Do not call unavailable runtime hooks.")
    }

    @Test
    fun buildScriptSystemPromptAppendsInjectionWhenPresent() {
        val context = TestScriptContext()

        val prompt = buildScriptSystemPrompt(
            baseSystemPrompt = "You are a coding agent.",
            scriptContext = context,
        )

        assertContains(prompt, "You are a coding agent.")
        assertContains(prompt, "### Greeting API")
        assertContains(prompt, "implicit receiver = TestScriptContext")
    }

    @Test
    fun evalReturnsSuccessAndCapturesStdoutAndDefaultImports() {
        val context = TestScriptContext()

        val result = context.eval(
            """
            println("script-stdout")
            "${'$'}{greet("demo")} / ${'$'}{PI > 3}"
            """.trimIndent()
        )

        val success = assertIs<KotlinScriptResult.Success>(result)
        assertEquals("hello, demo / true", success.returnValue)
        assertContains(success.stdout, "script-stdout")
    }

    @Test
    fun evalReturnsFailureForScriptError() {
        val context = TestScriptContext()

        val result = context.eval("""error("boom")""")

        val failure = assertIs<KotlinScriptResult.Failure>(result)
        assertContains(failure.message, "boom")
    }

    @Test
    fun executeReturnsSuccessForValidScript() = runTest {
        withContext(Dispatchers.Default.limitedParallelism(1)) {
            val tool = KotlinScriptTool(scriptContext = TestScriptContext())

            val result = tool.execute(
                KotlinScriptParams(
                    script = """greet("world")""",
                    timeoutSeconds = 5,
                )
            )

            val success = assertIs<KotlinScriptResult.Success>(result)
            assertEquals("hello, world", success.returnValue)
        }
    }

    @Test
    fun executeTimesOutForLongRunningScript() = runTest {
        withContext(Dispatchers.Default.limitedParallelism(1)) {
            val tool = KotlinScriptTool(scriptContext = TestScriptContext())

            assertFailsWith<TimeoutCancellationException> {
                tool.execute(
                    KotlinScriptParams(
                        script = """
                            Thread.sleep(5000L)
                            "done"
                        """.trimIndent(),
                        timeoutSeconds = 1,
                    )
                )
            }
        }
    }
}
