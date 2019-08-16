package com.cameo.source.dao

import com.cameo.source.table.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class SeriesDetailsDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SeriesDetailsDAO>(SeriesTable)

    var title by SeriesTable.title
    var overview by SeriesTable.overview
    var firstAirDate by SeriesTable.firstAirDate
    var status by SeriesTable.status
    var backdropPath by SeriesTable.backdropPath
    val genres by SeriesDetailsGenreDAO referrersOn SeriesGenresTable.seriesId
    val seasons by SeriesDetailsSeasonDAO referrersOn SeasonsTable.seriesId
    val cast by SeriesDetailsCastPersonDAO referrersOn SeriesCastPersonsTable.seriesId
    val crew by SeriesDetailsCrewPersonDAO referrersOn SeriesCrewPersonsTable.seriesId
}

class SeriesDetailsGenreDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SeriesDetailsGenreDAO>(SeriesGenresTable)

    var series by SeriesDetailsDAO referencedOn SeriesGenresTable.seriesId
    var genreId by SeriesGenresTable.genreId
}

class SeriesDetailsSeasonDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SeriesDetailsSeasonDAO>(SeasonsTable)

    var seasonNumber by SeasonsTable.seasonNumber
    var title by SeasonsTable.title
    var series by SeriesDetailsDAO referencedOn SeasonsTable.seriesId
}

class SeriesDetailsCastPersonDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SeriesDetailsCastPersonDAO>(SeriesCastPersonsTable)

    var series by SeriesDetailsDAO referencedOn SeriesCastPersonsTable.seriesId
    var person by SeriesDetailsPersonInfoDAO referencedOn SeriesCastPersonsTable.personId
    var character by SeriesCastPersonsTable.character
}

class SeriesDetailsCrewPersonDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SeriesDetailsCrewPersonDAO>(SeriesCrewPersonsTable)

    var department by SeriesCrewPersonsTable.department
    var job by SeriesCrewPersonsTable.job
    var person by SeriesDetailsPersonInfoDAO referencedOn SeriesCrewPersonsTable.personId
    var series by SeriesDetailsDAO referencedOn SeriesCrewPersonsTable.seriesId
}

class SeriesDetailsPersonInfoDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SeriesDetailsPersonInfoDAO>(PersonsTable)

    var name by PersonsTable.name
    var photoPath by PersonsTable.photoPath
}