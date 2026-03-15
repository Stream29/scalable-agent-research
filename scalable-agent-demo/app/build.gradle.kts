plugins {
    application
    id("kotlin-jvm")
}

dependencies {
    implementation(projects.liteKoog)
    implementation(libs.koogAgents)
    runtimeOnly(libs.slf4jSimple)

    testImplementation(libs.bundles.testing)
}

application {
    mainClass = "ai.dify.stream.MainKt"
}
