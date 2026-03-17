plugins {
    id("kotlin-jvm")
}

dependencies {
    api(project(":tool-kotlin-script-contract"))
    testImplementation(libs.bundles.testing)
}
