plugins {
    kotlin("jvm") version "2.3.0"
}

group = "ai.dify.stream"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://packages.jetbrains.team/maven/p/jcs/maven")
}

dependencies {
    implementation("ai.koog:koog-agents:0.6.4")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}