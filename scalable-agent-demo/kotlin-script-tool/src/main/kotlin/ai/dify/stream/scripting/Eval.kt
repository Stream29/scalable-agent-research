package ai.dify.stream.scripting

import ai.dify.stream.scripting.KotlinScriptResult.Failure
import ai.dify.stream.scripting.KotlinScriptResult.Success
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.kotlin.mainKts.CompilerOptions
import org.jetbrains.kotlin.mainKts.Import
import org.jetbrains.kotlin.mainKts.MainKtsConfigurator
import org.jetbrains.kotlin.mainKts.MainKtsScript
import org.jetbrains.kotlin.mainKts.MainKtsScriptDefinition
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.concurrent.withLock
import kotlin.reflect.typeOf
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.api.refineConfiguration
import kotlin.script.experimental.api.scriptsInstancesSharing
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.Repository
import kotlin.script.experimental.host.StringScriptSource
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.util.renderError

public suspend inline fun <reified T : ScriptContext> T.evalInThreadCancellable(script: String): KotlinScriptResult {
    return suspendCancellableCoroutine { cont ->
        val thread = Thread(
            {
                cont.resumeWith(runCatching { eval(script) })
            },
            "kotlin-script-tool-eval"
        )
        thread.isDaemon = true
        cont.invokeOnCancellation {
            thread.interrupt()
        }
        thread.start()
    }
}

public inline fun <reified T : ScriptContext> T.eval(script: String): KotlinScriptResult {
    return scriptEvaluationMutex.withLock {
        captureStdout {
            host.evalWithTemplate<MainKtsScript>(
                script = script.toScriptSource(),
                compilation = {
                    defaultImports.invoke(
                        T::class,
                        DependsOn::class,
                        Repository::class,
                        Import::class,
                        CompilerOptions::class,
                    )
                    defaultImports.append("java.io.*")
                    defaultImports.append(this@eval.defaultImports)
                    jvm {
                        dependenciesFromClassContext(
                            MainKtsScriptDefinition::class,
                            "kotlin-main-kts",
                            "kotlin-stdlib",
                            "kotlin-reflect",
                            wholeClasspath = true,
                        )
                    }
                    refineConfiguration {
                        onAnnotations(
                            DependsOn::class,
                            Repository::class,
                            Import::class,
                            CompilerOptions::class,
                            handler = MainKtsConfigurator()
                        )
                    }
                    implicitReceivers(typeOf<T>())
                },
                evaluation = {
                    constructorArgs(emptyArray<String>())
                    implicitReceivers(this@eval)
                    scriptsInstancesSharing(false)
                },
            )
        }.toEvalResult()
    }
}

public fun buildScriptSystemPrompt(baseSystemPrompt: String, scriptContext: ScriptContext): String {
    val normalizedInjection = scriptContext.systemPromptInjection.trim()
    if (normalizedInjection.isBlank()) {
        return baseSystemPrompt
    }
    return """
        $baseSystemPrompt

        $normalizedInjection
    """.trimIndent()
}

@PublishedApi
internal inline fun <T> captureStdout(block: () -> T): WithStdout<T> {
    val originalOut = System.out
    val outputStream = ByteArrayOutputStream()
    val printStream = PrintStream(outputStream, true, Charsets.UTF_8)
    try {
        System.setOut(printStream)
        val result = block()
        printStream.flush()
        val stdout = outputStream.toString(Charsets.UTF_8)
        return WithStdout(result, stdout)
    } finally {
        System.setOut(originalOut)
        printStream.close()
    }
}

@PublishedApi
internal data class WithStdout<T>(val value: T, val stdout: String)

@PublishedApi
internal fun WithStdout<ResultWithDiagnostics<EvaluationResult>>.toEvalResult(): KotlinScriptResult {
    val (evaluationResult, stdout) = this
    return when (evaluationResult) {
        is ResultWithDiagnostics.Success<EvaluationResult> -> {
            when (val returnValue = evaluationResult.value.returnValue) {
                is ResultValue.Value -> Success(returnValue.value.toString(), stdout)
                is ResultValue.Error -> Failure(returnValue.renderError(), stdout)
                is ResultValue.Unit -> Success("kotlin.Unit", stdout)
                is ResultValue.NotEvaluated -> Failure("Script did not evaluate", stdout)
            }
        }

        is ResultWithDiagnostics.Failure -> {
            Failure(
                message = evaluationResult.reports
                    .filter { it.severity != ScriptDiagnostic.Severity.DEBUG }
                    .joinToString("\n")
                    .ifBlank { "Script evaluation failed" },
                stdout = stdout,
            )
        }
    }
}
