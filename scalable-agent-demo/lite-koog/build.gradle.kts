plugins {
    id("kotlin-jvm")
}

dependencies {
    implementation(libs.koogAgents)

    testImplementation(libs.bundles.testing)
}
