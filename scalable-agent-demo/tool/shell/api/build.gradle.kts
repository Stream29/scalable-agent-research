plugins {
    id("kotlin-jvm")
}

dependencies {
    api(libs.koogAgents)
    api(project(":tool-shell-contract"))
    implementation(project(":tool-shell-implementation"))
    testImplementation(libs.bundles.testing)
}
