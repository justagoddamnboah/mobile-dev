package com.example.mobiledev.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PonyEntity::class], version = 1, exportSchema = false)
@TypeConverters(PonyConverters::class)
abstract class PonyDatabase : RoomDatabase() {

    abstract fun ponyDao(): PonyDao

    companion object {
        @Volatile
        private var instance: PonyDatabase? = null

        fun getInstance(context: Context): PonyDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PonyDatabase::class.java,
                    "ponies_database"
                ).build().also { instance = it }
            }
    }
}