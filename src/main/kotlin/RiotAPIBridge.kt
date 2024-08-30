package com.lowbudgetlcs

import no.stelar7.api.r4j.basic.APICredentials
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard
import no.stelar7.api.r4j.impl.R4J
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch
import org.slf4j.LoggerFactory

object RiotAPIBridge {
    private val logger = LoggerFactory.getLogger("com.lowbudgetlcs.RiotAPIBridge")

    private val client by lazy {
        val creds = APICredentials(System.getenv("RIOT_API_TOKEN"))
        R4J(creds)
    }

    fun healthCheck(): Int {
        try {
            logger.info("Starting healthcheck!")
            val ruuffian = client.accountAPI.getAccountByTag(RegionShard.AMERICAS, "ruuffian", "FUNZ")
            val summoner = client.loLAPI.summonerAPI.getSummonerByPUUID(LeagueShard.NA1, ruuffian.puuid)
            logger.info("Welcome, ${ruuffian.name}. Congrats on hitting level ${summoner.summonerLevel}!")
        } catch (e: Throwable) {
            logger.error(e.message)
            return 1
        }
        return 0
    }

    fun getMatchData(matchId: Long): LOLMatch {
        // Fetch match
        return client.loLAPI.matchAPI.getMatch(RegionShard.AMERICAS, "NA1_$matchId")
    }
}