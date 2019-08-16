package com.cameo.useCase.markEpisodeWatched

import com.cameo.common.createSignatureForUserEpisode
import com.cameo.common.isNotAired
import com.cameo.common.requestThread
import com.cameo.source.dao.EpisodeShortDAO
import com.cameo.source.dao.UserDAO
import com.cameo.source.table.UsersWatchedEpisodesTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class MarkEpisodeWatchedUseCase {

    suspend fun execute(userId: Int, episodeId: Int): Unit = requestThread {
        val episode = EpisodeShortDAO.findById(episodeId)
        val user = UserDAO.findById(userId)
        if (episode == null || user == null) throw IllegalArgumentException()
        else if (isNotAired(episode.airDate)) throw IllegalArgumentException()
        else {
            val contains = UsersWatchedEpisodesTable
                    .select { UsersWatchedEpisodesTable.userId.eq(userId) and UsersWatchedEpisodesTable.episodeId.eq(episodeId) }
                    .empty()
                    .not()
            if (!contains) insertEpisodeToWatched(user, episode)
        }
    }

    private fun insertEpisodeToWatched(user: UserDAO, episode: EpisodeShortDAO) {
        UsersWatchedEpisodesTable.insert {
            it[UsersWatchedEpisodesTable.userId] = user.id
            it[UsersWatchedEpisodesTable.seasonId] = episode.season.id
            it[UsersWatchedEpisodesTable.episodeId] = episode.id
            it[UsersWatchedEpisodesTable.watchedTimestamp] = System.currentTimeMillis() / 1000
            it[UsersWatchedEpisodesTable.signature] = createSignatureForUserEpisode(episode.id.value, user.id.value)
        }
    }
}