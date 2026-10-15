package com.example.mobiledev.prefs

import android.content.Context
import androidx.core.content.edit
import com.example.mobiledev.models.Pony
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object BackupManager {
    private const val BACKUP_FILE_NAME = "ponies_backup.json"

    private const val KEY_USE_BACKUP = "use_backup"

    fun setUseBackup(context: Context, use: Boolean) {
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit {
            putBoolean(KEY_USE_BACKUP, use)
        }
    }

    fun shouldUseBackup(context: Context): Boolean {
        return context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            .getBoolean(KEY_USE_BACKUP, false)
    }

    fun hasBackup(context: Context): Boolean {
        return File(context.filesDir, BACKUP_FILE_NAME).exists()
    }

    fun createBackup(context: Context, ponies: List<Pony>): Boolean {
        return try {
            val jsonArray = JSONArray()
            for (pony in ponies) {
                val obj = JSONObject().apply {
                    put("name", pony.name)
                    put("kind", pony.kind)
                    put("sex", pony.sex)
                    put("photoSrc", pony.photoSrc)
                    put("color", pony.color)
                }
                jsonArray.put(obj)
            }
            File(context.filesDir, BACKUP_FILE_NAME).writeText(jsonArray.toString())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun restoreBackup(context: Context): List<Pony>? {
        return try {
            val file = File(context.filesDir, BACKUP_FILE_NAME)
            if (!file.exists()) return null

            val jsonString = file.readText()
            val jsonArray = JSONArray(jsonString)
            val ponies = mutableListOf<Pony>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                ponies.add(Pony(
                    name = obj.getString("name"),
                    kind = obj.getString("kind"),
                    sex = obj.getString("sex"),
                    photoSrc = obj.getInt("photoSrc"),
                    color = obj.getInt("color")
                ))
            }
            ponies
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteBackup(context: Context): Boolean {
        return try {
            File(context.filesDir, BACKUP_FILE_NAME).delete()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}