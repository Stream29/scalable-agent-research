pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://packages.jetbrains.team/maven/p/jcs/maven")
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://packages.jetbrains.team/maven/p/jcs/maven")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

private class NestedIncludeScope(val segments: List<String>)

private fun NestedIncludeScope.include(name: String, block: NestedIncludeScope.() -> Unit) {
    includeBySegments(segments)
    NestedIncludeScope(segments = segments + name).block()
}

private fun NestedIncludeScope.include(vararg names: String) =
    names.forEach { includeBySegments(segments + it) }

private inline fun Settings.include(name: String, block: NestedIncludeScope.() -> Unit) {
    include(name)
    NestedIncludeScope(segments = listOf(name)).block()
}

private fun includeBySegments(segments: List<String>) {
    val projectPath = ":${segments.joinToString(separator = "-")}"
    include(projectPath)
    project(projectPath).projectDir = rootDir.resolve(segments.joinToString(File.separator))
}


include(":app")
include(":lite-koog")
include("tool") {
    include("kotlin-script") {
        include("contract", "api", "feature", "implementation")
    }

    include("shell") {
        include("contract", "api", "implementation")
    }
}

rootProject.name = "scalable-agent-demo"