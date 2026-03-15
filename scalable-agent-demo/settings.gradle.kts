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

include(":app")
include(":kotlin-scripting:contract")
include(":kotlin-scripting:api")
include(":kotlin-scripting:feature")
include(":kotlin-scripting:implementation")
include(":lite-koog")

rootProject.name = "scalable-agent-demo"
