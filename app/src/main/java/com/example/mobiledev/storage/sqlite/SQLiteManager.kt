package com.example.mobiledev.storage.sqlite

import com.example.mobiledev.database.PonyDatabase
import com.example.mobiledev.network.Pony
import com.example.mobiledev.storage.OfflineStorage
import kotlinx.coroutines.flow.Flow

class SQLiteManager : OfflineStorage {

    override fun load(): Flow<List<Pony>> =
        PonyDatabase.db.ponyDao().getAllPonies()

    override suspend fun save(items: List<Pony>) {
        PonyDatabase.db.ponyDao().replaceAll(items)
    }
}