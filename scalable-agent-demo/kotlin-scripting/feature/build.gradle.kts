plugins {
    id("kotlin-jvm")
}

dependencies {
    api(projects.kotlinScripting.contract)
    testImplementation(libs.bundles.testing)
}
