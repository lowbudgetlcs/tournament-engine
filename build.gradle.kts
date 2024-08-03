plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.lowbudgetlcs"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}

dependencies {
    implementation("com.rabbitmq:amqp-client:5.21.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("ch.qos.logback:logback-classic:1.4.14")
}
