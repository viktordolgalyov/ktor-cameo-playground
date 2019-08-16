package com.cameo.scrapper

import com.cameo.common.data.formatDate
import com.cameo.common.getOrNull
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import info.movito.themoviedbapi.TmdbMovies
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.tools.ApiUrl
import info.movito.themoviedbapi.tools.RequestMethod
import org.slf4j.LoggerFactory

fun TmdbTV.getChanges(from: Long, to: Long, page: Int = 1): ChangeResponse {
    val url = ApiUrl(TmdbTV.TMDB_METHOD_TV, "changes")
    val start = Math.min(from, to)
    val end = Math.max(from, to)
    url.addParam("start_date", start.formatDate())
    url.addParam("end_date", end.formatDate())
    url.addParam("page", page)
    val mapper = ObjectMapper()
    val results = getOrNull { randomApi().requestWebPage(url, null, RequestMethod.GET) }
    return getOrNull {
        val json = mapper.readTree(results)
        val p = json.get("page").asInt(1)
        val totalPages = json.get("total_pages").asInt(1)
        val totalResults = json.get("total_results").asInt(0)
        val array = json.get("results")
        val items = (0..totalResults).mapNotNull {
            val result = array.get(it) as? JsonNode
            val id = result?.get("id")?.asInt(-1) ?: -1
            val adult = result?.get("adult")?.asBoolean(false) ?: false
            if (id <= 0) null else ChangeDTO(id, adult)
        }
        ChangeResponse(items, p, totalPages)
    } ?: ChangeResponse(emptyList(), 1, 1)
}

fun TmdbMovies.getChanges(from: Long, to: Long, page: Int = 1): ChangeResponse {
    val url = ApiUrl(TmdbMovies.TMDB_METHOD_MOVIE, "changes")
    val start = Math.min(from, to)
    val end = Math.max(from, to)
    url.addParam("start_date", start.formatDate())
    url.addParam("end_date", end.formatDate())
    url.addParam("page", page)
    val mapper = ObjectMapper()
    val results = getOrNull { randomApi().requestWebPage(url, null, RequestMethod.GET) }
    return getOrNull {
        LoggerFactory.getLogger(this::class.java).error(results)
        val json = mapper.readTree(results)
        val p = json.get("page").asInt(1)
        val totalPages = json.get("total_pages").asInt(1)
        val totalResults = json.get("total_results").asInt(0)
        val array = json.get("results")
        val items = (0..totalResults).mapNotNull {
            val result = array.get(it) as? JsonNode
            val id = result?.get("id")?.asInt(-1) ?: -1
            val adult = result?.get("adult")?.asBoolean(false) ?: false
            if (id <= 0) null else ChangeDTO(id, adult)
        }
        ChangeResponse(items, p, totalPages)
    } ?: ChangeResponse(emptyList(), 1, 1)
}

data class ChangeResponse(var results: List<ChangeDTO>,
                          var page: Int,
                          var total_pages: Int)

data class ChangeDTO(var id: Int, var adult: Boolean)