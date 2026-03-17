package ai.dify.stream.tool.shell.implementation.platform

public enum class OperatingSystemFamily {
    WINDOWS,
    MAC_OS,
    LINUX,
    POSIX_OTHER,
    UNKNOWN,
    ;

    public companion object {
        public val current: OperatingSystemFamily = detectCurrent()

        private fun detectCurrent(): OperatingSystemFamily {
            val osName = System.getProperty("os.name").lowercase()
            return when {
                "win" in osName -> WINDOWS
                "mac" in osName -> MAC_OS
                "linux" in osName -> LINUX
                "freebsd" in osName -> POSIX_OTHER
                "openbsd" in osName -> POSIX_OTHER
                "netbsd" in osName -> POSIX_OTHER
                "solaris" in osName -> POSIX_OTHER
                "sunos" in osName -> POSIX_OTHER
                "aix" in osName -> POSIX_OTHER
                "bsd" in osName -> POSIX_OTHER
                else -> UNKNOWN
            }
        }
    }
}
