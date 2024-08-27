import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jacksonVersion = "2.17.1"
val logbackEncoderVersion = "7.4"
val azureAdClient = "0.0.7"
val wiremockVersion = "3.0.1"
val mockitoKotlinVersion = "5.4.0"
val navTokenSupportVersion = "5.0.2"
val hibernateValidatorVersion = "8.0.1.Final"

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    id("org.jetbrains.kotlin.plugin.spring") version libs.versions.kotlin.get()
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "no.nav.pensjonopptjening"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.github.com/navikt/maven-release") {
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation(project(":pensjon-opptjening-gcp-maskinporten-client"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("no.nav.security:token-validation-spring:$navTokenSupportVersion")
    implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("no.nav.security:token-validation-spring-test:$navTokenSupportVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
        )
    }
}
