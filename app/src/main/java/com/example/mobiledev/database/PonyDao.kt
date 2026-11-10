package com.example.mobiledev.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mobiledev.network.Pony
import kotlinx.coroutines.flow.Flow

@Dao
interface PonyDao {

    @Query("SELECT * FROM ponies ORDER BY id")
    fun getAllPonies(): Flow<List<Pony>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Pony>)

    @Update
    suspend fun update(pony: Pony)

    @Delete
    suspend fun delete(pony: Pony)

    @Query("DELETE FROM ponies")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(items: List<Pony>) {
        clear()
        insertAll(items)
    }
}