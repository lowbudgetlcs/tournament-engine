package com.lowbudgetlcs

import com.lowbudgetlcs.data.MetaData
import com.lowbudgetlcs.data.Result
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant
import org.slf4j.LoggerFactory

class MatchHandler(private val result: Result) {

    private val logger = LoggerFactory.getLogger("com.lowbudgetlcs.MatchHandler")
    private val db = Db.db
    @OptIn(ExperimentalSerializationApi::class)
    private val metaData: MetaData = Json.decodeFromString<MetaData>(result.metaData)
    private val seriesId = metaData.seriesId
    private val code = result.shortCode
    private val series by lazy {
        val seriesQueries = db.seriesQueries
        seriesQueries.selectSeriesById(seriesId).executeAsOne()
    }

    fun recieveGameCallback() = runBlocking {
        // Write result to database
        launch {
            val id: Int = saveResult()
            val match: LOLMatch = RiotAPIBridge.getMatchData(result.gameId)
            updateGame(id, match)
            updateSeries()
            //updateStandings()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun saveResult(): Int {
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
            gameQueries.selectIdByCode(code).executeAsOneOrNull()
        when (id) {
            null -> {
                logger.warn("No game found for series '{}' game code '{}'", seriesId, code)
                return
            }

            else -> {
                logger.debug("Updating game id '{}' result...", id)
                gameQueries.updateGameResult(
                    resultId, id
                )
                logger.debug("Successfully updated game id '{}' result!", id)
                logger.debug("Updating series id '{}' game id '{}' outcome...", seriesId, id)
                val (winnerId: Int, loserId: Int) = Pair(
                    getTeamId(match.participants.filter { participant -> participant.didWin() }),
                    getTeamId(match.participants.filter { participant -> !participant.didWin() }),
                )
                gameQueries.updateGameOutcome(
                    winnerId, loserId, id
                )
                logger.debug("Succsesfully updated series id '{}' game id '{}'!", seriesId, id)
            }
        }
    }

    private fun updateSeries() {
        logger.info("Updating series outcome...")
        val seriesQueries = db.seriesQueries
        val gameQueries = db.gameQueries
        val team1Wins = gameQueries.countWinsBySeriesId(series.id, series.team1_id).executeAsOneOrNull() ?: 0
        val team2Wins = gameQueries.countWinsBySeriesId(series.id, series.team2_id).executeAsOneOrNull() ?: 0
        when (series.win_condition) {
            team1Wins.toInt() -> {
                seriesQueries.updateSeries(series.team1_id, series.id)
                logger.info("Successfully updated series!")
            }
            team2Wins.toInt() -> {
                seriesQueries.updateSeries(series.team2_id, series.id)
                logger.info("Successfully updated series!")
            }
            else -> {
                logger.info("Series id '{}' not concluded!", seriesId)
            }
        }
    }

    private fun updateStandings() {
        // Recalculate standings for a div:group
        logger.info("Standings TBI...")
    }

    private fun getTeamId(players: List<MatchParticipant>): Int {
        val playerQueries = db.playerQueries
        for (player in players) {
            val teamId = playerQueries.selectTeamId(player.puuid).executeAsOneOrNull()
            teamId?.let {
                it.team_id?.let { team_id ->
                    logger.debug("Fetched team id: {}", team_id)
                    return team_id
                }
            }
        }
        logger.warn("No valid player's provided.")
        return -1
    }
}