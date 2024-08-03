package com.lowbudgetlcs

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class MatchHandler(private val result: MatchResult) {

    private val logger = LoggerFactory.getLogger("com.lowbudgetlcs.MatchHandler")

    fun recieveGameCallback() = runBlocking {
        // Write result to database
        launch {
            saveResult()
        }
        // Update game in database
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
        logger.debug("Saving result for game id #${result.metaData.id}")
    }

    private fun updateGame() {
        logger.info("Updating game id #${result.metaData.id}")
    }

    private fun updateSeries() {
        logger.info("Updating series id #${result.metaData.series_id}")
    }

    private fun updateStandings() {
        logger.info("Updating standings...")
    }
}