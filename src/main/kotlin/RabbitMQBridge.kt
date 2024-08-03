package com.lowbudgetlcs

import com.rabbitmq.client.*
import kotlinx.serialization.json.Json
import com.lowbudgetlcs.MatchResult
import kotlinx.coroutines.GlobalScope
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import org.slf4j.LoggerFactory

@OptIn(ExperimentalSerializationApi::class)
object RabbitMQBridge {
    private val logger = LoggerFactory.getLogger("com.lowbudgetlcs.RabbitMQBridge")
    private const val EXCHANGE_NAME = "RIOT_CALLBACKS"
    private val factory = ConnectionFactory().apply {
        host = System.getenv("MESSAGEQ_HOST") ?: "rabbitmq"
        isAutomaticRecoveryEnabled = true
        networkRecoveryInterval = 15000
    }

    private val connection: Connection by lazy {
        factory.newConnection().also {
            logger.debug("Created new messageq connection.")
        }
    }

    private val channel: Channel by lazy {
        connection.createChannel().apply {
            exchangeDeclare(EXCHANGE_NAME, "topic")
        }.also {
            logger.debug("Created new messageq channel.")
        }
    }

    fun listen() {
        channel.run {
            val queue = queueDeclare().queue
            queueBind(queue, EXCHANGE_NAME, "callback")
            val callback = DeliverCallback { _, delivery: Delivery ->
                val message = String(delivery.body, charset("UTF-8"))
                val result = Json.decodeFromString<MatchResult>(message)
                MatchHandler(result).recieveGameCallback()
                println(" [x] Received '" + delivery.envelope.routingKey + "':'" + message + "'")
            }
            basicConsume(queue, true, callback) { _ -> }
        }
    }
}