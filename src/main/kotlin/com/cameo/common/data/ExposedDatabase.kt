package com.cameo.common.data

import com.cameo.source.table.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedDatabase private constructor() {

    companion object {
        fun install() {
            ExposedDatabase()
        }
    }

    init {
        Database.connect(hikari())
        transaction { createTables() }
    }

    private fun createTables() {
        create(PersonsTable)

        create(MovieCastPersonsTable)
        create(MovieCrewPersonsTable)
        create(MovieGenresTable)
        create(MoviesTable)
        create(MovieTrailersTable)

        create(SeriesTable)
        create(SeriesTrailersTable)
        create(SeriesCastPersonsTable)
        create(SeriesCrewPersonsTable)
        create(SeriesGenresTable)

        create(SeasonsTable)
        create(EpisodesTable)

        create(UsersTable)
        create(UsersScheduleMoviesTable)
        create(UsersScheduleSeriesTable)

        create(UsersWatchedEpisodesTable)
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig("/hikari.properties")
        return HikariDataSource(config)
    }
}