package com.lowbudgetlcs.data

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val startTime: Long?,
    val shortCode: String,
    val metaData: MetaData,
    val gameId: Int?,
    val gameName: String?,
    val gameType: String?,
    val gameMap: Int?,
    val gameMode: String?,
    val region: String?
)

@Serializable
data class MetaData(
    val gameNum: Int,
    val seriesId: Int
)