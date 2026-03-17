package ai.dify.stream.tool.shell

internal fun echoCommand(text: String): String = if (isWindowsForTests()) {
    "Write-Output '$text'"
} else {
    "printf '%s\\n' '$text'"
}

internal fun currentDirectoryCommand(): String = if (isWindowsForTests()) {
    "Write-Output (Get-Location).Path"
} else {
    "pwd"
}

internal fun ansiTextCommand(text: String): String = if (isWindowsForTests()) {
    "Write-Output \"`e[31m$text`e[0m\""
} else {
    "printf '\\033[31m%s\\033[0m\\n' '$text'"
}

internal fun sleepCommand(seconds: Int): String = if (isWindowsForTests()) {
    "Start-Sleep -Seconds $seconds"
} else {
    "sleep $seconds"
}

internal fun echoThenSleepCommand(text: String, seconds: Int): String = if (isWindowsForTests()) {
    "Write-Output '$text'; Start-Sleep -Seconds $seconds"
} else {
    "printf '%s\\n' '$text'; sleep $seconds"
}
