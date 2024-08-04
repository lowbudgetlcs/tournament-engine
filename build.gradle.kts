group = "com.lowbudgetlcs"
version = "0.0.1"

plugins {
    kotlin("jvm") version "2.0.0"
    id("application")
    id("io.ktor.plugin") version "2.3.12"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    id("app.cash.sqldelight") version "2.0.2"
}

application {
    mainClass.set("$group.AppKt")
}

repositories {
    google()
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("$group")
            dialect("app.cash.sqldelight:postgresql-dialect:2.0.2")
        }
    }
}

dependencies {
    implementation("com.rabbitmq:amqp-client:5.21.0")

    implementation("app.cash.sqldelight:jdbc-driver:2.0.2")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.postgresql:postgresql:42.7.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("ch.qos.logback:logback-classic:1.4.14")
}