package com.cameo.useCase.markEpisodeNotWatched

import com.cameo.common.requestThread
import com.cameo.source.table.UsersWatchedEpisodesTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere

class MarkEpisodeNotWatchedUseCase {

    suspend fun execute(userId: Int, episodeId: Int): Unit = requestThread {
        deleteFromWatched(userId, episodeId)
    }

    private fun deleteFromWatched(userId: Int, episodeId: Int) {
        UsersWatchedEpisodesTable.deleteWhere {
            UsersWatchedEpisodesTable.userId eq userId and (UsersWatchedEpisodesTable.episodeId eq episodeId)
        }
    }
}