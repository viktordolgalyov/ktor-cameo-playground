package com.cameo.useCase.markSeasonNotWatched

import com.cameo.common.requestThread
import com.cameo.source.table.UsersWatchedEpisodesTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere

class MarkSeasonNotWatchedUseCase {

    suspend fun execute(seasonId: Int, userId: Int): Unit = requestThread {
        UsersWatchedEpisodesTable.deleteWhere {
            UsersWatchedEpisodesTable.userId.eq(userId) and UsersWatchedEpisodesTable.seasonId.eq(seasonId)
        }
    }
}