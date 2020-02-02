import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "io.github.xstefanox"
version = findProperty("release") ?: "SNAPSHOT"

object Version {
    const val kotlin = "1.3.61"
    const val kotlintest = "3.4.2"
    const val undertow = "2.0.29.Final"
    const val slf4j = "1.7.25"
    const val restassured = "4.2.0"
    const val mockk = "1.9.3"
    const val okhttp = "4.3.1"
    const val coroutines = "1.3.3"
    const val klaxon = "5.2"
    const val junit = "5.6.0"
    const val eventsource = "1.10.1"
    const val failsafe = "2.3.2"
}

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.gradle.plugin.idea-ext")
    id("org.jlleitschuh.gradle.ktlint")
    id("com.github.ben-manes.versions")
    id("org.jetbrains.dokka")
    id("signing")
    id("maven-publish")
    id("java-library")
}

repositories {
    jcenter()
}

sourceSets {
    create("example") {
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].output
    }
}

val exampleImplementation: Configuration by configurations.named("exampleImplementation")

dependencies {
    implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom:${Version.kotlin}"))
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8", version = Version.kotlin)
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = Version.coroutines)
    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = Version.okhttp)
    api(group = "io.undertow", name = "undertow-core", version = Version.undertow)
    api(group = "org.slf4j", name = "slf4j-api", version = Version.slf4j)

    testImplementation(group = "io.kotlintest", name = "kotlintest-assertions", version = Version.kotlintest)
    testImplementation(group = "org.slf4j", name = "slf4j-simple", version = Version.slf4j)
    testImplementation(group = "io.rest-assured", name = "rest-assured", version = Version.restassured)
    testImplementation(group = "io.mockk", name = "mockk", version = Version.mockk)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = Version.junit)
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = Version.junit)
    testImplementation(group = "com.launchdarkly", name = "okhttp-eventsource", version = Version.eventsource)
    testImplementation(group = "net.jodah", name = "failsafe", version = Version.failsafe)

    exampleImplementation(group = "com.beust", name = "klaxon", version = Version.klaxon)
}

configurations {
    all {
        resolutionStrategy {
            failOnVersionConflict()
        }
    }
}

tasks.withType<KotlinCompile> {

    sourceCompatibility = VERSION_1_8.toString()
    targetCompatibility = sourceCompatibility

    kotlinOptions {
        jvmTarget = sourceCompatibility
        apiVersion = "1.3"
        languageVersion = "1.3"
        allWarningsAsErrors = true
    }
}

ktlint {
    coloredOutput.set(false)
    version.set("0.36.0")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        events = setOf(PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR)
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }

    systemProperty("org.jboss.logging.provider", "slf4j")
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

tasks.dokka {
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
}

val sourcesJar = tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadocJar = tasks.create<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.dokka)
}

artifacts {
    archives(sourcesJar)
    archives(javadocJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            from(components.named("kotlin").get())
            artifact(sourcesJar)
            artifact(javadocJar)
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Underkow")
                packaging = "jar"
                description.set("A Kotlin DSL to configure Undertow routing")
                url.set("https://github.com/xstefanox/underkow")
                scm {
                    connection.set("scm:https://github.com/xstefanox/underkow.git")
                    developerConnection.set("scm:https://github.com/xstefanox/underkow.git")
                    url.set("https://github.com/xstefanox/underkow")
                }
                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("http://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("xstefanox")
                        name.set("Stefano Varesi")
                        email.set("stefano.varesi@gmail.com")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            credentials {
                username = findProperty("nexusUsername").toString()
                password = findProperty("nexusPassword").toString()
            }
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenKotlin"])
}
