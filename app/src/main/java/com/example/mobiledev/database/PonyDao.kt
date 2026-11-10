package com.example.mobiledev.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PonyDao {

    @Query("SELECT * FROM ponies ORDER BY id ASC")
    fun observePonies(): Flow<List<PonyEntity>>

    @Query("SELECT * FROM ponies ORDER BY id ASC")
    suspend fun getPonies(): List<PonyEntity>

    @Query("SELECT COUNT(*) FROM ponies")
    suspend fun countPonies(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPonies(ponies: List<PonyEntity>)

    @Update
    suspend fun updatePony(pony: PonyEntity)

    @Delete
    suspend fun deletePony(pony: PonyEntity)

    @Query("DELETE FROM ponies")
    suspend fun clearPonies()

    @Transaction
    suspend fun replacePonies(ponies: List<PonyEntity>) {
        clearPonies()
        insertPonies(ponies)
    }
}