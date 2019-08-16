package com.cameo.source.dao

import com.cameo.source.table.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class SearchMovieDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SearchMovieDAO>(MoviesTable)

    var title by MoviesTable.title
    var poster by MoviesTable.posterPath
    var popularity by MoviesTable.popularity
    val genres by SearchMovieGenreDAO referrersOn MovieGenresTable.movieId
    val cast by SearchMovieCastPersonDAO referrersOn MovieCastPersonsTable.movieId
    val crew by SearchMovieCrewPersonDAO referrersOn MovieCrewPersonsTable.movieId
}

class SearchMovieCastPersonDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SearchMovieCastPersonDAO>(MovieCastPersonsTable)

    var movie by SearchMovieDAO referencedOn MovieCastPersonsTable.movieId
    var person by SearchMoviePersonInfoDAO referencedOn MovieCastPersonsTable.personId
    var character by MovieCastPersonsTable.character
}

class SearchMovieCrewPersonDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SearchMovieCrewPersonDAO>(MovieCrewPersonsTable)

    var department by MovieCrewPersonsTable.department
    var job by MovieCrewPersonsTable.job
    var person by SearchMoviePersonInfoDAO referencedOn MovieCrewPersonsTable.personId
    var movie by SearchMovieDAO referencedOn MovieCrewPersonsTable.movieId
}

class SearchMoviePersonInfoDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SearchMoviePersonInfoDAO>(PersonsTable)

    var name by PersonsTable.name
    var photoPath by PersonsTable.photoPath
}

class SearchMovieGenreDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SearchMovieGenreDAO>(MovieGenresTable)

    var genreId by MovieGenresTable.genreId
    var movie by SearchMovieDAO referencedOn MovieGenresTable.movieId
}