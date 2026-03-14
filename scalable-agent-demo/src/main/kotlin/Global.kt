package ai.dify.stream

import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels

val anthropicApiKey = System.getenv("ANTHROPIC_API_KEY")
    ?: error("ANTHROPIC_API_KEY environment variable is not set")

val agentConfig = AIAgentConfig(
    prompt = Prompt.Empty,
    model = AnthropicModels.Haiku_4_5,
    maxAgentIterations = 500
)