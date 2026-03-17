package ai.dify.stream.tool.shell.implementation

internal fun Process.destroyProcessTree() {
    runCatching {
        descendants().forEach { descendant -> descendant.destroyProcessTree() }
    }
    destroyForcibly()
}

private fun ProcessHandle.destroyProcessTree() {
    runCatching {
        descendants().forEach { descendant -> descendant.destroyProcessTree() }
    }
    destroyForcibly()
}
