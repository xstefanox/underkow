rootProject.name = "underkow"

pluginManagement {

    val ideaPluginVersion: String by settings
    val kotlinVersion: String by settings
    val koverVersion: String by settings
    val ktlintPluginVersion: String by settings
    val sonarqubeVersion: String by settings
    val versionsPluginVersion: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("org.jetbrains.kotlinx.kover") version koverVersion
        id("org.jetbrains.gradle.plugin.idea-ext") version ideaPluginVersion
        id("org.jetbrains.dokka") version kotlinVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintPluginVersion
        id("com.github.ben-manes.versions") version versionsPluginVersion
        id("signing")
        id("maven")
        id("java-library")
        id("org.sonarqube") version sonarqubeVersion
    }
}
