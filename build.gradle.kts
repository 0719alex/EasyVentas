// build.gradle.kts (root)

plugins {
    // vacío en el root
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
