package com.lowbudgetlcs

import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

private val logger = LoggerFactory.getLogger("com.lowbudgetlcs.MainKT")

fun main() {
    logger.info("Starting tournament engine...")
    when (RiotAPIBridge.healthCheck()) {
        0 -> logger.info("Riot connection health!")
        else -> exitProcess(1)
    }
    // If you wanted to do more than one thing in the main function, you could
    // launch RabbitMQBridge.listen() as a coroutine with launch {}. Coroutines
    // are lightweight Threads, and very useful. Specifically with inserting player
    // data- those tasks dont depend on each other, so using launch {} to insert all 10
    // participants at the same time would be a huge performance boost.
    RabbitMQBridge.listen()
}