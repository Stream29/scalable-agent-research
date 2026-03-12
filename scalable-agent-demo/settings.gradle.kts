pluginManagement {
    includeBuild("../reference-repository/koog/convention-plugin-ai")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://packages.jetbrains.team/maven/p/jcs/maven")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../reference-repository/koog/gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "scalable-agent-demo"

includeBuild("../reference-repository/koog")