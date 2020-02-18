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

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
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
exampleImplementation.extendsFrom(configurations.implementation.get())

dependencies {

    val coroutinesVersion: String by project
    val eventsourceVersion: String by project
    val failsafeVersion: String by project
    val junitVersion: String by project
    val kotlinVersion: String by project
    val kotlintestVersion: String by project
    val kotlinxSerializationVersion: String by project
    val mockkVersion: String by project
    val okhttpVersion: String by project
    val restassuredVersion: String by project
    val slf4jVersion: String by project
    val undertowVersion: String by project

    implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8", version = kotlinVersion)
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = coroutinesVersion)
    api(group = "io.undertow", name = "undertow-core", version = undertowVersion)
    api(group = "org.slf4j", name = "slf4j-api", version = slf4jVersion)

    testImplementation(group = "io.kotlintest", name = "kotlintest-assertions", version = kotlintestVersion)
    testImplementation(group = "org.slf4j", name = "slf4j-simple", version = slf4jVersion)
    testImplementation(group = "io.rest-assured", name = "rest-assured", version = restassuredVersion)
    testImplementation(group = "io.mockk", name = "mockk", version = mockkVersion)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junitVersion)
    testImplementation(group = "com.launchdarkly", name = "okhttp-eventsource", version = eventsourceVersion)
    testImplementation(group = "com.squareup.okhttp3", name = "okhttp", version = okhttpVersion)
    testImplementation(group = "net.jodah", name = "failsafe", version = failsafeVersion)
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junitVersion)

    exampleImplementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-runtime", version = kotlinxSerializationVersion)
    exampleImplementation(group = "org.slf4j", name = "slf4j-simple", version = slf4jVersion)

    constraints {
        api(group = "org.slf4j", name = "slf4j-api") {
            version {
                strictly(slf4jVersion)
            }
        }
        api(group = "com.squareup.okhttp3", name = "okhttp") {
            version {
                strictly(okhttpVersion)
            }
        }
        api(group = "org.jetbrains.kotlin", name = "kotlin-stdlib") {
            version {
                strictly(kotlinVersion)
            }
        }
        api(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-common") {
            version {
                strictly(kotlinVersion)
            }
        }
    }
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
        freeCompilerArgs = freeCompilerArgs + listOf("-Xuse-experimental=kotlin.Experimental")
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
