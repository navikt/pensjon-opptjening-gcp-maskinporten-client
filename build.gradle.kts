import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    kotlin("jvm") version "1.9.22"
}

group = "no.nav.pensjonopptjening"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
}


tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }
    test {
        useJUnitPlatform()
        testLogging {
            events(
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
            )
        }
    }
}
