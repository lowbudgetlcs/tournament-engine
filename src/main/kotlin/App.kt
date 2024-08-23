package com.lowbudgetlcs

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("com.lowbudgetlcs.MainKT")

fun main() {
    logger.info("Starting tournament engine...")
    RiotAPIBridge.healthCheck()
    RabbitMQBridge.listen()
}