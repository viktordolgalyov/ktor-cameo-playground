package com.cameo.scrapper

import com.cameo.common.data.toDateTime
import com.cameo.common.data.toEpoch
import com.cameo.common.getOrNull
import com.cameo.common.scrapperThread
import com.cameo.source.table.*
import info.movito.themoviedbapi.model.MovieDb
import info.movito.themoviedbapi.model.Video
import info.movito.themoviedbapi.model.people.PersonCast
import info.movito.themoviedbapi.model.people.PersonCrew
import org.jetbrains.exposed.sql.*
import org.slf4j.LoggerFactory
import java.util.*

object MovieScrapper {

    suspend fun start() {
        // load missed movies
        try {
            val file = getOrNull { loadMovieFile() }
            if (file != null) {
                readMovieLines(file, { it.forEach { processLine(it) } })
            } else {
                LoggerFactory.getLogger(this::class.java).error("File is null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //load updates for the past 3 days
        val from = Calendar.getInstance().apply { this.add(Calendar.DATE, -3) }.timeInMillis
        val to = Calendar.getInstance().apply { this.add(Calendar.DATE, -1) }.timeInMillis
        val updates = getOrNull { randomApi().movies.getChanges(from, to) }
        if (updates != null) {
            val collectedUpdates = mutableListOf<ChangeDTO>()
            collectedUpdates.addAll(updates.results)
            if (updates.total_pages > 1) {
                (2..updates.total_pages).forEach {
                    collectedUpdates.addAll(getOrNull { randomApi().movies.getChanges(from, to, it) }?.results.orEmpty())
                }
            }
            collectedUpdates.forEach { changed ->
                scrapperThread {
                    if (containsMovie(changed.id)) {
                        val movieRows = readMovie(changed.id)
                        movieRows?.let { updateMovie(it) }
                    }
                }
            }
        } else {
            LoggerFactory.getLogger(this::class.java).error("Updates is null")
        }
    }

    private suspend fun processLine(line: MovieLineInfo) = scrapperThread {
        if (line.isAdult) {
            deleteMovie(line)
        } else {
            if (!containsMovie(line.tmdbId)) {
                LoggerFactory.getLogger(this::class.java).debug("Processing movie: ${line.originalTitle}")
                val movieRows = readMovie(line.tmdbId)
                movieRows?.let { writeMovie(it) }
            }
        }
    }

    private fun readMovie(tmdbId: Int): MovieRows? {
        val movie = getOrNull { randomApi().movies.getMovie(tmdbId, "en") }
        return movie?.let {
            val trailers = getOrNull { randomApi().movies.getVideos(tmdbId, "en") }
            val credits = getOrNull { randomApi().movies.getCredits(tmdbId) }
            MovieRows(it, credits?.cast.orEmpty(), credits?.crew.orEmpty(), trailers.orEmpty())
        }
    }

    private fun deleteMovie(line: MovieLineInfo) {
        LoggerFactory.getLogger(this::class.java).error("Delete movie:${line.originalTitle}")
        MoviesTable.deleteIgnoreWhere { MoviesTable.tmdbId eq line.tmdbId }
    }

    private fun writeMovie(rows: MovieRows) {
        val id = MoviesTable.insertAndGetId {
            it[MoviesTable.tmdbId] = rows.movie.id
            it[MoviesTable.imdbId] = rows.movie.imdbID.orEmpty()
            it[MoviesTable.title] = rows.movie.title.orEmpty()
            it[MoviesTable.originalTitle] = rows.movie.originalTitle.orEmpty()
            it[MoviesTable.overview] = rows.movie.overview.orEmpty()
            it[MoviesTable.popularity] = getOrNull { rows.movie.popularity } ?: 0.0f
            it[MoviesTable.releaseDate] = getOrNull { rows.movie.releaseDate.orEmpty().toDateTime().toEpoch() } ?: -1
            it[MoviesTable.collectionId] = rows.movie.belongsToCollection?.id ?: -1
            it[MoviesTable.budget] = getOrNull { rows.movie.budget } ?: 0
            it[MoviesTable.homepage] = rows.movie.homepage.orEmpty()
            it[MoviesTable.originalLanguage] = rows.movie.originalLanguage.orEmpty()
            it[MoviesTable.revenue] = getOrNull { rows.movie.revenue } ?: 0
            it[MoviesTable.runtime] = getOrNull { rows.movie.runtime } ?: 0
            it[MoviesTable.tagline] = rows.movie.tagline.orEmpty()
            it[MoviesTable.status] = rows.movie.status.orEmpty()
            it[MoviesTable.posterPath] = rows.movie.posterPath.orEmpty()
            it[MoviesTable.backdropPath] = rows.movie.backdropPath.orEmpty()
        }
        //store cast
        val cast = rows.castRows.mapNotNull {
            val localId = PersonScrapper.getPersonId(it.id)
            if (localId == null) null else localId to it
        }
        MovieCastPersonsTable.batchInsert(cast, true, {
            this[MovieCastPersonsTable.movieId] = id
            this[MovieCastPersonsTable.character] = it.second.character.orEmpty()
            this[MovieCastPersonsTable.personId] = it.first
            this[MovieCastPersonsTable.signature] = "${id.value}_${it.first.value}_${it.second.character}"
        })

        //store crew
        val crew = rows.crewRows.mapNotNull {
            val localId = PersonScrapper.getPersonId(it.id)
            if (localId == null) null else localId to it
        }
        MovieCrewPersonsTable.batchInsert(crew, true, {
            this[MovieCrewPersonsTable.movieId] = id
            this[MovieCrewPersonsTable.department] = it.second.department.orEmpty()
            this[MovieCrewPersonsTable.job] = it.second.job.orEmpty()
            this[MovieCrewPersonsTable.personId] = it.first
            this[MovieCrewPersonsTable.signature] = "${id.value}_${it.first.value}_${it.second.job}"
        })

        //store genres
        rows.movie.genres.orEmpty().forEach { genreRow ->
            MovieGenresTable.insert {
                it[MovieGenresTable.movieId] = id
                it[MovieGenresTable.genreId] = genreRow.id
            }
        }

        //store trailers
        MovieTrailersTable.batchInsert(rows.trailers, true, {
            this[MovieTrailersTable.movieId] = id
            this[MovieTrailersTable.trailerPath] = it.key.orEmpty()
            this[MovieTrailersTable.signature] = "${id.value}_${it.key.orEmpty()}"
        })
    }

    private fun updateMovie(rows: MovieRows) {
        val movieId = MoviesTable
                .slice(MoviesTable.id)
                .select { MoviesTable.tmdbId eq rows.movie.id }
                .limit(1)
                .map { it[MoviesTable.id] }
                .firstOrNull() ?: return

        MoviesTable.update({ MoviesTable.id eq movieId }, body = {
            it[MoviesTable.tmdbId] = rows.movie.id
            it[MoviesTable.imdbId] = rows.movie.imdbID.orEmpty()
            it[MoviesTable.title] = rows.movie.title.orEmpty()
            it[MoviesTable.originalTitle] = rows.movie.originalTitle.orEmpty()
            it[MoviesTable.overview] = rows.movie.overview.orEmpty()
            it[MoviesTable.popularity] = getOrNull { rows.movie.popularity } ?: 0.0f
            it[MoviesTable.releaseDate] = getOrNull { rows.movie.releaseDate.orEmpty().toDateTime().toEpoch() } ?: -1
            it[MoviesTable.collectionId] = rows.movie.belongsToCollection?.id ?: -1
            it[MoviesTable.budget] = getOrNull { rows.movie.budget } ?: 0
            it[MoviesTable.homepage] = rows.movie.homepage.orEmpty()
            it[MoviesTable.originalLanguage] = rows.movie.originalLanguage.orEmpty()
            it[MoviesTable.revenue] = getOrNull { rows.movie.revenue } ?: 0
            it[MoviesTable.runtime] = getOrNull { rows.movie.runtime } ?: 0
            it[MoviesTable.tagline] = rows.movie.tagline.orEmpty()
            it[MoviesTable.status] = rows.movie.status.orEmpty()
            it[MoviesTable.posterPath] = rows.movie.posterPath.orEmpty()
            it[MoviesTable.backdropPath] = rows.movie.backdropPath.orEmpty()
        })

        //update trailers
        MovieTrailersTable.batchInsert(rows.trailers, true, {
            this[MovieTrailersTable.movieId] = movieId
            this[MovieTrailersTable.trailerPath] = it.key.orEmpty()
            this[MovieTrailersTable.signature] = "${movieId.value}_${it.key.orEmpty()}"
        })

        // update crew
        val crew = rows.crewRows.mapNotNull {
            val localId = PersonScrapper.getPersonId(it.id)
            if (localId == null) null else localId to it
        }
        MovieCrewPersonsTable.batchInsert(crew, true, {
            this[MovieCrewPersonsTable.movieId] = movieId
            this[MovieCrewPersonsTable.department] = it.second.department.orEmpty()
            this[MovieCrewPersonsTable.job] = it.second.job.orEmpty()
            this[MovieCrewPersonsTable.personId] = it.first
            this[MovieCrewPersonsTable.signature] = "${movieId.value}_${it.first.value}_${it.second.job}"
        })

        //store cast
        val cast = rows.castRows.mapNotNull {
            val localId = PersonScrapper.getPersonId(it.id)
            if (localId == null) null else localId to it
        }
        MovieCastPersonsTable.batchInsert(cast, true, {
            this[MovieCastPersonsTable.movieId] = movieId
            this[MovieCastPersonsTable.character] = it.second.character.orEmpty()
            this[MovieCastPersonsTable.personId] = it.first
            this[MovieCastPersonsTable.signature] = "${movieId.value}_${it.first.value}_${it.second.character}"
        })
    }

    private fun containsMovie(tmdbId: Int): Boolean {
        return MoviesTable
                .slice(MoviesTable.id)
                .select { MoviesTable.tmdbId eq tmdbId }
                .limit(1)
                .empty()
                .not()
    }
}

internal data class MovieRows(val movie: MovieDb,
                              val castRows: List<PersonCast>,
                              val crewRows: List<PersonCrew>,
                              val trailers: List<Video>)