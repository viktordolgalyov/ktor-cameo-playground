package com.cameo.useCase.markSeasonWatched

import com.cameo.common.createSignatureForUserEpisode
import com.cameo.common.isNotAired
import com.cameo.common.requestThread
import com.cameo.source.dao.SeasonShortDAO
import com.cameo.source.dao.UserDAO
import com.cameo.source.table.UsersWatchedEpisodesTable
import org.jetbrains.exposed.sql.batchInsert

class MarkSeasonWatchedUseCase {

    suspend fun execute(seasonId: Int, userId: Int): Unit = requestThread {
        val season = SeasonShortDAO.findById(seasonId)
        val user = UserDAO.findById(userId)

        if (season == null || user == null) throw IllegalArgumentException()
        else {
            val airedEpisodes = season.episodes.filterNot { isNotAired(it.airDate) }
            UsersWatchedEpisodesTable.batchInsert(airedEpisodes, true, {
                this[UsersWatchedEpisodesTable.userId] = user.id
                this[UsersWatchedEpisodesTable.seasonId] = season.id
                this[UsersWatchedEpisodesTable.episodeId] = it.id
                this[UsersWatchedEpisodesTable.watchedTimestamp] = System.currentTimeMillis() / 1000
                this[UsersWatchedEpisodesTable.signature] = createSignatureForUserEpisode(it.id.value, user.id.value)
            })
        }
    }
}