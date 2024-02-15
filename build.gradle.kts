import org.jetbrains.kotlin.utils.doNothing

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
}

repositories {
    mavenCentral()
}

tasks.withType<Jar> {
    enabled = false //dont create jar for root
}