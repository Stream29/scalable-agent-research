plugins {
    application
    id("kotlin-jvm")
}

dependencies {
    implementation(project(":lite-koog"))
    implementation(libs.koogAgents)
    implementation(project(":tool-shell-api"))
    implementation(project(":tool-kotlin-script-api"))
    runtimeOnly(libs.slf4jSimple)

    testImplementation(libs.bundles.testing)
}

application {
    mainClass = "ai.dify.stream.MainKt"
}
