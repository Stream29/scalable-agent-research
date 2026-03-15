plugins {
    id("kotlin-jvm")
}

dependencies {
    implementation(libs.koogAgents)
    implementation(libs.kotlinxCoroutinesCore)
    implementation(libs.bundles.kotlinScripting)

    testImplementation(libs.bundles.testing)
}
