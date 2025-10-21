import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.kotlin.dsl.withType

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    id("com.github.ben-manes.versions") version "0.52.0"
}

repositories {
    mavenCentral()
}

tasks.withType<Jar> {
    enabled = false //dont create jar for root
}

tasks.withType<DependencyUpdatesTask>().configureEach {
    rejectVersionIf {
        isNonStableVersion(candidate.version)
    }
}

fun isNonStableVersion(version: String): Boolean {
    return listOf("BETA","RC","-M",".CR").any { version.uppercase().contains(it) }
}