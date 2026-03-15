plugins {
    id("kotlin-jvm")
}

dependencies {
    implementation(libs.kotlinxCoroutinesCore)
    implementation(libs.bundles.kotlinScripting)
    implementation(projects.kotlinScripting.contract)
}
