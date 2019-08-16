package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object MovieGenresTable : IntIdTable() {
    val movieId = reference("movie_id", MoviesTable).index()
    val genreId = integer("genre_id").nullable()
}