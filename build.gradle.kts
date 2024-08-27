plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    id("com.github.ben-manes.versions") version "0.51.0"
}

repositories {
    mavenCentral()
}

tasks.withType<Jar> {
    enabled = false //dont create jar for root
}