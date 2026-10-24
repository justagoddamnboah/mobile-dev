package com.example.mobiledev.models

import com.example.mobiledev.R

object ProfilePictureRepository {
    fun getAvailableProfilePictures(): List<Int> {
        return listOf(
            R.drawable.profile_twilight_sparkle,
            R.drawable.profile_rarity,
            R.drawable.profile_rainbow_dash,
            R.drawable.profile_fluttershy,
            R.drawable.profile_pinkie_pie,
            R.drawable.profile_applejack,
            R.drawable.profile_sweetie_belle
        )
    }

    fun getProfilePictureNames(): Array<String> {
        return arrayOf(
            "Твайлайт Спаркл",
            "Рарити",
            "Рэйнбоу Дэш",
            "Флаттершай",
            "Пинки Пай",
            "Эпплджек",
            "Свити Белль"
        )
    }

    fun getProfilePictureByPosition(position: Int): Int {
        val avatars = getAvailableProfilePictures()
        return avatars.getOrElse(position) { avatars.first() }
    }

    fun getPositionByProfilePicture(avatarResId: Int): Int {
        val avatars = getAvailableProfilePictures()
        return avatars.indexOf(avatarResId).coerceAtLeast(0)
    }
}