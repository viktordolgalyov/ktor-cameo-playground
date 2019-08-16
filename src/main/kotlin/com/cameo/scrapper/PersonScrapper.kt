package com.cameo.scrapper

import com.cameo.common.getOrNull
import com.cameo.source.table.PersonsTable
import info.movito.themoviedbapi.model.people.PersonPeople
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select

object PersonScrapper {

    fun getPersonId(tmdbPersonId: Int): EntityID<Int>? {
        val alreadyStored = PersonsTable
                .slice(PersonsTable.id)
                .select { PersonsTable.tmdbId eq tmdbPersonId }
                .limit(1)
                .firstOrNull()
                ?.get(PersonsTable.id)
        return alreadyStored ?: readPerson(tmdbPersonId)?.let { writePerson(it) }
    }

    private fun readPerson(tmdbId: Int): PersonInfo? {
        val person = getOrNull { randomApi().people.getPersonInfo(tmdbId) }
        return person?.let { PersonInfo(person) }
    }

    private fun writePerson(personRows: PersonInfo): EntityID<Int> {
        return PersonsTable.insertAndGetId {
            it[tmdbId] = personRows.person.id
            it[imdbId] = personRows.person.imdbId.orEmpty()
            it[name] = personRows.person.name.orEmpty()
            it[biography] = personRows.person.biography.orEmpty()
            it[birthday] = personRows.person.birthday.orEmpty()
            it[deathday] = personRows.person.deathday.orEmpty()
            it[homepage] = personRows.person.homepage.orEmpty().take(240)
            it[birthplace] = personRows.person.birthplace.orEmpty()
            it[popularity] = personRows.person.popularity
            it[photoPath] = personRows.person.profilePath.orEmpty()
        }
    }

    data class PersonInfo(val person: PersonPeople)
}