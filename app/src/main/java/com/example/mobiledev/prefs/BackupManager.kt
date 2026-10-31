package com.example.mobiledev.prefs

import android.content.Context
import androidx.core.content.edit
import com.example.mobiledev.network.Pony
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
                    put("id", pony.id)
                    put("name", pony.name)
                    put("sex", pony.sex)
                    put("residence", pony.residence)
                    put("kind", JSONArray(pony.kind))
                    put("imageUrl", pony.imageUrl)
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

                val kindArray = obj.optJSONArray("kind")
                val kindList = if (kindArray != null) {
                    (0 until kindArray.length()).map { index ->
                        kindArray.getString(index)
                    }
                } else {
                    val oldKind = obj.optString("kind", "")
                    if (oldKind.isNotBlank()) listOf(oldKind) else emptyList()
                }

                ponies.add(
                    Pony(
                        id = obj.optInt("id", 0),
                        name = obj.getString("name"),
                        sex = obj.optString("sex").takeIf { it.isNotBlank() },
                        residence = obj.optString("residence").takeIf { it.isNotBlank() },
                        kind = kindList,
                        imageUrl = obj.optString("imageUrl", "")
                    )
                )
            }
            ponies
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteBackup(context: Context): Boolean {
        return try {
            val file = File(context.filesDir, BACKUP_FILE_NAME)

            val deleted = if (file.exists()) {
                file.delete()
            } else {
                true
            }

            if (deleted) {
                setUseBackup(context, false)
            }

            deleted

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}