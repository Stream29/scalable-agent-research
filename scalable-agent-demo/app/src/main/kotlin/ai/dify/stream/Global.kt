package ai.dify.stream

import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.params.LLMParams

internal val anthropicApiKey: String = System.getenv("ANTHROPIC_API_KEY")
    ?: error("ANTHROPIC_API_KEY environment variable is not set")

internal val agentModel: LLModel = AnthropicModels.Haiku_4_5
internal val agentLlmParams: LLMParams = LLMParams()
