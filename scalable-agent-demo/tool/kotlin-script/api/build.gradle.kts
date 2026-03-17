plugins {
    id("kotlin-jvm")
}

dependencies {
    api(libs.koogAgents)
    implementation(project(":tool-kotlin-script-contract"))
    implementation(project(":tool-kotlin-script-implementation"))
    implementation(project(":tool-kotlin-script-feature"))
    testImplementation(libs.bundles.testing)
}
