package ai.dify.stream.shell.implementation

private val ansiControlSequenceRegex: Regex = Regex("""\u001B\[[0-?]*[ -/]*[@-~]""")
private val oscSequenceRegex: Regex = Regex("""\u001B\][^\u0007]*(\u0007|\u001B\\)""")

internal fun String.normalizeShellOutput(
    skipControlChars: Boolean,
): String = if (skipControlChars) {
    replace(oscSequenceRegex, "")
        .replace(ansiControlSequenceRegex, "")
} else {
    this
}
