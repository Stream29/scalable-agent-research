package ai.dify.stream

import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.params.LLMParams

val anthropicApiKey = System.getenv("ANTHROPIC_API_KEY")
    ?: error("ANTHROPIC_API_KEY environment variable is not set")

val agentModel = AnthropicModels.Haiku_4_5
val agentLlmParams = LLMParams()
