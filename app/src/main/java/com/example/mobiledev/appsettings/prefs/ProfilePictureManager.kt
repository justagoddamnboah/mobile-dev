package com.example.mobiledev.appsettings.prefs

import android.content.Context
import com.example.mobiledev.models.repositories.ProfilePictureRepository
import androidx.core.content.edit

object ProfilePictureManager {
    private const val PREFS_NAME = "profile_picture_settings"
    private const val KEY_PROFILE_PICTURE = "profile_picture"

    fun getProfilePicture(context: Context): Int {
        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        return preferences.getInt(
            KEY_PROFILE_PICTURE,
            ProfilePictureRepository.getAvailableProfilePictures().first()
        )
    }

    fun saveProfilePicture(context: Context, profilePictureResId: Int) {
        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        preferences.edit {
            putInt(KEY_PROFILE_PICTURE, profilePictureResId)
        }
    }
}