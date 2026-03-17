package ai.dify.stream.tool.kotlinscript.contract

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class KotlinScriptParams(
    val script: String,
    val timeoutSeconds: Int = 30,
)

@Serializable
public sealed interface KotlinScriptResult {
    @Serializable
    @SerialName("Success")
    public data class Success(
        val returnValue: String,
        val stdout: String,
    ) : KotlinScriptResult

    @Serializable
    @SerialName("Failure")
    public data class Failure(
        val message: String,
        val stdout: String,
    ) : KotlinScriptResult
}
