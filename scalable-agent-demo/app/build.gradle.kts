plugins {
    application
    id("kotlin-jvm")
}

dependencies {
    implementation(projects.liteKoog)
    implementation(libs.koogAgents)
    implementation(projects.localShell.api)
    implementation(projects.kotlinScripting.api)
    runtimeOnly(libs.slf4jSimple)

    testImplementation(libs.bundles.testing)
}

application {
    mainClass = "ai.dify.stream.MainKt"
}
