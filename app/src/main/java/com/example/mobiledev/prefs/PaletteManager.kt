package com.example.mobiledev.prefs

import android.content.Context
import com.example.mobiledev.R
import androidx.core.content.edit

object PaletteManager {
    private const val PREFS_NAME = "AppPrefs"
    private const val KEY_PALETTE = "palette"

    const val PALETTE_PINK = "pink"
    const val PALETTE_BLUE = "blue"
    const val PALETTE_GREEN = "green"
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
        return arrayOf("Розовая", "Синяя", "Зеленая")
    }

    fun getPaletteKeyByPosition(position: Int): String {
        return when (position) {
            1 -> PALETTE_BLUE
            2 -> PALETTE_GREEN
            else -> PALETTE_PINK
        }
    }

    fun getPositionByPaletteKey(paletteKey: String): Int {
        return when (paletteKey) {
            PALETTE_BLUE -> 1
            PALETTE_GREEN -> 2
            else -> 0
        }
    }

    fun getThemeResByPalette(palette: String): Int {
        return when (palette) {
            PALETTE_BLUE -> R.style.Theme_MobileDev_Blue
            PALETTE_GREEN -> R.style.Theme_MobileDev_Green
            else -> R.style.Theme_MobileDev_Pink
        }
    }
}