plugins {
    id("kotlin-jvm")
}

dependencies {
    implementation(libs.kotlinxCoroutinesCore)
    implementation(libs.bundles.kotlinScripting)
    implementation(project(":tool-kotlin-script-contract"))
}
