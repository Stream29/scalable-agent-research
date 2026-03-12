package ai.dify.stream

import ai.koog.agents.core.agent.AIAgent

fun main() {
    println("Checking AIAgent class availability...")
    try {
        val agentClass = AIAgent::class.java
        println("AIAgent class is available: ${agentClass.name}")
    } catch (e: NoClassDefFoundError) {
        println("AIAgent class NOT found!")
    } catch (e: Exception) {
        println("An error occurred: ${e.message}")
    }
}