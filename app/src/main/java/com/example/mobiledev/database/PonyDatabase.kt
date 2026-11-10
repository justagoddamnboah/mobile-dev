package com.example.mobiledev.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mobiledev.MobileApplication
import com.example.mobiledev.network.Pony


@Database(entities = [Pony::class], version = 2, exportSchema = false)
@TypeConverters(PonyConverters::class)
abstract class PonyDatabase : RoomDatabase() {

    abstract fun ponyDao(): PonyDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // В версии 1 и 2 поля таблицы ponies одинаковые.
            }
        }

        val db: PonyDatabase by lazy {
            Room.databaseBuilder(
                MobileApplication.mobileApplicationContext(),
                PonyDatabase::class.java,
                "ponies_database"
            ).addMigrations(MIGRATION_1_2).build()
        }
    }
}