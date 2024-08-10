package com.lowbudgetlcs

import com.lowbudgetlcs.data.Result
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant
import org.slf4j.LoggerFactory

class MatchHandler(private val result: Result) {

    private val logger = LoggerFactory.getLogger("com.lowbudgetlcs.MatchHandler")
    private val db = Db.db
    private val seriesId = result.metaData.seriesId
    private val gameNum = result.metaData.gameNum
    private val series by lazy {
        val seriesQueries = db.seriesQueries
        seriesQueries.selectSeriesById(seriesId).executeAsOne()
    }

    fun recieveGameCallback() = runBlocking {
        // Write result to database
        launch {
            val id: Int? = saveResult()
            id?.let {
                val match: LOLMatch = RiotAPIBridge.getMatchData(result.gameId)
                updateGame(it, match)
                updateSeries()
                //updateStandings()
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun saveResult(): Int? {
        logger.info("Saving result to db...")
        val resultQueries = db.resultQueries
        val meta = Json.encodeToString(result.metaData)
        val resultId = resultQueries.insertResult(
            result.startTime,
            result.shortCode,
            meta,
            result.gameId,
            result.gameName,
            result.gameType,
            result.gameMap,
            result.gameMode,
            result.region
        ).executeAsOne()
        logger.info("Result saved!")
        return resultId
    }

    private fun updateGame(resultId: Int, match: LOLMatch) {
        logger.info("Updating game...")
        val gameQueries = db.gameQueries
        val id =
            gameQueries.selectIdBySeriesGameNum(seriesId, gameNum).executeAsOneOrNull()
        when (id) {
            null -> {
                logger.warn("No game found for series {} game {}", seriesId, gameNum)
                return
            }

            else -> {
                logger.debug("Updating game #{} result...", id)
                gameQueries.updateGameResult(
                    resultId, id
                )
                logger.debug("Successfully updated game #{} result!", id)
                val (winnerId: Int, loserId: Int) = Pair(
                    getTeamId(match.participants.filter { participant -> participant.didWin() }),
                    getTeamId(match.participants.filter { participant -> !participant.didWin() }),
                )
                logger.debug("Updating series {} game {} outcome...", series.id, id)
                gameQueries.updateGameOutcome(
                    winnerId, loserId, id
                )
                logger.debug("Finished updating series {} game {}!", seriesId, gameNum)
            }
        }
    }

    private fun updateSeries() {
        logger.info("Updating series outcome...")
        val seriesQueries = db.seriesQueries
        val gameQueries = db.gameQueries
        val team1Wins = gameQueries.countWinningGames(series.id, series.team1_id).executeAsOneOrNull() ?: 0
        val team2Wins = gameQueries.countWinningGames(series.id, series.team2_id).executeAsOneOrNull() ?: 0
        when {
            team1Wins >= series.win_condition -> {
                seriesQueries.updateSeries(series.team1_id, series.id)
            }

            team2Wins >= series.win_condition -> {
                seriesQueries.updateSeries(series.team2_id, series.id)
            }
        }
        logger.info("Successfully updated series!")
    }

    private fun updateStandings() {
        // Recalculate standings for a div:group
        logger.info("Updating standings...")

    }

    private fun getTeamId(players: List<MatchParticipant>): Int {
        val playerQueries = db.playerQueries
        for (player in players) {
            val teamId = playerQueries.selectTeamId(player.puuid).executeAsOneOrNull()
            teamId?.let {
                it.team_id?.let { team_id ->
                    return team_id
                }
            }
        }
        logger.warn("No valid player's provided.")
        return -1
    }
}