package com.cameo.common.data

import com.cameo.common.data.model.LocalGenre

private val genreMap = mapOf(
        12 to LocalGenre(12, "Adventure"),
        14 to LocalGenre(14, "Fantasy"),
        16 to LocalGenre(16, "Animation"),
        18 to LocalGenre(18, "Drama"),
        27 to LocalGenre(27, "Horror"),
        28 to LocalGenre(28, "Action"),
        35 to LocalGenre(35, "Comedy"),
        36 to LocalGenre(36, "History"),
        37 to LocalGenre(37, "Western"),
        53 to LocalGenre(53, "Thriller"),
        80 to LocalGenre(80, "Crime"),
        99 to LocalGenre(99, "Documentary"),
        878 to LocalGenre(878, "Science Fiction"),
        9648 to LocalGenre(9648, "Mystery"),
        10402 to LocalGenre(10402, "Music"),
        10749 to LocalGenre(10749, "Romance"),
        10751 to LocalGenre(10751, "Family"),
        10752 to LocalGenre(10752, "War"),
        10759 to LocalGenre(10759, "Action & Adventure"),
        10762 to LocalGenre(10762, "Kids"),
        10763 to LocalGenre(10763, "News"),
        10764 to LocalGenre(10764, "Reality"),
        10765 to LocalGenre(10765, "Sci-Fi & Fantasy"),
        10766 to LocalGenre(10766, "Soap"),
        10767 to LocalGenre(10767, "Talk"),
        10768 to LocalGenre(10768, "War & Politics"),
        10770 to LocalGenre(10770, "TV Movie")
)

fun getGenre(id: Int) = genreMap[id]