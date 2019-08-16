package com.cameo.scrapper

import com.fasterxml.jackson.databind.ObjectMapper
import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.tools.WebBrowser
import kotlinx.coroutines.experimental.runBlocking
import org.apache.commons.io.FileUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPInputStream

private val apiKey1 = "api1"
private val apiKey2 = "api2"
private val apiKey3 = "api3"
private val apiKey4 = "api4"

private val api1 = TmdbApi(apiKey1, WebBrowser(), true)
private val api2 = TmdbApi(apiKey2, WebBrowser(), true)
private val api3 = TmdbApi(apiKey3, WebBrowser(), true)
private val api4 = TmdbApi(apiKey4, WebBrowser(), true)

fun randomApi(): TmdbApi {
    return listOf(api1, api2, api3, api4).shuffled().first()
}

fun stillSizes() = listOf("w92", "w185", "w300", "original")

fun loadMovieFile(): File {
    val format = SimpleDateFormat("MM_dd_yyyy").format(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)))
    val url = "http://files.tmdb.org/p/exports/movie_ids_$format.json.gz"
    val target = File("movies/$format.json.gz")
    val targetJson = File("movies/$format.json")
    target.parentFile.mkdirs()
    target.createNewFile()
    targetJson.createNewFile()
    FileUtils.copyURLToFile(URL(url), target)
    val stream = GZIPInputStream(FileInputStream(target))
    Files.copy(stream, targetJson.toPath(), StandardCopyOption.REPLACE_EXISTING)
    return targetJson
}

fun readMovieLines(file: File, onNext: suspend (List<MovieLineInfo>) -> Unit) {
    val mapper = ObjectMapper()
    val reader = file.bufferedReader()
    reader.lineSequence().chunked(10).forEach { list ->
        val info = list.mapNotNull {
            if (it.contains('{') && it.contains('}')) {
                val lineInfo = mapper.readTree(it)
                MovieLineInfo(
                        isAdult = lineInfo.get("adult").asBoolean(false),
                        tmdbId = lineInfo.get("id").asInt(0),
                        originalTitle = lineInfo.get("original_title").asText(""),
                        popularity = lineInfo.get("popularity").floatValue())
            } else {
                null
            }
        }
        runBlocking { onNext(info) }
    }
}

fun loadSeriesFile(): File {
    val format = SimpleDateFormat("MM_dd_yyyy").format(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)))
    val url = "http://files.tmdb.org/p/exports/tv_series_ids_$format.json.gz"
    val target = File("series/$format.json.gz")
    val targetJson = File("series/$format.json")
    target.parentFile.mkdirs()
    target.createNewFile()
    targetJson.createNewFile()
    FileUtils.copyURLToFile(URL(url), target)
    val stream = GZIPInputStream(FileInputStream(target))
    Files.copy(stream, targetJson.toPath(), StandardCopyOption.REPLACE_EXISTING)
    return targetJson
}

fun readSeriesLines(file: File, onNext: (SeriesLineInfo) -> Unit) {
    val mapper = ObjectMapper()
    BufferedReader(FileReader(file)).use {
        it.lines().forEach {
            if (it.contains('{') && it.contains('}')) {
                val lineInfo = mapper.readTree(it)
                val info = SeriesLineInfo(lineInfo.get("id").asInt(0), lineInfo.get("original_name").asText(""), lineInfo.get("popularity").floatValue())
                onNext(info)
            }
        }
    }
}

data class MovieLineInfo(val isAdult: Boolean,
                         val tmdbId: Int,
                         val originalTitle: String,
                         val popularity: Float)

data class SeriesLineInfo(val id: Int,
                          val original_name: String,
                          val popularity: Float)