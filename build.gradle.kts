import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("se.patrikerdes.use-latest-versions") version "0.2.14"
    id("net.researchgate.release") version "2.8.1"
    `maven-publish`
    `java-library`
}

group = "no.nav.pensjonopptjening"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.15.2")
    implementation("com.nimbusds", "nimbus-jose-jwt", "9.1.2")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.7.0")
    testImplementation("com.github.tomakehurst", "wiremock", "2.27.2")
}

release {
    newVersionCommitMessage = "[Release Plugin] - next version commit: "
    tagTemplate = "release-\${version}"
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

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    test {
        useJUnitPlatform()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
