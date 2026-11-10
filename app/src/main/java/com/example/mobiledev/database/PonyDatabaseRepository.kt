package com.example.mobiledev.database

import android.content.Context
import com.example.mobiledev.network.Pony
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PonyDatabaseRepository(context: Context) {

    private val dao = PonyDatabase.getInstance(context).ponyDao()

    val poniesFlow: Flow<List<Pony>> = dao.observePonies()
        .map { entities -> entities.map { it.toPony() } }
        .distinctUntilChanged()

    suspend fun getCachedPonies(): List<Pony> =
        dao.getPonies().map { it.toPony() }

    suspend fun hasCachedPonies(): Boolean = dao.countPonies() > 0

    suspend fun savePonies(ponies: List<Pony>) {
        dao.insertPonies(ponies.map { it.toEntity() })
    }

    suspend fun replacePonies(ponies: List<Pony>) {
        dao.replacePonies(ponies.map { it.toEntity() })
    }

    suspend fun updatePony(pony: Pony) {
        dao.updatePony(pony.toEntity())
    }

    suspend fun deletePony(pony: Pony) {
        dao.deletePony(pony.toEntity())
    }

    suspend fun clearPonies() {
        dao.clearPonies()
    }
}

private fun Pony.toEntity(): PonyEntity = PonyEntity(
    id = id,
    name = name,
    sex = sex,
    residence = residence,
    kind = kind,
    imageUrl = imageUrl
)

private fun PonyEntity.toPony(): Pony = Pony(
    id = id,
    name = name,
    sex = sex,
    residence = residence,
    kind = kind,
    imageUrl = imageUrl
)