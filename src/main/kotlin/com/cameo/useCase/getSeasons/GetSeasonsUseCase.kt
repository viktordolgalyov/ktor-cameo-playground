package com.cameo.useCase.getSeasons

import com.cameo.common.asImageUrl
import com.cameo.common.data.fromEpoch
import com.cameo.common.requestThread
import com.cameo.source.dao.UsersWatchedEpisodesDAO
import com.cameo.source.table.EpisodesTable
import com.cameo.source.table.SeasonsTable
import com.cameo.source.table.UsersWatchedEpisodesTable
import org.jetbrains.exposed.sql.*

class GetSeasonsUseCase {

    suspend fun execute(userId: Int, seriesId: Int) = requestThread {
        val columns = listOf<Column<*>>(
                SeasonsTable.id,
                SeasonsTable.seriesId,
                SeasonsTable.title,
                SeasonsTable.seasonNumber,
                SeasonsTable.airDate,
                EpisodesTable.id,
                EpisodesTable.title,
                EpisodesTable.overview,
                EpisodesTable.episodeNumber,
                EpisodesTable.posterPath,
                EpisodesTable.airDate,
                UsersWatchedEpisodesTable.id,
                UsersWatchedEpisodesTable.userId,
                UsersWatchedEpisodesTable.watchedTimestamp
        )
        val query = EpisodesTable
                .join(SeasonsTable, JoinType.LEFT, additionalConstraint = {
                    EpisodesTable.seasonId eq SeasonsTable.id
                })
                .join(UsersWatchedEpisodesTable, JoinType.LEFT, additionalConstraint = {
                    (EpisodesTable.id eq UsersWatchedEpisodesTable.episodeId) and
                            ((UsersWatchedEpisodesTable.userId eq userId) or (UsersWatchedEpisodesTable.userId.isNull()))
                })
                .slice(columns)
                .select { (SeasonsTable.seriesId eq seriesId) }

        UsersWatchedEpisodesDAO.wrapRows(query).convert()
    }

    private fun SizedIterable<UsersWatchedEpisodesDAO>.convert(): SeasonsResponse {
        val seasons = this.distinctBy { it.seasonId }
        val result = this
                .groupBy { it.seasonId }
                .mapValues { episodesEntry ->
                    episodesEntry.value.map { episode ->
                        EpisodeInfo(id = episode.episodeId.value,
                                seasonId = episode.seasonId.value,
                                seasonTitle = episode.seasonTitle,
                                episodeTitle = episode.episodeTitle,
                                episodeNumber = episode.episodeNumber,
                                airDate = episode.episodeAirDate.fromEpoch(),
                                imageUrl = episode.episodePoster.asImageUrl(),
                                isWatched = episode.watchedTimestamp != null)
                    }
                }
                .mapKeys { keyValue -> seasons.first { it.seasonId == keyValue.key } }
                .map { seasonEpisodes ->
                    SeasonInfo(seasonEpisodes.key.seasonId.value,
                            seasonEpisodes.key.seriesId.value,
                            seasonEpisodes.key.seasonTitle,
                            seasonEpisodes.key.seasonNumber,
                            seasonEpisodes.value)
                }
        return SeasonsResponse(result)
    }
}