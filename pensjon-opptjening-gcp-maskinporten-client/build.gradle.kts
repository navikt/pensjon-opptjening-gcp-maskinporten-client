import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val junitJupiterVersion = "5.11.0"

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    kotlin("plugin.serialization") version libs.versions.kotlin.get()
    id("se.patrikerdes.use-latest-versions") version "0.2.18"
    id("net.researchgate.release") version "3.1.0"
    id("com.github.ben-manes.versions") version "0.52.0"
    `maven-publish`
    `java-library`
}

group = "no.nav.pensjonopptjening"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.19.0")
    implementation("com.nimbusds", "nimbus-jose-jwt", "10.3")
    testImplementation("com.github.tomakehurst", "wiremock", "3.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitJupiterVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${junitJupiterVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

release {
    newVersionCommitMessage = "[Release Plugin] - next version commit: "
    tagTemplate = "release-\${version}"
    git {
        requireBranch = "master"
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/navikt/${rootProject.name}")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
    }
}
tasks.test {
    useJUnitPlatform()
    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
        )
    }
}

tasks.withType<DependencyUpdatesTask>().configureEach {
    rejectVersionIf {
        isNonStableVersion(candidate.version)
    }
}

fun isNonStableVersion(version: String): Boolean {
    return listOf("BETA", "RC", "-M", "-rc-", "Alpha").any { version.uppercase().contains(it) }
}
