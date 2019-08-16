package com.cameo.source.table

import org.jetbrains.exposed.dao.IntIdTable

object UsersScheduleSeriesTable : IntIdTable() {
    val userId = reference("user_id", UsersTable).index()
    val seriesId = reference("series_id", SeriesTable).index()
    val subscribeTimestamp = long("subscribe_timestamp")
}