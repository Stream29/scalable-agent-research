package ai.dify.stream.scripting

import ai.dify.stream.scripting.contract.KotlinScriptResult
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.jvm.util.renderError

internal fun WithStdout<ResultWithDiagnostics<EvaluationResult>>.toEvalResult(): KotlinScriptResult {
    val (evaluationResult, stdout) = this
    return when (evaluationResult) {
        is ResultWithDiagnostics.Success<EvaluationResult> -> {
            when (val returnValue = evaluationResult.value.returnValue) {
                is ResultValue.Value -> KotlinScriptResult.Success(returnValue.value.toString(), stdout)
                is ResultValue.Error -> KotlinScriptResult.Failure(returnValue.renderError(), stdout)
                is ResultValue.Unit -> KotlinScriptResult.Success("kotlin.Unit", stdout)
                is ResultValue.NotEvaluated -> KotlinScriptResult.Failure("Script did not evaluate", stdout)
            }
        }

        is ResultWithDiagnostics.Failure -> {
            KotlinScriptResult.Failure(
                message = evaluationResult.reports
                    .filter { it.severity != ScriptDiagnostic.Severity.DEBUG }
                    .joinToString("\n")
                    .ifBlank { "Script evaluation failed" },
                stdout = stdout,
            )
        }
    }
}