plugins {
    id("kotlin-jvm")
}

dependencies {
    api(libs.koogAgents)
    implementation(projects.kotlinScripting.contract)
    implementation(projects.kotlinScripting.implementation)
    testImplementation(libs.bundles.testing)
}
