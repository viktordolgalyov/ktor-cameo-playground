package com.cameo.useCase.getMovieDetails

import com.cameo.common.asImageUrl
import com.cameo.common.asYoutubeImage
import com.cameo.common.asYoutubeVideo
import com.cameo.common.data.fromEpoch
import com.cameo.common.data.getGenre
import com.cameo.common.data.model.PersonListItem
import com.cameo.common.data.model.Video
import com.cameo.common.requestThread
import com.cameo.source.dao.MovieDetailsCastPersonDAO
import com.cameo.source.dao.MovieDetailsCrewPersonDAO
import com.cameo.source.dao.MovieDetailsDAO
import com.cameo.source.dao.UserScheduleMovieDAO
import com.cameo.source.table.UsersScheduleMoviesTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select

class GetMovieDetailsUseCase {

    suspend fun execute(userId: Int, movieId: Int): MovieDetails? {
        return requestThread {
            val isFollowed = UsersScheduleMoviesTable
                    .slice(UsersScheduleMoviesTable.id)
                    .select { (UsersScheduleMoviesTable.userId eq userId) and (UsersScheduleMoviesTable.movieId eq movieId) }
                    .limit(1)
                    .empty()
                    .not()
            MovieDetailsDAO.findById(movieId)?.toMovie(isFollowed)
        }
    }
}

private fun MovieDetailsDAO.toMovie(isFollowed: Boolean): MovieDetails {
    return MovieDetails(id = this.id.value,
            title = this.title,
            overview = this.overview,
            backdrop = this.backdropPath.asImageUrl(),
            tagline = this.tagline,
            status = this.status,
            poster = this.posterPath.asImageUrl(),
            releaseDate = this.releaseDate.fromEpoch(),
            budget = this.budget,
            revenue = this.revenue,
            homepage = this.homepage,
            runtime = this.runtime,
            trailers = this.trailers.map { Video(it.trailerPath.asYoutubeImage(), it.trailerPath.asYoutubeVideo()) },
            genres = this.genres.mapNotNull { it.genreId?.let { getGenre(it) }?.name },
            cast = this.cast.map { it.toListItem() },
            crew = this.crew.map { it.toListItem() },
            isFollowed = isFollowed)
}

private fun MovieDetailsCastPersonDAO.toListItem(): PersonListItem {
    return PersonListItem(id = this.person.id.value,
            name = this.person.name,
            subtitle = this.character.orEmpty(),
            photo = this.person.photoPath.asImageUrl())
}

private fun MovieDetailsCrewPersonDAO.toListItem(): PersonListItem {
    return PersonListItem(id = this.person.id.value,
            name = this.person.name,
            subtitle = this.job.orEmpty(),
            photo = this.person.photoPath.asImageUrl())
}