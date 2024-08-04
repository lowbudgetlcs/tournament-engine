package com.lowbudgetlcs

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class MatchHandler(private val result: MatchResult) {

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

    private fun saveResult() {
        logger.info("Saving result...")
        val resultQueries = db.resultQueries
        logger.info(resultQueries.selectAll().executeAsList().toString())
    }

    private fun updateGame() {
        logger.info("Updating game...")
        val gameQueries = db.gameQueries
        logger.info(gameQueries.selectAll().executeAsList().toString())
    }

    private suspend fun updateSeries() {
        logger.info("Updating series id #${result.metaData.series_id}")
    }

    private suspend fun updateStandings() {
        logger.info("Updating standings...")
    }
}