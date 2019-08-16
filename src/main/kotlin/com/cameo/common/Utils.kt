package com.cameo.common

import java.util.*

inline fun <T> getOrNull(block: () -> T): T? {
    return try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.asImageUrl(): String = if (this.isBlank()) "" else "https://image.tmdb.org/t/p/original/$this"

fun String.asYoutubeVideo(): String = if (this.isBlank()) "" else "https://www.youtube.com/watch?v=$this"

fun String.asYoutubeImage(): String = if (this.isBlank()) "" else "http://img.youtube.com/vi/$this/0.jpg"

fun createSignatureForUserEpisode(episodeId: Int, userId: Int) = "${episodeId}_$userId"

fun isNotAired(timestamp: Long): Boolean {
    val airDate = Calendar.getInstance().apply { timeInMillis = timestamp }
    val today = Calendar.getInstance()
    return airDate.after(today)
}