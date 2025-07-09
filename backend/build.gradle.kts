import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
    id("org.jetbrains.kotlin.plugin.spring") version "2.2.0"
    id("org.springframework.boot") version "3.4.1"
}

group = "de.solugo.scrumpoker"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.5.3"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("ch.qos.logback:logback-classic:1.5.15")

    testImplementation(platform("io.kotest:kotest-bom:5.9.1"))
    testImplementation(platform("org.junit:junit-bom:5.13.3"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("io.kotest:kotest-assertions-core")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

springBoot {
    buildInfo()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<BootJar>{
    archiveFileName = "backend.jar"
}
