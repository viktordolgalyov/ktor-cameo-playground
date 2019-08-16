package com.cameo.useCase.getSeriesDetails

import com.cameo.common.asImageUrl
import com.cameo.common.data.fromEpoch
import com.cameo.common.data.getGenre
import com.cameo.common.data.model.PersonListItem
import com.cameo.common.getOrNull
import com.cameo.common.requestThread
import com.cameo.source.dao.SeriesDetailsCastPersonDAO
import com.cameo.source.dao.SeriesDetailsCrewPersonDAO
import com.cameo.source.dao.SeriesDetailsDAO
import com.cameo.source.table.UsersScheduleSeriesTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select

class GetSeriesDetailsUseCase {

    suspend fun execute(userId: Int, seriesId: Int): SeriesDetails? {
        return requestThread {
            val isFollowed = UsersScheduleSeriesTable
                    .slice(UsersScheduleSeriesTable.id)
                    .select { (UsersScheduleSeriesTable.userId eq userId) and (UsersScheduleSeriesTable.seriesId eq seriesId) }
                    .limit(1)
                    .empty()
                    .not()
            SeriesDetailsDAO.findById(seriesId)?.toSeries(isFollowed)
        }
    }
}

private fun SeriesDetailsDAO.toSeries(isFollowed: Boolean): SeriesDetails {
    return SeriesDetails(
            this.id.value,
            this.title,
            this.overview,
            getOrNull { this.firstAirDate.fromEpoch() }.orEmpty(),
            genres.firstOrNull()?.genreId?.let { getGenre(it) }?.name.orEmpty(),
            this.seasons.count(),
            this.status,
            this.backdropPath.asImageUrl(),
            this.cast.map { it.toPersonCastItem() },
            this.crew.map { it.toPersonCrewItem() },
            isFollowed
    )
}

private fun SeriesDetailsCastPersonDAO.toPersonCastItem(): PersonListItem {
    return PersonListItem(
            this.id.value,
            this.person.name,
            this.character.orEmpty(),
            this.person.photoPath.asImageUrl()
    )
}

private fun SeriesDetailsCrewPersonDAO.toPersonCrewItem(): PersonListItem {
    return PersonListItem(
            this.person.id.value,
            this.person.name,
            this.job.orEmpty(),
            this.person.photoPath.asImageUrl()
    )
}