package com.lowbudgetlcs

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import com.lowbudgetlcs.data.Result
import com.lowbudgetlcs.data.ResultsAdapter

class MatchHandler(result: Result) {

    private val logger = LoggerFactory.getLogger("com.lowbudgetlcs.MatchHandler")
    private val db = Db.db
    private val results: Results = ResultsAdapter().toNonSerializable(result)

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

    private fun saveResult() {
        logger.info("Saving result...")
        val resultQueries = db.resultQueries
        resultQueries.insertResult(results)
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