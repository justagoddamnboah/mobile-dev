package com.example.mobiledev.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ponies")
data class PonyEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val sex: String?,
    val residence: String?,
    val kind: List<String>,
    val imageUrl: String
)