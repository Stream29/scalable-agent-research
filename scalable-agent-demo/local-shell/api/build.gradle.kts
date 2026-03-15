plugins {
    id("kotlin-jvm")
}

dependencies {
    api(libs.koogAgents)
    api(projects.localShell.contract)
    implementation(projects.localShell.implementation)
    testImplementation(libs.bundles.testing)
}
