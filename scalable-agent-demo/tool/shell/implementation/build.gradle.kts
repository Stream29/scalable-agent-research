plugins {
    id("kotlin-jvm")
}

dependencies {
    implementation(libs.kotlinxCoroutinesCore)
    implementation(libs.pty4j)
    implementation(project(":tool-shell-contract"))
}
