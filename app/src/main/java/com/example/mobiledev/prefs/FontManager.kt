package com.example.mobiledev.prefs

import android.content.Context
import androidx.core.content.edit

object FontManager {
    private const val PREFS_NAME = "AppPrefs"
    private const val KEY_FONT_SCALE = "font_scale"

    private const val MIN_SCALE = 0.8f
    private const val MAX_SCALE = 1.5f
    private const val SCALE_STEP = 0.1f
    const val MAX_PROGRESS = 7

    fun getFontScale(context: Context): Float {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getFloat(KEY_FONT_SCALE, 1.0f)
    }

    fun setFontScale(context: Context, scale: Float) {
        val safeScale = scale.coerceIn(MIN_SCALE, MAX_SCALE)

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putFloat(KEY_FONT_SCALE, safeScale)
            }
    }

    fun getProgressFromScale(scale: Float): Int {
        val progress = ((scale - MIN_SCALE) / SCALE_STEP).toInt()
        return progress.coerceIn(0, MAX_PROGRESS)
    }

    fun getScaleFromProgress(progress: Int): Float {
        val safeProgress = progress.coerceIn(0, MAX_PROGRESS)
        return MIN_SCALE + (safeProgress * SCALE_STEP)
    }
}