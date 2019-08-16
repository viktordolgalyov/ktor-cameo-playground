package com.cameo.source.dao

import com.cameo.source.table.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class MovieDetailsDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieDetailsDAO>(MoviesTable)

    var title by MoviesTable.title
    var overview by MoviesTable.overview
    var posterPath by MoviesTable.posterPath
    var backdropPath by MoviesTable.backdropPath
    var tagline by MoviesTable.tagline
    var status by MoviesTable.status
    var releaseDate by MoviesTable.releaseDate
    var budget by MoviesTable.budget
    var revenue by MoviesTable.revenue
    var homepage by MoviesTable.homepage
    var runtime by MoviesTable.runtime
    val trailers by MovieDetailsTrailerDAO referrersOn MovieTrailersTable.movieId
    val genres by MovieDetailsGenreDAO referrersOn MovieGenresTable.movieId
    val cast by MovieDetailsCastPersonDAO referrersOn MovieCastPersonsTable.movieId
    val crew by MovieDetailsCrewPersonDAO referrersOn MovieCrewPersonsTable.movieId
}

class MovieDetailsTrailerDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieDetailsTrailerDAO>(MovieTrailersTable)

    var movie by MovieDetailsDAO referencedOn MovieTrailersTable.movieId
    var trailerPath by MovieTrailersTable.trailerPath
}

class MovieDetailsGenreDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieDetailsGenreDAO>(MovieGenresTable)

    var movie by MovieDetailsDAO referencedOn MovieGenresTable.movieId
    var genreId by MovieGenresTable.genreId
}

class MovieDetailsCastPersonDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieDetailsCastPersonDAO>(MovieCastPersonsTable)

    var movie by MovieDetailsDAO referencedOn MovieCastPersonsTable.movieId
    var person by MovieDetailsPersonInfoDAO referencedOn MovieCastPersonsTable.personId
    var character by MovieCastPersonsTable.character
}

class MovieDetailsCrewPersonDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieDetailsCrewPersonDAO>(MovieCrewPersonsTable)

    var movie by MovieDetailsDAO referencedOn MovieCrewPersonsTable.movieId
    var person by MovieDetailsPersonInfoDAO referencedOn MovieCrewPersonsTable.personId
    var department by MovieCrewPersonsTable.department
    var job by MovieCrewPersonsTable.job
}

class MovieDetailsPersonInfoDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieDetailsPersonInfoDAO>(PersonsTable)

    var name by PersonsTable.name
    var photoPath by PersonsTable.photoPath
}