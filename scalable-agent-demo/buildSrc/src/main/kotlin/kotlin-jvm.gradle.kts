plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    jvmToolchain(24)
    explicitApi()
    compilerOptions {
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
        allWarningsAsErrors.set(true)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
