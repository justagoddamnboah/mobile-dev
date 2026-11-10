package com.example.mobiledev.database

import androidx.room.TypeConverter
import org.json.JSONArray

class PonyConverters {
    @TypeConverter
    fun fromKindList(kinds: List<String>): String = JSONArray(kinds).toString()

    @TypeConverter
    fun toKindList(json: String): List<String> {
        val array = JSONArray(json)
        return (0 until array.length()).map { i -> array.getString(i) }
    }
}