package com.example.mobiledev.prefs

import android.content.Context
import com.example.mobiledev.R
import androidx.core.content.edit

object PaletteManager {
    private const val PREFS_NAME = "AppPrefs"
    private const val KEY_PALETTE = "palette"

    const val PALETTE_PINK = "Розовая"
    const val PALETTE_PURPLE = "Фиолетовая"
    const val PALETTE_ORANGE = "Оранжевая"
    const val DEFAULT_PALETTE = PALETTE_PINK

    fun getPalette(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_PALETTE, DEFAULT_PALETTE) ?: DEFAULT_PALETTE
    }

    fun setPalette(context: Context, palette: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_PALETTE, palette)
            }
    }

    fun getPaletteNames(): Array<String> {
        return arrayOf(PALETTE_PINK, PALETTE_PURPLE, PALETTE_ORANGE)
    }

    fun getPaletteKeyByPosition(position: Int): String {
        return when (position) {
            1 -> PALETTE_PURPLE
            2 -> PALETTE_ORANGE
            else -> PALETTE_PINK
        }
    }

    fun getPositionByPaletteKey(paletteKey: String): Int {
        return when (paletteKey) {
            PALETTE_PURPLE -> 1
            PALETTE_ORANGE -> 2
            else -> 0
        }
    }

    fun getThemeResByPalette(palette: String): Int {
        return when (palette) {
            PALETTE_PURPLE -> R.style.Theme_MobileDev_Purple
            PALETTE_ORANGE -> R.style.Theme_MobileDev_Orange
            else -> R.style.Theme_MobileDev_Pink
        }
    }
}