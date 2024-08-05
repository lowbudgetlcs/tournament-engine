package com.lowbudgetlcs

import com.lowbudgetlcs.data.Result
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class MatchHandler(private val result: Result) {

    private val logger = LoggerFactory.getLogger("com.lowbudgetlcs.MatchHandler")
    private val db = Db.db

    fun recieveGameCallback() = runBlocking {
        // Write result to database
        launch {
            saveResult()
        }
        // Update 1_init_tables.sqm in database
        launch {
            updateGame()
        }
        // Update series
        launch {
            updateSeries()
        }
        // Update standings
        launch {
            updateStandings()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun saveResult() {
        logger.info("Saving result...")
        val resultQueries = db.resultQueries
        val meta = Json.encodeToString(result.metaData)
        resultQueries.insertResult(
            result.startTime,
            result.shortCode,
            meta,
            result.gameId,
            result.gameName,
            result.gameType,
            result.gameMap,
            result.gameMode,
            result.region
        )
        logger.info("Result saved!")
    }

    private fun updateGame() {
        logger.info("Updating game...")
        val gameQueries = db.gameQueries
        logger.info(gameQueries.selectAll().executeAsList().toString())
    }

    private fun updateSeries() {
        logger.info("Updating series...")
//        val metaData = Json.decodeFromString<ResultMetaData>(result.meta_data)
//        logger.debug("Metadata: $metaData")
    }

    private fun updateStandings() {
        // Fetch series row
        // Check if a team has won (Bo3, Bo5)
        // If a team won, update standings
        logger.info("Updating standings...")

    }
}