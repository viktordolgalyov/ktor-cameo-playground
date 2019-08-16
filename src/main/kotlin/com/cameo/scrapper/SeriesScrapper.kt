package com.cameo.scrapper

import com.cameo.common.data.toEpoch
import com.cameo.common.getOrNull
import com.cameo.common.scrapperThread
import com.cameo.source.table.*
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.model.Video
import info.movito.themoviedbapi.model.people.PersonCast
import info.movito.themoviedbapi.model.people.PersonCrew
import info.movito.themoviedbapi.model.tv.TvEpisode
import info.movito.themoviedbapi.model.tv.TvSeason
import info.movito.themoviedbapi.model.tv.TvSeries
import kotlinx.coroutines.experimental.async
import org.jetbrains.exposed.sql.*
import org.slf4j.LoggerFactory
import java.util.*

object SeriesScrapper {

    suspend fun start() {
        // load missing
        val file = getOrNull { loadSeriesFile() }
        if (file != null) {
            readSeriesLines(file, { async { processLine(it) } })
        } else {
            LoggerFactory.getLogger(this::class.java).error("File is null")
        }

        // load updates for the past 3 days
        val from = Calendar.getInstance().apply { this.add(Calendar.DATE, -3) }.timeInMillis
        val to = Calendar.getInstance().apply { this.add(Calendar.DATE, -1) }.timeInMillis
        val updates = getOrNull { randomApi().tvSeries.getChanges(from, to) }
        if (updates != null) {
            val collectedUpdates = mutableListOf<ChangeDTO>()
            collectedUpdates.addAll(updates.results)
            if (updates.total_pages > 2) {
                (2..updates.total_pages).forEach {
                    collectedUpdates.addAll(getOrNull { randomApi().tvSeries.getChanges(from, to, it) }?.results.orEmpty())
                }
            }
            collectedUpdates.forEach { changed ->
                scrapperThread {
                    if (containsSeries(changed.id) && !changed.adult) {
                        val seriesRows = readSeries(changed.id)
                        seriesRows?.let { updateSeries(it) }
                    }
                }
            }
        } else {
            LoggerFactory.getLogger(this::class.java).error("Updates is null")
        }
    }

    private suspend fun processLine(line: SeriesLineInfo) = scrapperThread {
        if (!containsSeries(line.id)) {
            val seriesRows = readSeries(line.id)
            seriesRows?.let { writeSeries(it) }
        }
    }

    private fun readSeries(tmdbId: Int): SeriesRows? {
        val info = getOrNull { randomApi().tvSeries.getSeries(tmdbId, "en", TmdbTV.TvMethod.videos) }
        return info?.let {
            val credits = getOrNull { randomApi().tvSeries.getCredits(tmdbId, "en") }
            val seasons = info.seasons.orEmpty()
            val seasonRows = seasons.mapNotNull { season ->
                val seasonInfo = getOrNull { randomApi().tvSeasons.getSeason(tmdbId, season.seasonNumber, "en") }
                val seasonEpisodes = seasonInfo?.episodes.orEmpty()
                val episodeRows = seasonEpisodes.mapNotNull { episode -> EpisodeRows(episode) }
                seasonInfo?.let { SeasonRows(it, episodeRows) }
            }
            info.let { SeriesRows(it, credits?.cast.orEmpty(), credits?.crew.orEmpty(), seasonRows, info.videos.orEmpty()) }
        }
    }

    private fun writeSeries(rows: SeriesRows) {
        //store info
        val seriesId = SeriesTable.insertAndGetId {
            it[SeriesTable.tmdbId] = rows.info.id
            it[SeriesTable.title] = rows.info.name.orEmpty()
            it[SeriesTable.originalTitle] = rows.info.originalName.orEmpty()
            it[SeriesTable.overview] = rows.info.overview.orEmpty()
            it[SeriesTable.firstAirDate] = getOrNull { rows.info.firstAirDate.toEpoch() } ?: 0
            it[SeriesTable.lastAirDate] = getOrNull { rows.info.lastAirDate.toEpoch() } ?: 0
            it[SeriesTable.homepage] = rows.info.homepage.orEmpty()
            it[SeriesTable.popularity] = rows.info.popularity
            it[SeriesTable.status] = rows.info.status.orEmpty()
            it[SeriesTable.posterPath] = rows.info.posterPath.orEmpty()
            it[SeriesTable.backdropPath] = rows.info.backdropPath.orEmpty()
        }
        //store cast
        val cast = rows.cast.mapNotNull {
            val migratedPersonId = PersonScrapper.getPersonId(it.id)
            if (migratedPersonId == null) null else migratedPersonId to it
        }
        SeriesCastPersonsTable.batchInsert(cast, true, {
            this[SeriesCastPersonsTable.seriesId] = seriesId
            this[SeriesCastPersonsTable.character] = it.second.character.orEmpty()
            this[SeriesCastPersonsTable.personId] = it.first
            this[SeriesCastPersonsTable.signature] = "${seriesId.value}_${it.first.value}_${it.second.character}"
        })

        //store crew
        val crew = rows.crew.mapNotNull {
            val migratedPersonId = PersonScrapper.getPersonId(it.id)
            if (migratedPersonId == null) null else migratedPersonId to it
        }
        SeriesCrewPersonsTable.batchInsert(crew, true, {
            this[SeriesCrewPersonsTable.seriesId] = seriesId
            this[SeriesCrewPersonsTable.department] = it.second.department.orEmpty()
            this[SeriesCrewPersonsTable.job] = it.second.job.orEmpty()
            this[SeriesCrewPersonsTable.personId] = it.first
            this[SeriesCrewPersonsTable.signature] = "${seriesId.value}_${it.first.value}_${it.second.job}"
        })

        //store genres
        rows.info.genres.orEmpty().forEach { genreRow ->
            SeriesGenresTable.insert {
                it[SeriesGenresTable.seriesId] = seriesId
                it[SeriesGenresTable.genreId] = genreRow.id
            }
        }

        //store trailers
        SeriesTrailersTable.batchInsert(rows.trailers, true, {
            this[SeriesTrailersTable.seriesId] = seriesId
            this[SeriesTrailersTable.trailerPath] = it.key.orEmpty()
            this[SeriesTrailersTable.signature] = "${seriesId.value}_${it.key.orEmpty()}"
        })

        //store seasons
        rows.seasons.forEach { seasonRow ->
            //store season
            val seasonId = SeasonsTable.insertAndGetId {
                it[SeasonsTable.seriesId] = seriesId
                it[SeasonsTable.title] = seasonRow.info.name.orEmpty()
                it[SeasonsTable.overview] = seasonRow.info.overview.orEmpty()
                it[SeasonsTable.airDate] = getOrNull { seasonRow.info.airDate.toEpoch() } ?: 0
                it[SeasonsTable.seasonNumber] = seasonRow.info.seasonNumber
                it[SeasonsTable.posterPath] = seasonRow.info.posterPath.orEmpty()
            }
            //store episodes
            EpisodesTable.batchInsert(seasonRow.episodes, true, {
                this[EpisodesTable.seasonId] = seasonId
                this[EpisodesTable.title] = it.info.name.orEmpty()
                this[EpisodesTable.overview] = it.info.overview.orEmpty()
                this[EpisodesTable.airDate] = getOrNull { it.info.airDate.toEpoch() } ?: 0
                this[EpisodesTable.episodeNumber] = it.info.episodeNumber
                this[EpisodesTable.posterPath] = it.info.stillPath.orEmpty()
                this[EpisodesTable.signature] = "${seasonId.value}_${it.info.episodeNumber}"
            })
        }
    }

    private fun updateSeries(rows: SeriesRows) {
        val seriesId = SeriesTable
                .slice(SeriesTable.id)
                .select { SeriesTable.tmdbId eq rows.info.id }
                .limit(1)
                .map { it[SeriesTable.id] }
                .firstOrNull() ?: return

        //store info
        SeriesTable.update({ SeriesTable.id eq seriesId }, body = {
            it[SeriesTable.tmdbId] = rows.info.id
            it[SeriesTable.title] = rows.info.name.orEmpty()
            it[SeriesTable.originalTitle] = rows.info.originalName.orEmpty()
            it[SeriesTable.overview] = rows.info.overview.orEmpty()
            it[SeriesTable.firstAirDate] = getOrNull { rows.info.firstAirDate.toEpoch() } ?: 0
            it[SeriesTable.lastAirDate] = getOrNull { rows.info.lastAirDate.toEpoch() } ?: 0
            it[SeriesTable.homepage] = rows.info.homepage.orEmpty()
            it[SeriesTable.popularity] = rows.info.popularity
            it[SeriesTable.status] = rows.info.status.orEmpty()
            it[SeriesTable.posterPath] = rows.info.posterPath.orEmpty()
            it[SeriesTable.backdropPath] = rows.info.backdropPath.orEmpty()
        })

        //store trailers
        SeriesTrailersTable.batchInsert(rows.trailers, true, {
            this[SeriesTrailersTable.seriesId] = seriesId
            this[SeriesTrailersTable.trailerPath] = it.key.orEmpty()
            this[SeriesTrailersTable.signature] = "${seriesId}_${it.key.orEmpty()}"
        })

        //store cast
        val cast = rows.cast.mapNotNull {
            val migratedPersonId = PersonScrapper.getPersonId(it.id)
            if (migratedPersonId == null) null else migratedPersonId to it
        }
        SeriesCastPersonsTable.batchInsert(cast, true, {
            this[SeriesCastPersonsTable.seriesId] = seriesId
            this[SeriesCastPersonsTable.character] = it.second.character.orEmpty()
            this[SeriesCastPersonsTable.personId] = it.first
            this[SeriesCastPersonsTable.signature] = "${seriesId.value}_${it.first.value}_${it.second.character}"
        })

        //store crew
        val crew = rows.crew.mapNotNull {
            val migratedPersonId = PersonScrapper.getPersonId(it.id)
            if (migratedPersonId == null) null else migratedPersonId to it
        }
        SeriesCrewPersonsTable.batchInsert(crew, true, {
            this[SeriesCrewPersonsTable.seriesId] = seriesId
            this[SeriesCrewPersonsTable.department] = it.second.department.orEmpty()
            this[SeriesCrewPersonsTable.job] = it.second.job.orEmpty()
            this[SeriesCrewPersonsTable.personId] = it.first
            this[SeriesCrewPersonsTable.signature] = "${seriesId.value}_${it.first.value}_${it.second.job}"
        })

        //store seasons
        rows.seasons.forEach { seasonRow ->
            //store season
            val seasonId = SeasonsTable.insertIgnoreAndGetId {
                it[SeasonsTable.seriesId] = seriesId
                it[SeasonsTable.title] = seasonRow.info.name.orEmpty()
                it[SeasonsTable.overview] = seasonRow.info.overview.orEmpty()
                it[SeasonsTable.airDate] = getOrNull { seasonRow.info.airDate.toEpoch() } ?: 0
                it[SeasonsTable.seasonNumber] = seasonRow.info.seasonNumber
                it[SeasonsTable.posterPath] = seasonRow.info.posterPath.orEmpty()
                it[SeasonsTable.signature] = "${seriesId.value}_${seasonRow.info.seasonNumber}"
            }
            if (seasonId != null) {
                //store episodes
                EpisodesTable.batchInsert(seasonRow.episodes, true, {
                    this[EpisodesTable.seasonId] = seasonId
                    this[EpisodesTable.title] = it.info.name.orEmpty()
                    this[EpisodesTable.overview] = it.info.overview.orEmpty()
                    this[EpisodesTable.airDate] = getOrNull { it.info.airDate.toEpoch() } ?: 0
                    this[EpisodesTable.episodeNumber] = it.info.episodeNumber
                    this[EpisodesTable.posterPath] = it.info.stillPath.orEmpty()
                    this[EpisodesTable.signature] = "${seasonId}_${it.info.episodeNumber}"
                })
            } else {
                val storedSeasonId = SeasonsTable
                        .slice(SeasonsTable.id)
                        .select { SeasonsTable.seriesId eq seriesId and (SeasonsTable.seasonNumber eq seasonRow.info.seasonNumber) }
                        .limit(1)
                        .map { it[SeasonsTable.id] }
                        .firstOrNull()
                if (storedSeasonId != null) {
                    //store episodes
                    EpisodesTable.batchInsert(seasonRow.episodes, true, {
                        this[EpisodesTable.seasonId] = storedSeasonId
                        this[EpisodesTable.title] = it.info.name.orEmpty()
                        this[EpisodesTable.overview] = it.info.overview.orEmpty()
                        this[EpisodesTable.airDate] = getOrNull { it.info.airDate.toEpoch() } ?: 0
                        this[EpisodesTable.episodeNumber] = it.info.episodeNumber
                        this[EpisodesTable.posterPath] = it.info.stillPath.orEmpty()
                        this[EpisodesTable.signature] = "${storedSeasonId}_${it.info.episodeNumber}"
                    })
                }
            }
        }
    }

    private fun containsSeries(tmdbId: Int): Boolean {
        return SeriesTable
                .slice(SeriesTable.id)
                .select { SeriesTable.tmdbId eq tmdbId }
                .limit(1)
                .empty()
                .not()
    }
}

internal data class SeriesRows(val info: TvSeries,
                               val cast: List<PersonCast>,
                               val crew: List<PersonCrew>,
                               val seasons: List<SeasonRows>,
                               val trailers: List<Video>)

internal data class SeasonRows(val info: TvSeason,
                               val episodes: List<EpisodeRows>)

internal data class EpisodeRows(val info: TvEpisode)