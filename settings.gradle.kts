rootProject.name = "underkow"

pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.3.61"
        id("org.jetbrains.gradle.plugin.idea-ext") version "0.7"
        id("org.jlleitschuh.gradle.ktlint") version "9.1.1"
        id("com.github.ben-manes.versions") version "0.27.0"
        id("org.jetbrains.dokka") version "0.10.0"
        id("signing")
        id("maven")
        id("java-library")
    }
}
