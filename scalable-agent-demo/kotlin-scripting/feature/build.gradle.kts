plugins {
    id("kotlin-jvm")
}

dependencies {
    api(projects.kotlinScripting.contract)
    api(projects.kotlinScripting.api)

    testImplementation(libs.bundles.testing)
}
