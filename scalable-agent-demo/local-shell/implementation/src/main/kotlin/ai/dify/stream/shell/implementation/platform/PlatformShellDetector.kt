package ai.dify.stream.shell.implementation.platform

public fun detectPlatformShell(
    operatingSystemFamily: OperatingSystemFamily = OperatingSystemFamily.current,
): PlatformShell = when (operatingSystemFamily) {
    OperatingSystemFamily.WINDOWS -> detectWindowsShell()
    OperatingSystemFamily.MAC_OS,
    OperatingSystemFamily.LINUX,
    OperatingSystemFamily.POSIX_OTHER,
    OperatingSystemFamily.UNKNOWN,
    -> detectPosixShell(operatingSystemFamily)
}
