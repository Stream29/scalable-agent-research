plugins {
    id("ai.kotlin.jvm")
}

group = "ai.dify.stream"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("ai.koog:agents-core")
    implementation("ai.koog:agents-features-acp")
    implementation("ai.koog:agents-features-memory")
    testImplementation(kotlin("test"))
}