package com.example.mobiledev.models

import android.graphics.Color
import com.example.mobiledev.R

object PonyRepository {
    fun getAllPonies(): List<Pony> {
        return listOf(
            Pony("Твайлайт Спаркл", "Аликорн", "Женский", R.drawable.twilight_sparkle, Color.rgb(230, 210, 255)),
            Pony("Спайк", "Дракон", "Мужской", R.drawable.spike, Color.rgb(190, 235, 200)),
            Pony("Рэйнбоу Дэш", "Пегас", "Женский", R.drawable.rainbow_dash, Color.rgb(190, 225, 255)),
            Pony("Эпплджек", "Земная пони", "Женский", R.drawable.applejack, Color.rgb(255, 225, 170)),
            Pony("Рарити", "Единорог", "Женский", R.drawable.rarity, Color.rgb(230, 215, 255)),
            Pony("Флаттершай", "Пегас", "Женский", R.drawable.fluttershy, Color.rgb(255, 235, 170)),
            Pony("Пинки Пай", "Земная пони", "Женский", R.drawable.pinkie_pie, Color.rgb(255, 190, 220)),
            Pony("Эппл Блум", "Земная пони", "Женский", R.drawable.apple_bloom, Color.rgb(255, 205, 130)),
            Pony("Свити Белль", "Единорог", "Женский", R.drawable.sweetie_belle, Color.rgb(245, 225, 240)),
            Pony("Скуталу", "Пегас", "Женский", R.drawable.scootaloo, Color.rgb(255, 175, 125))
        )
    }

    fun getBackupPonies(): List<Pony> {
        return listOf(
            Pony("Твайлайт Спаркл", "Аликорн", "Женский", R.drawable.twilight_sparkle, Color.rgb(230, 210, 255)),
            Pony("Спайк", "Дракон", "Мужской", R.drawable.spike, Color.rgb(190, 235, 200))
        )
    }
}