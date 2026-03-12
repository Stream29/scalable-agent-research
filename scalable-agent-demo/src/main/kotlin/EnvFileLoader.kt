package ai.dify.stream

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

data class LoadedEnv(
    val path: Path,
    val values: Map<String, String>,
)

object EnvFileLoader {
    fun load(startDir: Path = Paths.get("").toAbsolutePath()): LoadedEnv? {
        val envPath = findDotEnv(startDir) ?: return null
        val content = Files.readString(envPath)
        return LoadedEnv(envPath, parse(content))
    }

    internal fun findDotEnv(startDir: Path): Path? {
        var current: Path? = startDir.toAbsolutePath().normalize()

        while (current != null) {
            val candidate = current.resolve(".env")
            if (Files.isRegularFile(candidate)) {
                return candidate
            }
            current = current.parent
        }

        return null
    }

    internal fun parse(content: String): Map<String, String> {
        val values = linkedMapOf<String, String>()

        content.lineSequence().forEach { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty() || line.startsWith("#")) {
                return@forEach
            }

            val normalized = if (line.startsWith("export ")) line.removePrefix("export ").trim() else line
            val separatorIndex = normalized.indexOf('=')
            if (separatorIndex <= 0) {
                return@forEach
            }

            val key = normalized.substring(0, separatorIndex).trim()
            if (key.isEmpty()) {
                return@forEach
            }

            val rawValue = normalized.substring(separatorIndex + 1).trim()
            values[key] = parseValue(rawValue)
        }

        return values
    }

    private fun parseValue(rawValue: String): String {
        if (rawValue.isEmpty()) {
            return ""
        }

        return when {
            rawValue.startsWith('"') && rawValue.endsWith('"') && rawValue.length >= 2 -> {
                unescapeDoubleQuoted(rawValue.substring(1, rawValue.length - 1))
            }

            rawValue.startsWith('\'') && rawValue.endsWith('\'') && rawValue.length >= 2 -> {
                rawValue.substring(1, rawValue.length - 1)
            }

            else -> rawValue.substringBefore(" #").trim()
        }
    }

    private fun unescapeDoubleQuoted(value: String): String {
        val builder = StringBuilder()
        var index = 0

        while (index < value.length) {
            val current = value[index]
            if (current == '\\' && index + 1 < value.length) {
                val escaped = value[index + 1]
                when (escaped) {
                    'n' -> builder.append('\n')
                    'r' -> builder.append('\r')
                    't' -> builder.append('\t')
                    '\\' -> builder.append('\\')
                    '"' -> builder.append('"')
                    else -> {
                        builder.append(escaped)
                    }
                }
                index += 2
            } else {
                builder.append(current)
                index += 1
            }
        }

        return builder.toString()
    }
}
