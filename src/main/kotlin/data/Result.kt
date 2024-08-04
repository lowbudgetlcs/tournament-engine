package com.lowbudgetlcs.data

import com.lowbudgetlcs.Results
import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val id: Int,
    val startTime: Long?,
    val shortCode: String,
    val metaData: String,
    val gameId: Int?,
    val gameName: String?,
    val gameType: String?,
    val gameMap: String?,
    val gameMode: String?,
    val region: String?
)

class ResultsAdapter {
    fun toSerializable(results: Results): Result {
        val (id, start_time, short_code, meta_data, game_id, game_name, game_type, game_map, game_mode, region) = results
        return Result(id, start_time, short_code, meta_data, game_id, game_name, game_type, game_map, game_mode, region)
    }

    fun toNonSerializable(result: Result): Results {
        val (id, startTime, shortCode, metaData, gameId, gameName, gameType, gameMap, gameMode, region) = result
        return Results(
            id,
            startTime,
            shortCode,
            metaData,
            gameId,
            gameName,
            gameType,
            gameMap,
            gameMode,
            region
        )
    }
}

