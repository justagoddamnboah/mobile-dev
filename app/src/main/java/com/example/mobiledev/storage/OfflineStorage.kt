package com.example.mobiledev.storage

import com.example.mobiledev.network.Pony
import kotlinx.coroutines.flow.Flow

interface OfflineStorage {
    fun load(): Flow<List<Pony>>
    suspend fun save(items: List<Pony>)
}