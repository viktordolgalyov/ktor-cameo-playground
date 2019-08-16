package com.cameo.common.data

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

fun String.toDateTime(): DateTime {
    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
    return formatter.parseDateTime(this)
}

fun DateTime.format(): String {
    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
    return formatter.print(this)
}

fun Long.formatDate(): String {
    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
    return formatter.print(this)
}

fun DateTime.formatAsYear(): String {
    val formatter = DateTimeFormat.forPattern("yyyy")
    return formatter.print(this)
}

fun DateTime.toEpoch(): Long {
    return millis / 1000
}

fun String.toEpoch(): Long {
    return toDateTime().toEpoch()
}

fun Long.dateFromEpoch(): DateTime {
    return DateTime(Date(this * 1000))
}

fun Long.fromEpoch(): String {
    return dateFromEpoch().format()
}