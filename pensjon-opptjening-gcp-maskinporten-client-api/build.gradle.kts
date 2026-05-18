import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jacksonVersion = "2.21.3"
val logbackEncoderVersion = "9.0" // kan ikke oppgraderes pga spring/logback-classic
val azureAdClient = "0.0.7"
val wiremockVersion = "3.13.2"


val mockitoKotlinVersion = "6.3.0"
val navTokenSupportVersion = "6.0.7"
val hibernateValidatorVersion = "9.1.0.Final"

val junitJupiterVersion = "5.11.0"

val jettyVersion = "12.1.9" // trengs pga wiremock

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    id("org.jetbrains.kotlin.plugin.spring") version libs.versions.kotlin.get()
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.ben-manes.versions") version libs.versions.benManesVersions.get()
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
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("no.nav.security:token-validation-spring:$navTokenSupportVersion")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("no.nav.security:token-validation-spring-test:$navTokenSupportVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.wiremock:wiremock-jetty12:${wiremockVersion}")
    testImplementation("no.nav.security:token-validation-spring-test:${navTokenSupportVersion}")

// trengs fordi wiremock henger etter på jetty-versjon i forhold til spring 4
    testImplementation("org.eclipse.jetty:jetty-bom:${jettyVersion}")
    testImplementation("org.eclipse.jetty.ee10:jetty-ee10-bom:${jettyVersion}")
    testImplementation("org.eclipse.jetty.ee10:jetty-ee10-servlet:${jettyVersion}")
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

tasks.withType<DependencyUpdatesTask>().configureEach {
    rejectVersionIf {
        isNonStableVersion(candidate.version)
    }
}

fun isNonStableVersion(version: String): Boolean {
    return listOf("BETA", "RC", "-M", "-rc-", "Alpha").any { version.uppercase().contains(it) }
}

