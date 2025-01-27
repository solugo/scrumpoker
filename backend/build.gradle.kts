import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.1.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
    id("org.springframework.boot") version "3.4.2"
}

group = "de.solugo.scrumpoker"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlinx:kotlinx-serialization-bom:1.8.0"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.1"))
    implementation(platform("io.ktor:ktor-bom:3.0.3"))
    implementation(platform("io.micrometer:micrometer-bom:1.14.3"))
    implementation(platform("org.junit:junit-bom:5.11.4"))
    implementation(platform("io.kotest:kotest-bom:5.9.1"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("io.ktor:ktor-server-cio")
    implementation("io.ktor:ktor-server-websockets")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-metrics-micrometer")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("ch.qos.logback:logback-classic:1.5.16")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotest:kotest-assertions-core-jvm")
}


kotlin {
    jvmToolchain(21)
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
